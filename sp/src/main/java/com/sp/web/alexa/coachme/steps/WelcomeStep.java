package com.sp.web.alexa.coachme.steps;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.coachme.CoacheMeSteps;
import com.sp.web.alexa.flow.AbstractAlexaStep;
import com.sp.web.alexa.flow.Step;
import com.sp.web.alexa.flow.StepContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("welcomeStep")
public class WelcomeStep extends AbstractAlexaStep {
  
  /** Initalizing the logger. */
  private static final Logger log = Logger.getLogger(WelcomeStep.class);
  
  /**
   * execute the step.
   * 
   * @see Step#execute(StepContext)
   */
  @Override
  public void execute(StepContext context) throws Exception {
    SPIntentRequest intentRequest = (SPIntentRequest) context.getSeedObject();
    log.info("Enter execute Coache me welcome step ");
    if (intentRequest.getIntentRequest().getIntent() != null) {
      if (intentRequest.getIntentRequest().getIntent().getSlot("Name") != null) {
        String userName = intentRequest.getIntentRequest().getIntent().getSlot("Name").getValue();
        SpeechletResponse createSpeechletAskResponse = createSpeechletAskResponse(
            "Welcome Coache Me", "alexa.relationship.welcome", intentRequest.getUser().getLocale(),
            intentRequest.getUser().getFirstName(), userName);
        context.setResponse(createSpeechletAskResponse);
      }
    }
  }
  
  @Override
  public String getStepName() {
    return CoacheMeSteps.WelcomeStep.getStepName();
  }
  
}
