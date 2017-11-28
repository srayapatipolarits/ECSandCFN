package com.sp.web.alexa.coachme;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.alexa.flow.StepManagerFactory;
import com.sp.web.model.User;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("coacheMeIntent")
public class CoacheMeIntentHandler extends AbstractIntentHander {
  
  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(CoacheMeIntentHandler.class);
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  @Qualifier("coachMeStepManagerFactory")
  private StepManagerFactory stepManagerFactory;
  
  /**
   * Execute method will handle the intent for the for the alex intent.
   * 
   * @param intentRequest
   *          is the intent request.
   */
  @Override
  public SpeechletResponse executeHandler(SPIntentRequest intentRequest) {
    log.info("Execute COache me intent handler");
    User user = intentRequest.getUser();
    
    switch (intentRequest.getAlexaIntentType()) {
    case AskName:
    case CoachMe:
      
      /* this is a fresh request, check whether it is partial or full */
      if (!isFullOrPartialEvent(user, intentRequest.getIntentRequest(),
          intentRequest.getAlexaIntentType(), intentRequest.getSession(), userFactory)) {
        return getAskCollegueName(intentRequest.getSession(), intentRequest.getUser());
      }
      
    default:
      StepContext stepContext = new StepContext();
      stepContext.setSeedObject(intentRequest);
      stepManagerFactory.executeStep(stepContext);
      return (SpeechletResponse) stepContext.getResponse();
      
    }
  }
}