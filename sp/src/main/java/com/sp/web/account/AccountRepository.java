package com.sp.web.account;

import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.AssistedAccount;
import com.sp.web.model.Company;
import com.sp.web.model.DeletedUser;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.demoaccount.DemoUser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AccountRepository interface manages the account for the users.
 * 
 * @author pradeep
 * 
 *         This is the repository interface to ensure that contract remains database agnostic.
 */
public interface AccountRepository {
  
  public Company findCompanyById(String companyId);
  
  /**
   * Find the company id's for the given list of company id's.
   * 
   * @param companyIds
   *          - company ids
   * @return the list of companies
   */
  public List<Company> findCompanyById(Set<String> companyIds);
  
  public Company createCompany(Company company);
  
  public Account createAccount(Account account);
  
  public PaymentInstrument createPaymentInstrument(PaymentInstrument paymentInstrument);
  
  public boolean removeAccount(Account account);
  
  public boolean removePaymentInstrument(PaymentInstrument paymentInstrument);
  
  public boolean removeCompany(Company company);
  
  /**
   * @param account
   *          - assisted account to create.
   */
  public AssistedAccount createAssistedAccount(AssistedAccount account);
  
  /**
   * Finds the account entity for the given company name.
   * 
   * @param companyId
   *          - company id
   * @return the account for the company
   */
  public Account findAccountByCompanyId(String companyId);
  
  /**
   * This method updates the given account.
   * 
   * @param accountToUpdate
   *          - account
   * @return the updated account
   */
  public Account updateAccount(Account accountToUpdate);
  
  /**
   * Gets all the users of the given company.
   * 
   * @param companyId
   *          - company id
   * @return list of users for the company
   */
  public List<User> getAllMembersForCompany(String companyId);
  
  /**
   * Search for users in the company with the given search string.
   * 
   * @param companyId
   *          - company name
   * @param searchString
   *          - the search query
   * @param fieldsToInclude
   *          - the fields to include in the response
   * @return the list of users matching the search criteria
   */
  public List<User> search(String companyId, String searchString, String[] fieldsToInclude);
  
  /**
   * Gets the payment instrument for the given payment instrument id.
   * 
   * @param paymentInstrumentId
   *          - the payment instrument id
   * @return - the payment instrument id
   */
  public PaymentInstrument findPaymentInstrumentById(String paymentInstrumentId);
  
  /**
   * Finds the company for the given company name but validates if the company is found or not.
   * 
   * @param companyId
   *          - the company id
   * @return the company
   */
  public Company findCompanyByIdValidated(String companyId);
  
  /**
   * Update the company information passed.
   * 
   * @param company
   *          - company to update
   * @return the updated company
   */
  public Company updateCompany(Company company);
  
  /**
   * Finds the company account for the given company id.
   * 
   * @param companyId
   *          - the company id
   * @return the account
   */
  public Account findValidatedAccountByCompanyId(String companyId);
  
  /**
   * Find the account with the given account id.
   * 
   * @param accountId
   *          - account id
   * @return the account for the given account id
   */
  public Account findValidatedAccountByAccountId(String accountId);
  
  /**
   * Updates the new payment instrument for the for the plan
   * 
   * @param spPlanspPlan
   *          - account to update
   * @param pi
   *          - payment instrument
   */
  void updatePaymentInstrument(SPPlan spPlan, PaymentInstrument pi);
  
  /**
   * Gets the company for the given account id.
   * 
   * @param accountId
   *          - account id
   * @return the company
   */
  Company getCompanyForAccount(String accountId);
  
  /**
   * Gets all the deleted users for the company.
   * 
   * @param companyId
   *          - the company
   * @return list of deleted users
   */
  List<DeletedUser> getAllDeletedMemberForCompany(String companyId);
  
  /**
   * Removes the given deleted user.
   * 
   * @param deletedUser
   *          - deleted user
   */
  void removeDeletedUser(DeletedUser deletedUser);
  
  /**
   * <code>createDemoAccount</code> method will create the demo account for the user.
   * 
   * @param demoUser
   *          demo account for the user.
   */
  public void createDemoAccount(DemoUser demoUser);
  
  /**
   * Get a list of all the companies.
   * 
   * @return list of companies
   */
  public List<Company> findAllCompanies();
  
  /**
   * @param accountType
   *          AccountType for which accounts to be retrieved.
   * @return the accounts
   */
  List<Account> getAllAccountForAccountType(AccountType accountType);
  
  /**
   * findCompanyByAccountId will return the companyId of the user.
   * 
   * @param accountId
   *          for which company is to be retrieved.
   * @return the company.
   */
  Company findCompanybyAccountId(String accountId);
  
  /**
   * <code>getAdminstratorUsersForAccount</code> method will return all the adminstrator user for
   * the account.
   * 
   * @param companyId
   *          for which administrator users are to be returned
   * @return the list of administrators.
   */
  List<User> getAdminstratorUsersForCompany(String companyId);
  
  /**
   * Get all the companies with the given account id.
   * 
   * @param accountIdList
   *          - account id list
   * @return the list of company
   */
  public List<Company> findCompanyByAccountId(List<String> accountIdList);
  
  /**
   * Get the order number from the sequence.
   * 
   * @return the order number
   */
  public String getNextAccountNumber();
  
  /**
   * <code>findAdminsForPlan</code> method will return the admin for plans passed to the system.
   * 
   * @param plansType
   *          set of plan types.
   * @param companyId
   *          for which admin users are to be found.
   * @return the list of Admin Users.
   */
  public Map<SPPlanType, List<User>> findAdminsForPlans(Set<SPPlanType> plansType, String companyId);
  
  /**
   * <code>findAdminsForPlan</code> method will return the admin for plans passed to the system.
   * 
   * @param plansType
   *          set of plan types.
   * @param companyId
   *          for which admin users are to be found.
   * @return the list of Admin Users.
   */
  public List<User> findAdminsForPlan(SPPlanType plansType, String companyId);
  
  /**
   * Get the list of companies with the features.
   * 
   * @param featureType
   *          - the feature type array
   * @return the list of companies
   */
  public List<Company> findCompaniesByFeature(SPFeature[] featureType);
  
}
