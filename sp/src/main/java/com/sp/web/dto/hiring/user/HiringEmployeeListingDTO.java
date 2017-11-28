package com.sp.web.dto.hiring.user;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.hiring.group.HiringGroupBaseDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the employee listing.
 */
public class HiringEmployeeListingDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = 1274907107009813382L;
  private LocalDate createdOn;
  private LocalDate dateToShow;
  private LocalDateTime lastRemindedOn;
  private List<String> tagList;
  private Set<RoleType> roles;
  private List<HiringGroupBaseDTO> groups;
  private Set<String> hiringCoordinatorIds;
  private UserType type;
  private boolean inErti; 
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringEmployeeListingDTO(HiringUser user) {
    super(user);
    if (user.getUserStatus() == UserStatus.VALID) {
      setDateToShow(user.getAnalysis().getCreatedOn()
          .toLocalDate());
    } else {
      setDateToShow(user.getCreatedOn());
      lastRemindedOn = user.getRemindedOn();
    }
  }

  /**
   * Constructor.
   * 
   * @param user
   *        - user
   * @param userGroupsMap
   *        - user groups mapping
   */
  public HiringEmployeeListingDTO(HiringUser user, Map<String, List<HiringGroupBaseDTO>> userGroupsMap) {
    this(user);
    groups = userGroupsMap.get(user.getId());
  }

  public List<String> getTagList() {
    return tagList;
  }
  
  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
  }

  public LocalDate getDateToShow() {
    return dateToShow;
  }

  public void setDateToShow(LocalDate dateToShow) {
    this.dateToShow = dateToShow;
  }

  public Set<RoleType> getRoles() {
    return roles;
  }

  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
  }

  public List<HiringGroupBaseDTO> getGroups() {
    return groups;
  }

  public void setGroups(List<HiringGroupBaseDTO> groups) {
    this.groups = groups;
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public Set<String> getHiringCoordinatorIds() {
    return hiringCoordinatorIds;
  }

  public void setHiringCoordinatorIds(Set<String> hiringCoordinatorIds) {
    this.hiringCoordinatorIds = hiringCoordinatorIds;
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
