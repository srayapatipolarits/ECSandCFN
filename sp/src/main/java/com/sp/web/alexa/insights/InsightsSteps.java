package com.sp.web.alexa.insights;

import com.sp.web.alexa.AlexaIntentType;

public enum InsightsSteps {
  WelcomeStep("welcomeInsightsStep", AlexaIntentType.AskName), GetInsights("getInsights", AlexaIntentType.Yes,
      AlexaIntentType.No);
  
  private InsightsSteps(String stepName, AlexaIntentType... alexaIntentTypes) {
    this.stepName = stepName;
    this.alexaIntentTypes = alexaIntentTypes;
  }
  
  private AlexaIntentType[] alexaIntentTypes;
  private String stepName;
  
  public String getStepName() {
    return stepName;
  }
  
  public AlexaIntentType[] getAlexaIntentTypes() {
    return alexaIntentTypes;
  }
}
