package com.sp.web.controller.admin.account;

import com.sp.web.Constants;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.systemadmin.company.PartnerAccountFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.AccountDTO;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.PaymentInstrumentDTO;
import com.sp.web.dto.PaymentRecordDTO;
import com.sp.web.dto.alternatebilling.AccountAdminUserDTO;
import com.sp.web.dto.alternatebilling.SPPlanDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.CompanyForm;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.User;
import com.sp.web.model.UserProfileSettings;
import com.sp.web.model.account.BillingCycleType;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.DateTimeUtil;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class for all the functionality provided by the Account Controller.
 */
@Component
public class AccountControllerHelper {
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  AccountRechargeRepository accountRechargeRepository;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  private PartnerAccountFactory accountFactory;
  
  /**
   * The helper method to get the business account details.
   * 
   * @param user
   *          - the administrator user
   * @return the response to the get request
   */
  public SPResponse getBusinessAccountDetails(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    
    SPPlanType spPlanType = (SPPlanType) params[0];
    Assert.notNull(spPlanType, "SPPlanType cannot be null");
    
    // get the company account for the given user
    Account account = accountRepository.findValidatedAccountByCompanyId(user.getCompanyId());
    
    SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
    Assert.notNull(spPlan, "Plan is not subscribed by the the company");
    BusinessAccountDTO accountDTO = new BusinessAccountDTO(account);
    
    updateAccountDetails(user, resp, account, accountDTO, false, spPlan);
    
    return resp;
  }
  
  /**
   * Update the account information for the given account DTO.
   * 
   * @param user
   *          - user
   * @param resp
   *          - the response object
   * @param account
   *          - the account
   * @param accountDTO
   *          - account dto
   * @param spPlan is the spPlan.
   */
  private void updateAccountDetails(User user, SPResponse resp, Account account,
      AccountDTO accountDTO, boolean isEditAccount, SPPlan spPlan) {
    // // get the product info for the account
    // updateProductInfo(accountDTO, account, user.getCompanyId());
    
    updateSPPlanInfo(accountDTO, user.getCompanyId(), resp, isEditAccount, spPlan, account);
    
    // update the profile settings for the user if present
    if (user.getProfileSettings() != null) {
      resp.add(Constants.PARAM_USER_PROFILE_SETTINGS, user.getProfileSettings());
    }
    PartnerAccount partnerAccount = accountFactory.findPartnerAccountByCompany(user.getCompanyId());
    if (partnerAccount != null) {
      accountDTO.setPartnerId(partnerAccount.getPartnerId());
    }
    resp.add(Constants.PARAM_ACCOUNT, accountDTO);
  }
  
  /**
   * Helper method to get the individual user account details.
   * 
   * @param user
   *          - logged in user
   * @return the response of the get request
   */
  public SPResponse getIndividualAccountDetails(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the account for the user
    Account account = accountRepository.findValidatedAccountByAccountId(user.getAccountId());
    
    // create a new DTO object
    AccountDTO accountDTO = new AccountDTO(account);
    
    // update the rest of the information like products and payment information
    updateAccountDetails(user, resp, account, accountDTO, false,
        account.getPlan(SPPlanType.Primary));
    
    return resp;
  }
  
  /**
   * Update the block all user flag.
   * 
   * @param user
   *          - logged in user
   * @param args
   *          - the arguments for the block user request
   * @return the response to the block all user request
   */
  public SPResponse blockAllUsers(User user, Object[] args) {
    final SPResponse resp = new SPResponse();
    
    boolean isBlockAllMembers = (boolean) args[0];
    
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    
    // update the block all members flag
    company.setBlockAllMembers(isBlockAllMembers);
    companyFactory.updateCompany(company);
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Updates the company details for the given user.
   * 
   * @param user
   *          - logged in user
   * @param args
   *          - company details to update 0 : CompanyForm 1 : AddressForm
   * @return the response to the update request
   */
  public SPResponse updateCompany(User user, Object[] args) {
    final SPResponse resp = new SPResponse();
    
    // get the company form
    CompanyForm companyForm = (CompanyForm) args[0];
    
    // get the address form
    AddressForm addressForm = (AddressForm) args[1];
    
    // get the company object
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    
    // update the company details
    companyForm.updateCompany(company);
    
    // update the address details
    addressForm.update(company);
    
    // save to db
    companyFactory.updateCompanyDao(company);
    
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Cancel request for the users account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the cancel request
   */
  public SPResponse cancel(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    SPPlanType spPlanType = (SPPlanType) params[0];
    Assert.notNull(spPlanType, "SPPlanType cannot be null");
    // get the user account
    Account account = null;
    if (user.getAccountId() == null) {
      account = accountRepository.findValidatedAccountByCompanyId(user.getCompanyId());
    } else {
      account = accountRepository.findValidatedAccountByAccountId(user.getAccountId());
    }
    
    // process the cancel request
    // account.setStatus(AccountStatus.CANCEL);
    SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
    
    Assert.notNull(spPlan, "Plan not found cannot be null");
    if (spPlan != null) {
      spPlan.setPlanStatus(PlanStatus.CANCEL);
    }
    accountRepository.updateAccount(account);
    
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Reactivate a cancelled account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the re-activate request
   */
  public SPResponse reActivate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    SPPlanType spPlanType = (SPPlanType) params[0];
    Assert.notNull(spPlanType, "SPPlanType cannot be null");
    
    // get the user account
    Account account = null;
    if (user.getAccountId() == null) {
      account = accountRepository.findValidatedAccountByCompanyId(user.getCompanyId());
    } else {
      account = accountRepository.findValidatedAccountByAccountId(user.getAccountId());
    }
    
    SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
    if (spPlan != null) {
      // process the cancel request
      if (spPlan.getPlanStatus() != PlanStatus.CANCEL) {
        throw new InvalidRequestException("Account must be cancelled first. "
            + spPlan.getPlanType());
      }
      spPlan.setPlanStatus(PlanStatus.ACTIVE);
    }
    
    account.setStatus(AccountStatus.VALID);
    accountRepository.updateAccount(account);
    
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Get the company details.
   * 
   * @param user
   *          - the logged in user
   * @return the response to the get company details
   */
  public SPResponse getCompanyDetails(User user) {
    SPResponse resp = new SPResponse();
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    
    resp.add(Constants.PARAM_COMPANY, new CompanyDTO(company));
    
    return resp;
  }
  
  /**
   * Helper method to update the individual users profile.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - update params
   * @return the response to the update request
   */
  public SPResponse updateIndividualProfile(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the allow hiring access flag
    boolean isHiringAccessAllowed = (boolean) params[0];
    
    // get the allow 360 profile access flag
    boolean is360ProfileAccessAllowed = (boolean) params[1];
    
    // get the profile settings for the user
    UserProfileSettings profile = user.getProfileSettings();
    
    // set the values received in the request
    profile.setHiringAccessAllowed(isHiringAccessAllowed);
    profile.setIs360ProfileAccessAllowed(is360ProfileAccessAllowed);
    // update the database
    userRepository.updateUser(user);
    
    // set success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Helper method to update the certificate profile access flag.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse updateIndividualProfileSpCertificate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the allow certificate public profile view
    boolean isCertificateProfilePublicViewAllowed = (boolean) params[0];
    
    // get the profile settings for the user
    UserProfileSettings profile = user.getProfileSettings();
    
    profile.setCertificateProfilePublicView(isCertificateProfilePublicViewAllowed);
    
    // update the database
    userRepository.updateUser(user);
    
    // set success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Controller method to generate a new token for the individual profile settings.
   * 
   * @param user
   *          - logged in user
   * @return the response to the generate request
   */
  public SPResponse generateProfileToken(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the profile settings and generate the new token
    UserProfileSettings profileSettings = user.getProfileSettings();
    profileSettings.updateToken();
    userRepository.updateUser(user);
    // send the token back in response
    resp.add(Constants.PARAM_TOKEN, profileSettings.getToken());
    
    return resp;
  }
  
  /**
   * Controller method to generate a new token for the individual profile settings.
   * 
   * @param user
   *          - logged in user
   * @return the response to the generate request
   */
  public SPResponse restrictRelationshipAdvisor(User user, Object[] param) {
    boolean restrictAdvisor = (Boolean) param[0];
    
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    
    // set the flag.
    company.setRestrictRelationShipAdvisor(restrictAdvisor);
    
    companyFactory.updateCompanyDao(company);
    
    // send success
    SPResponse resp = new SPResponse();
    resp.isSuccess();
    return resp;
  }
  
  /**
   * Controller method to enable or disable share portrait.
   * 
   * @param user
   *          - logged in user
   * @return the response to the generate request
   */
  public SPResponse enableSharePortrait(User user, Object[] param) {
    boolean enableSharePortrait = (Boolean) param[0];
    
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    
    // set the flag.
    company.setSharePortrait(enableSharePortrait);
    
    companyFactory.updateCompanyDao(company);
    
    // send success
    SPResponse resp = new SPResponse();
    resp.isSuccess();
    return resp;
  }
  
  /**
   * <code>updateSPPlanInfo</code> method will update the plan infor
   * 
   * @param accountDTO
   *          accountDTO which to be updated with the plan information.
   * @param account
   *          account containing the plan information.
   * @param companyId
   *          of the company.
   * @param resp
   *          contains the admin user count for the plan present for the compamy.
   */
  public void updateSPPlanInfo(AccountDTO accountDTO, Account account, String companyId,
      SPResponse resp, boolean isEditAccount) {
    // Map<String, Object> productInfo = accountDTO.getProductInfo();
    Map<SPPlanType, List<User>> adminUsersMap = accountRepository.findAdminsForPlans(account
        .getSpPlanMap().keySet(), companyId);
    
    Map<SPPlanType, List<AccountAdminUserDTO>> adminUserDtoMap = new HashMap<>();
    Map<SPPlanType, Integer> adminUserCountMap = new HashMap<>();
    adminUsersMap.forEach((key, users) -> {
      List<AccountAdminUserDTO> adminUserDTOs = users.stream().map(AccountAdminUserDTO::new)
          .collect(Collectors.toList());
      adminUserDtoMap.put(key, adminUserDTOs);
      adminUserCountMap.put(key, users.size());
    });
    
    Map<SPPlanType, SPPlan> spPlanMap = account.getSpPlanMap();
    Map<SPPlanType, SPPlanDTO> spPlans = accountDTO.getSpPlans();
    spPlanMap.forEach((planType, plan) -> {
      if (plan != null) {
        SPPlanDTO spPlanDTO = spPlans.get(planType);
        int numberOfActiveMembers = 0;
        if (spPlanDTO == null) {
          if (plan.getAggreementEndDate() == null) {
            LocalDateTime dateTime = DateTimeUtil.getLocalDateTime(account.getStartDate());
            plan.setAggreementEndDate(dateTime.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
                * plan.getAgreementTerm()));
          }
          spPlanDTO = new SPPlanDTO(plan);
          spPlans.put(planType, spPlanDTO);
        }
        if (planType == SPPlanType.Primary) {
          numberOfActiveMembers = userRepository.getNumberOfActiveMembers(companyId, planType);
          int numberOfDeletedMembers = userRepository
              .getNumberOfDeletedMembers(companyId, planType);
          long numOfMembers = numberOfActiveMembers + numberOfDeletedMembers;
          spPlanDTO.setNumberOfMembers(numOfMembers);
          spPlanDTO.setNumActive(numberOfActiveMembers);
          spPlanDTO.setNumDeleted(numberOfDeletedMembers);
          spPlanDTO.setPlanName(plan.getName());
          
        }
        if (isEditAccount) {
          spPlanDTO.setAvailalbeMemberSubscriptions(plan.getNumMember() + numberOfActiveMembers);
          spPlanDTO.setAvailalbeAdminSubscriptions(plan.getNumAdmin());
          // - adminUserCountMap.get(planType));
      } else {
        spPlanDTO.setAvailalbeMemberSubscriptions(plan.getNumMember());
        spPlanDTO.setAvailalbeAdminSubscriptions(plan.getNumAdmin()
            - adminUserCountMap.get(planType));
      }
      spPlanDTO.getPlanInfo().put(Constants.PARAM_EXPIRES_TIME,
          MessagesHelper.formatDate(plan.getExpiresTime()));
      
      // add the next payment amount
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_TOTAL, plan.getNextChargeAmount());
      
      // set the next payment date
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_DATE,
          MessagesHelper.formatDate(plan.getNextPaymentDate()));
      
      // set the validity type
      spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE_TYPE,
          plan.getBillingCycle().getBillingCycleType());
      if (plan.getBillingCycle().getBillingCycleType() == BillingCycleType.Custom) {
        int noOfMonths = plan.getBillingCycle().getNoOfMonths();
        spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE, noOfMonths);
      }
      spPlanDTO.getPlanInfo().put(Constants.PARAM_EXPIRES_TIME,
          MessagesHelper.formatDate(plan.getExpiresTime()));
      
      // add the next payment amount
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_TOTAL, plan.getNextChargeAmount());
      
      // set the next payment date
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_DATE,
          MessagesHelper.formatDate(plan.getNextPaymentDate()));
      
      // set the validity type
      spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE_TYPE,
          plan.getBillingCycle().getBillingCycleType());
      if (plan.getBillingCycle().getBillingCycleType() == BillingCycleType.Custom) {
        int noOfMonths = plan.getBillingCycle().getNoOfMonths();
        spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE, noOfMonths);
      }
      
      // add the last payment details
      spPlanDTO.setLastPayment(new PaymentRecordDTO(accountRechargeRepository
          .findPaymentRecordById(plan.getLastPaymentId())));
      PaymentInstrument paymentInstrument = accountRepository.findPaymentInstrumentById(plan
          .getPaymentInstrumentId());
      // add the payment instrument if found
        if (paymentInstrument != null) {
          spPlanDTO.setPaymentInstrument(new PaymentInstrumentDTO(paymentInstrument, plan));
        }
        spPlanDTO.setUpdateDetails(account.getAccountUpdateDetailHistory().get(planType));
        spPlanDTO.setTagsKeywords(plan.getTagsKeywords());
      }
    });
    
    resp.add("planAdminUsers", adminUserDtoMap);
  }
  
  /**
   * <code>updateSPPlanInfo</code> method will update the plan infor
   * 
   * @param accountDTO
   *          accountDTO which to be updated with the plan information.
   * @param account
   *          account containing the plan information.
   * @param companyId
   *          of the company.
   * @param resp
   *          contains the admin user count for the plan present for the compamy.
   */
  public void updateSPPlanInfo(AccountDTO accountDTO, String companyId, SPResponse resp,
      boolean isEditAccount, SPPlan spPlan, Account account) {
    // Map<String, Object> productInfo = accountDTO.getProductInfo();
    HashSet<SPPlanType> hashSet = new HashSet<SPPlanType>();
    hashSet.add(spPlan.getPlanType());
    Map<SPPlanType, List<User>> adminUsersMap = accountRepository.findAdminsForPlans(hashSet,
        companyId);
    
    Map<SPPlanType, List<AccountAdminUserDTO>> adminUserDtoMap = new HashMap<>();
    Map<SPPlanType, Integer> adminUserCountMap = new HashMap<>();
    adminUsersMap.forEach((key, users) -> {
      List<AccountAdminUserDTO> adminUserDTOs = users.stream().map(AccountAdminUserDTO::new)
          .collect(Collectors.toList());
      adminUserDtoMap.put(key, adminUserDTOs);
      adminUserCountMap.put(key, users.size());
    });
    
    SPPlanDTO spPlanDTO = accountDTO.getSpPlans().get(spPlan.getPlanType());
    if (spPlan != null) {
      int numberOfActiveMembers = 0;
      if (spPlanDTO == null) {
        if (spPlan.getAggreementEndDate() == null) {
          LocalDateTime dateTime = DateTimeUtil.getLocalDateTime(account.getStartDate());
          spPlan.setAggreementEndDate(dateTime.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
              * spPlan.getAgreementTerm()));
        }
        spPlanDTO = new SPPlanDTO(spPlan);
        accountDTO.getSpPlans().put(spPlan.getPlanType(), spPlanDTO);
      }
      if (spPlan.getPlanType() == SPPlanType.Primary) {
        numberOfActiveMembers = userRepository.getNumberOfActiveMembers(companyId,
            spPlan.getPlanType());
        int numberOfDeletedMembers = userRepository.getNumberOfDeletedMembers(companyId,
            spPlan.getPlanType());
        long numOfMembers = numberOfActiveMembers + numberOfDeletedMembers;
        spPlanDTO.setNumberOfMembers(numOfMembers);
        spPlanDTO.setNumActive(numberOfActiveMembers);
        spPlanDTO.setNumDeleted(numberOfDeletedMembers);
        spPlanDTO.setPlanName(spPlan.getName());
        
      }
      if (isEditAccount) {
        spPlanDTO.setAvailalbeMemberSubscriptions(spPlan.getNumMember() + numberOfActiveMembers);
        spPlanDTO.setAvailalbeAdminSubscriptions(spPlan.getNumAdmin());
        // - adminUserCountMap.get(planType));
      } else {
        spPlanDTO.setAvailalbeMemberSubscriptions(spPlan.getNumMember());
        spPlanDTO.setAvailalbeAdminSubscriptions(spPlan.getNumAdmin()
            - adminUserCountMap.get(spPlan.getPlanType()));
        
      }
      spPlanDTO.getPlanInfo().put(Constants.PARAM_EXPIRES_TIME,
          MessagesHelper.formatDate(spPlan.getExpiresTime()));
      
      // add the next payment amount
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_TOTAL, spPlan.getNextChargeAmount());
      
      // set the next payment date
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_DATE,
          MessagesHelper.formatDate(spPlan.getNextPaymentDate()));
      
      // set the validity type
      spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE_TYPE,
          spPlan.getBillingCycle().getBillingCycleType());
      if (spPlan.getBillingCycle().getBillingCycleType() == BillingCycleType.Custom) {
        int noOfMonths = spPlan.getBillingCycle().getNoOfMonths();
        spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE, noOfMonths);
      }
      spPlanDTO.getPlanInfo().put(Constants.PARAM_EXPIRES_TIME,
          MessagesHelper.formatDate(spPlan.getExpiresTime()));
      
      // add the next payment amount
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_TOTAL, spPlan.getNextChargeAmount());
      
      // set the next payment date
      spPlanDTO.getPlanInfo().put(Constants.PARAM_RENEWAL_DATE,
          MessagesHelper.formatDate(spPlan.getNextPaymentDate()));
      
      // set the validity type
      spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE_TYPE,
          spPlan.getBillingCycle().getBillingCycleType());
      if (spPlan.getBillingCycle().getBillingCycleType() == BillingCycleType.Custom) {
        int noOfMonths = spPlan.getBillingCycle().getNoOfMonths();
        spPlanDTO.getPlanInfo().put(Constants.PARAM_PLAN_BILLING_CYCLE, noOfMonths);
      }
      
      PaymentInstrument paymentInstrument = accountRepository.findPaymentInstrumentById(spPlan
          .getPaymentInstrumentId());
      // add the payment instrument if found
      if (paymentInstrument != null) {
        spPlanDTO.setPaymentInstrument(new PaymentInstrumentDTO(paymentInstrument, spPlan));
      }
      
      // add the last payment details
      final String lastPaymentId = spPlan.getLastPaymentId();
      if (lastPaymentId != null) {
        spPlanDTO.setLastPayment(new PaymentRecordDTO(accountRechargeRepository
            .findPaymentRecordById(lastPaymentId)));
      }
      
      spPlanDTO.setUpdateDetails(account.getAccountUpdateDetailHistory().get(spPlan.getPlanType()));
    }
    
    resp.add("planAdminUsers", adminUserDtoMap);
  }
  
  /**
   * Method gets all the available subscriptions for the account.
   * 
   * @param user
   *          administrator
   * @return the response
   */
  public SPResponse getAvailableAccounts(User user, Object[] params) {
    Account account = accountRepository.findValidatedAccountByCompanyId(user.getCompanyId());
    SPPlanType spPlanType = (SPPlanType) params[0];
    
    if (account.getSpPlanMap().get(spPlanType) != null) {
      
      return new SPResponse().add(Constants.PARAM_AVAILABLE_MEMBER_SUBSCRIPTIONS, account
          .getSpPlanMap().get(spPlanType).getNumMember());
    } else {
      throw new InvalidRequestException("No Plan subscribed by the company ");
    }
    
  }
}
