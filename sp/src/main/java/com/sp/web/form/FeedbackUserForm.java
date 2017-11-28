package com.sp.web.form;

/**
 * @author Dax Abraham
 * 
 *         The form that captures the details of the feedback user, the feedback user could be an
 *         internal user or an external user.
 */
public class FeedbackUserForm {
  
  private String firstName;
  private String lastName;
  private String email;
  private String existingUserEmail;
  
  /**
   * Default constructor.
   */
  public FeedbackUserForm() {
    super();
  }

  /**
   * Constructor from first name, last name and email.
   * 
   * @param firstName
   *          - first name
   * @param lastName
   *          - last name
   * @param email
   *          - email
   */
  public FeedbackUserForm(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  /**
   * Constructor from existing user email.
   * 
   * @param existingUserEmail
   *            - existing user email
   */
  public FeedbackUserForm(String existingUserEmail) {
    this.existingUserEmail = existingUserEmail;
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

  public String getExistingUserEmail() {
    return existingUserEmail;
  }

  public void setExistingUserEmail(String existingUserEmail) {
    this.existingUserEmail = existingUserEmail;
  }
}
