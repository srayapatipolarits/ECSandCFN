package com.sp.web.dto.goal;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.UserPracticeAreaActionDetailsDTO;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;

import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The DTO class for Action Plan.
 */
public class UserActionPlanDTO extends BaseActionPlanDTO {
  
  private int actionCount;
  private int completedCount;
  private String imageUrl;
  private StepType stepType;
  private List<UserPracticeAreaActionDetailsDTO> practiceAreaList;
  

  /**
   * Constructor.
   * 
   * @param actionPlan
   *            - action plan
   * @param userActionPlan
   *            - user action plan
   */
  public UserActionPlanDTO(ActionPlanDao actionPlan, UserActionPlan userActionPlan) {
    super(actionPlan);
    final ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlan
        .getId());
    Assert.notNull(actionPlanProgress, "Action plan not assigned to user.");
    this.completedCount = actionPlanProgress.getCompletedCount();
    LocalDate taskDueDate = actionPlanProgress.getStartedOn();
    LocalDate now = LocalDate.now();
    practiceAreaList = new ArrayList<UserPracticeAreaActionDetailsDTO>();
    for (SPGoal step : actionPlan.getPracticeAreaList()) {
      if (step.getStatus() == GoalStatus.HIDDEN) {
        continue;
      }
      final UserPracticeAreaActionDetailsDTO stepDto = 
              new UserPracticeAreaActionDetailsDTO(step,
                      actionPlanProgress, actionPlan.getId());
      if (stepType != StepType.All) {
        taskDueDate = taskDueDate.plusDays(step.getDurationDays());
        stepDto.setDaysLeft(Period.between(now, taskDueDate).getDays());
        stepDto.setTaskDueDate(taskDueDate);
      }
      practiceAreaList.add(stepDto);
    }
  }

  public List<UserPracticeAreaActionDetailsDTO> getPracticeAreaList() {
    return practiceAreaList;
  }

  public void setPracticeAreaList(List<UserPracticeAreaActionDetailsDTO> practiceAreaList) {
    this.practiceAreaList = practiceAreaList;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public StepType getStepType() {
    return stepType;
  }

  public void setStepType(StepType stepType) {
    this.stepType = stepType;
  }

  public int getActionCount() {
    return actionCount;
  }

  public void setActionCount(int actionCount) {
    this.actionCount = actionCount;
  }

  public int getCompletedCount() {
    return completedCount;
  }

  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
}
