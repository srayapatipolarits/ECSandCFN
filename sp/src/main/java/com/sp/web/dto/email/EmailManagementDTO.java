package com.sp.web.dto.email;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.NotificationTypeDTO;
import com.sp.web.model.SPFeature;
import com.sp.web.model.email.EmailManagement;
import com.sp.web.product.CompanyFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for email management.
 */
public class EmailManagementDTO {
  
  private String id;
  private CompanyDTO company;
  private boolean companyAllowed;
  private Map<NotificationType, Map<String, String>> content = null;
  private Map<NotificationType, String> notificationTypeMap;
  
  /**
   * Constructor.
   * 
   * @param emailManagement
   *            - email management
   * @param companyFactory
   *            - company factory
   *            
   */
  public EmailManagementDTO(EmailManagement emailManagement, CompanyFactory companyFactory) {
    this(emailManagement, companyFactory, false);
  }

  /**
   * Constructor for email management.
   * 
   * @param emailManagement
   *            - email management
   * @param companyFactory
   *            - company factory
   * @param isSummary
   *            - is summary email
   */
  public EmailManagementDTO(EmailManagement emailManagement, CompanyFactory companyFactory,
      boolean isSummary) {
    if (!isSummary) {
      BeanUtils.copyProperties(emailManagement, this);
      if (!CollectionUtils.isEmpty(content)) {
        notificationTypeMap = content
            .keySet()
            .stream()
            .collect(
                Collectors.mapping(NotificationTypeDTO::new,
                    Collectors.toMap(NotificationTypeDTO::getType, NotificationTypeDTO::getName)));
      }
    }
    final String companyId = emailManagement.getCompanyId();
    if (!companyId.equals(Constants.DEFAULT_COMPANY_ID)) {
      this.company = new CompanyDTO(companyFactory.getCompany(companyId));
      this.companyAllowed = company.hasFeature(SPFeature.EmailManagement);
    }
    
  }

  /**
   * Default Constructor.
   */
  public EmailManagementDTO() {
    super();
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public CompanyDTO getCompany() {
    return company;
  }
  
  public void setCompany(CompanyDTO company) {
    this.company = company;
  }
  
  public Map<NotificationType, Map<String, String>> getContent() {
    return content;
  }
  
  public void setContent(Map<NotificationType, Map<String, String>> content) {
    this.content = content;
  }

  public boolean isCompanyAllowed() {
    return companyAllowed;
  }

  public void setCompanyAllowed(boolean companyAllowed) {
    this.companyAllowed = companyAllowed;
  }

  public Map<NotificationType, String> getNotificationTypeMap() {
    return notificationTypeMap;
  }

  public void setNotificationTypeMap(Map<NotificationType, String> notificationTypeMap) {
    this.notificationTypeMap = notificationTypeMap;
  }
  
}
