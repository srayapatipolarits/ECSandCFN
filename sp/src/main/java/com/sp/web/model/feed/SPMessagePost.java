package com.sp.web.model.feed;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.RoleType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The entity to define the SurePeople message post.
 */
public class SPMessagePost {
  
  private String id;
  private Comment message;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  private LocalDateTime publishedOn;
  private boolean allMember;
  private List<RoleType> userRoles;
  private List<String> companyIds;
  private boolean published;
  private UserMarkerDTO updatedBy;
  private UserMarkerDTO publishedBy;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Comment getMessage() {
    return message;
  }
  
  public void setMessage(Comment message) {
    this.message = message;
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
  
  public List<String> getCompanyIds() {
    return companyIds;
  }
  
  public void setCompanyIds(List<String> companyIds) {
    this.companyIds = companyIds;
  }
  
  public boolean isPublished() {
    return published;
  }
  
  public void setPublished(boolean published) {
    this.published = published;
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
  
  /**
   * Create a new instance of message post.
   * 
   * @return
   *    the new message post
   */
  public static SPMessagePost newInstance() {
    SPMessagePost messagePost = new SPMessagePost();
    messagePost.setCreatedOn(LocalDateTime.now());
    return messagePost;
  }

  /**
   * Reset the published status for the message post.
   */
  public void resetPublish() {
    published = false;
    publishedBy = null;
    publishedOn = null;
    allMember = false;
    companyIds = null;
    userRoles = null;
  }
}
