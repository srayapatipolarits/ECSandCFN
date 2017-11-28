package com.sp.web.dto.blueprint;

import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.GoalStatus;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 *
 *         The Base DTO class to share the blueprint.
 */
public class BaseBlueprintDTO {
 
  private String id;
  private GoalStatus status;

  /**
   * Constructor.
   * 
   * @param blueprint
   *          - blueprint
   */
  public BaseBlueprintDTO(Blueprint blueprint) {
    BeanUtils.copyProperties(blueprint, this);
  }

  public GoalStatus getStatus() {
    return status;
  }

  public void setStatus(GoalStatus status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
}
