package com.sp.web.alexa.flow;

import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.exception.SPException;

import org.apache.log4j.Logger;

public abstract class AbstractAlexaStepManagerFactory implements StepManagerFactory {
  
  private Logger log = Logger.getLogger(AbstractAlexaStepManagerFactory.class);
  
  /**
   * error occurred while executing the step.
   * 
   * @throws SPException
   *           in case any exception occurred.
   * @see StepManagerFactory#executeStep(StepContext)
   */
  @Override
  public void executeStep(StepContext stepContext) throws SPException {
    
    SPIntentRequest intentRequest = (SPIntentRequest) stepContext.getSeedObject();
    if (intentRequest.getAlexaIntentType().isHeadIntent()) {
      intentRequest.getSession().removeAttribute("nextStep");
    }
    
    Step nextStep = getCurrentStep(stepContext);
    if (nextStep != null) {
      log.info("Executing step " + nextStep.getStepName());
      try {
        nextStep.execute(stepContext);
        updateNextStep(stepContext);
      } catch (Exception e) {
        log.error("Error occurred while executing the step " + nextStep.getStepName(), e);
        throw new SPException("Error occurred while executing the step " + nextStep.getStepName(),
            e);
      }
      ;
    }
  }
  
  protected abstract Step getCurrentStep(StepContext stepContext);
}
