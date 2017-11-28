package com.sp.web.alexa.flow;

import com.sp.web.exception.SPException;

public interface StepManagerFactory {
  
  public void executeStep(StepContext stepContext) throws SPException;
  
  public void updateNextStep(StepContext stepContext);
}
