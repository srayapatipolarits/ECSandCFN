package com.sp.web.dto.user;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.User;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.ImageUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The DTO to hold the basic user information.
 */
public class UserMarkerDTO implements Serializable {
  
  private static final long serialVersionUID = 7839605977915829511L;
  private String id;
  private String firstName;
  private String lastName;
  private String title;
  private String email;
  private String smallProfileImage;
  private String userInitials;
  
  /**
   * Default Constructor.
   */
  public UserMarkerDTO() {
  }
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public UserMarkerDTO(User user) {
    load(user);
  }
  
  /**
   * Constructor from company.
   * 
   * @param company
   *          - company
   */
  public UserMarkerDTO(CompanyDao company) {
    this.firstName = company.getName();
  }
  
  /**
   * Load the given user details.
   * 
   * @param user
   *          - user
   */
  private void load(User user) {
    BeanUtils.copyProperties(user, this);
    if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
      userInitials = "" + firstName.charAt(0) + lastName.charAt(0);
    }
    if (user.getProfileImage() != null || userInitials == null) {
      this.smallProfileImage = ImageUtils.getUserImage(user);
    }
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
    this.email = email;
  }
  
  public String getSmallProfileImage() {
    return smallProfileImage;
  }
  
  public void setSmallProfileImage(String smallProfileImage) {
    this.smallProfileImage = smallProfileImage;
  }
  
  public String getUserInitials() {
    return userInitials;
  }
  
  public void setUserInitials(String userInitials) {
    this.userInitials = userInitials;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Method to get the name to display for the user.
   * 
   * @return - the name of the user
   */
  public String getName() {
    if (firstName == null) {
      return "";
    } else {
      return MessagesHelper.getMessage(Constants.KEY_NAME, firstName, (lastName != null) ? lastName
          : "");
    }
  }

  public String getFullNameOrEmail() {
    return (StringUtils.isBlank(firstName)) ? email : getName();
  }
  
  /**
   * Get a new instance for the user marker dto.
   * 
   * @return - new instance
   */
  public static UserMarkerDTO newInstance() {
    UserMarkerDTO user = new UserMarkerDTO();
    user.setId(GenericUtils.getId());
    return user;
  }

  public boolean sameEmail(String emailToCheck) {
    return email.equals(emailToCheck);
  }
  
}
