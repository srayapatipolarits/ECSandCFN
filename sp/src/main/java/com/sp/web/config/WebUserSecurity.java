package com.sp.web.config;

import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.utils.GenericUtils;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("webUserSecurity")
public class WebUserSecurity {
  
  /**
   * checkUserStatus method will check the whether user profile is complete is not.
   * 
   * @param authentication
   *          is the authentication of the user.
   * @return true if user is valid else false.
   */
  public boolean checkUserStatus(Authentication authentication) {
    
    User user = GenericUtils.getUserFromAuthentication(authentication);
    if (user.getUserStatus() == UserStatus.VALID) {
      return true;
    } else {
      return false;
    }
  }
}
