package com.sp.web.alexa.insights;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.coachme.AbstractIntentHander;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.alexa.flow.StepManagerFactory;
import com.sp.web.model.User;
import com.sp.web.user.UserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("insightsIntent")
public class InsightsIntentHandler extends AbstractIntentHander {
  
  private static final Logger log = Logger.getLogger(InsightsIntentHandler.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  @Qualifier("insightsStepManagerFactory")
  private StepManagerFactory stepManagerFactory;
  
  @Override
  public SpeechletResponse executeHandler(SPIntentRequest intentRequest) {
    
    log.info("Execute insights me intent handler");
    User user = intentRequest.getUser();
    switch (intentRequest.getAlexaIntentType()) {
    case AskName:
    case Insights:
      
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
