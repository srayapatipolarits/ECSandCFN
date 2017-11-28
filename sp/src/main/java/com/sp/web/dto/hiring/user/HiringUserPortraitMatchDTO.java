package com.sp.web.dto.hiring.user;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.hiring.match.HiringPortraitMatchResultDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;

import java.time.LocalDateTime;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring user portrait match details.
 */
public class HiringUserPortraitMatchDTO extends BaseUserDTO {

  private static final long serialVersionUID = -5264814421833001523L;
  private HiringPortraitMatchResultDTO matchResult;
  private String phoneNumber;
  private LocalDateTime createdOn;
  private LocalDateTime lastRemindedOn;
  private UserType type;
  private boolean inErti;

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringUserPortraitMatchDTO(HiringUser user) {
    super(user);
    if (user.getUserStatus() == UserStatus.VALID) {
      createdOn = user.getAnalysis().getCreatedOn();
    } else {
      lastRemindedOn = user.getRemindedOn();
    }
  }

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param matchResult
   *          - match result
   */
  public HiringUserPortraitMatchDTO(HiringUser user, HiringPortraitMatchResultDTO matchResult) {
    this(user);
    this.matchResult = matchResult;
  }

  public HiringPortraitMatchResultDTO getMatchResult() {
    return matchResult;
  }

  public void setMatchResult(HiringPortraitMatchResultDTO matchResult) {
    this.matchResult = matchResult;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public LocalDateTime getLastRemindedOn() {
    return lastRemindedOn;
  }

  public void setLastRemindedOn(LocalDateTime lastRemindedOn) {
    this.lastRemindedOn = lastRemindedOn;
  }

  public UserType getType() {
    return type;
  }

  public void setType(UserType type) {
    this.type = type;
  }

  public boolean isInErti() {
    return inErti;
  }

  public void setInErti(boolean inErti) {
    this.inErti = inErti;
  }
  
}
