package com.sp.web.model.goal;

import com.sp.web.model.User;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The entity class to store the user actions.
 */
public class UserActionPlan {
  
  private String id;
  private String selectedActionPlan;
  @Deprecated
  private HashSet<String> completedActions;
  @Deprecated
  private Map<String, Set<String>> completeActionsMap;
  @Deprecated
  private Map<String, Set<String>> removedActionPlanMap;
  
  private Map<String, ActionPlanProgress> actionPlanProgressMap;
  private Map<String, ActionPlanProgress> removedActionPlanProgressMap;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getSelectedActionPlan() {
    return selectedActionPlan;
  }
  
  public void setSelectedActionPlan(String selectedActionPlan) {
    this.selectedActionPlan = selectedActionPlan;
  }
  
  @Deprecated
  public HashSet<String> getCompletedActions() {
    return completedActions;
  }
  
  @Deprecated
  public void setCompletedActions(HashSet<String> completedActions) {
    this.completedActions = completedActions;
  }
  
  /**
   * Validate if the given uid is in the completed actions list.
   * 
   * @param uid
   *          - unique id
   * @return true if completed else false
   * @Deprecated public boolean hasCompleted(String uid) { return (completedActions != null) ?
   *             completedActions.contains(uid) : false; }
   */
  
  /**
   * Return the completd actoin map against the action plan id.
   * 
   * @return the map.
   */
  @Deprecated
  public Map<String, Set<String>> getCompleteActionsMap() {
    if (completeActionsMap == null) {
      completeActionsMap = new HashMap<String, Set<String>>();
    }
    return completeActionsMap;
  }
  
  @Deprecated
  public void setCompleteActionsMap(Map<String, Set<String>> completeActionsMap) {
    this.completeActionsMap = completeActionsMap;
  }
  
  /**
   * @return - get the removed actions map.
   */
  @Deprecated
  public Map<String, Set<String>> getRemovedActionPlanMap() {
    if (removedActionPlanMap == null) {
      removedActionPlanMap = new HashMap<String, Set<String>>();
    }
    return removedActionPlanMap;
  }
  
  @Deprecated
  public void setRemovedActionPlanMap(Map<String, Set<String>> removedActionPlanMap) {
    this.removedActionPlanMap = removedActionPlanMap;
  }
  
  // /**
  // * Gets the list of completed actions for the given action plan id.
  // *
  // * @param apId
  // * - action plan id
  // * @return the set of completed actions.
  // */
  // @Deprecated
  // public Set<String> getCompletedActionsForActionPlan(String apId) {
  // final Map<String, Set<String>> completeActionsMap2 = getCompleteActionsMap();
  // return Optional.ofNullable(completeActionsMap2.get(apId)).orElseGet(() -> {
  // HashSet<String> completedActions = new HashSet<String>();
  // completeActionsMap2.put(apId, completedActions);
  // return completedActions;
  // });
  // }
  
  /**
   * Method to remove the completed actions from the completed actions map and add the same to the
   * removed action plans map.
   * 
   * @param actionPlanId
   *          - action plan id to remove
   * @param publicChannelHelper
   *          - public channel helper
   */
  public void removeActionPlan(User user, String actionPlanId, boolean isActionPlanDelete,
      TodoFactory todoFactory, PublicChannelHelper publicChannelHelper) {
    // check if action plan id is not null
    Assert.hasText(actionPlanId, "Organisation plan not found.");
    
    ActionPlanProgress actionPlanProgress = null;
    if (actionPlanProgressMap == null) {
      actionPlanProgressMap = new HashMap<String, ActionPlanProgress>();
    } else {
      actionPlanProgress = actionPlanProgressMap.remove(actionPlanId);
    }
    
    if (actionPlanProgress != null) {
      if (!isActionPlanDelete) {
        getOrCreateRemovedActionPlanProgressMap().put(actionPlanId, actionPlanProgress);
        actionPlanProgress.setRemovedOn(LocalDate.now());
        Map<String, Set<String>> stepActionCompleted = actionPlanProgress
            .getCompletedActionsForStep();
        if (!CollectionUtils.isEmpty(stepActionCompleted)) {
          stepActionCompleted.keySet().forEach(
              paId -> publicChannelHelper.removeUser(user, paId));
        }
        actionPlanProgress.reset();
      }
    }
    
    // changing the selected action plan id
    if (StringUtils.equals(selectedActionPlan, actionPlanId)) {
      if (!actionPlanProgressMap.isEmpty()) {
        selectedActionPlan = actionPlanProgressMap.keySet().iterator().next();
      } else {
        selectedActionPlan = null;
      }
    }
    
    // removing the todo list
    todoFactory.removeAllTasksWithParentId(user, actionPlanId);
  }
  
  /**
   * @return - gets the removed action plan progress map creates a new one if not present.
   */
  private synchronized Map<String, ActionPlanProgress> getOrCreateRemovedActionPlanProgressMap() {
    if (removedActionPlanProgressMap == null) {
      removedActionPlanProgressMap = new HashMap<String, ActionPlanProgress>();
    }
    return removedActionPlanProgressMap;
  }
  
  // /**
  // * Method to remove the action plan id's from the completed actions list. removes from both
  // * completed and removed action plans.
  // *
  // * @param actionPlanId
  // * - action plan id
  // * @param actionIdList
  // * - action id list
  // * @return flag to indicate if any was removed
  // */
  // @Deprecated
  // public boolean removeActionIdList(String actionPlanId, List<String> actionIdList) {
  // boolean isUpdated = false;
  //
  // // removing from the completed actions
  // Set<String> completedActions = getCompletedActionsForActionPlan(actionPlanId);
  // if (!CollectionUtils.isEmpty(completedActions)) {
  // isUpdated = completedActions.removeAll(actionIdList);
  // }
  //
  // // removing from removed actions
  // completedActions = getRemovedActionPlanMap().get(actionPlanId);
  // if (!CollectionUtils.isEmpty(completedActions)) {
  // isUpdated = completedActions.removeAll(actionIdList);
  // }
  //
  // return isUpdated;
  // }
  
  /**
   * Get the action plan progress map.
   * 
   * @return
   *    action plan progress map
   */
  public Map<String, ActionPlanProgress> getActionPlanProgressMap() {
    if (actionPlanProgressMap == null) {
      actionPlanProgressMap = new HashMap<String, ActionPlanProgress>();
    }
    return actionPlanProgressMap;
  }
  
  public void setActionPlanProgressMap(Map<String, ActionPlanProgress> actionPlanProgressMap) {
    this.actionPlanProgressMap = actionPlanProgressMap;
  }
  
  public Map<String, ActionPlanProgress> getRemovedActionPlanProgressMap() {
    return removedActionPlanProgressMap;
  }
  
  public void setRemovedActionPlanProgressMap(
      Map<String, ActionPlanProgress> removedActionPlanProgressMap) {
    this.removedActionPlanProgressMap = removedActionPlanProgressMap;
  }
  
  /**
   * Gets the action plan progress for the given action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @return the user action plan progress
   */
  public ActionPlanProgress getActionPlanProgress(String actionPlanId) {
    if (!CollectionUtils.isEmpty(actionPlanProgressMap)) {
      return actionPlanProgressMap.get(actionPlanId);
    }
    return null;
  }
  
  /**
   * Gets the action plan progress for the given action plan if not found creates a new one.
   * 
   * @param actionPlanId
   *          - action plan id
   * @return the action plan progress
   */
  public ActionPlanProgress getOrCreateActionPlanProgress(String actionPlanId) {
    // check if action plan id is not null
    Assert.hasText(actionPlanId, "Organisation plan not found.");
    
    // getting the action plan progress
    ActionPlanProgress actionPlanProgress = null;
    
    if (actionPlanProgressMap != null) {
      actionPlanProgress = actionPlanProgressMap.get(actionPlanId);
    } else {
      actionPlanProgressMap = new HashMap<String, ActionPlanProgress>();
    }
    
    if (actionPlanProgress == null) {
      // trying to get from previously removed action plan progress
      if (removedActionPlanProgressMap != null) {
        actionPlanProgress = removedActionPlanProgressMap.remove(actionPlanId);
        if (actionPlanProgress != null) {
          // adding it back to the action plan progress map
          actionPlanProgress.restartPlan();
        }
      }
      
      // if not found creating a new one
      if (actionPlanProgress == null) {
        actionPlanProgress = ActionPlanProgress.newInstance();
      }
      
      // adding the action plan progress to the action plan progress map
      actionPlanProgressMap.put(actionPlanId, actionPlanProgress);
    }
    
    // setting the selected action plan
    if (selectedActionPlan == null) {
      selectedActionPlan = actionPlanId;
    }
    return actionPlanProgress;
  }
  
  /**
   * Method to mark action completion for the given action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param practiceAreaId
   *          - practice area id
   * @param actionId
   *          - action id
   * @param completed
   *          - flag to indicate completion or removal
   * @return the updated action plan progress
   */
  public ActionPlanProgress addCompletion(String actionPlanId, String practiceAreaId,
      String actionId, boolean completed) {
    final ActionPlanProgress actionPlanProgress = getActionPlanProgress(actionPlanId);
    Assert.notNull(actionPlanProgress, MessagesHelper.getMessage("orgplan.error.nolonger"));
    actionPlanProgress.completeAction(practiceAreaId, actionId, completed);
    return actionPlanProgress;
  }

  /**
   * Get the completed actions for the given action plan id and step.
   * 
   * @param actionPlanId
   *            - action plan id
   * @param practiceAreaId
   *            - practice area
   * @return
   *    the set of completed actions else null
   */
  public Set<String> getCompletedActionsForActionPlan(String actionPlanId, String practiceAreaId) {
    final ActionPlanProgress actionPlanProgress = getActionPlanProgress(actionPlanId);
    return (actionPlanProgress != null) ? actionPlanProgress.getCompletedActions(practiceAreaId)
        : null;
  }
}
