package com.sp.web.dto;

import com.sp.web.dto.goal.UserDSActionCategory;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.KeyOutcomes;
import com.sp.web.model.goal.SPGoal;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The DTO class to store the practice area details.
 */
public class UserPracticeAreaActionDetailsDTO extends PracticeAreaDTO {
  
  private static final long serialVersionUID = 3795936740042247881L;

  private GoalStatus status;
  private Map<String, String> introVideo;
  private KeyOutcomes keyOutcomes;
  private List<UserDSActionCategory> devStrategyActionCategoryList;
  private boolean enabled;
  private int daysLeft;
  private LocalDate taskDueDate;
  
  /**
   * Constructor.
   * 
   * @param goal
   *          - goal
   * @param actionPlanProgress
   *          - action plan progress
   * @param actionPlanId
   *          - action plan id
   */
  public UserPracticeAreaActionDetailsDTO(SPGoal goal, ActionPlanProgress actionPlanProgress,
      String actionPlanId) {
    super(goal);
    if (goal.getStatus() == GoalStatus.ACTIVE) {
      final Set<String> completedActions = actionPlanProgress.getCompletedActions(goal.getId());
      if (completedActions != null) {
        setEnabled(true);
        devStrategyActionCategoryList = goal
            .getDevStrategyActionCategoryList()
            .stream()
            .filter(dsa -> dsa.getStatus() == GoalStatus.ACTIVE)
            .collect(
                Collectors.mapping(dsa -> new UserDSActionCategory(dsa, completedActions::contains),
                    Collectors.toList()));
      }
    }
  }

  public GoalStatus getStatus() {
    return status;
  }

  public void setStatus(GoalStatus status) {
    this.status = status;
  }

  public Map<String, String> getIntroVideo() {
    return introVideo;
  }

  public void setIntroVideo(Map<String, String> introVideo) {
    this.introVideo = introVideo;
  }

  public KeyOutcomes getKeyOutcomes() {
    return keyOutcomes;
  }

  public void setKeyOutcomes(KeyOutcomes keyOutcomes) {
    this.keyOutcomes = keyOutcomes;
  }

  public List<UserDSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }

  public void setDevStrategyActionCategoryList(
      List<UserDSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getDaysLeft() {
    return daysLeft;
  }

  public void setDaysLeft(int daysLeft) {
    this.daysLeft = daysLeft;
  }
  
  public void setTaskDueDate(LocalDate taskDueDate) {
    this.taskDueDate = taskDueDate;
  }
  
  public LocalDate getTaskDueDate() {
    return taskDueDate;
  }
  
}
