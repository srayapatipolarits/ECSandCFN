package com.sp.web.dto.todo;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.User;
import com.sp.web.model.todo.TodoRequest;

/**
 * Development Feedback todo dto.
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedbackTodoDTO extends TodoTaskDTO {

  private static final long serialVersionUID = -7619057355018271418L;
  private UserMarkerDTO user;

  /**
   * Constructor
   * 
   * @param request
   *          todo request.
   */
  public DevelopmentFeedbackTodoDTO(TodoRequest request) {
    super(request);
  }

  public DevelopmentFeedbackTodoDTO(User user, TodoRequest request) {
    this(request);
  }

  public UserMarkerDTO getUser() {
    return user;
  }

  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
}
