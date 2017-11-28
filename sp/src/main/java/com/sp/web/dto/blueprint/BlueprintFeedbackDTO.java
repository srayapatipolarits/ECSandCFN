package com.sp.web.dto.blueprint;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.blueprint.BlueprintFeedback;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

/**
 * @author Dax Abraham
 *
 *         The DTO class for the feedback received entity.
 */
public class BlueprintFeedbackDTO {
  
  private String uid;
  private BaseUserDTO feedbackUser;
  private String createdOnFormatted;
  private String comment;
  
  /**
   * Constructor.
   * 
   * @param feedback
   *            - feedback 
   * @param userFactory
   *            - user factory
   */
  public BlueprintFeedbackDTO(BlueprintFeedback feedback, UserFactory userFactory) {
    this.uid = feedback.getUid();
    this.feedbackUser = userFactory.getFeedbackUserDTO(feedback.getFeedbackUserId());
    this.createdOnFormatted = MessagesHelper.formatDate(feedback.getCreatedOn());
    this.comment = feedback.getComment();
  }

  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public BaseUserDTO getFeedbackUser() {
    return feedbackUser;
  }
  
  public void setFeedbackUser(BaseUserDTO feedbackUser) {
    this.feedbackUser = feedbackUser;
  }
  
  public String getCreatedOnFormatted() {
    return createdOnFormatted;
  }
  
  public void setCreatedOnFormatted(String createdOnFormatted) {
    this.createdOnFormatted = createdOnFormatted;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
  
}
