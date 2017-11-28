package com.sp.web.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.sp.web.model.User;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AlexaServiceHandler Service handles all the user interaction and response to the alexa device. It
 * contains all the sentenses we have to return to the alexa device to the end user.
 * 
 */
@Component
@Qualifier("alexaServiceHandler")
public class AlexaServiceHandler implements Speechlet {
  
  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(AlexaServiceHandler.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Override
  public void onSessionStarted(final SessionStartedRequest request, final Session session)
      throws SpeechletException {
    log.info("onSessionStarted requestId={" + request.getRequestId() + "}, sessionId={"
        + session.getSessionId() + "}");
    
  }
  
  @Override
  public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
      throws SpeechletException {
    if (log.isInfoEnabled()) {
      log.info("onLaunch requestId={" + request.getRequestId() + "}, sessionId={"
          + session.getSessionId() + "}");
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = GenericUtils.getUserFromAuthentication(authentication);
    
    SPIntentRequest intentRequest = new SPIntentRequest();
    intentRequest.setAlexaIntentType(AlexaIntentType.SPWelcomeIntent);
    intentRequest.setSession(session);
    intentRequest.setUser(user);
    AlexaIntentHandler alexaIntentHandler = (AlexaIntentHandler) ApplicationContextUtils
        .getBean(AlexaIntentType.SPWelcomeIntent.getIntentHandler());
    return alexaIntentHandler.execute(intentRequest);
    
  }
  
  /**
   * check the Intent from the request.
   * 
   * @param request
   *          Intent request for the job
   * @return the alexa intent type of the request.
   */
  private AlexaIntentType getIntentType(IntentRequest request, User user) {
    
    Intent intent = request.getIntent();
    log.info("Intent name " + intent.getName());
    if (intent.getName().equalsIgnoreCase("coachme")) {
      return AlexaIntentType.CoachMe;
    } else if (intent.getName().equalsIgnoreCase("yes")) {
      return AlexaIntentType.Yes;
    } else if (intent.getName().equalsIgnoreCase("no")) {
      return AlexaIntentType.No;
    } else if (intent.getName().equalsIgnoreCase("askName")) {
      return AlexaIntentType.AskName;
    } else if (intent.getName().equalsIgnoreCase("Insights")) {
      return AlexaIntentType.Insights;
    }
    return AlexaIntentType.SPWelcomeIntent;
  }
  
  @Override
  public SpeechletResponse onIntent(final IntentRequest request, final Session session)
      throws SpeechletException {
    if (log.isInfoEnabled()) {
      log.info("onIntent requestId={" + request.getRequestId() + "}, sessionId={"
          + session.getSessionId() + "}");
    }
    
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = GenericUtils.getUserFromAuthentication(authentication);
    
    AlexaIntentType intentType = getIntentType(request, user);
    log.info("Intent returned " + intentType);
    /* check the intent parent */
    String actionTypeString = (String) session.getAttribute("actionType");
    
    AlexaIntentType actionType = AlexaIntentType.SPWelcomeIntent;
    if (StringUtils.isNotBlank(actionTypeString)) {
      actionType = AlexaIntentType.valueOf(actionTypeString);
    }
    
    if (actionType == null) {
      /* for the first action */
      if (!intentType.isHeadIntent()) {
        /*
         * Yes or NO intent are fired wihtout starting, telling the users, how to use the intents,
         * coache and insights
         */
        actionType = AlexaIntentType.SPWelcomeIntent;
      } else {
        session.setAttribute("actionType", intentType);
      }
      
    }
    
    if (intentType.isHeadIntent() && actionType != intentType) {
      session.setAttribute("actionType", intentType);
      actionType = intentType;
    }
    
    SPIntentRequest intentRequest = new SPIntentRequest();
    intentRequest.setAlexaIntentType(intentType);
    intentRequest.setIntentRequest(request);
    intentRequest.setSession(session);
    intentRequest.setUser(user);
    intentRequest.setActionType(actionType);
    AlexaIntentHandler alexaIntentHandler = (AlexaIntentHandler) ApplicationContextUtils
        .getBean(actionType.getIntentHandler());
    return alexaIntentHandler.execute(intentRequest);
    
  }
  
  private SpeechletResponse getThankYouMessage(String key, String name) {
    String speechText = MessagesHelper.getMessage(key, name);
    
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("ThankYou");
    card.setContent(speechText);
    
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    return SpeechletResponse.newTellResponse(speech, card);
  }
  
  private SpeechletResponse getReleationShipResponse(User user, String value) {
    
    StringBuilder builder = new StringBuilder();
    String speechText = MessagesHelper.getMessage("alexa.relationship.response.1",
        user.getFirstName(), value);
    builder.append(speechText);
    log.info("Relationship message" + user.getFirstName() + " " + value);
    
    builder.append(MessagesHelper.getMessage("alexa.relationship.response.conflict.1", value));
    
    builder.append(MessagesHelper.getMessage("alexa.relationship.response.final",
        user.getFirstName(), value));
    log.info("Relationship message" + builder.toString());
    
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Relationship");
    card.setContent(builder.toString());
    
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(builder.toString());
    
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);
    
    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }
  
  @Override
  public void onSessionEnded(final SessionEndedRequest request, final Session session)
      throws SpeechletException {
    if (log.isInfoEnabled()) {
      log.info("onSessionEnded requestId={" + request.getRequestId() + "}, sessionId={"
          + session.getSessionId() + "}");
    }
    session.removeAttribute("actionType");
  }
  
  /**
   * Creates a {@code SpeechletResponse} for the help intent.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  private SpeechletResponse getHelpResponse() {
    String speechText = "You can say hello to me!";
    
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("HelloWorld");
    card.setContent(speechText);
    
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    
    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);
    
    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }
}