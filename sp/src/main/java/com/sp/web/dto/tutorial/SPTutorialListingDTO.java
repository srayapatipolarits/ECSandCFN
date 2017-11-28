package com.sp.web.dto.tutorial;

import com.sp.web.model.tutorial.SPTutorial;

import org.springframework.beans.BeanUtils;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class for the listing of SPTutorial entity.
 */
public class SPTutorialListingDTO {
  
  private String id;
  private String name;
  private String description;
  private String imgUrl;
  private boolean active;
  
  /**
   * Constructor.
   */
  public SPTutorialListingDTO(SPTutorial tutorial) {
    BeanUtils.copyProperties(tutorial, this, "steps");
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
  
  public String getImgUrl() {
    return imgUrl;
  }
  
  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
}
