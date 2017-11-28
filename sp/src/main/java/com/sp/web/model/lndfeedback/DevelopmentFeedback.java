package com.sp.web.model.lndfeedback;

import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.utils.MessagesHelper;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <code>DevelopmentFeedback</code> is the model for the development feedback.
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedback implements Serializable {
  
  private static final long serialVersionUID = 6222620174421050630L;
  
  private String id;
  private String devFeedRefId;
  private String userId;
  private RequestStatus requestStatus;
  private LocalDateTime createdOn;
  private LocalDateTime repliedOn;
  private String response;
  private String comment;
  private String feedParentRefId;
  
  /* has to make that to string because of : https://jira.spring.io/browse/DATAMONGO-1422 */
  private String spFeature;
  private String tokenUrl;
  private String tokenId;
  private String companyId;
  private LocalDateTime updatedOn;
  /* title for the feedback to be shwown on the left trail on the listing page */
  private String title;
  /**
   * feedback user id is required in case to fetch the user from feedback user id when giving
   * feedback from email.
   */
  private String feedbackUserId;
  private String feedbackUserEmail;
  @Deprecated
  private FeedbackUser feedbackUser;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setDevFeedRefId(String devFeedRefId) {
    this.devFeedRefId = devFeedRefId;
  }
  
  public String getDevFeedRefId() {
    return devFeedRefId;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
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
  
  public String getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(String spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getTokenUrl() {
    return tokenUrl;
  }
  
  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }
  
  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }
  
  public String getFeedbackUserId() {
    return feedbackUserId;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }
  
  public String getTokenId() {
    return tokenId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  
  @Transient
  public boolean isCompleted() {
    return RequestStatus.COMPLETED != requestStatus;
  }
  
  @Transient
  public String getFormattedRequestStatus() {
    return MessagesHelper.getMessage("devFeedback." + requestStatus);
  }
  
  @Transient
  public String getFormattedCreatedOn() {
    return MessagesHelper.formatDate(createdOn);
  }
  
  @Transient
  public String getFormattedRepliedOn() {
    if (repliedOn != null) {
      return MessagesHelper.formatDate(repliedOn);
    }
    return null;
  }

  public String getFeedbackUserEmail() {
    return feedbackUserEmail;
  }

  public void setFeedbackUserEmail(String feedbackUserEmail) {
    this.feedbackUserEmail = feedbackUserEmail;
  }

  public FeedbackUser getFeedbackUser() {
    return feedbackUser;
  }

  public void setFeedbackUser(FeedbackUser feedbackUser) {
    this.feedbackUser = feedbackUser;
  }
}
