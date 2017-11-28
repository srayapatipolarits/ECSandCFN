package com.sp.web.dto.todo;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The Todo task DTO.
 */
public class TodoTaskDTO extends TodoDTO {
  
  private static final long serialVersionUID = -8750789906770161604L;
  private String refId;
  private TodoType type;
  private LocalDateTime createdOn;
  private LocalDateTime dueBy;
  private String text;
  private String url;
  private boolean completed;
  private UserMarkerDTO user;
  
  /**
   * Constructor.
   * 
   * @param request
   *          - request
   */
  public TodoTaskDTO(TodoRequest request) {
    BeanUtils.copyProperties(request, this);
  }

  public String getRefId() {
    return refId;
  }
  
  public void setRefId(String refId) {
    this.refId = refId;
  }
  
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
  
  public LocalDateTime getDueBy() {
    return dueBy;
  }
  
  public void setDueBy(LocalDateTime dueBy) {
    this.dueBy = dueBy;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public UserMarkerDTO getUser() {
    return user;
  }

  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
  
}
