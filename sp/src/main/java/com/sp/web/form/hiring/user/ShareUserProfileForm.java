package com.sp.web.form.hiring.user;

import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class for sharing the users profile.
 */
public class ShareUserProfileForm {
  private String userFor;
  private String firstName;
  private String lastName;
  private String email;
  
  public String getUserFor() {
    return userFor;
  }
  
  public void setUserFor(String userFor) {
    this.userFor = userFor;
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
    Optional.ofNullable(email).ifPresent(e -> this.email = StringUtils.trimWhitespace(e.toLowerCase()));
  }
  
  /**
   * Validate the form data.
   */
  public void validate() {
    Assert.hasText(userFor, "User for required.");
    Assert.hasText(firstName, "First name required.");
    Assert.hasText(lastName, "Last name required.");
    Assert.hasText(email, "Email required.");
  }

  /**
   * Create the feedback user from the data in the form.
   * 
   * @param hiringUser
   *          - hiring user
   * @return
   *    the feedback user
   */
  public FeedbackUser create(HiringUser hiringUser) {
    FeedbackUser feedbackUser = new FeedbackUser();
    feedbackUser.setFirstName(firstName);
    feedbackUser.setLastName(lastName);
    feedbackUser.setEmail(email);
    feedbackUser.setFeatureType(FeatureType.PortraitShare);
    feedbackUser.setCreatedOn(LocalDate.now());
    feedbackUser.addRole(RoleType.HiringPortraitShare);
    feedbackUser.setFeedbackFor(hiringUser.getId());
    feedbackUser.setCompanyId(hiringUser.getCompanyId());
    return feedbackUser;
  }
  
}
