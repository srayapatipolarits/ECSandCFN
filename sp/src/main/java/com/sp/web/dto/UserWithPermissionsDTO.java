package com.sp.web.dto;

import com.sp.web.model.User;
import com.sp.web.model.UserProfileSettings;

/**
 * @author Dax Abraham
 * 
 *         This is the DTO class with base user information and also the user permissions
 *         information.
 */
public class UserWithPermissionsDTO extends BaseUserDTO {

  /** The profile setting for individual users. */
  private UserProfileSettings profileSettings;
  
  public UserWithPermissionsDTO(User user) {
    super(user);
  }

  public UserProfileSettings getProfileSettings() {
    return profileSettings;
  }

  public void setProfileSettings(UserProfileSettings profileSettings) {
    this.profileSettings = profileSettings;
  }
  
}
