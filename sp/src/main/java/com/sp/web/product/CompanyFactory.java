package com.sp.web.product;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.Company;
import com.sp.web.model.MasterPassword;
import com.sp.web.model.SPFeature;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The factory class to help with product processing.
 */
@Component
public class CompanyFactory {
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  EventGateway eventGateway;
  
  @Autowired
  UserRepository userRepository;
  
  /**
   * Get the company dao object for the company.
   * 
   * @param companyId
   *          - company id
   * @return the company dao
   */
  @Cacheable(value = "company")
  public CompanyDao getCompany(String companyId) {
    Company company = accountRepository.findCompanyByIdValidated(companyId);
    return new CompanyDao(company);
  }
  
  /**
   * Update the given company.
   * 
   * @param company
   *          - company
   */
  @CacheEvict(value = "company", key = "#company.id")
  public void updateCompany(Company company) {
    Assert.notNull(company, "Company not found to update.");
    accountRepository.updateCompany(company);
  }
  
  /**
   * Update the given company from the dao.
   * 
   * @param company
   *          - company dao
   */
  @CacheEvict(value = "company", key = "#company.id")
  public void updateCompanyDao(CompanyDao company) {
    Assert.notNull(company, "Company not found to update.");
    accountRepository.updateCompany(company.getCompany());
  }
  
  /**
   * Method to update the users in session with the company permissions changes.
   * 
   * @param companyId
   *          - company id
   */
  public void updateUsersInSession(String companyId, ActionType actionType) {
    Map<String, Object> actionParams = new HashMap<String, Object>();
    actionParams.put(Constants.DO_LOGOUT, false);
    actionParams.put(Constants.PARAM_COMPANY, true);
    MessageEventRequest eventRequest = MessageEventRequest.newEvent(actionType, companyId,
        actionParams);
    eventRequest.setAllMembers(true);
    eventGateway.sendEvent(eventRequest);
    ;
    // // sessionManagementService.updateSessionForCompany(companyId,
    // UserUpdateAction.UpdatePermission,
    // actionParams);
  }
  
  /**
   * Get a list of all the companies in the system.
   * 
   * @return - list of companies
   */
  public List<Company> findAllCompanies() {
    return accountRepository.findAllCompanies();
  }
  
  /**
   * Get a list of companies that have the given list of features.
   * 
   * @param featureType
   *          - feature type
   * @return the list of companies
   */
  public List<Company> findCompaniesByFeature(SPFeature... featureType) {
    return accountRepository.findCompaniesByFeature(featureType);
  }
  
  /**
   * Add the given feature for a company.
   * 
   * @param companyId
   *          - company id
   * @param featureType
   *          - feature type to remove
   */
  @CacheEvict(value = "company", key = "#companyId")
  public void addFeature(String companyId, SPFeature featureType) {
    Company company = accountRepository.findCompanyByIdValidated(companyId);
    company.addFeature(featureType);
    accountRepository.updateCompany(company);
  }
  
  /**
   * Remove the given feature for a company.
   * 
   * @param companyId
   *          - company id
   * @param featureType
   *          - feature type to remove
   */
  @CacheEvict(value = "company", key = "#companyId")
  public void removeFeature(String companyId, SPFeature featureType) {
    Company company = accountRepository.findCompanyByIdValidated(companyId);
    company.removeFeature(featureType);
    accountRepository.updateCompany(company);
  }
  
  /**
   * Remove the given company from the DB.
   * 
   * @param company
   *          - company to remove
   */
  @CacheEvict(value = "company", key = "#company.id")
  public void removeCompany(Company company) {
    accountRepository.removeCompany(company);
  }
  
  /**
   * Cache the master password.
   * 
   * @return the master password.
   */
  @Cacheable(value = "masterPassword")
  public MasterPassword getMasterPassword() {
    return userRepository.getMasterPassword();
  }
  
  /**
   * update the master password.
   * 
   * @param masterPassword
   *          master password to be saved.
   */
  @CacheEvict(value = "masterPassword", allEntries = true)
  public void updateMasterPassword(MasterPassword masterPassword) {
    userRepository.updateMasterPassword(masterPassword);
  }
}
