package com.sp.web.dto.feed;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.RoleType;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.product.CompanyFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The SPMessage Post DTO.
 */
public class SPMessagePostDTO extends SPMessagePostListingDTO {
  
  private LocalDateTime updatedOn;
  private LocalDateTime publishedOn;
  private boolean allMember;
  private List<RoleType> userRoles;
  private UserMarkerDTO updatedBy;
  private UserMarkerDTO publishedBy;
  
  /**
   * Constructor.
   * 
   * @param message
   *            - message 
   * @param companyFactory
   *            - company factory
   */
  public SPMessagePostDTO(SPMessagePost message, CompanyFactory companyFactory) {
    super(message, companyFactory);
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getPublishedOn() {
    return publishedOn;
  }
  
  public void setPublishedOn(LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
  
  public boolean isAllMember() {
    return allMember;
  }
  
  public void setAllMember(boolean allMember) {
    this.allMember = allMember;
  }
  
  public List<RoleType> getUserRoles() {
    return userRoles;
  }
  
  public void setUserRoles(List<RoleType> userRoles) {
    this.userRoles = userRoles;
  }
  
  public UserMarkerDTO getPublishedBy() {
    return publishedBy;
  }
  
  public void setPublishedBy(UserMarkerDTO publishedBy) {
    this.publishedBy = publishedBy;
  }

  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
}
