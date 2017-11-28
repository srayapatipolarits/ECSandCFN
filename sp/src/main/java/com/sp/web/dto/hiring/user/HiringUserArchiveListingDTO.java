package com.sp.web.dto.hiring.user;

import com.sp.web.model.HiringUser;
import com.sp.web.model.UserType;
import com.sp.web.service.hiring.role.HiringRoleFactory;

public class HiringUserArchiveListingDTO extends HiringCandidateListingDTO {

  private static final long serialVersionUID = -4759737731322433771L;
  private UserType type;
  private boolean hired;
  private String phoneNumber;

  /**
   * Constructor.
   * 
   * @param user
   *        - user
   * @param roleFactory
   *        - role factory
   */
  public HiringUserArchiveListingDTO(HiringUser user, HiringRoleFactory roleFactory) {
    super(user, roleFactory);
  }

  public UserType getType() {
    return type;
  }

  public void setType(UserType type) {
    this.type = type;
  }

  public boolean isHired() {
    return hired;
  }

  public void setHired(boolean hired) {
    this.hired = hired;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
}
