package com.sp.web.form.hiring.lens;

import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 
 * @author Dax Abraham
 *
 *         The form for the hiring lens requests for both candidates and employees.
 */
public class HiringLensForm implements GenericForm<FeedbackUser> {
  
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String referenceType;
  private String feedbackFor;
  private boolean requestNow;
  private String phoneNumber;
  private boolean archiveUser;
  
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
    return StringUtils.trimWhitespace(email.toLowerCase());
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getReferenceType() {
    return referenceType;
  }
  
  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }
  
  public String getFeedbackFor() {
    return feedbackFor;
  }
  
  public void setFeedbackFor(String feedbackFor) {
    this.feedbackFor = feedbackFor;
  }
  
  public boolean isRequestNow() {
    return requestNow;
  }
  
  public void setRequestNow(boolean requestNow) {
    this.requestNow = requestNow;
  }
  
  public String getPhoneNumber() {
    return phoneNumber;
  }
  
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public boolean isArchiveUser() {
    return archiveUser;
  }
  
  public void setArchiveUser(boolean archiveUser) {
    this.archiveUser = archiveUser;
  }
  
  /**
   * Validate the lens request.
   */
  public void validate() {
    Assert.hasText(feedbackFor, "Feedback for required.");
    Assert.hasText(firstName, "First name required.");
    Assert.hasText(lastName, "Last name required.");
    Assert.hasText(email, "Email required.");
  }
  
  /**
   * Validate reference check lens request.
   * 
   * @param user
   *          - user requesting the lens
   */
  public void validateReference(User user) {
    validate();
    Assert.hasText(phoneNumber, "Phone number required.");
    Assert.hasText(referenceType, "Reference type required.");
    Assert.isTrue(!email.equalsIgnoreCase(user.getEmail()), "Cannot add self as reference.");
  }
  
  @Override
  public void validateUpdate() {
    validateGet();
    validate();
  }
  
  @Override
  public void validateGet() {
    Assert.hasText(id, "Id required.");
  }
  
  public void validateGetAllRequest() {
    Assert.hasText(feedbackFor, "Feedback for required.");
  }
  
  @Override
  public FeedbackUser create(User user) {
    FeedbackUser feedbackUser = new FeedbackUser();
    BeanUtils.copyProperties(this, feedbackUser);
    feedbackUser.setUserStatus(UserStatus.INVITATION_NOT_SENT);
    feedbackUser.setFeatureType(FeatureType.PrismLensHiring);
    feedbackUser.setCompanyId(user.getCompanyId());
    feedbackUser.setCreatedOn(LocalDate.now());
    feedbackUser.addRole(RoleType.FeedbackUser);
    return feedbackUser;
  }
  
  @Override
  public void update(User user, FeedbackUser instanceToUpdate) {
    BeanUtils.copyProperties(this, instanceToUpdate);
  }
}
