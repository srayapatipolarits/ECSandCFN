package com.sp.web.dto.hiring.user;

import com.sp.web.model.HiringUser;
import com.sp.web.service.hiring.role.HiringRoleFactory;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the details of the hiring archive user.
 */
public class HiringArchiveUserDTO extends HiringCandidateDTO {

  private static final long serialVersionUID = 2104750393380529928L;
  private boolean hired;

  public HiringArchiveUserDTO(HiringUser user, HiringRoleFactory roleFactory) {
    super(user, roleFactory);
  }

  public boolean isHired() {
    return hired;
  }

  public void setHired(boolean hired) {
    this.hired = hired;
  }
  
}
