package com.sp.web.service.lndfeedback;

import com.sp.web.dto.todo.DevelopmentFeedbackTodoDTO;
import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.service.todo.TodoDisplayProcessor;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.stereotype.Component;

/**
 * DevFeedbackDisplayProcessor is the feedback display processor.
 * 
 * @author pradeepruhil
 *
 */
@Component("devFeedbackDisplayProcessor")
public class DevFeedbackDisplayProcessor implements TodoDisplayProcessor {
  
  /**
   * @see com.sp.web.service.todo.TodoDisplayProcessor#process(com.sp.web.model.todo.TodoRequest,
   *      org.apache.commons.lang3.mutable.MutableBoolean)
   */
  @Override
  public TodoDTO process(TodoRequest request, MutableBoolean updateUserTodoRequests) {
    TodoDTO developmentFeedbackTodoDTO = new DevelopmentFeedbackTodoDTO(request);
    return developmentFeedbackTodoDTO;
  }
  
}
