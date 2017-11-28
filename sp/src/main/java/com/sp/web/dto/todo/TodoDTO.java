package com.sp.web.dto.todo;

import com.sp.web.model.todo.TodoType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the Todo requests.
 */
public class TodoDTO implements Serializable {

  private static final long serialVersionUID = -3000010631474632377L;
  private TodoType type;
  private LocalDateTime createdOn;
  
  public TodoType getType() {
    return type;
  }
  
  public void setType(TodoType type) {
    this.type = type;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
}
