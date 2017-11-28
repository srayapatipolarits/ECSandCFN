package com.sp.web.dto.alternatebilling;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.User;

/**
 * <code>AccountAdminUserDTO</code> class is the base class for the account admin users. 
 * @author pradeepruhil
 *
 */
public class AccountAdminUserDTO extends BaseUserDTO {
  
  private String phoneNumber;
  
  public AccountAdminUserDTO() {
    
  }
  
  public AccountAdminUserDTO(User user) {
    super(user);
    this.phoneNumber = user.getPhoneNumber();
  }
  
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
  public String getPhoneNumber() {
    return phoneNumber;
  }
}
