package com.sp.web.dto.goal;

import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.UserActionPlan;

/**
 * @author Dax Abraham
 * 
 *         The DTO to store the user details and the count of completed actions for the user.
 */
public class UserActionPlanCompletionDTO {
  private String id;
  private int completedActionCount;
  
  /**
   * Constructor.
   * 
   * @param userActionPlan
   *          - user action plan
   * @param actionPlanId
   *          - action plan id
   */
  public UserActionPlanCompletionDTO(String userId, UserActionPlan userActionPlan,
      String actionPlanId) {
    id = userId;
    ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlanId);
    if (actionPlanProgress != null) {
      completedActionCount = actionPlanProgress.getCompletedCount();
    } else {
      completedActionCount = 0;
    }
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public int getCompletedActionCount() {
    return completedActionCount;
  }
  
  public void setCompletedActionCount(int completedActionCount) {
    this.completedActionCount = completedActionCount;
  }
}
