package com.sp.web.alexa.insights.steps;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.flow.AbstractAlexaStep;
import com.sp.web.alexa.flow.Step;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.alexa.insights.InsightsSteps;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("welcomeInsightsStep")
public class WelcomeInsightsStep extends AbstractAlexaStep {
  
  /** Initalizing the logger. */
  private static final Logger log = Logger.getLogger(WelcomeInsightsStep.class);
  
  /**
   * execute the step.
   * 
   * @see Step#execute(StepContext)
   */
  @Override
  public void execute(StepContext context) throws Exception {
    SPIntentRequest intentRequest = (SPIntentRequest) context.getSeedObject();
    log.info("Enter execute Coache me welcome step ");
    
    String userName = intentRequest.getIntentRequest().getIntent().getSlot("Name").getValue();
    SpeechletResponse createSpeechletAskResponse = createSpeechletAskResponse("Welcome Coache Me",
        "alexa.relationship.welcome.insights", intentRequest.getUser().getLocale(), intentRequest.getUser()
            .getFirstName(), userName);
    context.setResponse(createSpeechletAskResponse);
  }
  
  @Override
  public String getStepName() {
    return InsightsSteps.WelcomeStep.getStepName();
  }
  
}
