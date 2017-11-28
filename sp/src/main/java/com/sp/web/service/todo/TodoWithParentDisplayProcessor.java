package com.sp.web.service.todo;

import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.model.User;
import com.sp.web.model.todo.ParentTodoTaskRequests;

import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * 
 * @author Dax Abraham
 *
 *         The interface for the display processor with parent todo tasks.
 */
public interface TodoWithParentDisplayProcessor {
  
  /**
   * Process the request with parent and the list of child requests.
   * 
   * @param user
   *          - user
   * @param parentRequest
   *          - parent todo request
   * @param requests
   *          - requests
   * @param updateUserTodoRequests
   *          - flag to update user todo requests
   * @return the todo
   */
  TodoDTO process(User user, ParentTodoTaskRequests parentRequest,
      MutableBoolean updateUserTodoRequests);
}
