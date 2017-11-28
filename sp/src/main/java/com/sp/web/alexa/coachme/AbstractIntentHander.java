package com.sp.web.alexa.coachme;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.sp.web.alexa.AlexaIntentHandler;
import com.sp.web.alexa.AlexaIntentType;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.insights.InsightsSteps;
import com.sp.web.model.User;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class AbstractIntentHander implements AlexaIntentHandler {
  
  private static final Logger log = Logger.getLogger(AbstractIntentHander.class);
  
  @Override
  public SpeechletResponse execute(SPIntentRequest intentRequest) {
    Session session = intentRequest.getSession();
    // validate intent
    switch (intentRequest.getAlexaIntentType()) {
    case No:
    case Yes:
      Slot slot = intentRequest.getIntentRequest().getIntent().getSlot("Literal");
      String value = slot.getValue();
      if (StringUtils.isEmpty(value)
          || !(StringUtils.containsIgnoreCase(value, "absolutely")
              || StringUtils.containsIgnoreCase(value, "of course")
              || StringUtils.containsIgnoreCase(value, "not now")
              || StringUtils.containsIgnoreCase(value, "no thank you")
              || StringUtils.containsIgnoreCase(value, "nope")
              || StringUtils.containsIgnoreCase(value, "no")
              || StringUtils.containsIgnoreCase(value, "yup")
              || StringUtils.containsIgnoreCase(value, "I don't know")
              || StringUtils.containsIgnoreCase(value, "yup")
              || StringUtils.containsIgnoreCase(value, "please") || StringUtils.containsIgnoreCase(
              value, "yes"))) {
        log.info("Invalid , yes or no intent , slot value " + value);
        String speechText = MessagesHelper.getMessage("alexa.yes.no.validate", intentRequest
            .getUser().getFirstName());
        // Create reprompt
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt);
      }
      break;
    
    case AskName:
      
      /* check if correct intent is requsted */
      switch (intentRequest.getActionType()) {
      case CoachMe:
        String stepString = (String) session.getAttribute("nextStep");
        CoacheMeSteps step;
        if (stepString == null) {
          step = CoacheMeSteps.WelcomeStep;
          session.setAttribute("nextStep", step.toString());
        } else {
          step = CoacheMeSteps.valueOf(stepString);
        }
        
        AlexaIntentType[] intentInocation = step.getIntentInocation();
        if (!ArrayUtils.contains(intentInocation, AlexaIntentType.AskName)) {
          String speechText = MessagesHelper.getMessage("alexa.yes.no.validate", intentRequest
              .getUser().getFirstName());
          // Create reprompt
          PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
          speech.setText(speechText);
          Reprompt reprompt = new Reprompt();
          reprompt.setOutputSpeech(speech);
          
          return SpeechletResponse.newAskResponse(speech, reprompt);
        }
        
        break;
      case Insights:
        String stepInsightsString = (String) session.getAttribute("nextStep");
        InsightsSteps insightStep;
        if (stepInsightsString == null) {
          insightStep = InsightsSteps.WelcomeStep;
          session.setAttribute("nextStep", insightStep.toString());
        } else {
          insightStep = InsightsSteps.valueOf(stepInsightsString);
        }
        
        AlexaIntentType[] intentInsightsInocation = insightStep.getAlexaIntentTypes();
        if (!ArrayUtils.contains(intentInsightsInocation, AlexaIntentType.AskName)) {
          String speechText = MessagesHelper.getMessage("alexa.yes.no.validate", intentRequest
              .getUser().getFirstName());
          // Create reprompt
          PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
          speech.setText(speechText);
          Reprompt reprompt = new Reprompt();
          reprompt.setOutputSpeech(speech);
          
          return SpeechletResponse.newAskResponse(speech, reprompt);
        }
        
      default:
        break;
      }
      
    default:
      break;
    }
    
    // Validate correct request.
    
    return executeHandler(intentRequest);
    
  }
  
  /**
   * Creates a {@code SpeechletResponse} for the help intent.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  protected SpeechletResponse getAskCollegueName(Session session, User user) {
    
    String collegeAskCountString = (String) session.getAttribute("nameAskCount");
    int collegeAskCount;
    if (collegeAskCountString == null) {
      collegeAskCount = 1;
    } else {
      collegeAskCount = Integer.valueOf(collegeAskCountString);
    }
    
    String key = "alexa.relationship.welcome.nointent." + collegeAskCount;
    
    if (collegeAskCount < 3) {
      collegeAskCount += 1;
    }
    session.setAttribute("nameAskCount", String.valueOf(collegeAskCount));
    
    String speechText = MessagesHelper.getMessage(key, user.getFirstName());
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Ask College Name ");
    card.setContent(speechText);
    
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    
    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);
    
    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }
  
  /**
   * isFullOrPartialEvent method tells whether the intent is full or partial intent.
   * 
   * @param user
   *          logged in user.
   * @param request
   *          alexa request.
   * @param alexaIntentType
   *          alexaIntent type.
   * @param session
   *          session.
   * @return true or false;
   */
  protected boolean isFullOrPartialEvent(User user, final IntentRequest request,
      AlexaIntentType alexaIntentType, Session session, UserFactory userFactory) {
    switch (alexaIntentType) {
    case CoachMe:
    case Insights:
    case AskName:
      Slot slot = request.getIntent().getSlot("Name");
      if (slot != null) {
        String value = slot.getValue();
        if (value == null) {
          return false;
        }
        log.info("check value " + value);
        List<User> users = userFactory.findUserByName(value, user.getCompanyId());
        log.info("Total users" + users + ",: user " + user.getCompanyId());
        if (CollectionUtils.isEmpty(users)) {
          return false;
        } else if (users.size() > 1) {
          return false;
        } else {
          User user2 = users.get(0);
          session.setAttribute("userid", user2.getId());
          return true;
        }
      } else {
        return false;
      }
      
    default:
      return false;
    }
  }
  
  protected abstract SpeechletResponse executeHandler(SPIntentRequest intentRequest);
}
