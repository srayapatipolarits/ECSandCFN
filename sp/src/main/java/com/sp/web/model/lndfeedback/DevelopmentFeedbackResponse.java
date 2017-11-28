package com.sp.web.model.lndfeedback;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 
 * @author Dax Abraham
 * 
 *         The entity to store the development feedback response.
 */
public class DevelopmentFeedbackResponse {
  
  private String devFeedRefId;
  private UserMarkerDTO user;
  private RequestStatus requestStatus;
  private LocalDateTime createdOn;
  private LocalDateTime repliedOn;
  private String response;
  private String comment;
  private String feedParentRefId;
  private SPFeature spFeature;
  private String title;
  
  /**
   * Default constructor.
   */
  public DevelopmentFeedbackResponse() { }

  /**
   * Constructor.
   * 
   * @param feedback
   *          - development feedback
   * @param user
   *          - user
   */
  public DevelopmentFeedbackResponse(DevelopmentFeedback feedback, User user) {
    BeanUtils.copyProperties(feedback, this);
    this.user = new UserMarkerDTO(user);
    this.spFeature = SPFeature.valueOf(feedback.getSpFeature());
  }

  public String getDevFeedRefId() {
    return devFeedRefId;
  }
  
  public void setDevFeedRefId(String devFeedRefId) {
    this.devFeedRefId = devFeedRefId;
  }
  
  public UserMarkerDTO getUser() {
    return user;
  }
  
  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
  
  public RequestStatus getRequestStatus() {
    return requestStatus;
  }
  
  public void setRequestStatus(RequestStatus requestStatus) {
    this.requestStatus = requestStatus;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getRepliedOn() {
    return repliedOn;
  }
  
  public void setRepliedOn(LocalDateTime repliedOn) {
    this.repliedOn = repliedOn;
  }
  
  public String getResponse() {
    return response;
  }
  
  public void setResponse(String response) {
    this.response = response;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getFeedParentRefId() {
    return feedParentRefId;
  }
  
  public void setFeedParentRefId(String feedParentRefId) {
    this.feedParentRefId = feedParentRefId;
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
}
