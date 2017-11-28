package com.sp.web.controller.todo;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.form.todo.TodoForm;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.service.todo.TodoFactory;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The todo controller helper.
 */
@Component
public class TodoControllerHelper extends
    GenericControllerHelper<TodoRequest, TodoDTO, TodoDTO, TodoForm, TodoFactory> {
  
  private static final String MODULE_NAME = "todo";
  
  /**
   * Constructor.
   * 
   * @param factory
   *          - todo factory
   */
  @Inject
  public TodoControllerHelper(TodoFactory factory) {
    super(MODULE_NAME, factory);
  }
  
}
