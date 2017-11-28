package com.sp.web.dto.blueprint;

import com.sp.web.model.blueprint.BlueprintApprover;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the approver.
 */
public class BlueprintApproverDTO {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String createdOnFormatted;
  private String approvedOnFormatted;
  
  /**
   * Constructor.
   * 
   * @param approver
   *          - approver
   */
  public BlueprintApproverDTO(BlueprintApprover approver) {
    BeanUtils.copyProperties(approver, this);
    createdOnFormatted = MessagesHelper.formatDate(approver.getCreatedOn());
    final LocalDateTime approvedOn = approver.getApprovedOn();
    if (approvedOn != null) {
      approvedOnFormatted = MessagesHelper.formatDate(approvedOn);
    }
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }

  public String getCreatedOnFormatted() {
    return createdOnFormatted;
  }

  public void setCreatedOnFormatted(String createdOnFormatted) {
    this.createdOnFormatted = createdOnFormatted;
  }

  public String getApprovedOnFormatted() {
    return approvedOnFormatted;
  }

  public void setApprovedOnFormatted(String approvedOnFormatted) {
    this.approvedOnFormatted = approvedOnFormatted;
  }
}
