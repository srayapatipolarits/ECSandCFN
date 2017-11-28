package com.sp.web.model.goal;

import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * <code>PracticeFeedback</code> class holds the feedback associated with the goal / dev strategy.
 * 
 * @author vikram
 */
@Document(collection = "sPNote")
public class PracticeFeedback extends SPNote {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = -1529289979952202399L;
 
  private String tokenUrl;
  
  private String tokenId;
  
  private RequestStatus feedbackStatus;
  
  private String comment;
  
  private String feedbackUserId;
  
  private String feedbackUserEmail;
  
  private FeedbackUser feedbackUser;
  
  private String feedbackResponse;
  
  /**
   * Default constructor.
   */
  public PracticeFeedback() {
  }

  /**
   * Constructor to copy the feedback from the given feedback object.
   *  
   * @param baseFeedback
   *          - feedback to copy from
   */
  public PracticeFeedback(PracticeFeedback baseFeedback) {
    BeanUtils.copyProperties(baseFeedback, this);
  }

  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }
  
  public String getTokenUrl() {
    return tokenUrl;
  }
  
  public RequestStatus getFeedbackStatus() {
    return feedbackStatus;
  }

  public void setFeedbackStatus(RequestStatus feedbackStatus) {
    this.feedbackStatus = feedbackStatus;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getFeedbackUserEmail() {
    return feedbackUserEmail;
  }

  public void setFeedbackUserEmail(String feedbackUserEmailList) {
    this.feedbackUserEmail = feedbackUserEmailList;
  }

  public String getFeedbackResponse() {
    return feedbackResponse;
  }

  public void setFeedbackResponse(String feedbackResponse) {
    this.feedbackResponse = feedbackResponse;
  }

  public String getFeedbackUserId() {
    return feedbackUserId;
  }

  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }

  public FeedbackUser getFeedbackUser() {
    return feedbackUser;
  }

  public void setFeedbackUser(FeedbackUser feedbackUser) {
    this.feedbackUser = feedbackUser;
  }
  
  /**
   * @param tokenId 
   *      the tokenId to set.
   */
  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }
  
  /**
   * @return 
   *    - the tokenId.
   */
  public String getTokenId() {
    return tokenId;
  }
}
