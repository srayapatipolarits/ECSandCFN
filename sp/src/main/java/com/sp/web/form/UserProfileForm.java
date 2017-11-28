package com.sp.web.form;

import com.sp.web.model.Gender;
import com.sp.web.model.User;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Dax Abraham
 * 
 *         This is the form bean to store the user profile information.
 */
public class UserProfileForm extends BaseUserForm {

  @NotBlank
  private String email;
  @NotNull
  private Gender gender;
  @NotNull
  @Range(min = 1, max = 12)
  private int month;
  @NotNull
  @Range(min = 1, max = 31)
  private int day;
  @NotNull
  @Min(value = 1900)
  private int year;

  private String phone;

  private String linkedInUrl;

  /**
   * Constructor to store the user profile information.
   * 
   * @param user
   *          - the user object
   */
  public UserProfileForm(User user) {
    super(user);
    if (user.getDob() != null) {
      LocalDate dob = user.getDob();
      this.month = dob.getMonthValue();
      this.day = dob.getDayOfMonth();
      this.year = dob.getYear();
    }
    this.phone = user.getPhoneNumber();
  }

  /**
   * Default Constructor.
   */
  public UserProfileForm() {
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
    
    // Rmeoved the lower case becuase it was cuasing issue. to check. case sensitive email with mongo
    Optional.ofNullable(email).ifPresent(e -> this.email = StringUtils.trimWhitespace(e.toLowerCase()));
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
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
    userToUpdate.setDob(LocalDate.of(year, month, day));
    userToUpdate.setPhoneNumber(phone);
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }

  public String getLinkedInUrl() {
    return linkedInUrl;
  }
}