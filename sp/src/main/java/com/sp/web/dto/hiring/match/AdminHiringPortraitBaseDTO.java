package com.sp.web.dto.hiring.match;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.hiring.match.HiringPortrait;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring portrait base information.
 */
public class AdminHiringPortraitBaseDTO extends HiringPortraitBaseDTO {
  
  private static final long serialVersionUID = 9092610849984478346L;
  private String description;
  private Set<String> tags;
  private LocalDateTime updatedOn;
  private UserMarkerDTO updatedBy;
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          - the hiring portrait
   */
  public AdminHiringPortraitBaseDTO(HiringPortrait portrait) {
    super(portrait);
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Set<String> getTags() {
    return tags;
  }
  
  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
}
