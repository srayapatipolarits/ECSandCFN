package com.sp.web.model.todo;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the user Todo requests.
 */
public class UserTodoRequests {
  
  private String id;
  private Map<String, TodoRequest> todoTasks;
  private Map<String, ParentTodoTaskRequests> todoWithParentTasks;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Map<String, TodoRequest> getTodoTasks() {
    return todoTasks;
  }
  
  public void setTodoTasks(Map<String, TodoRequest> todoTasks) {
    this.todoTasks = todoTasks;
  }
  
  public Map<String, ParentTodoTaskRequests> getTodoWithParentTasks() {
    return todoWithParentTasks;
  }

  public void setTodoWithParentTasks(Map<String, ParentTodoTaskRequests> todoWithParentTasks) {
    this.todoWithParentTasks = todoWithParentTasks;
  }
  
  /**
   * Get a new instance of the user todo requests.
   * 
   * @return
   *      new user todo request 
   */
  public static UserTodoRequests newInstance() {
    UserTodoRequests userTodoRequests = new UserTodoRequests();
    userTodoRequests.setTodoTasks(new HashMap<String, TodoRequest>());
    userTodoRequests.setTodoWithParentTasks(new HashMap<String, ParentTodoTaskRequests>());
    return userTodoRequests;
  }


  /**
   * Add the given todo request.
   * 
   * @param todoRequest
   *            - todo request
   * @return
   *    true if updated else false
   */
  public boolean add(TodoRequest todoRequest) {
    String parentRefId = todoRequest.getParentRefId();
    
    if (parentRefId != null) {
      return addToParent(parentRefId, todoRequest);
    } 
    return addTask(todoRequest);
  }

  /**
   * Add the parent todo task request.
   * 
   * @param parentTodoTaskRequest
   *              - parent todo task request
   */
  public void add(ParentTodoTaskRequests parentTodoTaskRequest) {
    todoWithParentTasks.put(parentTodoTaskRequest.getParentRefId(), parentTodoTaskRequest);
  }
  
  
  /**
   * Add the given task.
   * 
   * @param todoRequest
   *            - todo request
   * @return
   *    true if updated else false
   */
  private boolean addTask(TodoRequest todoRequest) {
    todoRequest.validate();
    boolean updated = false;
    
    String refId = todoRequest.getRefId();
    TodoRequest request = todoTasks.get(refId);
    
    if (request == null) {
      todoTasks.put(refId, todoRequest);
      updated = true;
    } else {
      if (!request.isSameDueBy(todoRequest.getDueBy())) {
        todoTasks.put(refId, todoRequest);
        updated = true;
      } else {
        BeanUtils.copyProperties(request, todoRequest);
      }
    }
    return updated;
  }

  /**
   * Add to the parent requests.
   * 
   * @param parentRefId
   *          - parent reference id
   * @param todoRequest
   *          - todo request
   * @return
   *    true if updated else false
   */
  private boolean addToParent(String parentRefId, TodoRequest todoRequest) {
    todoRequest.validateParentRequest();
    
    boolean updated = false;
    
    ParentTodoTaskRequests parentRequest = todoWithParentTasks.get(parentRefId);
    
    if (parentRequest == null) {
      parentRequest = new ParentTodoTaskRequests(todoRequest);
      todoWithParentTasks.put(parentRefId, parentRequest);
    }
    
    String refId = todoRequest.getRefId();
    final TodoRequest existingTodoRequest = parentRequest.getRequest(refId);
    if (existingTodoRequest == null) {
      parentRequest.add(todoRequest);
      updated = true;
    } else {
      final LocalDateTime dueBy = todoRequest.getDueBy();
      if (!existingTodoRequest.isSameDueBy(dueBy)) {
        existingTodoRequest.setDueBy(dueBy);
        updated = true;
      }
    }
    return updated;    
  }

  /**
   * Get the parent todo task request.
   * 
   * @param parentRefId
   *          - parent reference id
   * @return
   *    the parent todo task request
   */
  public ParentTodoTaskRequests getParentTodoTaskRequest(String parentRefId) {
    return todoWithParentTasks.get(parentRefId);
  }

  /**
   * Remove all the tasks for the given parent id.
   * 
   * @param parentRefId
   *            - parent reference id
   * @return 
   *    true if removed else false
   */
  public boolean removeByParent(String parentRefId) {
    return todoWithParentTasks.remove(parentRefId) != null;
  }

  /**
   * Get the todo task.
   * 
   * @param refId
   *          - reference id
   * @param parentRefId
   *          - parent reference id
   * @return
   *      the todo request
   */
  public TodoRequest findRequest(String refId, String parentRefId) {
    TodoRequest request = null;
    if (parentRefId != null) {
      ParentTodoTaskRequests parentRequest = todoWithParentTasks.get(parentRefId);
      if (parentRequest != null) {
        request = parentRequest.getRequest(refId);
      }
    } else {
      request = todoTasks.get(refId);
    }
    return request;
  }

  /**
   * Get the todo task.
   * 
   * @param refId
   *          - reference id
   * @return
   *      the todo request
   */
  public TodoRequest findRequest(String refId) {
    return findRequest(refId, null);
  }
  
  /**
   * Remove the given todo task.
   * 
   * @param refId
   *          - reference id
   * @param parentRefId
   *          - parent reference id
   * @return
   *    the removed todo task
   */
  public TodoRequest deleteRequest(String refId, String parentRefId) {
    TodoRequest request = null;
    if (parentRefId != null) {
      ParentTodoTaskRequests parentRequest = todoWithParentTasks.get(parentRefId);
      if (parentRequest != null) {
        request = parentRequest.remove(refId);
      }
    } else {
      request = todoTasks.remove(refId);
    }
    return request;
  }
}
