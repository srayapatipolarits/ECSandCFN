package com.sp.web.form.tutorial;

import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class for the tutorial step.
 */
public class TutorialStepForm {
  
  private String id;
  private String name;
  private GoalStatus status;
  private List<DSActionCategory> devStrategyActionCategoryList;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }

  /**
   * Add a new step from the data given in the form.
   * 
   * @param steps
   *          - the array to add the new step to.
   */
  public void addNew(List<SPGoal> steps) {
    validate();
    SPGoal step = new SPGoal();
    BeanUtils.copyProperties(this, step);
    step.setCategory(GoalCategory.Tutorial);
    steps.add(step);
  }

  private void validate() {
    Assert.hasText(name, "Name is required.");
    Assert.notEmpty(devStrategyActionCategoryList, "At least one category required.");
    devStrategyActionCategoryList.forEach(dsac -> validate(dsac));
  }

  private void validate(DSActionCategory dsac) {
    final List<DSAction> actionList = dsac.getActionList();
    Assert.notEmpty(actionList, "At least one action required.");
    actionList.forEach(a -> validate(a));
  }

  private void validate(DSAction action) {
    Assert.hasText(action.getTitle(), "Action title required.");
  }

  /**
   * @param stepToUpdate
   *          - update the given step.
   */
  public void update(SPGoal stepToUpdate) {
    BeanUtils.copyProperties(this, stepToUpdate);
  }
  
}
