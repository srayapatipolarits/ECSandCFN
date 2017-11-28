package com.sp.web.alexa;

/**
 * Specifies wheather the alexa intenty type is partial or intent.
 * 
 * @author pradeepruhil
 *
 */
public enum AlexaIntentType {
  
  CoachMe(true, "coacheMeIntent"),
  
  Insights(true, "insightsIntent"),
  
  Yes(false, ""),
  
  No(false, ""),
  
  AskName(false,""),
  
  AmazonHelp(true, "amazonHelpIntent"),
  
  SPWelcomeIntent(true, "welcomeIntent");
  
  private AlexaIntentType(boolean headIntent, String intentHandler) {
    this.headIntent = headIntent;
    this.intentHandler = intentHandler;
  }
  
  private String intentHandler;
  
  public boolean headIntent;
  
  public boolean isHeadIntent() {
    return headIntent;
  }
  
  public String getIntentHandler() {
    return intentHandler;
  }
  
}
