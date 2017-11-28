package com.sp.web.dto.hiring.role;

import com.sp.web.dto.hiring.match.HiringPortraitBaseDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class for the hiring role listing.
 */
public class HiringRoleListingDTO extends HiringRoleBaseDTO {
  
  private static final long serialVersionUID = -1450621057810503070L;
  private String description;
  private HiringPortraitBaseDTO portrait;
  private LocalDateTime updatedOn;
  private UserMarkerDTO updatedBy;
  private int count;
  
  /**
   * Constructor.
   * 
   * @param role
   *          - role
   * @param userFactory
   *          - user factory
   * @param portraitFactory
   *          - portrait factory
   */
  public HiringRoleListingDTO(HiringRole role, HiringUserFactory userFactory,
      HiringPortraitMatchFactory portraitFactory) {
    super(role);
    List<HiringUser> users = userFactory.getUsersWithRole(role.getCompanyId(), role.getId());
    count = users.size();
    final String portraitId = role.getPortraitId();
    if (portraitId != null) {
      portrait = new HiringPortraitBaseDTO(portraitFactory.getPortrait(portraitId));
    }
  }

  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public HiringPortraitBaseDTO getPortrait() {
    return portrait;
  }
  
  public void setPortrait(HiringPortraitBaseDTO portrait) {
    this.portrait = portrait;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
}
