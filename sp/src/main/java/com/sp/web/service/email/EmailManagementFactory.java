package com.sp.web.service.email;

import com.sp.web.model.email.EmailManagement;
import com.sp.web.repository.email.EmailManagementRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The factory class to store all the email management services along with caching.
 */
@Component
public class EmailManagementFactory {

  @Autowired
  private EmailManagementRepository emailManagementRepository;
  
  public List<EmailManagement> getAll() {
    return emailManagementRepository.getAll();
  }

  /**
   * Get the email management for the given company id.
   * 
   * @param companyId
   *          - company id
   * @return
   *    the email management for the given company id.
   */
  @Cacheable(value = "emailManagement", key = "#companyId")
  public EmailManagement get(String companyId) {
    EmailManagement emailManagement = null;
    // getting the email management for the given company id
    if (!StringUtils.isEmpty(companyId)) {
      emailManagement = emailManagementRepository.getForCompanyId(companyId);
    } 
    
//    // if not found get the default email management
//    if (emailManagement == null) {
//      emailManagement = emailManagementRepository.getForCompanyId(Constants.DEFAULT_COMPANY_ID);
//    }
//    
    return emailManagement;
  }

  /**
   * Reset all the cache.
   */
  @CacheEvict(value = "emailManagement", allEntries = true)
  public void reset() {
    // resetting all the cahce entries
  }

  /**
   * Method to store/update the email management to the database.
   * 
   * @param emailManagement
   *              updating the email management
   */
  @CacheEvict(value = "emailManagement", key = "#emailManagement.companyId")
  public void put(EmailManagement emailManagement) {
    emailManagementRepository.save(emailManagement);
  }

  /**
   * Delete the email management from the DB.
   * 
   * @param emailManagement
   *          - delete email management from db
   */
  @CacheEvict(value = "emailManagement", key = "#emailManagement.companyId")
  public void delete(EmailManagement emailManagement) {
    emailManagementRepository.delete(emailManagement);
  }

  /**
   * This is the get method without caching.
   * 
   * @param companyId
   *          - company id
   * @return
   *    the email management
   */
  public EmailManagement getFromDB(String companyId) {
    return emailManagementRepository.getForCompanyId(companyId);
  }
  
}
