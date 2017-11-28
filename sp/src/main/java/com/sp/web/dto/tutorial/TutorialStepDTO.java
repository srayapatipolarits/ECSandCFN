package com.sp.web.dto.tutorial;

import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the tutorial step.
 */
public class TutorialStepDTO {
  
  private String id;
  private String name;
  private GoalStatus status;
  private List<DSActionCategory> devStrategyActionCategoryList;
  
  /**
   * Constructor.
   * 
   * @param step
   *          - tutorial step
   */
  public TutorialStepDTO(SPGoal step) {
    BeanUtils.copyProperties(step, this);
  }
  
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
}
