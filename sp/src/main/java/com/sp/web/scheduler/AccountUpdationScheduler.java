package com.sp.web.scheduler;

import com.sp.web.Constants;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.account.ExpiryAccountHelper;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.AccountType;
import com.sp.web.model.Company;
import com.sp.web.model.HiringUser;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.product.AccountFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dax Abraham
 * 
 *         This is the scheduler that periodically checks the accounts and processes various actions
 *         on the account like recharge, trial period validation, etc.
 */
@Component
public class AccountUpdationScheduler {
  
  private static final Logger LOG = Logger.getLogger(AccountUpdationScheduler.class);
  
  @Autowired
  AccountRechargeRepository accountRechargeRepository;
  
  @Autowired
  AccountFactory accountFactory;
  
  @Autowired
  Environment env;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  ExpiryAccountHelper expiryAccountHelper;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  CommunicationGateway emailGateway;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  /**
   * The scheduler method that will read the accounts and process the various statuses of the
   * account.
   */
  @Scheduled(cron = "${accountValidator.schedule}")
  public void processAccounts() {
    
    if (!GenericUtils.isJobServerNode(enviornment)) {
      return;
    }
    
    /** try to take a lock only by one thread at a time. */
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":The Account validator scheduller got called !!!");
    }
    
    Map<SPPlanType, List<Account>> accountListMap = accountRechargeRepository.getOverDueAccounts();
    
    Map<SPPlanType, List<Account>> accountToBeRecharged = accountRechargeRepository
        .getAllAccountsToBeRecharged();
    processPreRechargeNotification(accountToBeRecharged);
    
    List<Account> allTrialAccounts = accountRechargeRepository.getAllTrialAccounts();
    // accountList.addAll(accountRechargeRepository.getAllPaymentExpiredAccounts());
    Map<SPPlanType, List<Account>> allExpiredAccounts = accountRechargeRepository
        .getAllExpiredAccounts();
    
    /* add all the accounts in a common Map */
    if (accountListMap.get(SPPlanType.IntelligentHiring) != null) {
      accountListMap.get(SPPlanType.IntelligentHiring).addAll(
          allExpiredAccounts.get(SPPlanType.IntelligentHiring));
    }
    
    if (accountListMap.get(SPPlanType.Primary) != null) {
      accountListMap.get(SPPlanType.Primary).addAll(allExpiredAccounts.get(SPPlanType.Primary));
    }
    
    accountListMap.forEach((type, accs) -> {
      processRechargeAccounts(accs, type);
    });
    
    /** process tiral accounts */
    allTrialAccounts.forEach(ac -> {
      Map<SPPlanType, SPPlan> spPlanMap = ac.getSpPlanMap();
      spPlanMap.forEach((planType, plan) -> {
        processRechargeAccount(ac, plan);
      });
    });
    
  }
  
  /**
   * ProcessRecharegeAccount method will process the recharge of the account.
   * 
   * @param accountList
   *          is the list of accounts.
   * @param spPlanType
   *          is the plan type.
   */
  public void processRechargeAccounts(List<Account> accountList, SPPlanType spPlanType) {
    for (Account account : accountList) {
      SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
      if (spPlan == null) {
        return;
      }
      
      processRechargeAccount(account, spPlan);
    }
    accountList.stream().forEach(LOG::debug);
  }
  
  public void processRechargeAccount(Account account, SPPlan spPlan) {
    if (account.isDeactivated()) {
      return;
    }
    try {
      switch (account.getStatus()) {
      case VALID:
        // account is valid try to process payment
        switch (account.getType()) {
        case Business:
          processRecharge(account, spPlan);
          // processRecharge(account, businessProductList, promotionsMap);
          break;
        case Individual:
          // processRecharge(account, individualProductList, promotionsMap);
          // sendRechargeNotification(NotificationType.AccountRechargeSuccess, account);
          // break;
        default:
          LOG.warn("Do not know how to process account type :" + account.getType());
        }
        break;
      case EXPIRED:
        if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
            || StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
          if (spPlan.getNextPaymentDate() == null) {
            int gracePerid = env.getProperty(Constants.KEY_DEFAULT_PAYMENT_FAILURE_GRACE_TIME,
                Integer.class, Constants.DEFAULT_EXPIRY_GRACE_TIME_DAYS);
            int maxTimeAccountExp = env.getProperty(Constants.KEY_DEFAULT_ACCOUNT_EXPIRY_GRACE,
                Integer.class, Constants.DEFAULT_ACCOUNT_EXPIRY_DAYS);
            LocalDate expiresTime = LocalDate.fromDateFields(spPlan.getExpiresTime());
            LocalDate nextPaymemntDate = expiresTime.minusDays(gracePerid + maxTimeAccountExp);
            spPlan.setNextPaymentDate(nextPaymemntDate.toDate());
          }
          processRecharge(account, spPlan);
          sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
        } else {
          processExpired(account, spPlan);
        }
        
        break;
      case RENEWAL_PAYMENT_FAILED:
        if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
            || StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
          processRecharge(account, spPlan);
          sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
        } else {
          processRenewalPaymentFailed(account, spPlan);
        }
        
        break;
      case TRIAL:
        processTrial(account);
        break;
      case CANCEL:
        if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
            || StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
          processRecharge(account, spPlan);
          sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
        } else {
          
          /* removing the auto arcching in case of auto renwal is turned off */
          // expiryAccountHelper.expireAccount(account, spPlan);
        }
        break;
      default:
        LOG.fatal("Don't know how to process account status :" + account.getStatus());
      }
    } catch (Exception e) {
      if (account.getStatus() == AccountStatus.RENEWAL_PAYMENT_FAILED) {
        processRenewalPaymentFailed(account, spPlan);
      }
      LOG.fatal("Could not process account update request for :" + account.getId(), e);
    }
  }
  
  /**
   * <code>processRenewalPaymentFailed</code> method will process the renewal payment.
   * 
   * @param account
   *          for which renewal is to be happened.
   */
  private void processRenewalPaymentFailed(Account account, SPPlan spPlan) {
    int gracePerid = env.getProperty(Constants.KEY_DEFAULT_PAYMENT_FAILURE_GRACE_TIME,
        Integer.class, Constants.DEFAULT_EXPIRY_GRACE_TIME_DAYS);
    processTimeExpired(account, spPlan, spPlan.getNextPaymentDate(), gracePerid);
    if (spPlan.getPlanStatus() == PlanStatus.EXPIRED) {
      processExpired(account, spPlan);
    } else {
      int days = Days.daysBetween(LocalDate.fromDateFields(spPlan.getNextPaymentDate()),
          LocalDate.now()).getDays();
      Map<String, Object> emailParamMap = new HashMap<>();
      emailParamMap.put("daysLeft", gracePerid - days);
      
      sendRechargeNotification(NotificationType.AccountRenewalPaymentFailed, account,
          emailParamMap, spPlan);
    }
  }
  
  /**
   * Validate trial period expiry.
   * 
   * @param account
   *          - trial account
   */
  private void processTrial(Account account) {
    int days = Days.daysBetween(LocalDate.fromDateFields(account.getStartDate()), LocalDate.now())
        .getDays();
    int maxTime = env.getProperty(Constants.KEY_DEFAULT_TRIAL, Integer.class,
        Constants.DEFAULT_TRIAL_DAYS);
    if (days > maxTime) {
      // TODO: Process trial check
    }
  }
  
  /**
   * Archive the account post the grace period for the account expiry.
   * 
   * @param account
   *          - account to archive
   */
  private void processExpired(Account account, SPPlan spPlan) {
    int days = Days.daysBetween(LocalDate.now(), LocalDate.fromDateFields(spPlan.getExpiresTime()))
        .getDays();
    if (days <= 0) {
      // archiving the account
      // Stoping the archiving of the account and making them to block state so that even account
      // adminsitarot cannot loggin.
      spPlan.setPlanStatus(PlanStatus.BLOCKED);
      spPlan.setDeactivated(true);
      spPlan.setActive(false);
      SPPlanType otherPlanType = spPlan.getPlanType() == SPPlanType.IntelligentHiring ? SPPlanType.Primary
          : SPPlanType.IntelligentHiring;
      SPPlan otherPlan = account.getSpPlanMap().get(otherPlanType);
      Company company = accountRepository.findCompanybyAccountId(account.getId());
      Map<String, Object> emailParamMap = new HashMap<>();
      if (otherPlan == null || (otherPlan.getPlanStatus() == PlanStatus.BLOCKED)) {
        account.setStatus(AccountStatus.BLOCKED);
        account.setDeactivated(true);
        if (company != null) {
          company.setDeactivated(true);
          companyFactory.updateCompany(company);
          company.setErtiDeactivated(true);
          company.setPeopleAnalyticsDeactivated(true);
        } else {
          User individualUser = userRepository.findByAccountId(account.getId());
          individualUser.setDeactivated(true);
          userRepository.updateGenericUser(individualUser);
        }
        emailParamMap.put("accountBlocked", true);
      } else {
        List<User> users = userRepository.findUsers("companyId", company.getId());
        if (spPlan.getPlanType() == SPPlanType.IntelligentHiring) {
          company.setPeopleAnalyticsDeactivated(true);
          /* get all the erti members and remove the Hiring role from them. */
          users
              .stream()
              .filter(usr -> usr.getRoles().size() > 1)
              .forEach(
                  usr -> {
                    Arrays.stream(SPPlanType.IntelligentHiring.getFeatures())
                        .flatMap(feature -> Arrays.stream(feature.getRoles()))
                        .filter(r -> r.isAdminRole()).forEach(usr::removeRole);
                    userRepository.updateUser(usr);
                    HiringUser userByEmail = hiringUserFactory.getUserByEmail(usr.getEmail(), company.getId());
                    userByEmail.removeRole(RoleType.Hiring);
                    hiringUserFactory.updateUser(userByEmail);
                  });
          
        } else {
          company.setErtiDeactivated(true);
          /* get the hiring users for the company and disable the erti flag */
          List<HiringUser> all = hiringUserFactory.getAll(company.getId(), UserType.Member);
          all.stream().forEach(
              hu -> {
                if (hu.isInErti()) {
                  hu.setInErti(false);
                  hiringUserFactory.updateUser(hu);
                }
                
                Collection<? extends HiringUser> allArchivedUsers = hiringUserFactory
                    .getAllArchivedUsers(company.getId());
                allArchivedUsers.stream().forEach(ahu -> {
                  if (ahu.isInErti()) {
                    ahu.setInErti(false);
                    hiringUserFactory.updateUser(ahu);
                  }
                });
                
              });
          
          users
          .stream()
          .filter(usr -> usr.getRoles().size() > 1)
          .forEach(
              usr -> {
                Arrays.stream(SPPlanType.Primary.getFeatures())
                    .flatMap(feature -> Arrays.stream(feature.getRoles()))
                    .filter(r -> r.isAdminRole()).forEach(usr::removeRole);
                userRepository.updateUser(usr);
              });
          
        }
      }
      emailParamMap.put("accountBlocked", false);
      accountRepository.updateAccount(account);
      
      sendRechargeNotification(NotificationType.AccountExpiredAccount, account, emailParamMap,
          spPlan);
      
      // expiryAccountHelper.expireAccount(account);
    } else {
      Map<String, Object> emailParamsMap = new HashMap<>();
      emailParamsMap.put("daysLeft", days);
      sendRechargeNotification(NotificationType.AccountToBeExpiredAccount, account, emailParamsMap,
          spPlan);
    }
  }
  
  /**
   * Validates if the given account has passed the expiry.
   * 
   * @param account
   *          -account
   * @param startDate
   *          - the start date
   * @param maxTime
   *          - maximum time
   */
  private void processTimeExpired(Account account, SPPlan spPlan, Date startDate, int maxTime) {
    int days = Days.daysBetween(LocalDate.fromDateFields(startDate), LocalDate.now()).getDays();
    int maxTimeAccountExp = env.getProperty(Constants.KEY_DEFAULT_ACCOUNT_EXPIRY_GRACE,
        Integer.class, Constants.DEFAULT_ACCOUNT_EXPIRY_DAYS);
    String accountId = account.getId();
    if (days >= maxTime) {
      // expire the time
      spPlan.setPlanStatus(PlanStatus.EXPIRED);
      LocalDate nextPaymentDate = LocalDate.fromDateFields(startDate);
      LocalDate expireDate = nextPaymentDate.plusDays(maxTime + maxTimeAccountExp);
      spPlan.setExpiresTime(expireDate.toDate());
      // spPlan.setNextPaymentDate(null);
      SPPlanType otherPlanType = spPlan.getPlanType() == SPPlanType.IntelligentHiring ? SPPlanType.Primary
          : SPPlanType.IntelligentHiring;
      SPPlan otherPlan = account.getSpPlanMap().get(otherPlanType);
      if (otherPlan == null || (otherPlan.getPlanStatus() == PlanStatus.EXPIRED)) {
        account.setStatus(AccountStatus.EXPIRED);
      }
      if (account.getType() == AccountType.Business && spPlan.getPlanType() == SPPlanType.Primary) {
        // blocking all the users of the company
        Company company = accountRepository.getCompanyForAccount(accountId);
        if (company != null) {
          company.setBlockAllMembers(true);
          companyFactory.updateCompany(company);
        } else {
          LOG.fatal("Company not found for business account :" + accountId);
        }
      }
      
      accountRepository.updateAccount(account);
      
      LOG.warn("Account expired for account :" + accountId);
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Account id:" + accountId + ": days to expiry :" + (maxTime - days));
      }
    }
  }
  
  /**
   * Process the account recharge.
   * 
   * @param account
   *          - account
   * @param productList
   *          - product list
   * @param promotionsMap
   *          - promotions map
   * @return the success record
   */
  public List<PaymentRecord> processRecharge(Account account) {
    if (!account.getSpPlanMap().isEmpty()) {
      return accountFactory.processRecharge(account, true);
    } else {
      LOG.fatal("Could not recharge account :" + account.getId()
          + ": No valid products found for the account !!!");
    }
    return null;
  }
  
  public void processRecharge(Account account, SPPlan spPlan) {
    switch (spPlan.getPlanStatus()) {
    case ACTIVE:
      accountFactory.processRecharge(account, true, spPlan);
      sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
      break;
    case RENEWAL_PAYMENT_FAILED:
      if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
          || StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
        accountFactory.processRecharge(account, true, spPlan);
        sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
      } else {
        processRenewalPaymentFailed(account, spPlan);
      }
      break;
    
    case EXPIRED:
      if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
          || StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
        if (spPlan.getNextPaymentDate() == null) {
          int gracePerid = env.getProperty(Constants.KEY_DEFAULT_PAYMENT_FAILURE_GRACE_TIME,
              Integer.class, Constants.DEFAULT_EXPIRY_GRACE_TIME_DAYS);
          int maxTimeAccountExp = env.getProperty(Constants.KEY_DEFAULT_ACCOUNT_EXPIRY_GRACE,
              Integer.class, Constants.DEFAULT_ACCOUNT_EXPIRY_DAYS);
          LocalDate expiresTime = LocalDate.fromDateFields(spPlan.getExpiresTime());
          LocalDate nextPaymemntDate = expiresTime.minusDays(gracePerid + maxTimeAccountExp);
          spPlan.setNextPaymentDate(nextPaymemntDate.toDate());
        }
        accountFactory.processRecharge(account, true, spPlan);
        DateTime start = new DateTime(spPlan.getBillingCycleStartDate());
        spPlan.setExpiresTime(start.plusDays(
            Constants.DAYS_OF_MONTHLY_BILLING * 12 * spPlan.getAgreementTerm()).toDate());
        accountRepository.updateAccount(account);
        // blocking all the users of the company
        Company company = accountRepository.getCompanyForAccount(account.getId());
        if (company != null) {
          company.setBlockAllMembers(false);
          companyFactory.updateCompany(company);
        }
        
        sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
      } else {
        processExpired(account, spPlan);
      }
      break;
    case CANCEL:
      if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
          || StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
        accountFactory.processRecharge(account, true, spPlan);
        sendRechargeNotification(NotificationType.AccountRechargeSuccess, account, spPlan);
      } else {
        int expireDaysLeft = Days.daysBetween(LocalDate.now(),
            LocalDate.fromDateFields(spPlan.getExpiresTime())).getDays();
        if (expireDaysLeft < 0) {
          processExpired(account, spPlan);
        } else if (expireDaysLeft <= 5) {
          Map<String, Object> emailParamsMap = new HashMap<>();
          emailParamsMap.put("daysLeft", expireDaysLeft);
          sendRechargeNotification(NotificationType.AccountToBeExpiredAccount, account,
              emailParamsMap, spPlan);
          break;
        }
        
        int gracePerid = env.getProperty(Constants.KEY_DEFAULT_PAYMENT_FAILURE_GRACE_TIME,
            Integer.class, Constants.DEFAULT_EXPIRY_GRACE_TIME_DAYS);
        
        int days = Days.daysBetween(LocalDate.fromDateFields(spPlan.getNextPaymentDate()),
            LocalDate.now()).getDays();
        
        int maxTimeAccountExp = env.getProperty(Constants.KEY_DEFAULT_ACCOUNT_EXPIRY_GRACE,
            Integer.class, Constants.DEFAULT_ACCOUNT_EXPIRY_DAYS);
        
        if (days >= gracePerid) {
          // expire the time
          LocalDate nextPaymentDate = LocalDate.fromDateFields(spPlan.getNextPaymentDate());
          LocalDate expireDate = nextPaymentDate.plusDays(gracePerid + maxTimeAccountExp);
          spPlan.setExpiresTime(expireDate.toDate());
          // spPlan.setNextPaymentDate(null);
          SPPlanType otherPlanType = spPlan.getPlanType() == SPPlanType.IntelligentHiring ? SPPlanType.Primary
              : SPPlanType.IntelligentHiring;
          SPPlan otherPlan = account.getSpPlanMap().get(otherPlanType);
          if (otherPlan == null || (otherPlan.getPlanStatus() == PlanStatus.EXPIRED)) {
            account.setStatus(AccountStatus.EXPIRED);
          }
          if (account.getType() == AccountType.Business
              && spPlan.getPlanType() == SPPlanType.Primary) {
            // blocking all the users of the company
            Company company = accountRepository.getCompanyForAccount(account.getId());
            if (company != null) {
              company.setBlockAllMembers(true);
              companyFactory.updateCompany(company);
            } else {
              LOG.fatal("Company not found for business account :" + account.getId());
            }
          }
          accountRepository.updateAccount(account);
          
          LOG.warn("Account expired for account :" + account.getId());
        } else {
          Map<String, Object> emailParamMap = new HashMap<>();
          emailParamMap.put("daysLeft", gracePerid - days);
          
          sendRechargeNotification(NotificationType.AccountRenewalPaymentFailed, account,
              emailParamMap, spPlan);
        }
        
      }
      break;
    default:
      break;
    }
    
  }
  
  private void sendRechargeNotification(NotificationType notificationType, Account account,
      Map<String, Object> valueMap, SPPlan spPlan) {
    
    EmailParams emailParams = getRechargeEmailParams(notificationType, account, spPlan);
    if (emailParams != null) {
      if (valueMap != null) {
        emailParams.getValueMap().putAll(valueMap);
      }
      
      emailGateway.sendMessage(emailParams);
    }
    
  }
  
  private void sendRechargeNotification(NotificationType notificationType, Account account,
      SPPlan spPlan) {
    
    EmailParams emailParams = getRechargeEmailParams(notificationType, account, spPlan);
    // emailParams.addParam(Constants.PARAM_MEMBER, deletedUser);
    if (emailParams != null) {
      emailGateway.sendMessage(emailParams);
    }
    
  }
  
  /**
   * getRechargeEmailParams method will return the email params for the recharge notification
   * emails.
   * 
   * @param notificationType
   *          to be set.
   * @param account
   *          for the which notification is to be set.
   * @return the email parmas.
   */
  private EmailParams getRechargeEmailParams(NotificationType notificationType, Account account,
      SPPlan spPlan) {
    Company company = null;
    try {
      company = accountRepository.getCompanyForAccount(account.getId());
    } catch (Exception ex) {
      LOG.debug("Individual account, no company associated with the account");
    }
    if (company == null) {
      User user = userRepository.findByAccountId(account.getId());
      if (user == null) {
        LOG.error("No user or company found for account " + account.getId() + ", notificationType"
            + notificationType);
        return null;
      }
    }
    
    String name = company != null ? company.getName() : userRepository.findByAccountId(
        account.getId()).getEmail();
    String subject = MessagesHelper.getMessage("log.account." + notificationType + ".title", name,
        account.getAccountNumber(), MessagesHelper.getMessage("plan." + spPlan.getPlanType()));
    EmailParams emailParams = new EmailParams(notificationType.getTemplateName(), env.getProperty(
        Constants.BILLING_AR_EMAIL, Constants.DEFAULT_BILLING_AR_EMAIL), subject,
        Constants.DEFAULT_LOCALE);
    if (company != null) {
      if (spPlan.getPlanType() == SPPlanType.Primary) {
        emailParams.addParam(Constants.PARAM_MEMBER,
            accountFactory.getActiveMemberCount(account, SPPlanType.Primary)
                + account.getSpPlanMap().get(SPPlanType.Primary).getNumMember());
      }
      
      emailParams.addParam(Constants.PARAM_COMPANY, company);
      emailParams.addParam(Constants.PARAM_NAME, company.getName());
    }
    emailParams.addParam(Constants.PARAM_NOTIFICATION_TYPE, notificationType);
    emailParams.addParam(Constants.PARAM_CHARGEAMOUNT, account.getNextChargeAmount());
    
    emailParams.addParam(Constants.PARAM_ACCOUNT, account);
    emailParams.addParam("plan", spPlan);
    //
    return emailParams;
  }
  
  /**
   * Accouts to be recharged map.
   * 
   * @param accountToBeRecharged
   */
  private void processPreRechargeNotification(Map<SPPlanType, List<Account>> accountToBeRecharged) {
    
    for (Entry<SPPlanType, List<Account>> entry : accountToBeRecharged.entrySet()) {
      List<Account> value = entry.getValue();
      for (Account ac : value) {
        SPPlan spPlan = ac.getSpPlanMap().get(entry.getKey());
        
        if (spPlan.getCreditBalance() >= spPlan.getNextChargeAmount()
            || StringUtils.isNotEmpty((spPlan.getPreviousPaymentInstrumentId()))) {
          continue;
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("Sending preRecharge noticiation for account " + ac.getId());
        }
        
        // DateTime accountCreatedOn = new DateTime(ac.getNextPaymentDate());
        int days = Days.daysBetween(LocalDate.now(),
            LocalDate.fromDateFields(spPlan.getNextPaymentDate())).getDays();
        Map<String, Object> emailParamMap = new HashMap<>();
        emailParamMap.put("daysLeft", days);
        emailParamMap.put("planType", entry.getKey());
        emailParamMap.put("plan", spPlan);
        sendRechargeNotification(NotificationType.AccountPreRechargeNotification, ac,
            emailParamMap, spPlan);
      }
    }
    
  }
}
