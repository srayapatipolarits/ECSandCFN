package com.sp.web.model.blueprint;

import com.sp.web.model.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the blueprint feedback.
 */
public class BlueprintFeedback implements Serializable{
  
  private static final long serialVersionUID = 3154942621245793479L;
  private String uid;
  private String feedbackUserId;
  private LocalDate createdOn;
  private String comment;
  
  /**
   * Default constructor.
   */
  public BlueprintFeedback() {
    // default constructor
  }
  
  /**
   * Constructor.
   * 
   * @param feedbackUser
   *          - feedback user
   * @param uidGenerator
   *          - UID generator        
   * @param comment
   *          - comment from the user          
   */
  public BlueprintFeedback(User feedbackUser, Supplier<String> uidGenerator, String comment) {
    this.feedbackUserId = feedbackUser.getId();
    this.createdOn = LocalDate.now();
    this.uid = uidGenerator.get();
    this.comment = comment;
  }

  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getFeedbackUserId() {
    return feedbackUserId;
  }
  
  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
  
}
