package com.sp.web.model.hiring.role;

import com.sp.web.dto.user.UserMarkerDTO;

import java.time.LocalDateTime;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity class to store the people analytics roles.
 */
public class HiringRole {
  
  private String id;
  private String name;
  private String description;
  private String portraitId;
  private String companyId;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  private UserMarkerDTO createdBy;
  private UserMarkerDTO updatedBy;
  
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
  
  public String getPortraitId() {
    return portraitId;
  }
  
  public void setPortraitId(String portraitId) {
    this.portraitId = portraitId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
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

  /**
   * Removes the portrait for the role.
   * 
   * @return
   *    true if removed else false
   */
  public boolean removePortriat() {
    if (portraitId != null) {
      portraitId = null;
      return true;
    }
    return false;
  }
  
}
