package com.sp.web.dto.todo;

import com.sp.web.model.task.TaskType;
import com.sp.web.model.todo.CompetencyTodoRequest;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO for competency todo requests.
 */
public class CompetencyTodoTaskDTO extends TodoTaskDTO {

  private static final long serialVersionUID = 1428838048307031739L;
  private TaskType taskType;
  
  public CompetencyTodoTaskDTO(CompetencyTodoRequest request) {
    super(request);
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public void setTaskType(TaskType taskType) {
    this.taskType = taskType;
  }
  
}
