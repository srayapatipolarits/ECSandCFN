package com.sp.web.controller.goal;

import com.sp.web.Constants;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.goal.ActionPlanSummaryDTO;
import com.sp.web.dto.goal.UserActionPlanDTO;
import com.sp.web.exception.DashboardRedirectException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.Operation;
import com.sp.web.form.goal.MeetingInviteForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserMessage;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pradeep The helper class for goals controller.
 */
@Component
public class ActionPlanControllerHelper {
  
  private static final Logger log = Logger.getLogger(ActionPlanControllerHelper.class);
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Autowired
  CommunicationGateway gateway;
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  private BadgeFactory badgeFactory;
  
  /**
   * Helper method to get the user's organization goals.
   * 
   * @param user
   *          logged in user
   * @return - the response to the get request
   */
  public SPResponse getUserOrgGoals(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userEmail = (String) params[0];
    
    User userForOrgGoals = null;
    
    if (StringUtils.isEmpty(userEmail)) {
      userForOrgGoals = user;
    } else {
      userForOrgGoals = userFactory.getUserForGroupLead(userEmail, user);
    }
    
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(userForOrgGoals);
    
    String actionPlanId = (String) params[1];
    if (StringUtils.isBlank(actionPlanId)) {
      actionPlanId = userActionPlan.getSelectedActionPlan();
    }
    
    if (actionPlanId != null) {
      try {
        resp.add(Constants.PARAM_ACTION_PLAN, new UserActionPlanDTO(getActionPlan(actionPlanId),
            userActionPlan));
        
        // adding flag for more than one organization plan
        resp.add(Constants.PARAM_ACTION_PLAN_HAMBURGER, userActionPlan.getActionPlanProgressMap()
            .size() > 1);
      } catch (DashboardRedirectException e) {
        log.warn("Selected action plan not found.", e);
        removeActionPlan(userForOrgGoals, actionPlanId);
      }
    }
    
//    final List<UserMessage> userMessages = userForOrgGoals.getMessages();
//    if (!CollectionUtils.isEmpty(userMessages)) {
//      // adding any user messages if present for organization plan
//      resp.add(
//          Constants.PARAM_MESSAGE,
//          userMessages.stream().filter(m -> m.getFeature() == SPFeature.OrganizationPlan)
//              .collect(Collectors.toList()));
//    }
//    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get the applicable plans for the user.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get applicable plans request
   */
  public SPResponse getUserAcitonPlans(User user) {
    final SPResponse resp = new SPResponse();
    
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    Set<String> userActionPlans = userActionPlan.getActionPlanProgressMap().keySet();
    
    ArrayList<ActionPlanSummaryDTO> userActionPlanSummary = new ArrayList<ActionPlanSummaryDTO>();
    for (String actionPlanId : userActionPlans) {
      ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlanId);
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
      if (actionPlan != null) {
        userActionPlanSummary.add(new ActionPlanSummaryDTO(actionPlan, actionPlanProgress
            .getCompletedCount()));
      }
    }
    // add to the response
    return resp.add(Constants.PARAM_ACTION_PLAN_LIST, userActionPlanSummary);
  }
  
  /**
   * Helper method to select a particular action plan.
   * 
   * @param user
   *          - user
   * @param params
   *          - action plan id
   * @return the response to the select request
   */
  public SPResponse selectPlan(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String actionPlanId = (String) params[0];
    
    Assert.hasText(actionPlanId, "Organization Plan id is required.");
    
    // validate the action plan
    try {
      getActionPlan(actionPlanId);
    } catch (DashboardRedirectException e) {
      log.warn("Action plan not found.", e);
      removeActionPlan(user, actionPlanId);
      throw e;
    }
    
    // get the user action plan selection and set the selected action plan id
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlanId);
    // validate if the action plan is applicable for the user
    Assert.notNull(actionPlanProgress, MessagesHelper.getMessage("service.growl.message2"));
    userActionPlan.setSelectedActionPlan(actionPlanId);
    actionPlanFactory.updateUserActionPlan(userActionPlan);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to mark the action as completed.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for the action
   * @return the response to the complete action request
   */
  public SPResponse completeAction(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String actionPlanId = (String) params[0];
    final String stepId = (String) params[1];
    final String uid = (String) params[2];
    final boolean completed = (boolean) params[3];
    
    Assert.hasText(actionPlanId, "Organization Plan id is required.");
    Assert.hasText(uid, "UID uinique id is required.");
    
    // get the users action plan
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    
    // Removing this check as with action plans they can now complet any action plan
    // validate if the action plan is what the user is working on
    // final String selectedActionPlan = userActionPlan.getSelectedActionPlan();
    // if (!StringUtils.equals(selectedActionPlan, actionPlanId)) {
    // throw new DashboardRedirectException("Organization Plan not currently selected by user.");
    // }
    
    // validate the action plan
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // validate if the uid is still applicable
    if (completed) {
      if (!actionPlan.validateUID(stepId, uid)) {
        throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
      }
    }
    
    // Marking the action complete or removing it from the completion list
    ActionPlanProgress actionPlanProgress = userActionPlan.addCompletion(actionPlanId, stepId, uid,
        completed);
    
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
    if (actionPlan.getStepType() == StepType.TimeBasedWithCompletion) {
      final Set<String> completedActions = actionPlanProgress.getCompletedActions(stepId);
      boolean noActionLeft = checkIfAllStepActionsCompleted(stepId, completedActions);
      TodoRequest todoRequest = userTodoRequests.findRequest(stepId, actionPlanId);
      if (noActionLeft || todoRequest == null) {
        int sizeBefore = actionPlanProgress.getCompletedActionsForStep().size();
        actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlan,
            Operation.UPDATE);
        int sizeAfter = actionPlanProgress.getCompletedActionsForStep().size();
        if (sizeAfter != sizeBefore) {
          resp.add(Constants.PARAM_ACTION_PLAN_ENABLE_NEXT_STEP, true);
        }
        userTodoRequests = todoFactory.getUserTodoRequests(user);
      }
    } else {
      if (actionPlanProgress.getCompletedCount() == actionPlan.getActionCount()) {
        todoFactory.removeAllTasksWithParentId(user, actionPlanId, userTodoRequests);
      } else {
        ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests
            .getParentTodoTaskRequest(actionPlanId);
        if (parentTodoTaskRequest == null) {
          actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlan,
              Operation.UPDATE);
          userTodoRequests = todoFactory.getUserTodoRequests(user);
        }
      }
    }
    
    // update the changed data to the repository
    actionPlanFactory.updateUserActionPlan(userActionPlan);
    
    // update the tasks
    ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests
        .getParentTodoTaskRequest(actionPlanId);
    if (parentTodoTaskRequest != null) {
      todoFactory.updateTask(user, actionPlanId);
    }
    
    // update the badge
    badgeFactory.updateBadgeProgress(user, actionPlanId, BadgeType.OrgPlan, actionPlan);
    return resp.isSuccess();
  }
  
  /**
   * Check if all the actions for the given step are completed.
   * 
   * @param stepId
   *          - step id
   * @param completedActions
   *          - completed actions
   * @return true if all completed else false
   */
  private boolean checkIfAllStepActionsCompleted(String stepId, Set<String> completedActions) {
    SPGoal goal = goalFactory.getGoal(stepId);
    if (goal != null && completedActions != null) {
      return goal.getActionCount() == completedActions.size();
    }
    return false;
  }
  
  /**
   * Helper method to create the calendar invite.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - invite request params
   * @return the response to the invite request
   */
  public SPResponse sendCalInvite(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    MeetingInviteForm inviteForm = (MeetingInviteForm) params[1];
    
    // String actionPlanId = (String) params[0];
    // Assert.hasText(actionPlanId, "Organization Plan id is required.");
    // ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    // String stepId = (String) params[2];
    // String uid = (String) params[3];
    // if (StringUtils.isNotBlank(uid)) {
    // if (!actionPlan.validateUID(uid)) {
    // throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    // }
    // }
    
    // validate the form
    inviteForm.validate();
    
    // add the current user to the to list
    inviteForm.getTo().add(user.getEmail());
    
    // get the parameters from the form and send the invite
    EmailParams emailParams = inviteForm.getEmailParam();
    emailParams.addParam(Constants.PARAM_FIRSTNAME, user.getFirstName());
    emailParams.addParam(Constants.PARAM_LASTNAME, user.getLastName());
    
    gateway.sendMessage(emailParams);
    
    return resp.isSuccess();
  }
  
  /**
   * Get the action plan for the given action plan id.
   * 
   * @param actionPlanId
   *          - action plan id
   * @return the action plan
   * @throws InvalidRequestException
   *           - if action plan not found
   */
  private ActionPlanDao getActionPlan(String actionPlanId) {
    return Optional.ofNullable(actionPlanFactory.getActionPlan(actionPlanId)).orElseThrow(
        () -> new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message2")));
  }
  
  /**
   * Removing the given action plan from the user.
   * 
   * @param user
   *          - user
   * @param actionPlanId
   *          - action plan id
   */
  private void removeActionPlan(User user, String actionPlanId) {
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    userActionPlan.removeActionPlan(user, actionPlanId, true, todoFactory, publicChannelHelper);
    badgeFactory.deleteBadgeProgress(actionPlanId, user);
    actionPlanFactory.updateUserActionPlan(userActionPlan);
  }
  
}
