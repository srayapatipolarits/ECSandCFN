package com.sp.web.dto;

import com.sp.web.model.User;
import com.sp.web.model.UserType;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 *
 *         The certificate user dto.
 */
public class CertificateUserDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = 3059146614719116074L;
  private String certificateNumber;
  private LocalDateTime createdOn;
  private LocalDateTime expiresOn;
  private UserType userType;

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public CertificateUserDTO(User user) {
   super(user);
   this.userType = user.getType();
  }

  public String getCertificateNumber() {
    return "SP" + certificateNumber;
  }

  public void setCertificateNumber(String certificateNumber) {
    this.certificateNumber = certificateNumber;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public LocalDateTime getExpiresOn() {
    return expiresOn;
  }

  public void setExpiresOn(LocalDateTime expiresOn) {
    this.expiresOn = expiresOn;
  }
  
  public void setUserType(UserType userType) {
    this.userType = userType;
  }  
  
  public UserType getUserType() {
    return userType;
  }
}
