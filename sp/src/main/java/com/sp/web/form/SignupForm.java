package com.sp.web.form;

import com.sp.web.model.Company;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.hiring.user.HiringUserArchive;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         This the bean class to store the signup form information.
 */
public class SignupForm implements Serializable {

  /**
   * Serial version uid.
   */
  private static final long serialVersionUID = 2941362104739308171L;

  @NotEmpty
  private String firstName;

  @NotEmpty
  private String lastName;

  private String title;

  @NotEmpty
  private String email;

  private String password;

  private String company;

  private String industry;

  private int numEmp;

//  @NotEmpty
  private String phone;

  private int month;
  private int day;
  private int year;
  private Gender gender;
  
  private String referSource;
  
  private boolean existingMember;

  /**
   * Default Constructor.
   */
  public SignupForm() {
  }

  /**
   * Constructor to create a sign up form with the data from the user object.
   * 
   * @param user
   *          - user
   */
  public SignupForm(User user, Company comp) {
    if (user != null) {
      firstName = user.getFirstName();
      lastName = user.getLastName();
      title = user.getTitle();
      email = user.getEmail();
      company = comp.getName();
      phone = user.getPhoneNumber();
      gender = user.getGender();
      DateTime dateTime = new DateTime(user.getDob());
      day = dateTime.getDayOfMonth();
      month = dateTime.getMonthOfYear();
      year = dateTime.getYear();
    }
  }

  public String getCompany() {
    return company;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public Gender getGender() {
    return gender;
  }

  public String getIndustry() {
    return industry;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPassword() {
    return password;
  }

  public String getPhone() {
    return phone;
  }

  public String getTitle() {
    return title;
  }

  /**
   * Gets the user from the information present in this form bean.
   * 
   * @return user instance
   */
  public User getUser() {
    User user = new User();
    user.setEmail(getEmail());
    user.setFirstName(getFirstName());
    user.setLastName(getLastName());
    user.setTitle(getTitle());
    user.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
    user.addRole(RoleType.User);
    return user;
  }

  /**
   * Gets the user from the information present in this form bean, additionally also sets the
   * company to the user.
   * 
   * @param companyId
   *          - the company name to set
   * @return user instance
   */
  public User getUserWithCompany(String companyId) {
    User user = getUser();
    user.setCompanyId(companyId);
    return user;
  }

  /**
   * Gets the user information from the form as well as the hiring user.
   * 
   * @param companyId
   *          - company Id
   * @param hiringUser
   *          - hiring user
   * @return the newly created user
   */
  public User getUserWithCompany(String companyId, HiringUser hiringUser) {
    User user = getUserWithCompany(companyId);
    // setting the address
    if (hiringUser != null) {
      user.setAddress(hiringUser.getAddress());
      user.setGender(hiringUser.getGender());
      user.setDob(hiringUser.getDob());
      // setting the analysis
      user.setAnalysis(hiringUser.getAnalysis());
    }
    return user;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public void setEmail(String email) {
    Optional.ofNullable(email).ifPresent(e -> this.email = StringUtils.trimWhitespace(e.toLowerCase()));
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public void setIndustry(String industry) {
    this.industry = industry;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getNumEmp() {
    return numEmp;
  }

  public void setNumEmp(int numEmp) {
    this.numEmp = numEmp;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPhone(String number) {
    this.phone = number;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Method to update the individual users details.
   * 
   * @param user
   *          - user to update
   * @param addressForm 
   *          - address of the user
   */
  public void updateIndividualUserDetails(User user, AddressForm addressForm) {
    user.setPhoneNumber(phone);
    user.setDob(LocalDate.of(year, month, day));
    user.setGender(gender);
    user.setAddress(addressForm.getAddress());
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

  public String getReferSource() {
    return referSource;
  }

  public void setReferSource(String referSource) {
    this.referSource = referSource;
  }

  public boolean isExistingMember() {
    return existingMember;
  }

  public void setExistingMember(boolean existingMember) {
    this.existingMember = existingMember;
  }
}
