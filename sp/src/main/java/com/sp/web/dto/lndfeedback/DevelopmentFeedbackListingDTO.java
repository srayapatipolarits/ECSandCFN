package com.sp.web.dto.lndfeedback;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DevelopmentFeedbackListing DTO
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedbackListingDTO implements Serializable {
  
  private static final long serialVersionUID = -7065948389624240436L;
  
  private String spFeature;
  
  private String title;
  
  private String name;
  
  private String devFeedRefId;
  
  private int count;
  
  private LocalDateTime updatedOn;
  
  public String getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(String spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDevFeedRefId() {
    return devFeedRefId;
  }
  
  public void setDevFeedRefId(String devFeedRefId) {
    this.devFeedRefId = devFeedRefId;
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
}
