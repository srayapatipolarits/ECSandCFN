package com.sp.web.model.blueprint;

import com.sp.web.model.FeedbackUser;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 *
 *         The entity to store the blueprint approver details.
 */
public class BlueprintApprover implements Serializable {
  private static final long serialVersionUID = 8846358416559932303L;
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDateTime createdOn;
  private LocalDateTime approvedOn;
  
  /**
   * Default Constructor.
   */
  public BlueprintApprover() {
    // Default Constructor.
  }
  
  /**
   * Constructor.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  public BlueprintApprover(FeedbackUser feedbackUser) {
    BeanUtils.copyProperties(feedbackUser, this);
    createdOn = LocalDateTime.now();
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getApprovedOn() {
    return approvedOn;
  }
  
  public void setApprovedOn(LocalDateTime approvedOn) {
    this.approvedOn = approvedOn;
  }
  
}
