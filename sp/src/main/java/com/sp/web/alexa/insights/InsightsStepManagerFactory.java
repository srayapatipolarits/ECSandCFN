package com.sp.web.alexa.insights;

import com.amazon.speech.speechlet.Session;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.flow.AbstractAlexaStepManagerFactory;
import com.sp.web.alexa.flow.Step;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("insightsStepManagerFactory")
public class InsightsStepManagerFactory extends AbstractAlexaStepManagerFactory {
  
  private static final Logger log = Logger.getLogger(InsightsStepManagerFactory.class);
  
  private List<InsightsSteps> insightsSteps;
  
  public InsightsStepManagerFactory() {
    insightsSteps = new ArrayList<InsightsSteps>();
    insightsSteps.add(InsightsSteps.WelcomeStep);
    insightsSteps.add(InsightsSteps.GetInsights);
    
  }
  
  @Override
  public void updateNextStep(StepContext stepContext) {
    SPIntentRequest seedObject = (SPIntentRequest) stepContext.getSeedObject();
    Session session = seedObject.getSession();
    String stepString = (String) session.getAttribute("nextStep");
    InsightsSteps step = InsightsSteps.valueOf(stepString);
    
    int indexOf = insightsSteps.indexOf(step);
    log.info("Step to be executed " + indexOf + ". insightsSteps.size() " + insightsSteps.size());
    if (indexOf != insightsSteps.size() - 1) {
      session.setAttribute("nextStep", insightsSteps.get(indexOf + 1));
    } else {
      session.removeAttribute("nextStep");
    }
    
  }
  
  protected Step getCurrentStep(StepContext stepContext) {
    /* get the next step */
    SPIntentRequest seedObject = (SPIntentRequest) stepContext.getSeedObject();
    Session session = seedObject.getSession();
    String stepString = (String) session.getAttribute("nextStep");
    InsightsSteps step;
    if (stepString == null) {
      step = InsightsSteps.WelcomeStep;
      session.setAttribute("nextStep", step.toString());
    } else {
      step = InsightsSteps.valueOf(stepString);
    }
    
    if (log.isInfoEnabled()) {
      log.info("next step to be executed is " + step);
    }
    return (Step) ApplicationContextUtils.getApplicationContext().getBean(step.getStepName());
  }
  
}
