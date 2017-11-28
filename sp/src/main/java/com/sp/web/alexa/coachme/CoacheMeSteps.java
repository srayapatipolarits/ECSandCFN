package com.sp.web.alexa.coachme;

import com.sp.web.alexa.AlexaIntentType;

public enum CoacheMeSteps {
  
  WelcomeStep("welcomeStep", AlexaIntentType.AskName), UnderPressure("colleagueUnderPressure", AlexaIntentType.Yes,
      AlexaIntentType.No), GetRelationShip("getRelationShip", AlexaIntentType.Yes,
      AlexaIntentType.No), AdditionalCoaching("additionalCoaching", AlexaIntentType.Yes,
      AlexaIntentType.No), ThankYou("thankyou");
  
  private CoacheMeSteps(String stepName, AlexaIntentType... alexaIntentTypes) {
    this.stepName = stepName;
    this.intentInocation = alexaIntentTypes;
  }
  
  private String stepName;
  
  private AlexaIntentType[] intentInocation;
  
  public String getStepName() {
    return stepName;
  }
  
  public void setIntentInocation(AlexaIntentType[] intentInocation) {
    this.intentInocation = intentInocation;
  }
  
  public AlexaIntentType[] getIntentInocation() {
    return intentInocation;
  }
  
}
