package com.sp.web.model.goal;

import com.sp.web.service.translation.Translable;

import java.io.Serializable;

/**
 * <code>DevelopmentStrategy</code> class holds the dev strategy associated with the goal.
 * 
 * @author vikram
 */
public class DevelopmentStrategy implements Serializable {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = -3299361231996508783L;
  
  @Translable
  private String id;
  
  @Translable
  private String dsDescription;
  
  @Translable
  private String videoUrl;
  
  @Translable
  private String imageUrl;
  
  private boolean active;
  
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
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DevelopmentStrategy other = (DevelopmentStrategy) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
