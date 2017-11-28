package com.sp.web.dto.hiring.match;

import com.sp.web.dto.hiring.role.HiringRoleBaseDTO;
import com.sp.web.model.hiring.match.HiringPortrait;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring portrait listing for a given company.
 */
public class HiringPortraitMatchListingDTO extends HiringPortraitBaseDTO {

  private static final long serialVersionUID = -3394338815555664213L;
  private String description;
  private List<HiringRoleBaseDTO> roles;

  /**
   * Constructor.
   * 
   * @param portrait
   *          - portrait
   */
  public HiringPortraitMatchListingDTO(HiringPortrait portrait) {
    super(portrait);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<HiringRoleBaseDTO> getRoles() {
    return roles;
  }

  public void setRoles(List<HiringRoleBaseDTO> roles) {
    this.roles = roles;
  }

  /**
   * Add the roles and return the object.
   * 
   * @param roles
   *          - roles
   * @return
   *    the instance of the object
   */
  public HiringPortraitMatchListingDTO addRoles(List<HiringRoleBaseDTO> roles) {
    this.roles = roles;
    return this;
  }
  
}
