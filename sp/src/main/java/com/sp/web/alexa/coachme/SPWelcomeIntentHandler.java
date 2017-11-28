package com.sp.web.alexa.coachme;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.sp.web.alexa.AlexaIntentHandler;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.flow.AbstractAlexaStep;
import com.sp.web.model.User;

import org.springframework.stereotype.Component;

@Component("welcomeIntent")
public class SPWelcomeIntentHandler implements AlexaIntentHandler {
  
  @Override
  public SpeechletResponse execute(SPIntentRequest intentRequest) {
    User user = intentRequest.getUser();
    
    return AbstractAlexaStep.createSpeechletAskResponse("Welcome To SurePeople",
        "alexa.sp.welcome", user.getLocale(), user.getFirstName());
    
  }
  
}
