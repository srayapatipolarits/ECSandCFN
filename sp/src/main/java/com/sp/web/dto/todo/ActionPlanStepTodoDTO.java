package com.sp.web.dto.todo;

import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.todo.TodoRequest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class to store the action plan step todo information.
 */
public class ActionPlanStepTodoDTO implements Serializable {
  
  private static final long serialVersionUID = -6308848656300151789L;
  private String refId;
  private String name;
  private LocalDateTime dueBy;
  private List<ActionPlanStepActionTodoDTO> actions;
  
  /**
   * Constructor.
   * 
   * @param pa
   *          - learning program step
   * @param todoRequest
   *          - todo request
   * @param remainingActions
   *          - remaining actions
   */
  public ActionPlanStepTodoDTO(SPGoal pa, TodoRequest todoRequest, Set<String> remainingActions) {
    this.refId = pa.getId();
    this.name = pa.getName();
    this.dueBy = todoRequest.getDueBy();
    actions = new ArrayList<ActionPlanStepActionTodoDTO>();
    pa.addTodo(remainingActions, actions);
  }

  public String getRefId() {
    return refId;
  }
  
  public void setRefId(String refId) {
    this.refId = refId;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public LocalDateTime getDueBy() {
    return dueBy;
  }
  
  public void setDueBy(LocalDateTime dueBy) {
    this.dueBy = dueBy;
  }
  
  public List<ActionPlanStepActionTodoDTO> getActions() {
    return actions;
  }
  
  public void setActions(List<ActionPlanStepActionTodoDTO> actions) {
    this.actions = actions;
  }
  
}
