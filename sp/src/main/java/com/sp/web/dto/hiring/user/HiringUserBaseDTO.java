package com.sp.web.dto.hiring.user;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO for the hiring user basic information.
 */
public class HiringUserBaseDTO implements Serializable {
  
  private static final long serialVersionUID = 1397465669955702607L;
  private String id;
  private String firstName;
  private String lastName;
  private String title;
  private String email;
  private String userInitials;
  private UserType type;

  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringUserBaseDTO(User user) {
    load(user);
  }

  private void load(User user) {
    BeanUtils.copyProperties(user, this);
    if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
      userInitials = "" + firstName.charAt(0) + lastName.charAt(0);
    }
  }


  public UserType getType() {
    return type;
  }
  
  public void setType(UserType type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
    if (firstName == null) {
      return "";
    } else {
      return MessagesHelper.getMessage(Constants.KEY_NAME, firstName, lastName);
    }
  }
}
