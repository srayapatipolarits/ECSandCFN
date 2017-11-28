package com.sp.web.service.todo;

import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.dto.todo.TodoParentDTO;
import com.sp.web.dto.todo.TodoTaskDTO;
import com.sp.web.model.User;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The default todo with parent display processor.
 */
@Component("todoWithParentDisplayProcessor")
public class DefaultTodoWithParentDisplayProcessor implements TodoWithParentDisplayProcessor {
  
  @Override
  public TodoDTO process(User user, ParentTodoTaskRequests parentRequest,
      MutableBoolean updateUserTodoRequests) {
    TodoParentDTO parentDto = new TodoParentDTO(parentRequest);
    Map<String, TodoRequest> todoTasks = parentRequest.getTodoTasks();
    if (!todoTasks.isEmpty()) {
      todoTasks.values().stream().map(TodoTaskDTO::new).forEach(parentDto::add);
    }
    return parentDto;
  }
  
}
