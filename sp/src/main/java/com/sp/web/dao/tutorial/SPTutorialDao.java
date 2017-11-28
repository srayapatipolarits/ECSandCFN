package com.sp.web.dao.tutorial;

import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The DAO class for the SPTutorial object.
 */
@Document(collection = "sPTutorial")
public class SPTutorialDao extends SPTutorial {
  
  private static final long serialVersionUID = 6868822632889376220L;
  @Transient
  private List<SPGoal> steps;
  
  /**
   * Default constructor.
   */
  public SPTutorialDao() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param tutorial
   *          - tutorial
   * @param locale
   *          - locale
   * @param goalsFactory
   *          - goals factory
   */
  public SPTutorialDao(SPTutorial tutorial, String locale, SPGoalFactory goalsFactory) {
    BeanUtils.copyProperties(tutorial, this);
    steps = new ArrayList<SPGoal>();
    for (String stepId : tutorial.getStepIds()) {
      SPGoal step = goalsFactory.getGoal(stepId, locale);
      Assert.notNull(step, "Step not found :" + stepId);
      steps.add(step);
    }
  }

  public List<SPGoal> getSteps() {
    return steps;
  }
  
  public void setSteps(List<SPGoal> steps) {
    this.steps = steps;
  }
  
}
