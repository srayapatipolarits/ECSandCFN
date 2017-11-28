package com.sp.web.alexa.coachme.steps;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.sp.web.Constants;
import com.sp.web.alexa.AlexaIntentType;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.coachme.CoacheMeSteps;
import com.sp.web.alexa.flow.AbstractAlexaStep;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.model.User;
import com.sp.web.relationship.CompareReport;
import com.sp.web.relationship.RelationshipReportManager;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * GetRelationShipStep will get the relationship step for the user.
 * 
 * @author pradeepruhil
 *
 */
@Component("getRelationShip")
public class GetRelationShipStep extends AbstractAlexaStep {
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private RelationshipReportManager relationshipReportManager;
  
  @Override
  public String getStepName() {
    return CoacheMeSteps.GetRelationShip.getStepName();
  }
  
  @Override
  public void execute(StepContext context) throws Exception {
    
    SPIntentRequest spIntentRequest = (SPIntentRequest) context.getSeedObject();
    
    Session session = spIntentRequest.getSession();
    String userId = (String) session.getAttribute("userid");
    User withUser = userFactory.getUser(userId);
    User user = spIntentRequest.getUser();
    
    AlexaIntentType alexaIntentType = spIntentRequest.getAlexaIntentType();
    String userPresure = (String) session.getAttribute("userUnderPressure");
    if (userPresure == null) {
      userPresure = "false";
    }
    RangeType userRangeType = userPresure.equalsIgnoreCase("true") ? RangeType.UnderPressure
        : RangeType.Primary;
    switch (alexaIntentType) {
    case Yes:
      
      Map<String, CompareReport> underPressure = relationshipReportManager.getCompareReport(user,
          withUser, RangeType.UnderPressure, userRangeType, user.getLocale());
      getRElationShip(context, withUser, user, underPressure);
      break;
    case No:
      Map<String, CompareReport> compareReport = relationshipReportManager.getCompareReport(user,
          withUser, RangeType.Primary, userRangeType, user.getLocale());
      getRElationShip(context, withUser, user, compareReport);
      break;
    default:
    }
    
  }
  
  private void getRElationShip(StepContext context, User withUser, User user,
      Map<String, CompareReport> compareReport) {
    CompareReport compareReport2 = compareReport
        .get(Constants.PARAM_RELATIONSHIP_MANAGER_REPORT_PRIMARY);
    String effort = compareReport2.getEffort();
    /* split the text with stinr <p> */
    Document parse = Jsoup.parse(effort);
    Elements paragraphs = parse.select("p");
    StringBuilder efffotTxt = new StringBuilder();
    
    String speechText = MessagesHelper.getMessage("alexa.relationship.response.1",
        user.getFirstName(), withUser.getFirstName());
    efffotTxt.append(speechText);
    
    for (int i = 0; i < paragraphs.size(); i++) {
      
      if (i == 2) {
        efffotTxt.append("<p>and </p>").append(paragraphs.get(i).outerHtml());
        break;
      } else {
        efffotTxt.append(paragraphs.get(i).outerHtml());
      }
    }
    
    efffotTxt.append("<p>"
        + MessagesHelper.getMessage("alexa.relationship.response.2", user.getFirstName()) + "</p>");
    String avoid = compareReport2.getAvoid();
    Document avoidParse = Jsoup.parse(avoid);
    Elements avoidPaElements = avoidParse.select("p");
    
    for (int i = 0; i < avoidPaElements.size(); i++) {
      
      if (i == 2) {
        efffotTxt.append("<p>and </p>").append(avoidPaElements.get(i).outerHtml());
        break;
      } else {
        efffotTxt.append(avoidPaElements.get(i).outerHtml());
      }
    }
    efffotTxt.append("<p>"
        + MessagesHelper.getMessage("alexa.relationship.response.final", user.getFirstName(),
            withUser.getFirstName()) + "</p>");
    
    String ssml = "<speak>" + efffotTxt.toString() + "</speak>";
    SimpleCard card = new SimpleCard();
    card.setTitle("Coache Me");
    card.setContent(effort);
    
    // Create the plain text output.
    SsmlOutputSpeech speech = new SsmlOutputSpeech();
    speech.setSsml(ssml);
    
    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);
    
    SpeechletResponse newAskResponse = SpeechletResponse.newAskResponse(speech, reprompt, card);
    context.setResponse(newAskResponse);
  }
}
