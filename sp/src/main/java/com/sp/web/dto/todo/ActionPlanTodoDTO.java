package com.sp.web.dto.todo;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;

import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The Todo DTO for action plans.
 */
public class ActionPlanTodoDTO extends TodoDTO {
  
  private static final long serialVersionUID = 2656406818445568870L;
  private String parentRefId;
  private String name;
  private List<ActionPlanStepTodoDTO> steps;
  private int actionCount;
  private int completedCount;
  
  /**
   * Constructor.
   * 
   * @param actionPlan
   *          - action plan
   * @param actionPlanProgress
   *          - user action plan progress
   */
  public ActionPlanTodoDTO(ActionPlanDao actionPlan, ActionPlanProgress actionPlanProgress) {
    parentRefId = actionPlan.getId();
    name = actionPlan.getName();
    steps = new ArrayList<ActionPlanStepTodoDTO>();
    setType(TodoType.OrgPlan);
    this.actionCount = actionPlan.getActionCount();
    this.completedCount = actionPlanProgress.getCompletedCount();
  }
  
  public String getParentRefId() {
    return parentRefId;
  }
  
  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public List<ActionPlanStepTodoDTO> getSteps() {
    return steps;
  }
  
  public void setSteps(List<ActionPlanStepTodoDTO> steps) {
    this.steps = steps;
  }
  
  /**
   * Add the given learning program step.
   * 
   * @param pa
   *          - learning program step
   * @param actionPlanProgress
   *          - action plan progress
   * @param todoTasks
   *          - todo tasks
   * @param updateUserTodoRequests
   *          - flag to update user todo requests
   */
  public void add(SPGoal pa, ActionPlanProgress actionPlanProgress,
      Map<String, TodoRequest> todoTasks, MutableBoolean updateUserTodoRequests) {
    final String paId = pa.getId();
    Set<String> completedActions = actionPlanProgress.getCompletedActions(paId);
    if (completedActions == null) {
      todoTasks.remove(paId);
      updateUserTodoRequests.setTrue();
    } else {
      Set<String> remainingActions = pa.getRemainingActions(completedActions);
      if (remainingActions.isEmpty()) {
        todoTasks.remove(paId);
        updateUserTodoRequests.setTrue();
      } else {
        steps.add(new ActionPlanStepTodoDTO(pa, todoTasks.get(paId), remainingActions));
      }
    }
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
