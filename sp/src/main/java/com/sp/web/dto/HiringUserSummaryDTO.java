package com.sp.web.dto;

import com.sp.web.model.HiringUser;

import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the hiring candidates.
 */
public class HiringUserSummaryDTO extends BaseUserDTO {
  
  /* The hiring roles for the candidate */
  private Set<String> hiringRoles;
  /* Candidate public URL */

  /**
   * Constructor.
   * 
   * @param user
   *          - hiring user
   */
  public HiringUserSummaryDTO(HiringUser user) {
    super(user);
  }

  public Set<String> getHiringRoles() {
    return hiringRoles;
  }


  public void setHiringRoles(Set<String> hiringRoles) {
    this.hiringRoles = hiringRoles;
  }
}
