package com.sp.web.service.tracking;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.utils.MessagesHelper;
import com.sp.web.utils.RandomGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The prism activity tracking processor.
 */
@Component("actionPlanActivityTrackingProcessor")
public class ActionPlanActivityTrackingProcessor implements ActivityTrackingProcessor {
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Override
  public boolean updateActivityTracking(User user, LogActionType actionType, Object[] params,
      ActivityTracking activityTracking) {
    
    boolean logActivity = true;
    switch (actionType) {
    case ActionPlanCompleteAction:
      logActivity = updateActionPlanCompletion(user, params, actionType, activityTracking);
      break;
    
    default:
      logActivity = false;
      break;
    }
    return logActivity;
  }
  
  /**
   * Update the activity tracking.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @param actionType
   *          - action type
   * @param activityTracking
   *          - activity tracking to update
   */
  private boolean updateActionPlanCompletion(User user, Object[] params, LogActionType actionType,
      ActivityTracking activityTracking) {
    
    boolean logActivity = false;
    
    String actionPlanId = (String) params[0];
    String stepId = (String) params[1];
    //String uid = (String) params[2];
    
    // get the action plan
    ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
    Assert.notNull(actionPlan, "Action plan not found.");
    
    Optional<SPGoal> findFirst = actionPlan.getPracticeAreaList().stream()
        .filter(pa -> pa.getId().equals(stepId)).findFirst();
    if (findFirst.isPresent()) {
      SPGoal spGoal = findFirst.get();
      BaseUserDTO userDTO = new BaseUserDTO(user);
      int random = RandomGenerator.getRandomInteger();
      String text = random == 1 ? MessagesHelper.getMessage(actionType.getActivityKey() + random,
          userDTO.getName(), spGoal.getName(), actionPlan.getName()) : MessagesHelper.getMessage(
          actionType.getActivityKey() + random, userDTO.getName(),  actionPlan.getName());
      activityTracking.setMessage(Comment.newCommment(user, text));
      logActivity = true;
    }
    return logActivity;
  }
  
}
