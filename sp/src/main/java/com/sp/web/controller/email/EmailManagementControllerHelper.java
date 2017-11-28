package com.sp.web.controller.email;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.NotificationTypeDTO;
import com.sp.web.dto.email.EmailManagementDTO;
import com.sp.web.form.email.EmailManagementForm;
import com.sp.web.model.Company;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.email.EmailManagement;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.email.EmailManagementFactory;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The controller helper for email management.
 */
@Component
public class EmailManagementControllerHelper {
  
  @Autowired
  private EmailManagementFactory emailManagementFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * Helper method to get the list of companies that do not have 
   * email override.
   * 
   * @param user
   *        - logged in user
   * @return
   *    the list of companies
   */
  public SPResponse getCompanies(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory.findAllCompanies();
    List<String> existingCompanyIdList = emailManagementFactory.getAll().stream()
        .map(EmailManagement::getCompanyId).collect(Collectors.toList());
    return resp.add(
        "companies",
        findAllCompanies
            .stream()
            .filter(
                c -> !c.getId().equalsIgnoreCase(Constants.DEFAULT_COMPANY_ID)
                    && !existingCompanyIdList.contains(c.getId())).map(CompanyDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Get the list of email templates configured.
   * 
   * @param user
   *          - logged in user
   * @return
   *    the list of email templates
   */
  public SPResponse getAll(User user) {
    final SPResponse resp = new SPResponse();
    List<EmailManagement> emailManagementList = emailManagementFactory.getAll();
    return resp.add(
        Constants.PARAM_EMAIL_MANAGEMENT,
        emailManagementList.stream()
            .filter(em -> !em.getCompanyId().equalsIgnoreCase(Constants.DEFAULT_COMPANY_ID))
            .map(this::getSummaryEmailDTO).collect(Collectors.toList()));
  }
  
  /**
   * Helper method to create the email management DTO.
   * 
   * @param emailManagement
   *          - email management
   * @return
   *      the email management DTO
   */
  private EmailManagementDTO getEmailDTO(EmailManagement emailManagement) {
    return new EmailManagementDTO(emailManagement, companyFactory);
  }

  /**
   * Helper method to create the email management DTO.
   * 
   * @param emailManagement
   *          - email management
   * @return
   *      the email management DTO
   */
  private EmailManagementDTO getSummaryEmailDTO(EmailManagement emailManagement) {
    return new EmailManagementDTO(emailManagement, companyFactory, false);
  }
  
  /**
   * The helper method to get the email management for a given company.
   * 
   * @param user
   *          - logged in user
   * @return
   *    the email management
   */
  public SPResponse get(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String companyId = user.getCompanyId();
    if (user.hasRole(RoleType.SuperAdministrator) || user.hasRole(RoleType.SysEmails)) {
      String reqCompanyId = (String) params[0];
      if (!StringUtils.isEmpty(reqCompanyId)) {
        companyId = reqCompanyId;
      }
    }
    EmailManagement emailManagement = emailManagementFactory.getFromDB(companyId);
    EmailManagementDTO emailManagementDTO = new EmailManagementDTO();
    if (emailManagement == null) {
      // sending only the company data
      emailManagementDTO.setCompany(new CompanyDTO(companyFactory.getCompany(companyId)));
      resp.add(Constants.PARAM_EMAIL_MANAGEMENT, emailManagementDTO);
    } else {
      resp.add(Constants.PARAM_EMAIL_MANAGEMENT, getEmailDTO(emailManagement));
    }
    return resp;
  }
  
  /**
   * Helper method to get the list of notifications in the system.
   * 
   * @param user
   *          - logged in user
   * @return
   *    the list of notifications
   */
  public SPResponse getNotifications(User user) {
    return new SPResponse().add(
        Constants.PARAM_NOTIFICATION_LIST,
        Arrays.stream(NotificationType.values()).collect(
            Collectors.mapping(NotificationTypeDTO::new, Collectors.toList())));
  }

  /**
   * Helper method to create or update the email content.
   * 
   * @param user  
   *          - logged in user
   * @param params
   *          - params
   * @return
   *      the response to the create or update request
   */
  public SPResponse createUpdate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    EmailManagementForm form = (EmailManagementForm) params[0];
    
    EmailManagement emailManagement = getEmailManagement(user, form);
    
    Map<NotificationType, Map<String, String>> contentMap = emailManagement.getContent();
    // check if the type exists
    final NotificationType type = form.getType();
    Assert.notNull(type, "Type is required.");
    final Map<String, String> content = form.getContent();
    Assert.notNull(content, "Content is required.");
    // removing the line feed in the email body content
    content.put("emailBody", StringUtils.replace(content.get("emailBody"), "\n", ""));
    // update the data for the given type
    contentMap.put(type, content);
    // update the database and cache
    emailManagementFactory.put(emailManagement);
    
    return resp.isSuccess();
  }

  private EmailManagement getEmailManagement(User user, EmailManagementForm form) {
    String companyId = form.getCompanyId();
    if (!StringUtils.isEmpty(companyId)) {
      // validate if the user has system admin privileges
      Assert.isTrue(user.hasRole(RoleType.SuperAdministrator) || user.hasRole(RoleType.SysEmails), "User does not have permissions.");
    } else {
      companyId = user.getCompanyId();
    }
    
    // check if the data override already exists
    // else create a new one
    EmailManagement emailManagement = emailManagementFactory.getFromDB(companyId);
    if (emailManagement == null) {
      // creating a new one for the company
      emailManagement = new EmailManagement();
      emailManagement.setCompanyId(companyId);
      emailManagement.setContent(new HashMap<NotificationType, Map<String,String>>());
    }
    return emailManagement;
  }
  
  /**
   * Helper method to delete the email management.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for delete
   * @return
   *    the response to the delete
   */
  public SPResponse delete(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    EmailManagementForm form = (EmailManagementForm) params[0];
    
    EmailManagement emailManagement = getEmailManagement(user, form);
    
    if (form.isDeleteAll()) {
      // deleting the entire email management object
      emailManagementFactory.delete(emailManagement);
      // removing the email management from the company
      removeEmailManagement(emailManagement.getCompanyId());
    } else {
      Map<NotificationType, Map<String, String>> contentMap = emailManagement.getContent();
      // check if the type exists
      final NotificationType type = form.getType();
      Assert.notNull(type, "Type is required.");
      contentMap.remove(type);
      
      // remove or update the email management if it is empty
      if (contentMap.isEmpty()) {
        emailManagementFactory.delete(emailManagement);
      } else {
        emailManagementFactory.put(emailManagement);
      }
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to enable disable company access to email management.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for request
   * @return
   *    the status of the update request
   */
  public SPResponse companyPermission(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    EmailManagementForm form = (EmailManagementForm) params[0];
    
    // add the role to the company
    final String companyId = form.getCompanyId();
    Assert.hasText(companyId, "Company id is required.");

// Deleting this check as the request is to provide aspect
// even if there is no email overridden.
//    EmailManagement emailManagement = emailManagementFactory.get(companyId);
//    if (emailManagement == null
//        || emailManagement.getCompanyId().equals(Constants.DEFAULT_COMPANY_ID)) {
//      throw new InvalidRequestException("Email management not set for company.");
//    }
    
    if (form.isCompanyAllowed()) {
      companyFactory.addFeature(companyId, SPFeature.EmailManagement);
      // add the role to all the administrators of the company
      userFactory.addRoleToAdmins(companyId, RoleType.EmailManagement);
    } else {
      removeEmailManagement(companyId);
    }
    return resp.isSuccess();
  }

  /**
   * Removing the email management from the company as well as the roles from the admins.
   * 
   * @param companyId
   *          - company id
   */
  private void removeEmailManagement(final String companyId) {
    companyFactory.removeFeature(companyId, SPFeature.EmailManagement);
    userFactory.removeRoleFromAdmin(companyId, RoleType.EmailManagement);
  }
  
}
