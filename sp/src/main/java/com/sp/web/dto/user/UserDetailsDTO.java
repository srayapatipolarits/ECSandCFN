package com.sp.web.dto.user;

import com.sp.web.dto.UserBadgeDTO;
import com.sp.web.model.Address;
import com.sp.web.model.User;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class that stores the details of the user for transport to other systems.
 * 
 */
public class UserDetailsDTO extends UserBadgeDTO {
  
  private static final long serialVersionUID = 5275561561167183399L;
  
  /** Address. */
  private Address address;
  
  /** Phone number. */
  private String phoneNumber;
  
  public UserDetailsDTO(User user) {
    super(user);
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
}
