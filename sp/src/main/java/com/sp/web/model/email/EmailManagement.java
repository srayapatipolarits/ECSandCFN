package com.sp.web.model.email;

import com.sp.web.controller.notifications.NotificationType;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the email management data.
 */
public class EmailManagement implements Serializable {
  
  private static final long serialVersionUID = 8717548145547520646L;
  
  private String id;
  private String companyId;
  private Map<NotificationType, Map<String, String>> content;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public Map<NotificationType, Map<String, String>> getContent() {
    return content;
  }

  public void setContent(Map<NotificationType, Map<String, String>> content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "EmailManagement [id=" + id + ", companyId=" + companyId + ", content=" + content + "]";
  }
  
  
}
