package com.sp.web.dto.lndfeedback;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.user.UserFactory;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * <code>baseDevelopmentfeedbackDTO</code> is the basic dto for development feedback.
 * 
 * @author pradeepruhil
 *
 */
public class BaseDevelopmentFeedbackDTO {
  
  private String id;
  
  private String devFeedRefId;
  
  private RequestStatus requestStatus;
  
  private LocalDateTime createdOn;
  
  private LocalDateTime repliedOn;
  
  private String response;
  
  private String comment;
  
  private String feedParentRefId;
  
  private BaseUserDTO feedbackUserDto;
  
  private BaseUserDTO user;
  
  private LocalDateTime updatedOn;

  /**
   * Constructor.
   * 
   * @param devFeedback
   *            - development feedback
   * @param user
   *            - member requester
   */
  public BaseDevelopmentFeedbackDTO(DevelopmentFeedback devFeedback, User user, UserFactory userFactory) {
    BeanUtils.copyProperties(devFeedback, this);
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(devFeedback.getFeedbackUserId());
    feedbackUserDto = new BaseUserDTO(feedbackUser);
    this.user = new BaseUserDTO(user);
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getDevFeedRefId() {
    return devFeedRefId;
  }
  
  public void setDevFeedRefId(String devFeedRefId) {
    this.devFeedRefId = devFeedRefId;
  }
  
  public RequestStatus getRequestStatus() {
    return requestStatus;
  }
  
  public void setRequestStatus(RequestStatus requestStatus) {
    this.requestStatus = requestStatus;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
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
  
  public void setFeedbackUserDto(BaseUserDTO feedbackUserDto) {
    this.feedbackUserDto = feedbackUserDto;
  }
  
  public BaseUserDTO getFeedbackUserDto() {
    return feedbackUserDto;
  }
  
  public void setUser(BaseUserDTO user) {
    this.user = user;
  }
  
  public BaseUserDTO getUser() {
    return user;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
}
