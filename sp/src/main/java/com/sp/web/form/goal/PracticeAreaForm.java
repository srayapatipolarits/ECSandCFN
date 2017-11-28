package com.sp.web.form.goal;

import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @author Dax Abraham
 *
 *         The form to store the practice area details for create/update.
 */
public class PracticeAreaForm {

  @NotBlank
  private String name;
  
  @NotBlank
  private String description;
  
  @NotNull
  private GoalStatus status;
  
  private List<DevelopmentStrategy> developmentStrategyList;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public GoalStatus getStatus() {
    return status;
  }

  public void setStatus(GoalStatus status) {
    this.status = status;
  }

  public List<DevelopmentStrategy> getDevelopmentStrategyList() {
    return developmentStrategyList;
  }

  public void setDevelopmentStrategyList(List<DevelopmentStrategy> developmentStrategyList) {
    this.developmentStrategyList = developmentStrategyList;
  }

  /**
   * Create a new SP Goal object with the information given in the current form.
   * 
   * @return
   *      - the new goal object
   */
  public SPGoal createNewPracticeArea() {
    SPGoal goal = new SPGoal();
    BeanUtils.copyProperties(this, goal);
    goal.setCategory(GoalCategory.GrowthAreas);
    return goal;
  }

  /**
   * Update the goal with the information present in the form.
   * 
   * @param goal
   *          - goal to update
   */
  public void updateGoal(SPGoal goal) {
    BeanUtils.copyProperties(this, goal);
  }
}
