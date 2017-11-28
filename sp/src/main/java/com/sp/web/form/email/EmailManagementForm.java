package com.sp.web.form.email;

import com.sp.web.controller.notifications.NotificationType;

import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The form envelop to create a new email management.
 */
public class EmailManagementForm {
  
  private String companyId;
  private NotificationType type;
  /*
   * Required Keys for a template (could change depending on different templates): 
   * - template
   * - subject
   * - emailHeader
   * - bodyContent
   * - linkText
   */
  private Map<String, String> content;
  private boolean companyAllowed;
  private boolean deleteAll;
  
  public NotificationType getType() {
    return type;
  }
  
  public void setType(NotificationType type) {
    this.type = type;
  }
  
  public Map<String, String> getContent() {
    return content;
  }
  
  public void setContent(Map<String, String> content) {
    this.content = content;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public boolean isCompanyAllowed() {
    return companyAllowed;
  }

  public void setCompanyAllowed(boolean companyAllowed) {
    this.companyAllowed = companyAllowed;
  }

  public boolean isDeleteAll() {
    return deleteAll;
  }

  public void setDeleteAll(boolean deleteAll) {
    this.deleteAll = deleteAll;
  }
  
}
