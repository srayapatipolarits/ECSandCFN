package com.sp.web.service.todo;

import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.dto.todo.TodoTaskDTO;
import com.sp.web.model.todo.TodoRequest;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Dax Abraham
 *
 *         The default display processor for Todo's.
 */
@Component("defaultDisplayProcessor")
public class DefaultTodoDisplayProcessor implements TodoDisplayProcessor {
  
  @Override
  public TodoDTO process(TodoRequest request, MutableBoolean updateUserTodoRequests) {
    return new TodoTaskDTO(request);
  }
  
}
