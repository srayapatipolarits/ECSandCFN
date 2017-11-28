package com.sp.web.model.task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The user task.
 */
public class UserTask implements Serializable{

  private static final long serialVersionUID = 4879423554153793866L;
  private TaskType type;
  private Map<String, Object> params;
  private LocalDateTime endDate;

  /**
   * Constructor.
   * 
   * @param type
   *          - type
   */
  public UserTask(TaskType type) {
    this.type = type;
  }
  
  /**
   * Default Constructor.
   */
  public UserTask() {}

  public TaskType getType() {
    return type;
  }

  public void setType(TaskType type) {
    this.type = type;
  }

  public Map<String, Object> getParams() {
    return Optional.ofNullable(params).orElseGet(() -> {
        params = new HashMap<String, Object>();
        return params;
      });
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  /**
   * Adds the given parameter.
   * 
   * @param key
   *          - key
   * @param value
   *          - value
   */
  public void addParam(String key, Object value) {
    getParams().put(key, value);
  }

  /**
   * Gets the parameter for the given key.
   * 
   * @param key
   *          - key
   * @return
   *      the parameter value
   */
  public Object getParam(String key) {
    return getParams().get(key);
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

}
