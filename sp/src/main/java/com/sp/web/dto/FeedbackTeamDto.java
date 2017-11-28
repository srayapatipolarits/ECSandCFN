package com.sp.web.dto;


/**
 * @author pradeep
 *
 */
public class FeedbackTeamDto {

  private String feedbackRequestId;

  private String firstName;

  private String lastName;

  private String email;

  /**
   * return the feedback request id.
   * 
   * @return the feedbackRequestId
   */
  public String getFeedbackRequestId() {
    return feedbackRequestId;
  }

  /**
   * set the feedback request id.
   * 
   * @param feedbackRequestId
   *          the feedbackRequestId to set
   */
  public void setFeedbackRequestId(String feedbackRequestId) {
    this.feedbackRequestId = feedbackRequestId;
  }

  /**
   * return the first name.
   * 
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * set the first name.
   * 
   * @param firstName
   *          the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * return the lastname.
   * 
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * set the lastname.
   * 
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * return the email.
   * 
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * set the email.
   * 
   * @param email
   *          the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FeedbackTeamDto [feedbackRequestId=" + feedbackRequestId + ", firstName=" + firstName
        + ", lastName=" + lastName + ", email=" + email + "]";
  }

}
