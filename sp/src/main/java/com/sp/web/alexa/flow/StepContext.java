package com.sp.web.alexa.flow;

public class StepContext {
  
  private Object seedObject;
  
  private Object response;
  
  public void setResponse(Object response) {
    this.response = response;
  }
  
  public Object getResponse() {
    return response;
  }
  
  /**
   * Provide seed information to this StepContext, usually provided at time of workflow start by the
   * step manager factory.
   * 
   * @param seedObject
   *          - initial seed data for the workflow
   */
  public void setSeedObject(Object seedObject) {
    this.seedObject = seedObject;
  }
  
  public Object getSeedObject() {
    return seedObject;
  }
}
