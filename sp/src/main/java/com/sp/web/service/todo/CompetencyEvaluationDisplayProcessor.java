package com.sp.web.service.todo;

import com.sp.web.dto.todo.CompetencyTodoDTO;
import com.sp.web.dto.todo.CompetencyTodoTaskDTO;
import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.model.User;
import com.sp.web.model.todo.CompetencyTodoRequest;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The competency evaluation display processor.
 */
@Component("competencyEvaluationDisplayProcessor")
public class CompetencyEvaluationDisplayProcessor implements TodoWithParentDisplayProcessor {
  
  @Override
  public TodoDTO process(User user, ParentTodoTaskRequests parentRequest,
      MutableBoolean updateUserTodoRequests) {
    
    CompetencyTodoDTO competencyDto = new CompetencyTodoDTO(parentRequest);
    final Map<String, TodoRequest> todoTasks = parentRequest.getTodoTasks();
    if (!todoTasks.isEmpty()) {
      List<CompetencyTodoTaskDTO> requests = competencyDto.getRequests();
      todoTasks.values().stream().map(t -> new CompetencyTodoTaskDTO((CompetencyTodoRequest) t))
          .forEach(requests::add);
      return competencyDto;
    } 
    return null;
  }
  
}
