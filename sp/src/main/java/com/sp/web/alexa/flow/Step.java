package com.sp.web.alexa.flow;

public interface Step {
  
  public String getStepName();
  
  public void execute(StepContext context) throws Exception;
  
}
