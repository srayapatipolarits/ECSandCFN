package com.sp.web.form;

import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The basic form for getting the information for external users.
 */
public class ExternalUserForm {
  
  private String firstName;
  private String lastName;
  private String email;

  /**
   * Default constructor.
   */
  public ExternalUserForm() {
  }

  /**
   * Constructor.
   * 
   * @param firstName
   *          - first name
   * @param lastName
   *          - last name
   * @param email
   *          - email
   */
  public ExternalUserForm(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
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
   * Update the feedback user from the data in the form.
   * 
   * @param fbUser
   *          - feedback user to update
   */
  public void updateFeedbackUser(FeedbackUser fbUser) {
    BeanUtils.copyProperties(this, fbUser);
    fbUser.setType(UserType.External);
    fbUser.addRole(RoleType.FeedbackUser);
  }
  
  /**
   * Helper method to validate the form data. If basic validation only email is checked.
   * 
   * @param isBasicValidation
   *          - basic validation flag
   */
  public void validate(boolean isBasicValidation) {
    Assert.hasText(email, "User is required.");
    if (!isBasicValidation) {
      Assert.hasText(firstName, "First name required.");
      Assert.hasText(lastName, "Last name required.");
    }
  }
  
  /**
   * Helper method to create the external user with the information from this form.
   * 
   * @param user
   *          - user
   * @param featureType
   *          - feature type
   * @return the feedback user
   */
  public FeedbackUser getFeedbackUser(User user, FeatureType featureType) {
    Assert.isTrue(!user.getEmail().equalsIgnoreCase(email), "Cannot send request to yourself.");
    FeedbackUser feedbackUser = new FeedbackUser();
    feedbackUser.setFeedbackFor(user.getId());
    feedbackUser.setFeatureType(featureType);
    updateFeedbackUser(feedbackUser);
    return feedbackUser;
  }
}
