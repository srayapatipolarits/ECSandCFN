package com.sp.web.dto.hiring.user;

import com.sp.web.model.Address;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.UserStatus;
import com.sp.web.model.hiring.user.HiringComment;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring user employee.
 */
public class HiringEmployeeDTO extends HiringUserBaseDTO {
  
  private static final long serialVersionUID = 1430822327318149576L;
  private List<String> tagList;
  private Address address;
  private String phoneNumber;
  private Gender gender;
  private LocalDate dob;
  private List<HiringComment> comments;
  private UserStatus userStatus;
  private boolean inErti;
  private Set<RoleType> roles;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringEmployeeDTO(HiringUser user) {
    super(user);
  }
  
  public List<String> getTagList() {
    return tagList;
  }
  
  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
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
  
  public Gender getGender() {
    return gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public LocalDate getDob() {
    return dob;
  }
  
  public void setDob(LocalDate dob) {
    this.dob = dob;
  }
  
  public UserStatus getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }

  public List<HiringComment> getComments() {
    return comments;
  }

  public void setComments(List<HiringComment> comments) {
    this.comments = comments;
  }

  public boolean isInErti() {
    return inErti;
  }

  public void setInErti(boolean inErti) {
    this.inErti = inErti;
  }

  public Set<RoleType> getRoles() {
    return roles;
  }

  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
  }
  
}
