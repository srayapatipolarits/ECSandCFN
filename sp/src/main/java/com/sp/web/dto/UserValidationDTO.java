package com.sp.web.dto;

import com.sp.web.model.User;

/**
 * UserValidationDTO contains the stauts wheather the user is valid or not.
 * 
 * @author pradeepruhil
 *
 */
public class UserValidationDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = 3783720472873367492L;
  
  private boolean validUser;
  
  private String message;
  
  public UserValidationDTO(User usr) {
    super(usr);
  }
  
  public boolean isValidUser() {
    return validUser;
  }
  
  public void setValidUser(boolean validUser) {
    this.validUser = validUser;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public String getMessage() {
    return message;
  }
}
