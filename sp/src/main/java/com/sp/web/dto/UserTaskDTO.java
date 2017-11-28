package com.sp.web.dto;

import com.sp.web.model.task.TaskType;
import com.sp.web.model.task.UserTask;
import com.sp.web.utils.MessagesHelper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the task list.
 */
public class UserTaskDTO {

  private TaskType type;
  private Map<String, Object> params;
  private String endDate;

  /**
   * Default Constructor.
   */
  public UserTaskDTO() { }
  
  /**
   * Constructor to create the task DTO from the given User Task.
   * 
   * @param task
   *          - task
   */
  public UserTaskDTO(UserTask task) {
    type = task.getType();
    params = new HashMap<String, Object>(task.getParams());
    final LocalDateTime endDate = task.getEndDate();
    if (endDate != null) {
      this.endDate = MessagesHelper.formatDate(endDate);
    }
  }

  public TaskType getType() {
    return type;
  }

  public void setType(TaskType type) {
    this.type = type;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

}
