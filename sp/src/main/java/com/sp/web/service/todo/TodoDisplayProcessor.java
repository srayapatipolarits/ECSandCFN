package com.sp.web.service.todo;

import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.model.todo.TodoRequest;

import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * 
 * @author Dax Abraham
 * 
 *         The interface for the display processor for todo's.
 */
public interface TodoDisplayProcessor {
  
  /**
   * The method to process the request and provide the Todo DTO.
   * 
   * @param request
   *          - request
   * @param updateUserTodoRequests
   *          - flag to update the user todo request
   * @return the todo DTO
   */
  TodoDTO process(TodoRequest request, MutableBoolean updateUserTodoRequests);
}
