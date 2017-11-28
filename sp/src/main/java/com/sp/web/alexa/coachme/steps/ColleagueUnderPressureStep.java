package com.sp.web.alexa.coachme.steps;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sp.web.alexa.AlexaIntentType;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.coachme.CoacheMeSteps;
import com.sp.web.alexa.flow.AbstractAlexaStep;
import com.sp.web.alexa.flow.StepContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("colleagueUnderPressure")
public class ColleagueUnderPressureStep extends AbstractAlexaStep {
  
  private static final Logger log = Logger.getLogger(ColleagueUnderPressureStep.class);
  
  @Override
  public String getStepName() {
    return CoacheMeSteps.UnderPressure.getStepName();
  }
  
  @Override
  public void execute(StepContext context) throws Exception {
    SPIntentRequest intentRequest = (SPIntentRequest) context.getSeedObject();
    log.info("Enter execute Coache me colleagueUnderPressure step ");
    Session session = intentRequest.getSession();
    AlexaIntentType alexaIntentType = intentRequest.getAlexaIntentType();
    switch (alexaIntentType) {
    case Yes:
      session.setAttribute("userUnderPressure", "true");
      break;
    case No:
      session.setAttribute("userUnderPressure", "false");
      break;
    default:
    }
    SpeechletResponse createSpeechletAskResponse = createSpeechletAskResponse("Coache Me",
        "alexa.relationship.welcome1", intentRequest.getUser().getLocale());
    context.setResponse(createSpeechletAskResponse);
  }
  
}
