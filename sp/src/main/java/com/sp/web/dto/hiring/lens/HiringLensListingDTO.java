package com.sp.web.dto.hiring.lens;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.dto.hiring.user.HiringUserBaseDTO;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for hiring lens listing.
 */
public class HiringLensListingDTO extends HiringUserBaseDTO {
  
  private static final long serialVersionUID = 4416602658622651342L;
  
  private UserStatus userStatus;
  private LocalDate createdOn;
  private LocalDate completedOn;
  private LocalDateTime lastRemindedOn;
  private String referenceType;
  private String phoneNumber;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringLensListingDTO(FeedbackUser user) {
    super(user);
    final AnalysisBean analysis = user.getAnalysis();
    if (analysis != null) {
      completedOn = analysis.getCreatedOn().toLocalDate();
    } else {
      lastRemindedOn = user.getRemindedOn();
    }
  }
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDate getCompletedOn() {
    return completedOn;
  }
  
  public void setCompletedOn(LocalDate completedOn) {
    this.completedOn = completedOn;
  }

  public UserStatus getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
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

  public LocalDateTime getLastRemindedOn() {
    return lastRemindedOn;
  }

  public void setLastRemindedOn(LocalDateTime lastRemindedOn) {
    this.lastRemindedOn = lastRemindedOn;
  }
  
}
