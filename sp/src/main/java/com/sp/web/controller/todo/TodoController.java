package com.sp.web.controller.todo;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.form.todo.TodoForm;
import com.sp.web.model.todo.TodoRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The todo task controller.
 */
@Controller()
@RequestMapping("/todo")
public class TodoController extends GenericController<TodoRequest, TodoDTO, TodoDTO, TodoForm, TodoControllerHelper> {

  @Inject
  public TodoController(TodoControllerHelper helper) {
    super(helper);
  }
  
}
