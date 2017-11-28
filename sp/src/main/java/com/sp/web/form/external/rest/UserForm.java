package com.sp.web.form.external.rest;

import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.model.Gender;
import com.sp.web.model.UserType;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserForm class contains the user data.
 * 
 * @author pradeepruhil
 *
 */
public class UserForm implements Serializable {
  
  /**
   * Default serila version id.
   */
  private static final long serialVersionUID = 3912821238766664183L;
  
  private String email;
  
  private String firstName;
  
  private String lastName;
  
  private String uid;
  
  private UserType userType;
  
  private Gender gender;
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    if (email != null) {
      this.email = StringUtils.trimWhitespace(email.toLowerCase());
    }
    
  }
  
  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public void validate() {
    Assert.isTrue(EmailValidator.getInstance().isValid(email),
        "Error 1002: Invalid Request (Email)");
    Assert.hasText(uid, "Error 1002, Invalid Request(uid not present)");
    Assert.hasText(firstName, "Error 1002, Invalid Request(firstName not present)");
    Assert.hasText(lastName, "Error 1002, Invalid Request(lastName not present)");
  }
  
  public HiringAddForm createHiringAddForm() {
    HiringAddForm addForm = new HiringAddForm();
    List<String> emails = new ArrayList<String>();
    addForm.setEmails(emails);
    emails.add(email);
    addForm.setType(getUserType());
    return addForm;
  }
  
  public void setUserType(UserType userType) {
    this.userType = userType;
  }
  
  public UserType getUserType() {
    if (userType == null) {
      userType = UserType.HiringCandidate;
    }
    return userType;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public Gender getGender() {
    if (gender == null) {
      gender = Gender.N;
    }
    return gender;
  }
}
