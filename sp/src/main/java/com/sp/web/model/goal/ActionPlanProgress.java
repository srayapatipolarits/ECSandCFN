package com.sp.web.model.goal;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the progress for a given action plan.
 */
public class ActionPlanProgress {
  
  private LocalDate createdOn;
  private LocalDate startedOn;
  private int daysOffset;
  private LocalDate removedOn;
  private int completedCount;
  private Map<String, Set<String>> completedActionsForStep;
  private Map<String, Set<String>> savedCompletedActionsForStep;
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDate getStartedOn() {
    return startedOn;
  }
  
  public void setStartedOn(LocalDate startedOn) {
    this.startedOn = startedOn;
  }
  
  public LocalDate getRemovedOn() {
    return removedOn;
  }
  
  public void setRemovedOn(LocalDate removedOn) {
    this.removedOn = removedOn;
  }
  
  public int getCompletedCount() {
    return completedCount;
  }
  
  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
  
  public Map<String, Set<String>> getCompletedActionsForStep() {
    return completedActionsForStep;
  }
  
  public void setCompletedActionsForStep(Map<String, Set<String>> stepActionCompleted) {
    this.completedActionsForStep = stepActionCompleted;
  }
  
  public Map<String, Set<String>> getSavedCompletedActionsForStep() {
    return savedCompletedActionsForStep;
  }
  
  public void setSavedCompletedActionsForStep(Map<String, Set<String>> stepActionRemoved) {
    this.savedCompletedActionsForStep = stepActionRemoved;
  }
  
  /**
   * Create a new action plan progress instance.
   * 
   * @return new action plan progress
   */
  public static ActionPlanProgress newInstance() {
    ActionPlanProgress actionPlanProgress = new ActionPlanProgress();
    actionPlanProgress.setCreatedOn(LocalDate.now());
    actionPlanProgress.setStartedOn(LocalDate.now());
    actionPlanProgress.setCompletedActionsForStep(new HashMap<String, Set<String>>());
    return actionPlanProgress;
  }
  
  /**
   * Update the practice area.
   * 
   * @param practiceArea
   *          - practice area
   * @return completed actions
   */
  public synchronized Set<String> addPracticeArea(SPGoal practiceArea) {
    final String practiceAreaId = practiceArea.getId();
    Set<String> completedActions = completedActionsForStep.get(practiceAreaId);
    
    boolean updateActionIds = false;
    if (completedActions == null) {
      // check if removed contains the practice area
      if (savedCompletedActionsForStep != null) {
        completedActions = savedCompletedActionsForStep.remove(practiceAreaId);
        updateActionIds = completedActions != null;
      }
      // adding a new one
      if (completedActions == null) {
        completedActions = new HashSet<String>();
      }
      completedActionsForStep.put(practiceAreaId, completedActions);
    }
    
    if (!completedActions.isEmpty() && updateActionIds) {
      // removing any actions that are not present any longer
      final Set<String> actionIds = practiceArea.getActionIds();
      List<String> actionsToRemove = null;
      for (String actionId : completedActions) {
        if (!actionIds.contains(actionId)) {
          if (actionsToRemove == null) {
            actionsToRemove = new ArrayList<String>();
          }
          actionsToRemove.add(actionId);
        }
      }
      
      // removing any actions that were updated
      if (!CollectionUtils.isEmpty(actionsToRemove)) {
        completedActions.removeAll(actionsToRemove);
      }
      
      completedCount += completedActions.size();
    }
    
    return completedActions;
  }
  
  /**
   * Remove all the completed actions.
   */
  public void reset() {
    if (savedCompletedActionsForStep == null) {
      savedCompletedActionsForStep = new HashMap<String, Set<String>>();
    }
    
    savedCompletedActionsForStep.putAll(completedActionsForStep);
    completedActionsForStep.clear();
    completedCount = 0;
  }
  
  public int getDaysOffset() {
    return daysOffset;
  }
  
  public void setDaysOffset(int daysOffset) {
    this.daysOffset = daysOffset;
  }
  
  /**
   * Get the date to start calculating the due dates from.
   * 
   * @return the task start date
   */
  public LocalDate taskStartDate() {
    return startedOn.minusDays(daysOffset);
  }
  
  /**
   * Add the given action id to completed actions.
   * 
   * @param stepId
   *          - step id
   * @param actionId
   *          - action id
   * @param completed
   *          - flag to indicate completed or removal
   */
  public synchronized void completeAction(String stepId, String actionId, boolean completed) {
    Set<String> completeActions = completedActionsForStep.get(stepId);
    Assert.notNull(completeActions, "Unauthorised request.");
    if (completed) {
      if (completeActions.add(actionId)) {
        completedCount++;
      }
    } else {
      if (completeActions.remove(actionId)) {
        completedCount--;
      }
    }
    
    
  }
  
  public Set<String> getCompletedActions(String practiceAreaId) {
    return completedActionsForStep.get(practiceAreaId);
  }
  
  /**
   * Method restarts the plan for the user.
   */
  public void restartPlan() {
    startedOn = LocalDate.now();
    if (removedOn != null) {
      daysOffset += Period.between(removedOn, startedOn).getDays();
    }
  }

  public Set<String> getActiveSteps() {
    return completedActionsForStep.keySet();
  }
}
