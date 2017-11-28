package com.sp.web.dto;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.utils.ImageUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The base user DTO class.
 */
public class BaseUserDTO implements Serializable {

  private static final long serialVersionUID = -4301668841260163957L;

  private static final Logger log = Logger.getLogger(BaseUserDTO.class);

  private String id;
  private String firstName;
  private String lastName;
  private String title;
  private String email;
  private UserStatus userStatus;
  private String smallProfileImage;
  private String userInitials;
  
  private String fullNameOrEmail;
  
  /**
   * Default Constructor.
   */
  public BaseUserDTO() { }

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public BaseUserDTO(User user) {
    load(user);
  }

  /**
   * Load the given user details.
   * 
   * @param user
   *          - user
   */
  public void load(User user) {
    BeanUtils.copyProperties(user, this);
    addInitials();
    populateProfileImage(user);
  }

  public void updateInitialsAndProfileImage() {

  }

  private void addInitials() {
    if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
      userInitials = "" + firstName.charAt(0) + lastName.charAt(0);
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return - Get the first name.
   */
  public String getFirstName() {
    if (firstName == null || firstName.trim().length() == 0) {
      return "";
    }
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  /**
   * Get email or first name.
   * 
   * @return
   *      the first name if present else email
   */
  public String getFirstNameOrEmail() {
    if (StringUtils.isEmpty(firstName)) {
      return getEmail();
    } 
    return firstName;
  }
  
  /**
   * Get email or first name.
   * 
   * @return
   *      the first name if present else email
   */
  public String getFullNameOrEmail() {
    if (StringUtils.isEmpty(firstName)) {
      this.fullNameOrEmail = getEmail();
    } else { 
      this.fullNameOrEmail = firstName.concat(" " + lastName);
    }
    return fullNameOrEmail;
  }

  /**
   * @return - Get the last name.
   */
  public String getLastName() {
    if (lastName == null || lastName.trim().length() == 0) {
      return "";
    }
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

  public UserStatus getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
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

  /**
   * Method to get the name to display for the user.
   * 
   * @return - the name of the user
   */
  public String getName() {
    if (StringUtils.isBlank(firstName)) {
      return "";
    } else {
      return MessagesHelper.getMessage(Constants.KEY_NAME, firstName, lastName);
    }
  }

  /**
   * Gets the formatted user status to display.
   * 
   * @return user status string
   */
  public String getUserStatusFormatted() {
    try {
      if (userStatus != null) {
        return MessagesHelper.getMessage("userStatus." + userStatus);
      }
    } catch (Exception e) {
      log.warn("Could not get the user status !!!", e);
      return userStatus.toString();
    }
    return null;
  }

  /**
   * <code>populateProfileIMage</code> method gives the default profile image path.
   * 
   * @param id
   *          is the user id for which image is to stored.
   */
  private void populateProfileImage(User user) {
    if ((user != null && user.getProfileImage() != null) || userInitials == null) {
      this.smallProfileImage = ImageUtils.getUserImage(user);
    }
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BaseUserDTO other = (BaseUserDTO) obj;
    if (email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!email.equals(other.email)) {
      return false;
    }
    return true;
  }
}
