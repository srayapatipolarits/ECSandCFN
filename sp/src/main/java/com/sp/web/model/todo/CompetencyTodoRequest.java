package com.sp.web.model.todo;

import com.sp.web.model.task.TaskType;

/**
 * @author Dax Abraham
 * 
 *         The competency todo request.
 */
public class CompetencyTodoRequest extends TodoRequest {
  
  private TaskType taskType;
  
  public TaskType getTaskType() {
    return taskType;
  }
  
  public void setTaskType(TaskType taskType) {
    this.taskType = taskType;
  }
}
