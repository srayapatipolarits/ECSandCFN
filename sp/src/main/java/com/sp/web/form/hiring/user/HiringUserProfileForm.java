package com.sp.web.form.hiring.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.form.BaseUserForm;
import com.sp.web.model.Address;
import com.sp.web.model.Gender;
import com.sp.web.model.User;
import com.sp.web.utils.UrlUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         This is the form bean to store the user profile information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HiringUserProfileForm extends BaseUserForm {
  
  private String id;
  private String email;
  private Gender gender;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate dob;
  private String phoneNumber;
  private String linkedInUrl;
  private Address address;
  
  /**
   * Constructor to store the user profile information.
   * 
   * @param user
   *          - the user object
   */
  public HiringUserProfileForm(User user) {
    super(user);
  }
  
  /**
   * Default Constructor.
   */
  public HiringUserProfileForm() {
    super();
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
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    // Rmeoved the lower case becuase it was cuasing issue. to check. case sensitive email with
    // mongo
    Optional.ofNullable(email).ifPresent(
        e -> this.email = org.springframework.util.StringUtils.trimWhitespace(e.toLowerCase()));
  }
  
  public Gender getGender() {
    return gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }
  
  public String getLinkedInUrl() {
    return linkedInUrl;
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
  
  public Address getAddress() {
    return address;
  }
  
  public void setAddress(Address address) {
    this.address = address;
  }
  
  public LocalDate getDob() {
    return dob;
  }
  
  public void setDob(LocalDate dob) {
    this.dob = dob;
  }
  
  /**
   * Updates the user information stored in the user profile object.
   * 
   * @param userToUpdate
   *          - the user object to update
   */
  public void update(User userToUpdate) {
    userToUpdate.setFirstName(firstName);
    userToUpdate.setLastName(lastName);
    userToUpdate.setTitle(title);
    userToUpdate.setGender(gender);
    userToUpdate.setDob(getDob());
    userToUpdate.setPhoneNumber(phoneNumber);
    if (!StringUtils.isBlank(linkedInUrl)) {
      userToUpdate.setLinkedInUrl(UrlUtils.normailzeUrl(linkedInUrl));
    }
    userToUpdate.setAddress(address);
  }
  
  public void validate() {
    Assert.hasText(id, "Id required.");
  }
  
  /**
   * Validate the update form data.
   */
  public void validateUpdate() {
    Assert.hasText(firstName, "First name required.");
    Assert.hasText(lastName, "Last name required.");
    Assert.hasText(phoneNumber, "Phone number required.");
    Assert.notNull(gender, "Gender required.");
    Assert.notNull(getDob(), "DOB required.");
    Assert.notNull(address, "Address required.");
    Assert.hasText(address.getAddressLine1(), "Address line1 required.");
    Assert.hasText(address.getCity(), "City required.");
    Assert.hasText(address.getState(), "State required.");
    Assert.hasText(address.getCountry(), "Country required.");
    Assert.hasText(address.getZipCode(), "Zip required.");
  }
}