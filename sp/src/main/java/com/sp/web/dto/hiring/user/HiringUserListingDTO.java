package com.sp.web.dto.hiring.user;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the people analytics user listing.
 */
public class HiringUserListingDTO extends BaseUserDTO {

  private static final long serialVersionUID = -7184930340572366899L;
  private LocalDate createdOn;
  private LocalDateTime lastRemindedOn;
  private Set<RoleType> roles;
  private UserType type;
  private boolean inErti;

  /**
   * Constructor.
   * 
   * @param user
   *          - hiring user
   */
  public HiringUserListingDTO(HiringUser user) {
    super(user);
    if (user.getUserStatus() == UserStatus.VALID) {
      createdOn = Optional.ofNullable(user.getAnalysis().getCreatedOn())
          .map(LocalDateTime::toLocalDate).orElse(null);
    } else {
      setLastRemindedOn(user.getRemindedOn());
    }
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public Set<RoleType> getRoles() {
    return roles;
  }

  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
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

  public LocalDateTime getLastRemindedOn() {
    return lastRemindedOn;
  }

  public void setLastRemindedOn(LocalDateTime lastRemindedOn) {
    this.lastRemindedOn = lastRemindedOn;
  }
  
}
