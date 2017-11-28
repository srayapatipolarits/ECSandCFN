package com.sp.web.alexa;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.sp.web.model.User;

public class SPIntentRequest {
  
  private IntentRequest intentRequest;
  
  private AlexaIntentType alexaIntentType;
  
  private Session session;
  
  private User user;
  
  private AlexaIntentType actionType;
  
  public void setAlexaIntentType(AlexaIntentType alexaIntentType) {
    this.alexaIntentType = alexaIntentType;
  }
  
  public AlexaIntentType getAlexaIntentType() {
    return alexaIntentType;
  }
  
  public IntentRequest getIntentRequest() {
    return intentRequest;
  }
  
  public void setIntentRequest(IntentRequest intentRequest) {
    this.intentRequest = intentRequest;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public void setSession(Session session) {
    this.session = session;
  }
  
  public Session getSession() {
    return session;
  }
  
  public void setActionType(AlexaIntentType actionType) {
    this.actionType = actionType;
  }
  
  public AlexaIntentType getActionType() {
    return actionType;
  }
  
  
  
}
