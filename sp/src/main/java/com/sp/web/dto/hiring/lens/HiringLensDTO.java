package com.sp.web.dto.hiring.lens;

import com.sp.web.dto.hiring.user.HiringUserBaseDTO;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;

import java.time.LocalDate;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class to store the details for the hiring lens.
 */
public class HiringLensDTO extends HiringUserBaseDTO {
  
  private static final long serialVersionUID = 6715457280603338173L;
  private UserStatus userStatus;
  private LocalDate createdOn;
  private Map<String, Object> lensResponse;
  private String referenceType;
  private String phoneNumber;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringLensDTO(User user) {
    super(user);
  }
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param lensResponse
   *          - lens response
   */
  public HiringLensDTO(User user, Map<String, Object> lensResponse) {
    this(user);
    this.setLensResponse(lensResponse);
  }
  
  public Map<String, Object> getLensResponse() {
    return lensResponse;
  }
  
  public void setLensResponse(Map<String, Object> lensResponse) {
    this.lensResponse = lensResponse;
  }
  
  public UserStatus getUserStatus() {
    return userStatus;
  }
  
  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
}
