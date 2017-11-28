package com.sp.web.dto.goal;

import com.sp.web.model.goal.DevelopmentStrategy;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * <code>DevelopmentStrategyDTO</code> class holds the dev strategy associated with the goal.
 * 
 * @author vikram
 */
public class DevelopmentStrategyDTO implements Serializable {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = -3299361231996508783L;
  
  private String id;
  
  private String dsDescription;
  
  private String videoUrl;
  
  private String imageUrl;
  
  private boolean selectedByUser;
  
  /**
   * Development Strategy DTO will populate.
   */
  public DevelopmentStrategyDTO(DevelopmentStrategy developmentStrategy) {
    BeanUtils.copyProperties(developmentStrategy, this);
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setDsDescription(String dsDescription) {
    this.dsDescription = dsDescription;
  }
  
  public String getDsDescription() {
    return dsDescription;
  }
  
  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }
  
  public String getVideoUrl() {
    return videoUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public boolean isSelectedByUser() {
    return selectedByUser;
  }
  
  public void setSelectedByUser(boolean selectedByUser) {
    this.selectedByUser = selectedByUser;
  }
  
}
