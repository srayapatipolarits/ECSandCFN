package com.sp.web.dto;

import com.sp.web.model.FeedbackUser;

/**
 * @author Dax Abraham
 * 
 *         The DTO object for the feedback user entity.
 */
public class FeedbackUserDTO extends BaseUserDTO {

  private static final long serialVersionUID = 529602059898576132L;
  private String referenceType;
  /** Phone number. */
  private String phoneNumber;

  public FeedbackUserDTO(FeedbackUser referenceUser) {
    super(referenceUser);
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
