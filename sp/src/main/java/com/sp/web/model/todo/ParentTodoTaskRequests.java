package com.sp.web.model.todo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 * 
 *         The parent todo task envelop to store all the task requests for the parent.
 */
public class ParentTodoTaskRequests {
  
  private String parentRefId;
  private TodoType type;
  Map<String, TodoRequest> todoTasks;
  
  /**
   * Default constructor.
   */
  public ParentTodoTaskRequests() { }
  
  /**
   * Constructor.
   * 
   * @param type
   *          - todo type
   * @param parentRefId
   *          - parent ref id
   */
  public ParentTodoTaskRequests(TodoType type, String parentRefId) {
    this.type = type;
    this.parentRefId = parentRefId;
    todoTasks = new HashMap<String, TodoRequest>();
  }

  /**
   * Constructor.
   * 
   * @param todoRequest
   *          - todo request
   */
  public ParentTodoTaskRequests(TodoRequest todoRequest) {
    this(todoRequest.getType(), todoRequest.getParentRefId());
  }

  public String getParentRefId() {
    return parentRefId;
  }
  
  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }
  
  public TodoType getType() {
    return type;
  }
  
  public void setType(TodoType type) {
    this.type = type;
  }
  
  public Map<String, TodoRequest> getTodoTasks() {
    return todoTasks;
  }
  
  public void setTodoTasks(Map<String, TodoRequest> todoTasks) {
    this.todoTasks = todoTasks;
  }
  
  /**
   * Check if the current task is already present in the parent request.
   * 
   * @param refId
   *          - reference id
   * @return
   *      true if present else false
   */
  public boolean hasRequest(String refId) {
    return todoTasks.containsKey(refId);
  }

  /**
   * Gets the request with the given reference id.
   * 
   * @param refId
   *          - reference id 
   * @return
   *    the todo request
   */
  public TodoRequest getRequest(String refId) {
    return todoTasks.get(refId);
  }

  /**
   * Add the given todo request.
   * 
   * @param todoRequest
   *          - todo request
   */
  public void add(TodoRequest todoRequest) {
    todoTasks.put(todoRequest.getRefId(), todoRequest);
  }

  /**
   * Removing the request.
   * 
   * @param refId
   *          - reference id
   * @return
   *    the removed request 
   */
  public TodoRequest remove(String refId) {
    return todoTasks.remove(refId);
  }

  public boolean isEmpty() {
    return todoTasks.isEmpty();
  }

}
