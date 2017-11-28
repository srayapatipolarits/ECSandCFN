package com.sp.web.form.todo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.model.todo.TodoRequest;

import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The form for the Todo requests creation and updates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoForm implements GenericForm<TodoRequest> {
  
  private String parentRefId;
  private String refId;
  private String text;
  private boolean completed;
  
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dueBy;
  
  public String getRefId() {
    return refId;
  }
  
  public void setRefId(String refId) {
    this.refId = refId;
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
  
  @Override
  public void validate() {
    Assert.hasText(text, "Text is required.");
  }
  
  @Override
  public void validateUpdate() {
    Assert.hasText(refId, "RefId is required.");
    validate();
  }
  
  @Override
  public void validateGet() {
    Assert.hasText(refId, "Ref id is required.");
  }
  
  @Override
  public TodoRequest create(User user) {
    final TodoRequest todoRequest = new TodoRequest(text, dueBy);
    todoRequest.setParentRefId(parentRefId);
    return todoRequest;
  }
  
  @Override
  public void update(User user, TodoRequest instanceToUpdate) {
    instanceToUpdate.setText(text);
    instanceToUpdate.setDueBy(dueBy);
    instanceToUpdate.setCompleted(completed);
  }
  
  public String getParentRefId() {
    return parentRefId;
  }
  
  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
  
}
