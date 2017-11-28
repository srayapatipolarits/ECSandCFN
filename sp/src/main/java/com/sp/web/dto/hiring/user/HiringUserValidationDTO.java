package com.sp.web.dto.hiring.user;

import com.sp.web.model.HiringUser;

/**
 * @author Dax Abraham
 * 
 *         This is DTO class to send the validation information for the hiring candidates.
 */
public class HiringUserValidationDTO extends HiringUserDTO {

  private static final long serialVersionUID = 464945816912066190L;
  private HiringUserDTO existingCandidate;
  
  public HiringUserValidationDTO(HiringUser user) {
    super(user);
  }

  public HiringUserDTO getExistingCandidate() {
    return existingCandidate;
  }

  public void setExistingCandidate(HiringUserDTO existingCandidate) {
    this.existingCandidate = existingCandidate;
  }

}
