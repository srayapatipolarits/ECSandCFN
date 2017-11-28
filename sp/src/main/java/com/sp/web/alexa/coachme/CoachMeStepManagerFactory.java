package com.sp.web.alexa.coachme;

import com.amazon.speech.speechlet.Session;
import com.sp.web.alexa.SPIntentRequest;
import com.sp.web.alexa.flow.AbstractAlexaStepManagerFactory;
import com.sp.web.alexa.flow.Step;
import com.sp.web.alexa.flow.StepContext;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("coachMeStepManagerFactory")
public class CoachMeStepManagerFactory extends AbstractAlexaStepManagerFactory {
  
  private static final Logger log = Logger.getLogger(CoachMeStepManagerFactory.class);
  
  private List<CoacheMeSteps> coacheMeSteps;
  
  public CoachMeStepManagerFactory() {
    coacheMeSteps = new ArrayList<CoacheMeSteps>();
    
    /* order of the steps */
    coacheMeSteps.add(CoacheMeSteps.WelcomeStep);
    coacheMeSteps.add(CoacheMeSteps.UnderPressure);
    coacheMeSteps.add(CoacheMeSteps.GetRelationShip);
    coacheMeSteps.add(CoacheMeSteps.AdditionalCoaching);
    coacheMeSteps.add(CoacheMeSteps.ThankYou);
    
  }
  
  protected Step getCurrentStep(StepContext stepContext) {
    /* get the next step */
    SPIntentRequest seedObject = (SPIntentRequest) stepContext.getSeedObject();
    Session session = seedObject.getSession();
    String stepString = (String) session.getAttribute("nextStep");
    CoacheMeSteps step;
    if (stepString == null) {
      step = CoacheMeSteps.WelcomeStep;
      session.setAttribute("nextStep", step.toString());
    } else {
      step = CoacheMeSteps.valueOf(stepString);
    }
    
    if (log.isInfoEnabled()) {
      log.info("next step to be executed is " + step);
    }
    return (Step) ApplicationContextUtils.getApplicationContext().getBean(step.getStepName());
  }
  
  @Override
  public void updateNextStep(StepContext stepContext) {
    SPIntentRequest seedObject = (SPIntentRequest) stepContext.getSeedObject();
    Session session = seedObject.getSession();
    String stepString = (String) session.getAttribute("nextStep");
    CoacheMeSteps step = CoacheMeSteps.valueOf(stepString);
    
    int indexOf = coacheMeSteps.indexOf(step);
    log.info("Step to be executed " + indexOf + ". coacheMeSteps.size() " + coacheMeSteps.size());
    if (indexOf != coacheMeSteps.size() - 1) {
      session.setAttribute("nextStep", coacheMeSteps.get(indexOf + 1));
    } else {
      session.removeAttribute("nextStep");
    }
    
  }
  
}
