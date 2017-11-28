package com.sp.web.dto.goal;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanType;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 *
 *         The DTO to share the basic action plan details.
 */
public class BaseActionPlanDTO {
  
  private String id;
  private String name;
  private String description;
  private String imageUrl;
  private boolean active;
  private ActionPlanType type;
  
  /**
   * Constructor.
   * 
   * @param ap
   *          - action plan
   * @param company
   *          - company 
   */
  public BaseActionPlanDTO(ActionPlan ap) {
    BeanUtils.copyProperties(ap, this);
  }
  
  /**
   * Constructor from action plan dao.
   * 
   * @param actionPlan
   *          - action plan dao
   */
  public BaseActionPlanDTO(ActionPlanDao actionPlan) {
    BeanUtils.copyProperties(actionPlan, this);
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
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ActionPlanType getType() {
    return type;
  }

  public void setType(ActionPlanType type) {
    this.type = type;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

}
