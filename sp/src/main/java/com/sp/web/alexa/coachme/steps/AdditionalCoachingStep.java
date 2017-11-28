package com.sp.web.alexa.coachme.steps;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
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
 * 
 * @author pradeepruhil
 *
 */
@Component("additionalCoaching")
public class AdditionalCoachingStep extends AbstractAlexaStep {
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private RelationshipReportManager relationshipReportManager;
  
  @Override
  public String getStepName() {
    return CoacheMeSteps.AdditionalCoaching.getStepName();
  }
  
  @Override
  public void execute(StepContext context) throws Exception {
    
    SPIntentRequest spIntentRequest = (SPIntentRequest) context.getSeedObject();
    
    AlexaIntentType alexaIntentType = spIntentRequest.getAlexaIntentType();
    switch (alexaIntentType) {
    case Yes:
      
      Session session = spIntentRequest.getSession();
      String userId = (String) session.getAttribute("userid");
      User withUser = userFactory.getUser(userId);
      User user = spIntentRequest.getUser();
      
      Map<String, CompareReport> compareReport = relationshipReportManager.getCompareReport(user,
          withUser, RangeType.Primary, RangeType.Primary, user.getLocale());
      CompareReport compareReport2 = compareReport
          .get(Constants.PARAM_RELATIONSHIP_MANAGER_REPORT_PRIMARY);
      String effort = compareReport2.getEffort();
      /* split the text with stinr <p> */
      Document parse = Jsoup.parse(effort);
      Elements paragraphs = parse.select("p");
      StringBuilder efffotTxt = new StringBuilder();
      
      String continueMessage = MessagesHelper.getMessage("alexa.relationship.response.continue",
          user.getLocale(), user.getFirstName(), withUser.getFirstName());
      efffotTxt.append(continueMessage);
      if (paragraphs.size() > 3) {
        
        for (int i = 3; i < paragraphs.size(); i++) {
          
          if (i >= 6 && paragraphs.size() > 6) {
            efffotTxt.append("<p>and </p>").append(paragraphs.get(i).outerHtml());
            break;
          } else {
            efffotTxt.append(paragraphs.get(i).outerHtml());
          }
        }
      }
      efffotTxt.append("<p>"
          + MessagesHelper.getMessage("alexa.relationship.response.2", user.getFirstName())
          + "</p>");
      String avoid = compareReport2.getAvoid();
      Document avoidParse = Jsoup.parse(avoid);
      Elements avoidPaElements = avoidParse.select("p");
      
      for (int i = 3; i < avoidPaElements.size(); i++) {
        
        if (i >= 6 && paragraphs.size() > 6) {
          efffotTxt.append("<p>and </p>").append(avoidPaElements.get(i).outerHtml());
          break;
        } else {
          efffotTxt.append(avoidPaElements.get(i).outerHtml());
        }
      }
      efffotTxt.append("<p>"
          + MessagesHelper.getMessage("alexa.relationship.response.thanks", user.getFirstName(),
              withUser.getFirstName()) + "</p>");
      
      String ssml = "<speak>" + efffotTxt.toString() + "</speak>";
      SimpleCard card = new SimpleCard();
      card.setTitle("Coache Me");
      card.setContent(effort);
      
      // Create the plain text output.
      SsmlOutputSpeech speech = new SsmlOutputSpeech();
      speech.setSsml(ssml);
      
      SpeechletResponse newAskResponse = SpeechletResponse.newTellResponse(speech, card);
      context.setResponse(newAskResponse);
      break;
    
    case No:
      
      SpeechletResponse createSpeechletTellResponse = createSpeechletTellResponse("Coach Me",
          "alexa.relationship.response.thanks", spIntentRequest.getUser().getLocale(),
          spIntentRequest.getUser().getFirstName());
      context.setResponse(createSpeechletTellResponse);
    default:
      break;
    }
  }
}
