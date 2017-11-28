package com.sp.web.service.todo;

import com.sp.web.Constants;
import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.form.Operation;
import com.sp.web.form.todo.TodoForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The factory interface for the todo's.
 */
@Component
public class TodoFactory implements GenericFactory<TodoDTO, TodoDTO, TodoForm> {
  
  private static final HashMap<TodoType, TodoDisplayProcessor> displayProcessorMap = 
      new HashMap<TodoType, TodoDisplayProcessor>();
  
  private static final HashMap<TodoType, TodoWithParentDisplayProcessor> withParentDisplayProcessorMap = 
      new HashMap<TodoType, TodoWithParentDisplayProcessor>();
  
  private static final Logger log = Logger.getLogger(TodoFactory.class);
  
  @Autowired
  private TodoFactoryCache factoryCache;
  
  @Autowired
  private EventGateway eventGateway;
  
  /**
   * Add the given todo request.
   * 
   * @param user
   *          - user
   * @param todoRequest
   *          - todo request
   * @return todo request
   */
  public TodoRequest addTodo(User user, TodoRequest todoRequest) {
    return addTodo(user, todoRequest, true);
  }
  
  /**
   * Add the given todo request and also send SSE.
   * 
   * @param user
   *          - user
   * @param todoRequest
   *          - todo request
   * @param sendSSE
   *          - send sse flag
   * @return the new todo request
   */
  public TodoRequest addTodo(User user, TodoRequest todoRequest, boolean sendSSE) {
    // getting the user todo requests
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    // adding the new todo request
    if (userTodoRequests.add(todoRequest)) {
      // saving to DB if request added
      factoryCache.updateUserTodoRequests(userTodoRequests);
    }
    // check if send sse
    if (sendSSE) {
      // sending the SSE
      final String parentRefId = todoRequest.getParentRefId();
      sendSse(user, Operation.ADD, parentRefId != null ? parentRefId : todoRequest.getRefId());
    }
    return todoRequest;
  }
  
  /**
   * Add to the Todo list.
   * 
   * @param user
   *          - user
   * @param type
   *          - todo type
   * @param refId
   *          - task reference id
   * @param parentTaskId
   *          - parent task id
   * @return todo request
   */
  public TodoRequest addTodo(User user, TodoType type, String refId, String parentTaskId) {
    return addTodo(user, TodoRequest.newInstanceFromParentRefId(type, parentTaskId, refId, null),
        false);
  }
  
  /**
   * Add to the todo list the given list of tasks with the given due date.
   *
   * @param user
   *          - user
   * @param type
   *          - todo type
   * @param refId
   *          - task reference id
   * @param parentTaskId
   *          - parent task id
   * @param taskDueDate
   *          - task due date
   * @return todo request
   */
  public TodoRequest addTodo(User user, TodoType type, String refId, String parentTaskId,
      LocalDateTime taskDueDate) {
    return addTodo(user,
        TodoRequest.newInstanceFromParentRefId(type, parentTaskId, refId, taskDueDate), false);
  }
  
  /**
   * Remove all the tasks for the given parent task id.
   * 
   * @param user
   *          - user
   * @param parentRefId
   *          - parent task id
   */
  public void removeAllTasksWithParentId(User user, String parentRefId) {
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    if (userTodoRequests != null) {
      removeAllTasksWithParentId(user, parentRefId, userTodoRequests);
    }
    
  }
  
  /**
   * Remove the parent tasks from the given user todo requests.
   * 
   * @param user
   *          - user
   * @param parentRefId
   *          - parent reference id
   * @param userTodoRequests
   *          - user todo requests
   */
  public void removeAllTasksWithParentId(User user, String parentRefId,
      UserTodoRequests userTodoRequests) {
    ParentTodoTaskRequests request = userTodoRequests.getParentTodoTaskRequest(parentRefId);
    if (request != null) {
      userTodoRequests.removeByParent(parentRefId);
      factoryCache.updateUserTodoRequests(userTodoRequests);
      sendSse(user, Operation.DELETE, request, false);
    }
  }
  
  /**
   * Remove the given todo from the user.
   * 
   * @param user
   *          - user
   * @param refId
   *          - reference id
   * @param parentRefId
   *          - parent reference id
   */
  public void remove(User user, String refId, String parentRefId) {
    // get the user todo requests
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    
    // delete the given request
    if (userTodoRequests != null) {
      TodoRequest request = userTodoRequests.deleteRequest(refId, parentRefId);
      // Assert.notNull(request, "Request not found.");
      
      if (request != null) {
        // update the user todo requests and send sse
        factoryCache.updateUserTodoRequests(userTodoRequests);
        sendSse(user, Operation.DELETE, null, request, false);
      }
    }
    
  }
  
  /**
   * Remove the given todo from the user.
   * 
   * @param user
   *          - user
   * @param refId
   *          - reference id
   */
  public void remove(User user, String refId) {
    remove(user, refId, null);
  }
  
  /**
   * Remove the given todo from the feedback user.
   * 
   * @param fbUser
   *          - user
   */
  public void remove(FeedbackUser fbUser) {
    if (fbUser.getType() == UserType.Member) {
      remove(fbUser, fbUser.getId());
    }
  }
  
  /**
   * Update the task with the given task id.
   * 
   * @param user
   *          - user
   * @param refId
   *          - reference id
   */
  public void updateTask(User user, String refId) {
    sendSse(user, Operation.UPDATE, refId);
  }
  
  @Override
  public List<TodoDTO> getAll(User user) {
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    List<TodoDTO> todoDTOs = new ArrayList<TodoDTO>();
    final Map<String, TodoRequest> todoTasks = userTodoRequests.getTodoTasks();
    final MutableBoolean doUpdate = new MutableBoolean(false);
    if (!CollectionUtils.isEmpty(todoTasks)) {
      todoTasks.values().stream().map(t -> process(t, doUpdate)).filter(r -> r != null)
          .forEach(todoDTOs::add);
    }
    
    final Map<String, ParentTodoTaskRequests> todoWithParentTasks = userTodoRequests
        .getTodoWithParentTasks();
    if (!CollectionUtils.isEmpty(todoWithParentTasks)) {
      todoWithParentTasks.values().stream().map(t -> processWithParent(user, t, doUpdate))
          .filter(r -> r != null).forEach(todoDTOs::add);
    }
    
    // check if the user todo requests needs to be updated
    if (doUpdate.isTrue()) {
      factoryCache.updateUserTodoRequests(userTodoRequests);
    }
    
    return todoDTOs;
  }
  
  @Override
  public TodoDTO create(User user, TodoForm form) {
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    final TodoRequest create = form.create(user);
    if (userTodoRequests.add(create)) {
      factoryCache.updateUserTodoRequests(userTodoRequests);
    }
    TodoDTO todoDTO = getDTO(user, create);
    sendSse(user, Operation.ADD, todoDTO, true);
    return todoDTO;
  }
  
  @Override
  public TodoDTO update(User user, TodoForm form) {
    
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    
    TodoRequest request = userTodoRequests.findRequest(form.getRefId(), form.getParentRefId());
    Assert.notNull(request, "Request not found.");
    
    form.update(user, request);
    factoryCache.updateUserTodoRequests(userTodoRequests);
    
    TodoDTO todoDTO = getDTO(user, request);
    sendSse(user, Operation.UPDATE, todoDTO, true);
    
    return todoDTO;
  }
  
  @Override
  public void delete(User user, TodoForm form) {
    remove(user, form.getRefId(), form.getParentRefId());
  }
  
  /**
   * Check if the the given task is present for the user.
   * 
   * @param user
   *          - user
   * @param refId
   *          - reference id
   * @return true if present else false
   */
  public boolean hasTask(User user, String refId) {
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    return userTodoRequests.findRequest(refId) != null;
  }
  
  public boolean hasTask(User user, String parentRefId, String refId) {
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    return userTodoRequests.findRequest(refId, parentRefId) != null;
  }
  
  /**
   * Get the todo request for the given parent reference and child reference.
   * 
   * @param user
   *          - user
   * @param parentRefId
   *          - optional parent reference
   * @param refId
   *          - child reference
   * @return the todo request
   */
  public TodoRequest findRequest(User user, String parentRefId, String refId) {
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    return userTodoRequests.findRequest(refId, parentRefId);
  }
  
  /**
   * @param userRequests
   *          - delete the given user requests.
   */
  public void deleteUserTodoRequests(UserTodoRequests userRequests) {
    factoryCache.deleteUserTodoRequests(userRequests);
  }
  
  @Override
  public TodoDTO get(User user, TodoForm form) {
    final TodoDTO todoDTO = get(user, form.getRefId());
    Assert.notNull(todoDTO, "Task not found.");
    return todoDTO;
  }
  
  /**
   * Get the todo for the given reference id.
   * 
   * @param user
   *          - user
   * @param refId
   *          - reference id
   * @return the task todo
   */
  private TodoDTO get(User user, String refId) {
    MutableBoolean doUpdate = new MutableBoolean(false);
    UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
    TodoRequest todoRequest = userTodoRequests.findRequest(refId);
    TodoDTO resp = null;
    if (todoRequest != null) {
      resp = process(todoRequest, doUpdate);
    } else {
      ParentTodoTaskRequests parentRequest = userTodoRequests.getParentTodoTaskRequest(refId);
      if (parentRequest != null) {
        resp = processWithParent(user, parentRequest, doUpdate);
      }
    }
    
    if (doUpdate.isTrue()) {
      factoryCache.updateUserTodoRequests(userTodoRequests);
    }
    return resp;
  }
  
  /**
   * Process the parent todo request.
   * 
   * @param parentRequest
   *          - parent request
   * @param doUpdate
   *          - do update flag
   * @return the todo dto
   */
  public TodoDTO processWithParent(User user, ParentTodoTaskRequests parentRequest,
      MutableBoolean doUpdate) {
    TodoWithParentDisplayProcessor processor = getParentProcessor(parentRequest.getType());
    if (processor != null) {
      return processor.process(user, parentRequest, doUpdate);
    }
    return null;
  }
  
  /**
   * Helper method to process with parent.
   * 
   * @param user
   *          - user
   * @param parentTodoTaskRequest
   *          - parent todo task request
   * @return the todo dto
   */
  public TodoDTO processWithParent(User user, ParentTodoTaskRequests parentTodoTaskRequest) {
    return processWithParent(user, parentTodoTaskRequest, new MutableBoolean(false));
  }
  
  /**
   * Get the parent display processor.
   * 
   * @param type
   *          - type
   * @return the display processor
   */
  private TodoWithParentDisplayProcessor getParentProcessor(TodoType type) {
    TodoWithParentDisplayProcessor processor = withParentDisplayProcessorMap.get(type);
    if (processor == null) {
      try {
        processor = (TodoWithParentDisplayProcessor) ApplicationContextUtils.getBean(type
            .getDisplayProcessor());
        withParentDisplayProcessorMap.put(type, processor);
      } catch (Exception e) {
        log.warn("Error getting the display processor.", e);
      }
    }
    return processor;
  }
  
  /**
   * Processing the todo request to get the todo display dto.
   * 
   * @param request
   *          - request
   * @param todoDtos
   *          - response list
   * @param doUpdate
   *          - flag to update
   * @return the todo DTO
   */
  private TodoDTO process(TodoRequest request, MutableBoolean doUpdate) {
    TodoDisplayProcessor processor = getProcessor(request.getType());
    if (processor != null) {
      return processor.process(request, doUpdate);
    }
    return null;
  }
  
  /**
   * Get the display processor for the given type.
   * 
   * @param type
   *          - todo type
   * @return the display processor
   */
  private TodoDisplayProcessor getProcessor(TodoType type) {
    TodoDisplayProcessor processor = displayProcessorMap.get(type);
    if (processor == null) {
      try {
        processor = (TodoDisplayProcessor) ApplicationContextUtils.getBean(type
            .getDisplayProcessor());
        displayProcessorMap.put(type, processor);
      } catch (Exception e) {
        log.warn("Error getting the display processor.", e);
      }
    }
    return processor;
  }
  
  /**
   * Send the SSE event.
   * 
   * @param user
   *          - user
   * @param op
   *          - operation
   * @param refId
   *          - reference id
   */
  public void sendSse(User user, Operation op, String refId) {
    sendSse(user, op, get(user, refId), false);
  }
  
  /**
   * Send SSE.
   * 
   * @param user
   *          - user
   * @param op
   *          - operation
   * @param todoDTO
   *          - todo DTO
   * @param filterCurrentUser
   *          - filter current user
   */
  public void sendSse(User user, Operation op, TodoDTO todoDTO, boolean filterCurrentUser) {
    if (todoDTO != null) {
      sendSse(user, op, todoDTO, null, filterCurrentUser);
    } else {
      if (log.isDebugEnabled()) {
        log.debug("Todo DTO found null ignoring send SSE.");
      }
    }
  }
  
  /**
   * Send SSE event.
   * 
   * @param user
   *          - user
   * @param op
   *          - operation
   * @param todoDTO
   *          - todo
   * @param request
   *          - todo request
   * @param filterCurrentUser
   *          - flag to filter current user
   */
  public void sendSse(User user, Operation op, TodoDTO todoDTO, TodoRequest request,
      boolean filterCurrentUser) {
    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.PARAM_OPERATION, op);
    if (todoDTO != null) {
      payload.put(Constants.PARAM_TODO, todoDTO);
    }
    
    if (request != null) {
      payload.put(Constants.PARAM_TODO_REF_ID, request.getRefId());
      payload.put(Constants.PARAM_TODO_PARENT_REF_ID, request.getParentRefId());
      payload.put(Constants.PARAM_TODO_TYPE, request.getType());
    }
    
    sendEvent(user, filterCurrentUser, payload);
  }
  
  /**
   * Send the sse event to the user.
   * 
   * @param user
   *          - user
   * @param op
   *          - operation
   * @param parentRefId
   *          - parent reference id
   * @param refId
   *          - reference id
   * @param type
   *          - type
   */
  public void sendSse(User user, Operation op, String parentRefId, String refId, TodoType type) {
    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.PARAM_OPERATION, op);
    payload.put(Constants.PARAM_TODO_REF_ID, refId);
    payload.put(Constants.PARAM_TODO_PARENT_REF_ID, parentRefId);
    payload.put(Constants.PARAM_TODO_TYPE, type);
    
    sendEvent(user, false, payload);
  }
  
  /**
   * Send the event for the given parent request.
   * 
   * @param user
   *          - user
   * @param op
   *          - operation
   * @param request
   *          - request
   * @param filterCurrentUser
   *          - flag to filter current user
   */
  public void sendSse(User user, Operation op, ParentTodoTaskRequests request,
      boolean filterCurrentUser) {
    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.PARAM_OPERATION, op);
    
    if (request != null) {
      payload.put(Constants.PARAM_TODO_PARENT_REF_ID, request.getParentRefId());
      payload.put(Constants.PARAM_TODO_TYPE, request.getType());
    }
    
    sendEvent(user, filterCurrentUser, payload);
  }
  
  private void sendEvent(User user, boolean filterCurrentUser, Map<String, Object> payload) {
    MessageEventRequest eventRequest = MessageEventRequest.newEvent(ActionType.ActionPlan, payload,
        user);
    if (filterCurrentUser) {
      eventRequest.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
    }
    eventGateway.sendEvent(eventRequest);
  }
  
  /**
   * Get the todo DTO for the given request.
   * 
   * @param user
   *          - user
   * @param request
   *          - request
   * @return the todo DTO
   */
  private TodoDTO getDTO(User user, TodoRequest request) {
    TodoDTO todoDTO = null;
    if (request.getParentRefId() != null) {
      todoDTO = get(user, request.getParentRefId());
    } else {
      todoDTO = get(user, request.getRefId());
    }
    return todoDTO;
  }
  
  /**
   * Get user todo requests.
   * 
   * @param user
   *          - user
   * @return the user todo requests
   */
  public UserTodoRequests getUserTodoRequests(User user) {
    return factoryCache.getUserTodoRequests(user);
  }
  
  /**
   * Update the given user todo requests.
   * 
   * @param userTodoRequests
   *          - user todo requests
   */
  public void updateUserTodoRequests(UserTodoRequests userTodoRequests) {
    factoryCache.updateUserTodoRequests(userTodoRequests);
  }
}
