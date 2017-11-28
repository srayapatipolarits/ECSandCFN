package com.sp.web.dto.todo;

import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoType;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dax Abraham
 * 
 *         The todo task DTO for competency.
 */
public class CompetencyTodoDTO extends TodoDTO {

  private static final long serialVersionUID = 3459277607302275367L;
  private String parentRefId;
  private TodoType type;
  private List<CompetencyTodoTaskDTO> requests;

  /**
   * Constructor.
   * 
   * @param parentRequest
   *            - parent request
   */
  public CompetencyTodoDTO(ParentTodoTaskRequests parentRequest) {
    BeanUtils.copyProperties(parentRequest, this);
    this.requests = new ArrayList<CompetencyTodoTaskDTO>();
  }

  public String getParentRefId() {
    return parentRefId;
  }

  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }

  public List<CompetencyTodoTaskDTO> getRequests() {
    return requests;
  }

  public void setRequests(List<CompetencyTodoTaskDTO> requests) {
    this.requests = requests;
  }

  public TodoType getType() {
    return type;
  }

  public void setType(TodoType type) {
    this.type = type;
  }
}
