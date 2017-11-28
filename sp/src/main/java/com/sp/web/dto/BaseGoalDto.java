package com.sp.web.dto;

import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham The base goal DTO.
 */
public class BaseGoalDto implements Serializable {

  private static final long serialVersionUID = -4581622733124614355L;
  private String id;
  private String name;
  private String description;
  
  public BaseGoalDto(SPGoal spGoal) {
    BeanUtils.copyProperties(spGoal, this);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
}
