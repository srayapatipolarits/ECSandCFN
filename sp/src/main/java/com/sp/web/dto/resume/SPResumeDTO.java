package com.sp.web.dto.resume;

import com.sp.web.model.resume.SPResume;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

/**
 * SPResume DTO for resume.
 * 
 * @author pradeepruhil
 *
 */
public class SPResumeDTO {
  
  private String formattedDate;
  
  private String userRole;
  
  private String fileName;
  
  private String providerName;
  
  private String id;
  
  /**
   * Constructor.
   */
  public SPResumeDTO(SPResume resume) {
    BeanUtils.copyProperties(resume, this);
    this.providerName = resume.getProviderName();
    formattedDate = MessagesHelper.formatDate(resume.getDateCreated());
  }
  
  public String getFormattedDate() {
    return formattedDate;
  }
  
  public void setFormattedDate(String formattedDate) {
    this.formattedDate = formattedDate;
  }
  
  public String getUserRole() {
    return userRole;
  }
  
  public void setUserRole(String userRole) {
    this.userRole = userRole;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }
  
  public String getProviderName() {
    return providerName;
  }
  
}
