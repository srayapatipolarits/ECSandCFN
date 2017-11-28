package com.sp.web.form;

import org.springframework.util.StringUtils;

import java.util.Optional;


/**
 * @author Dax Abraham
 * 
 *         The form bean for external references.
 */
public class ReferencesForm {

  private String firstName;
  private String lastName;
  private String email;
  private String referenceType;
  /** Phone number. */
  private String phoneNumber;
  

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

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
