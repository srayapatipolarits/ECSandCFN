package com.sp.web.model.tracking;

import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.log.LogActionType;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the user activity tracking.
 */
public class ActivityTracking {
  
  private String id;
  private String companyId;
  private String userId;
  private LocalDateTime createdOn;
  private LogActionType actionType;
  private Comment message;
  private boolean postedToDashboard;
  
  /**
   * Default constructor.
   */
  public ActivityTracking() { }
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param actionType
   *          - action type
   */
  public ActivityTracking(User user, LogActionType actionType) {
    this.userId = user.getId();
    this.companyId = user.getCompanyId();
    createdOn = LocalDateTime.now();
    this.actionType = actionType;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LogActionType getActionType() {
    return actionType;
  }
  
  public void setActionType(LogActionType actionType) {
    this.actionType = actionType;
  }

  public boolean isPostedToDashboard() {
    return postedToDashboard;
  }

  public void setPostedToDashboard(boolean postedToDashboard) {
    this.postedToDashboard = postedToDashboard;
  }

  public Comment getMessage() {
    return message;
  }

  public void setMessage(Comment message) {
    this.message = message;
  }
  
}
