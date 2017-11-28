package com.sp.web.dto;

import com.sp.web.model.User;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The DTO class to display the different user sessions.
 */
public class UserSessionDTO extends BaseUserDTO {
  
  private List<String> sessionTimeList;
  
  /**
   * Default Constructor.
   * 
   * @param user
   *          - user
   */
  public UserSessionDTO(User user) {
    super(user);
  }
  
  public List<String> getSessionTimeList() {
    return sessionTimeList;
  }
  
  public void setSessionTimeList(List<String> sessionTimeList) {
    this.sessionTimeList = sessionTimeList;
  }
}
