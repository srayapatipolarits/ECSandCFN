package com.sp.web.dto.hiring.role;

import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.match.HiringPortraitDetailsDTO;
import com.sp.web.dto.hiring.user.HiringUserPrismAndMatchDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO for the hiring role details.
 */
public class HiringRoleDTO extends HiringRoleBaseDTO {
  
  private static final long serialVersionUID = -7128656907341798970L;
  private String description;
  private HiringPortraitDetailsDTO portrait;
  private List<HiringUserPrismAndMatchDTO> users;
  private LocalDateTime updatedOn;
  private UserMarkerDTO updatedBy;
  
  /**
   * Constructor.
   * 
   * @param role
   *          - hiring role
   * @param portraitFactory
   *          - portrait factory
   */
  public HiringRoleDTO(HiringRole role, HiringPortraitMatchFactory portraitFactory) {
    super(role);
    Optional.ofNullable(role.getPortraitId()).map(portraitFactory::getPortrait)
        .ifPresent(p -> this.setPortrait(new HiringPortraitDetailsDTO(p)));
  }
  
  /**
   * Constructor.
   * 
   * @param role
   *          - role
   * @param portrait
   *          - portrait
   */
  public HiringRoleDTO(HiringRole role, HiringPortraitDao portrait) {
    super(role);
    if (portrait != null) {
      this.setPortrait(new HiringPortraitDetailsDTO(portrait));
    }
  }
  
  /**
   * Constructor.
   * 
   * @param role
   *          - role
   */
  public HiringRoleDTO(HiringRole role) {
    super(role);
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public List<HiringUserPrismAndMatchDTO> getUsers() {
    return users;
  }
  
  public void setUsers(List<HiringUserPrismAndMatchDTO> users) {
    this.users = users;
  }
  
  public HiringPortraitDetailsDTO getPortrait() {
    return portrait;
  }
  
  public HiringPortraitDetailsDTO setPortrait(HiringPortraitDetailsDTO portrait) {
    this.portrait = portrait;
    return portrait;
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
}
