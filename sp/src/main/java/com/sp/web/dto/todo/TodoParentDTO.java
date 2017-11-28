package com.sp.web.dto.todo;

import com.sp.web.model.todo.ParentTodoTaskRequests;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The todo parent DTO.
 */
public class TodoParentDTO extends TodoDTO {

  private static final long serialVersionUID = -3683652464884421686L;
  private String parentRefId;
  private List<TodoTaskDTO> tasks;

  /**
   * Constructor.
   * 
   * @param parentRequest
   *            - parent request
   */
  public TodoParentDTO(ParentTodoTaskRequests parentRequest) {
    BeanUtils.copyProperties(parentRequest, this);
    tasks = new ArrayList<TodoTaskDTO>();
  }

  public String getParentRefId() {
    return parentRefId;
  }

  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }

  public List<TodoTaskDTO> getTasks() {
    return tasks;
  }

  public void setTasks(List<TodoTaskDTO> tasks) {
    this.tasks = tasks;
  }
  
  public void add(TodoTaskDTO taskDTO) {
    tasks.add(taskDTO);
  }
}
