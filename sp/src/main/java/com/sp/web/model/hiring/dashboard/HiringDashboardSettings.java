package com.sp.web.model.hiring.dashboard;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.SPFeature;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HiringDashboardSettings contains the dashboard settings for people analytics plateform.
 * 
 * @author pradeepruhil
 *
 */
public class HiringDashboardSettings implements Serializable {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 921662522269876568L;
  
  private String id;
  
  private Map<SPFeature, String> images;
  
  private List<String> artilces;
  
  private UserMarkerDTO createdBy;
  
  private UserMarkerDTO updatedBy;
  
  private LocalDateTime updatedOn;
  
  public void setImages(Map<SPFeature, String> images) {
    this.images = images;
  }
  
  public Map<SPFeature, String> getImages() {
    return images;
  }
  
  public List<String> getArtilces() {
    if (artilces == null) {
      artilces = new ArrayList<String>();
    }
    return artilces;
  }
  
  public void setArtilces(List<String> artilces) {
    this.artilces = artilces;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
  public UserMarkerDTO getCreatedBy() {
    return createdBy;
  }
  
  public void setCreatedBy(UserMarkerDTO createdBy) {
    this.createdBy = createdBy;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
}
