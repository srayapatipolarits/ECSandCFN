package com.sp.web.repository.email;

import com.sp.web.model.email.EmailManagement;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The repository class for email management.
 */
public interface EmailManagementRepository {

  /**
   * Gets all the email management configurations from the DB.
   * 
   * @return
   *    list of email management 
   */
  List<EmailManagement> getAll();

  /**
   * Get the email management for the given company id.
   * 
   * @param companyId
   *            - the company id
   * @return
   *    the email management for the given company id
   */
  EmailManagement getForCompanyId(String companyId);

  /**
   * Save or update the email management object.
   * 
   * @param emailManagement
   *          - email management to save or update
   */
  void save(EmailManagement emailManagement);

  /**
   * Delete the email management.
   * 
   * @param emailManagement
   *          - email management to delete
   */
  void delete(EmailManagement emailManagement);

}
