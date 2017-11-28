package com.sp.web.alexa.insights.steps;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.sp.web.Constants;
import com.sp.web.alexa.AlexaIntentType;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.flow.AbstractAlexaStep;
import com.sp.web.alexa.flow.Step;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.alexa.insights.InsightsSteps;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.model.User;
import com.sp.web.relationship.CompareReport;
import com.sp.web.relationship.RelationshipReportManager;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * GetRelationShipStep will get the relationship step for the user.
 * 
 * @author pradeepruhil
 *
 */
@Component("getInsights")
public class GetInsightsStep extends AbstractAlexaStep {
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private RelationshipReportManager relationshipReportManager;
  
  @Override
  public String getStepName() {
    return InsightsSteps.GetInsights.getStepName();
  }
  
  @Override
  public void execute(StepContext context) throws Exception {
    
    SPIntentRequest spIntentRequest = (SPIntentRequest) context.getSeedObject();
    
    AlexaIntentType alexaIntentType = spIntentRequest.getAlexaIntentType();
    Session session = spIntentRequest.getSession();
    String userId = (String) session.getAttribute("userid");
    User withUser = userFactory.getUser(userId);
    User user = spIntentRequest.getUser();
    
    switch (alexaIntentType) {
    case Yes:
      Map<String, CompareReport> compareReport = relationshipReportManager.getCompareReport(user,
          withUser, RangeType.Primary, RangeType.UnderPressure, user.getLocale());
      populateInsightsData(context, withUser, user, compareReport,
          Constants.PARAM_RELATIONSHIP_MANAGER_REPORT_SECONDARY);
      break;
    
    case No:
      Map<String, CompareReport> compareReportUnderPressure = relationshipReportManager
          .getCompareReport(user, withUser, RangeType.Primary, RangeType.Primary, user.getLocale());
      populateInsightsData(context, withUser, user, compareReportUnderPressure,
          Constants.PARAM_RELATIONSHIP_MANAGER_REPORT_SECONDARY);
      break;
    default:
      break;
    }
  }
  
  private void populateInsightsData(StepContext context, User withUser, User user,
      Map<String, CompareReport> compareReport, String reportType) {
    CompareReport compareReport2 = compareReport.get(reportType);
    String title = compareReport2.getTitle();
    /* split the text with stinr <p> */
    StringBuilder insightsBuilder = new StringBuilder();
    
    insightsBuilder.append("<p>"
        + MessagesHelper.getMessage("alexa.user.thankyou", user.getLocale(), user.getFirstName())
        + "</p>");
    insightsBuilder.append("<p>" + title + "</p>");
    insightsBuilder.append("<p>"
        + MessagesHelper.getMessage("alexa.insights.thankyou", user.getFirstName(),
            withUser.getFirstName()) + "</p>");
    
    String ssml = "<speak>" + insightsBuilder.toString() + "</speak>";
    SimpleCard card = new SimpleCard();
    card.setTitle("Insigts");
    card.setContent(insightsBuilder.toString());
    
    // Create the plain text output.
    SsmlOutputSpeech speech = new SsmlOutputSpeech();
    speech.setSsml(ssml);
    
    SpeechletResponse newAskResponse = SpeechletResponse.newTellResponse(speech, card);
    context.setResponse(newAskResponse);
  }
}
