package com.sp.web.dto.tutorial;

import com.sp.web.dto.goal.UserDSActionCategory;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.tutorial.TutorialActivityData;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 * 
 *         The details for the step of the tutorial along with user activity.
 */
public class StepUserActivityDTO {
  
  private String id;
  private String name;
  private int count;
  private int completedCount;
  private List<UserDSActionCategory> devStrategyActionCategoryList;
  
  /**
   * Constructor.
   * 
   * @param step
   *          - step
   * @param userActivity
   *          - user activity
   */
  public StepUserActivityDTO(SPGoal step, TutorialActivityData userActivity) {
    this.id = step.getId();
    this.name = step.getName();
    this.count = step.getActionCount();
    Set<String> stepCompletions = userActivity.getCompletionForStep(step.getId());
    this.completedCount = stepCompletions.size();
    devStrategyActionCategoryList = step.getDevStrategyActionCategoryList().stream()
        .map(dsac -> new UserDSActionCategory(dsac, stepCompletions::contains))
        .collect(Collectors.toList());
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
  
  public List<UserDSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(
      List<UserDSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getCompletedCount() {
    return completedCount;
  }

  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
  
}
