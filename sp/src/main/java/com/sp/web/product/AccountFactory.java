package com.sp.web.product;

import com.sp.web.Constants;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.account.plan.AccountHelper;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.PaymentGatewayException;
import com.sp.web.exception.PaymentProcessingException;
import com.sp.web.exception.SPException;
import com.sp.web.form.SPPlanForm;
import com.sp.web.form.SignupAccountForm;
import com.sp.web.form.SignupForm;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.AccountType;
import com.sp.web.model.Address;
import com.sp.web.model.Company;
import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.HiringUser;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.PaymentType;
import com.sp.web.model.Product;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.payment.PaymentReasonType;
import com.sp.web.payment.PaymentGatewayFactory;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;
import com.sp.web.promotions.PromotionsFactory;
import com.sp.web.repository.payment.PaymentInstrumentRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.stringtemplate.StringTemplate;
import com.sp.web.service.stringtemplate.StringTemplateFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.DateTimeUtil;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dax Abraham The factory class to help with product processing.
 */
@Component
public class AccountFactory {
  
  private static final String PRODUCT_HELPER_SUFFIX = "ProductHelper";
  
  private static final Logger LOG = Logger.getLogger(AccountFactory.class);
  
  @Autowired
  PaymentInstrumentRepository paymentInstrumentRepository;
  
  @Autowired
  PromotionsFactory promotionsFactory;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  AccountRechargeRepository rechargeRepository;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  private PaymentGatewayFactory gatewayFactory;
  
  @Autowired
  private StringTemplateFactory stringTemplateFactory;
  
  @Autowired
  private AccountHelper accountHelper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  private static final HashMap<String, ReentrantLock> lockMap = new HashMap<String, ReentrantLock>();
  
  private static final ReentrantLock masterLock = new ReentrantLock();
  
  /**
   * Create a new account with the information provided.
   * 
   * @param account
   *          - account to update
   * @param spPlan
   * @param pi
   *          - payment instrument
   */
  public PaymentRecord processNewAccount(Account account, SPPlan spPlan, PaymentInstrument pi) {
    
    /**
     * Create an unique customer id for the user in repository to get the account id as it will
     * required for creating customer profile in authroized.net adding a random number so that
     * unique id is generated always.
     */
    
    // update the number accounts added
    
    accountHelper.updateAccount(account, spPlan);
    
    double chargeAmount = accountHelper.getChargeAmount(account);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Going to charge the following :" + chargeAmount);
    }
    
    PaymentGatewayRequest request = null;
    PaymentGatewayResponse response = null;
    
    // create the payment request and process the payment
    boolean isCreditCard = (pi.getPaymentType() == PaymentType.CREDIT_CARD);
    request = new PaymentGatewayRequest(getReasonDescription(PaymentReasonType.NEW_ACCOUNT,
        account, spPlan, isCreditCard), chargeAmount, pi, spPlan.getCreditBalance());
    
    /**
     * Using account as customer ID for authorized.net customers, as An acccount belogns to
     * admininstrator or inidividual user adminstrator
     */
    
    request.setCustomerId(account.getCustomerProfileId());
    try {
      response = gatewayFactory.getPaymentGateway(pi.getPaymentType()).process(request);
    } catch (PaymentGatewayException gatewayException) {
      throw new PaymentProcessingException(gatewayException.getMessage());
      
    }
    
    if (response == null || !response.isSuccess()) {
      throw new PaymentProcessingException("Transaction was unsucessfull, please try again later!!");
    }
    
    account.setAuthorizedNetProfileId(response.getAuthorizedNetProfileId());
    // update the number accounts added
    // productHelper.updateAccount(account, product, promotion, numEmp);
    
    account.setStatus(AccountStatus.VALID);
    spPlan.setPlanStatus(PlanStatus.ACTIVE);
    
    // update the pi into the account
    paymentInstrumentRepository.save(pi);
    spPlan.setPaymentInstrumentId(pi.getId());
    
    accountHelper.updateAccountUpdateDetails(account, spPlan, spPlan.getNumMember(),
        spPlan.getNumAdmin());
    
    // update the account
    spPlan.setCreditBalance(spPlan.getCreditBalance() - chargeAmount);
    accountRepository.updateAccount(account);
    
    PaymentRecord record = null;
    // update the last payment record only if the payment was processed
    // need to create the account
    record = rechargeRepository.addPaymentRecord(account, spPlan, request, response);
    
    // update the last payment record in the account id
    spPlan.setLastPaymentId(record.getId());
    accountRepository.updateAccount(account);
    
    return record;
  }
  
  /**
   * <code>getReasonDesciption</code> method will give the reason description for the account and
   * the plans.
   * 
   * @param reasonType
   *          resoson type for the new account
   * @param spPlans
   *          list of spplans
   * @param account
   *          for which new account is to be created.
   * @param purchasedSpPlan
   *          which is purcahsed
   * @return the reason string.
   */
  private String getReasonDescription(PaymentReasonType reasonType, Account account,
      SPPlan purchasedSpPlan, boolean isCreditCard) {
    StringTemplate reasonTemplate = stringTemplateFactory.getStringTemplate(
        reasonType.getTemplateName(), Constants.DEFAULT_LOCALE, "default", reasonType.toString(),
        false);
    reasonTemplate.put("spPlans", purchasedSpPlan);
    reasonTemplate.put("endDate", purchasedSpPlan.getNextPaymentDate());
    reasonTemplate.put("isCreditCard", isCreditCard);
    reasonTemplate.put("isPA", purchasedSpPlan.getPlanType() == SPPlanType.IntelligentHiring);
    return populateReasonTemplate(account, reasonTemplate);
  }
  
  /**
   * @param account
   *          for the which reason template is to be populated.
   * @param reasonTemplate
   *          reason template
   * @return the reason string.
   */
  private String populateReasonTemplate(Account account, StringTemplate reasonTemplate) {
    
    reasonTemplate.put("account", account);
    reasonTemplate.put("isBusiness", account.getType() == AccountType.Business);
    reasonTemplate.put("paymentCycle", (account.getBillingCycle() != null) ? account
        .getBillingCycle().getBillingCycleType() : "Invalid Billing Cycle");
    
    String reasonStr = reasonTemplate.render();
    if (LOG.isDebugEnabled()) {
      LOG.debug("The reason Description :\n" + reasonStr);
    }
    return reasonStr;
  }
  
  /**
   * Process recharge.
   * 
   * @param account
   *          - account
   * @param isAutoRecharge
   *          - is auto recharge
   * @return successful recharge payment record
   */
  public List<PaymentRecord> processRecharge(Account account, boolean isAutoRecharge) {
    /* check if account has previous payment instrument and is of credit card or not */
    List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
    account.getSpPlanMap().forEach(
        (type, spPlan) -> {
          PaymentInstrument pi = paymentInstrumentRepository.findByIdValidated(spPlan
              .getPaymentInstrumentId());
          paymentRecords.add(processRecharge(account, spPlan, pi, isAutoRecharge));
          
        });
    return paymentRecords;
  }
  
  /**
   * Process recharge.
   * 
   * @param account
   *          - account
   * @param isAutoRecharge
   *          - is auto recharge
   * @return successful recharge payment record
   */
  public List<PaymentRecord> processRecharge(Account account, boolean isAutoRecharge, SPPlan spPlan) {
    /* check if account has previous payment instrument and is of credit card or not */
    List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
    
    PaymentInstrument pi = paymentInstrumentRepository.findByIdValidated(spPlan
        .getPaymentInstrumentId());
    paymentRecords.add(processRecharge(account, spPlan, pi, isAutoRecharge));
    
    return paymentRecords;
  }
  
  /**
   * Processes the account recharge request.
   * 
   * @param account
   *          - account
   * @param isAutoRecharge
   *          - is auto recharge
   * @return the response to the recharge request
   */
  public PaymentRecord processRecharge(Account account, SPPlan spPlan, PaymentInstrument pi,
      boolean isAutoRecharge) {
    
    // validate if the account has expired or not
    if (spPlan.getNextPaymentDate() != null) {
      DateTime expiresOn = new DateTime(spPlan.getNextPaymentDate());
      if (!expiresOn.isBeforeNow()) {
        throw new InvalidRequestException("Account not expired yet !!!");
      }
    }
    
    double chargeAmount = spPlan.getNextChargeAmount();
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Going to charge the following :" + chargeAmount);
    }
    if (chargeAmount > spPlan.getCreditBalance()) {
      /* check if credit card is present */
      if (StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
        pi = paymentInstrumentRepository.findByIdValidated(spPlan.getPreviousPaymentInstrumentId());
        if (LOG.isDebugEnabled()) {
          LOG.debug("Going to charge via credit card");
        }
      }
    }
    
    PaymentGatewayRequest request = null;
    PaymentGatewayResponse response = null;
    boolean isCreditCard = ((pi.getPaymentType() == null) || (pi.getPaymentType() == PaymentType.CREDIT_CARD));
    // create the payment request and process the payment
    request = new PaymentGatewayRequest(getReasonDescription(PaymentReasonType.RECHARGE_ACCOUNT,
        account, spPlan, isCreditCard), chargeAmount, pi, spPlan.getCreditBalance());
    
    if (pi.getPaymentType() == PaymentType.CREDIT_CARD) {
      request.setAuthorizedNetProfileId(spPlan.getAuthorizedNetProfileId());
    }
    /**
     * Using account as customer ID for authorized.net customers, as An acccount belogns to
     * admininstrator or inidividual user adminstrator
     */
    
    request.setCustomerId(spPlan.getCustomerProfileId());
    try {
      response = gatewayFactory.getPaymentGateway(pi.getPaymentType()).process(request);
    } catch (PaymentGatewayException gatewayException) {
      /*
       * only change the status of the account to renewal in case coming from valid, otherwise, in
       * case it is expired or not, then not changing the status of the account.
       */
      if (spPlan.getPlanStatus() == PlanStatus.ACTIVE) {
        spPlan.setPlanStatus(PlanStatus.RENEWAL_PAYMENT_FAILED);
      }
      accountRepository.updateAccount(account);
      throw new PaymentProcessingException(gatewayException.getMessage());
      
    }
    
    if (response != null && !response.isSuccess()) {
      spPlan.setPlanStatus(PlanStatus.RENEWAL_PAYMENT_FAILED);
      accountRepository.updateAccount(account);
      throw new PaymentProcessingException(response);
    }
    
    spPlan.setPlanStatus(PlanStatus.ACTIVE);
    
    accountHelper.updateAccountUpdateDetails(account, spPlan,
        (spPlan.getNumMember() + ((Long) getActiveMembers(account, spPlan.getPlanType()))
            .intValue()), spPlan.getNumAdmin());
    
    /* Extend the agreement in case auto renewal is set to true */
    
    // Extend the aggreement term if the account is to be expired today
    if (spPlan.getAggreementEndDate() != null && spPlan.getPlanStatus() == PlanStatus.ACTIVE) {
      long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), spPlan.getAggreementEndDate()
          .toLocalDate());
      if (daysRemaining <= 0) {
        int agreementTerm = spPlan.getAgreementTerm();
        long days = agreementTerm * 12 * Constants.DAYS_OF_MONTHLY_BILLING;
        spPlan.setAggreementEndDate(spPlan.getAggreementEndDate().plusDays(days));
        spPlan.setBillingCycleStartDate(new Date());
      }
      
    }
    
    PaymentRecord record = null;
    // update the last payment record only if the payment was processed
    // need to create the account
    /* update the mesage */
    request.setReason(getReasonDescription(PaymentReasonType.RECHARGE_ACCOUNT, account, spPlan,
        false));
    record = rechargeRepository.addPaymentRecord(account, spPlan, request, response);
    
    // update the last payment record in the account id
    spPlan.setLastPaymentId(record.getId());
    
    if (!isCreditCard) {
      spPlan.setCreditBalance(spPlan.getCreditBalance() - chargeAmount);
    }
    accountHelper.updateAccount(account, spPlan);
    
    accountRepository.updateAccount(account);
    
    return record;
  }
  
  /**
   * Gets the number active members for the given account.
   * 
   * @param account
   *          - account
   * @param spPlanType
   *          plan Type
   * @return the number of active accounts
   */
  private long getActiveMembers(Account account, SPPlanType spPlanType) {
    long activeMembers = 1;
    if (account.getType() == AccountType.Business) {
      activeMembers = userRepository.getNumberOfActiveMembers(accountRepository
          .getCompanyForAccount(account.getId()).getId(), spPlanType);
    }
    return activeMembers;
  }
  
  /**
   * Gets the lock for the given account.
   * 
   * @param account
   *          - account
   * @return the lock for the account
   */
  private ReentrantLock getAccountUpdateLock(Account account) {
    return getAccountUpdateLock(account.getId());
  }
  
  /**
   * Get the lock for the given account id.
   * 
   * @param accountId
   *          - account id
   * @return the lock
   */
  private ReentrantLock getAccountUpdateLock(String accountId) {
    ReentrantLock accountUpdateLock = null;
    masterLock.lock();
    try {
      accountUpdateLock = lockMap.get(accountId);
      if (accountUpdateLock == null) {
        // creating a new lock
        accountUpdateLock = new ReentrantLock();
        lockMap.put(accountId, accountUpdateLock);
      }
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      masterLock.unlock();
    }
    return accountUpdateLock;
  }
  
  /**
   * Processes the payment for the given product, promotion and units.
   * 
   * @param account
   *          - account to update
   * @param planForm
   *          SPPlanForm
   * @param planType
   *          planType.
   */
  public PaymentRecord processCandidateAdd(Account account, SPPlanForm planForm, SPPlanType planType) {
    
    PaymentInstrument pi = paymentInstrumentRepository.findByIdValidated(account.getSpPlanMap()
        .get(planType).getPaymentInstrumentId());
    
    return processCandidateAdd(account, planForm, planType, pi);
  }
  
  /**
   * Processes the payment for the given product, promotion and units.
   * 
   * @param account
   *          - account to update
   * @param planType
   *          planType.
   * @param purchasedPlan
   *          purchased plan
   * @param isEditAccount
   *          whether it is edit account request.
   */
  public PaymentRecord processCandidateAdd(Account account, SPPlan purchasedPlan,
      SPPlanType planType) {
    
    PaymentInstrument pi = paymentInstrumentRepository.findByIdValidated(account.getSpPlanMap()
        .get(planType).getPaymentInstrumentId());
    
    return processCandidateAdd(account, planType, pi, purchasedPlan);
  }
  
  /**
   * Processes the payment for the given product, promotion and units.
   * 
   * @param account
   *          - account
   * @param planForm
   *          SPPlan Form
   * @param planType
   *          SP Plan type.
   * @param pi
   *          - payment instrument
   * @return successful charge record
   */
  public PaymentRecord processCandidateAdd(Account account, SPPlanForm planForm,
      SPPlanType planType, PaymentInstrument pi) {
    SPPlan purchasedSpPlan = planForm.getSPPlan();
    // validate the purchase units
    if (purchasedSpPlan.getNumAdmin() == 0 && purchasedSpPlan.getNumMember() == 0) {
      throw new InvalidRequestException("Number to add must be greater than zero !!!");
    }
    if (!purchasedSpPlan.isOverrideMemberPrice()) {
      purchasedSpPlan.setOverrideMemberPrice(purchasedSpPlan.getUnitMemberPrice().multiply(
          new BigDecimal(purchasedSpPlan.getNumMember())));
    }
    
    if (!purchasedSpPlan.isOverrideAdminPrice()) {
      purchasedSpPlan.setOverrideAdminPrice(purchasedSpPlan.getUnitAdminPrice().multiply(
          new BigDecimal(purchasedSpPlan.getNumAdmin())));
    }
    
    return processCandidateAdd(account, planType, pi, purchasedSpPlan);
  }
  
  /**
   * processCandidateAdd method will process the candidate add.
   * 
   * @param account
   *          to which candiate to be added.
   * @param planType
   *          for which process to be addd.
   * @param pi
   *          paymeent instrument
   * @param purchasedSpPlan
   *          splant which is purcahcesed.
   * @return the payment record for the purchase.
   */
  public PaymentRecord processCandidateAdd(Account account, SPPlanType planType,
      PaymentInstrument pi, SPPlan purchasedSpPlan) {
    
    // get the amount to charge
    SPPlan spPlan = account.getSpPlanMap().get(planType);
    purchasedSpPlan.setNextPaymentDate(spPlan.getNextPaymentDate());
    purchasedSpPlan.setBillingCycle(spPlan.getBillingCycle());
    purchasedSpPlan.setLicensePrice(spPlan.getLicensePrice());
    purchasedSpPlan.setPlanStatus(spPlan.getPlanStatus());
    
    double chargeAmount = accountHelper.getChargeAmount(purchasedSpPlan,
        purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin());
    
    boolean isCreditCard = ((pi.getPaymentType() == null) || (pi.getPaymentType() == PaymentType.CREDIT_CARD));
    
    String reason = getReasonDescription(
        (planType == SPPlanType.IntelligentHiring) ? PaymentReasonType.ADD_CANDIDATE
            : PaymentReasonType.ADD_MEMBER, account, purchasedSpPlan, isCreditCard);
    SPPlan splan = account.getSpPlanMap().get(planType);
    PaymentGatewayRequest request = new PaymentGatewayRequest(reason, chargeAmount, pi,
        splan.getCreditBalance());
    request.setCustomerId(splan.getCustomerProfileId());
    request.setAuthorizedNetProfileId(splan.getAuthorizedNetProfileId());
    PaymentGatewayResponse response = gatewayFactory.getPaymentGateway(pi.getPaymentType())
        .process(request);
    
    if (!response.isSuccess()) {
      throw new PaymentProcessingException(response);
    }
    
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    
    // set the last payment date
    PaymentRecord record = null;
    try {
      // get the most updated account from the database
      account = accountRepository.findValidatedAccountByAccountId(account.getId());
      
      accountHelper.updateAccountUpdateDetails(account, purchasedSpPlan,
          purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin());
      SPPlan existingPlan = account.getSpPlanMap().get(planType);
      accountHelper.updateSPPlan(account, existingPlan, purchasedSpPlan);
      
      // update the number accounts added
      
      accountHelper.updateAccount(account, account.getSpPlanMap().get(planType));
      if (!isCreditCard) {
        existingPlan.setCreditBalance(existingPlan.getCreditBalance() - chargeAmount);
      }
      record = rechargeRepository.addPaymentRecord(account, existingPlan, request, response);
      existingPlan.setLastPaymentId(record.getId());
      
      if (account.getStatus() != AccountStatus.VALID) {
        account.setStatus(AccountStatus.VALID);
      }
      
      if (existingPlan.getPlanStatus() != PlanStatus.ACTIVE) {
        existingPlan.setPlanStatus(PlanStatus.ACTIVE);
      }
      /**
       * Update the payment instrument in the db as credit balance has been updated.
       */
      if (existingPlan.getPaymentType() != null
          && existingPlan.getPaymentType() != PaymentType.CREDIT_CARD) {
        paymentInstrumentRepository.save(pi);
      }
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal("Exception occurred while updating the account", e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
    return record;
  }
  
  /**
   * processCandidateAdd method will process the candidate add.
   * 
   * @param account
   *          to which candiate to be added.
   * @param purchasedSpPlan
   *          splant which is purcahcesed.
   * @return charge amount price.
   */
  public double getUpdateAccountCandidateAddChargeAmount(Account account, SPPlan existingPlan,
      SPPlan purchasedSpPlan) {
    
    // get the amount to charge
    accountHelper.updateAccountUpdateDetails(account, purchasedSpPlan,
        purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin());
    return accountHelper.getEditChargeAmount(existingPlan, purchasedSpPlan,
        purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin(), false);
  }
  
  /**
   * processAddNewPlan method will add the new plan to the account during the account update.
   * 
   * @param account
   *          to which members to be added.
   * @param purchasedSpPlan
   *          splant which is purcahcesed.
   * @return the payment record for the purchase.
   */
  public double getAddNewPlanChargeAmount(Account account, SPPlan existingPlan,
      SPPlan purchasedSpPlan) {
    
    // get the amount to charge
    accountHelper.updateAccountUpdateDetails(account, purchasedSpPlan,
        purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin());
    
    return accountHelper.getEditChargeAmount(existingPlan, purchasedSpPlan,
        purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin(), true);
  }
  
  /**
   * processCandidateAdd method will process the candidate add.
   * 
   * @param account
   *          to which candiate to be added.
   * @param purchasedSpPlan
   *          splant which is purcahcesed.
   * @return the payment record for the purchase.
   */
  public double getAccountUpdatePriceChanged(Account account, SPPlan purchasedSpPlan) {
    
    // get the amount to charge
    accountHelper.updateAccountUpdateDetails(account, purchasedSpPlan,
        purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin());
    
    return accountHelper.getProratedChargeAmount(account, purchasedSpPlan);
  }
  
  /**
   * <code>processUpdatePaymentInstrumnet</code> method will update the payment instrument for the
   * user in Authoroized.net or for credit note in the account repository.
   * 
   * @param account
   *          - account.
   * @param spPlan
   * @param pi
   *          - payment instrument.
   */
  public PaymentInstrument processUpdatePaymentInstrument(Account account, SPPlan spPlan,
      PaymentInstrument pi, Address address) {
    PaymentType paymentType = pi.getPaymentType();
    if (paymentType == null) {
      paymentType = PaymentType.CREDIT_CARD;
    }
    /*
     * call the gateway to update the payment instrument. IN case of error exception will be thrown.
     */
    PaymentGatewayRequest gatewayRequest = null;
    switch (paymentType) {
    case CREDIT_CARD:
      gatewayRequest = new PaymentGatewayRequest(null, 0, pi, 0);
      gatewayRequest.setCustomerId(spPlan.getCustomerProfileId());
      gatewayRequest.setAuthorizedNetProfileId(spPlan.getAuthorizedNetProfileId());
      break;
    case WIRE:
      /* Create a new payment instrument for tracking purpose. */
      CreditNotePaymentInstrument creditNotePaymentInstrument = new CreditNotePaymentInstrument(
          (CreditNotePaymentInstrument) pi);
      gatewayRequest = new PaymentGatewayRequest(null, 0, creditNotePaymentInstrument, null, null);
      
    default:
      break;
    }
    
    if (gatewayRequest == null) {
      LOG.error("PaymentGateway request is null. Please check the paymentType. Incorrect paymentType : "
          + paymentType);
      throw new PaymentGatewayException("Unable to update the payment instrument");
      
    }
    gatewayFactory.getPaymentGateway(pi.getPaymentType()).updatePaymentInstrument(gatewayRequest,
        address);
    accountRepository.updatePaymentInstrument(spPlan, pi);
    
    return pi;
  }
  
  /**
   * Gets the account for the given user.
   * 
   * @param user
   *          - user
   * @return the account if found
   */
  public Account getAccount(User user) {
    return (user.getAccountId() == null) ? accountRepository.findValidatedAccountByCompanyId(user
        .getCompanyId()) : accountRepository.findValidatedAccountByAccountId(user.getAccountId());
  }
  
  /**
   * Gets the product helper for the given product.
   *
   * @param product
   *          - product
   * @return the product helper
   */
  public ProductHelper getProductHelper(Product product) {
    try {
      return (ProductHelper) ApplicationContextUtils.getBean(product.getProductType().toString()
          .toLowerCase()
          + PRODUCT_HELPER_SUFFIX);
    } catch (Exception e) {
      throw new InvalidRequestException("Product helper not found for product type:"
          + product.getProductType());
    }
  }
  
  /**
   * Gets the payment history for the given account.
   * 
   * @param account
   *          - account
   * @param spPlanType
   * @return the list of payment records
   */
  public List<PaymentRecord> getPaymentHistory(Account account, SPPlanType spPlanType) {
    return rechargeRepository.getPaymentRecords(account, spPlanType);
  }
  
  /**
   * Gets the account members for the given account.
   * 
   * @param account
   *          - account
   * @return the number of active users
   */
  public long getActiveMemberCount(Account account, SPPlanType spPlanType) {
    return userRepository.getNumberOfActiveMembers(
        accountRepository.getCompanyForAccount(account.getId()).getId(), spPlanType);
  }
  
  /**
   * Reserve an administration subscription.
   * 
   * @param companyId
   *          - company id
   * @param planType
   *          - plan type
   */
  public void reserveAdminSubscription(String companyId, SPPlanType planType) {
    reserveAdminSubscription(accountRepository.findValidatedAccountByCompanyId(companyId), planType);
  }
  
  /**
   * Reserve an administration subscription.
   * 
   * @param account
   *          - account
   * @param planType
   *          - plan type
   */
  public void reserveAdminSubscription(Account account, SPPlanType planType) {
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      account = accountRepository.findValidatedAccountByAccountId(account.getId());
      
      SPPlan spPlan = account.getSpPlanMap().get(planType);
      /* check if subscription are present or not */
      if (spPlan.getNumAdmin() == 0) {
        throw new SPException(
            "Cannot send the invite, administrator subscription not availble for the plan");
      }
      spPlan.reserveAdminAccount();
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * Release the admin subscription for the given plan type.
   * 
   * @param companyId
   *          - company id
   * @param planType
   *          - plan type
   */
  public void releaseAdminSubscription(String companyId, SPPlanType planType) {
    releaseAdminSubscription(accountRepository.findValidatedAccountByCompanyId(companyId), planType);
  }
  
  /**
   * Release the admin subscription for the given plan type.
   * 
   * @param account
   *          - account
   * @param planType
   *          - plan type
   */
  public void releaseAdminSubscription(Account account, SPPlanType planType) {
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      account = accountRepository.findValidatedAccountByAccountId(account.getId());
      
      SPPlan spPlan = account.getSpPlanMap().get(planType);
      spPlan.releaseAdminAccount();
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * Reserve the subscription for the given company.
   * 
   * @param companyId
   *          - company id
   * @param planType
   *          - plan type
   */
  public void reserveSubscription(String companyId, SPPlanType planType) {
    reserveSubscription(accountRepository.findValidatedAccountByCompanyId(companyId), planType);
  }
  
  /**
   * Synchronized method to reserver a subscription for the mebers for the plan type.
   * 
   * @param account
   *          - account
   */
  public void reserveSubscription(Account account, SPPlanType planType) {
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      account = accountRepository.findValidatedAccountByAccountId(account.getId());
      
      SPPlan spPlan = account.getSpPlanMap().get(planType);
      /* check if subscription are present or not */
      if (spPlan.getNumMember() == 0) {
        throw new SPException(
            "Error 1002 : Cannot send the invite, subscription not availble for the plan");
      }
      spPlan.setNumMember(spPlan.getNumMember() - 1);
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * This method releases the hiring subscription.
   * 
   * @param companyId
   *          - company id
   * @param count
   *          - number of accounts to release
   */
  public void releaseHiringSubscription(String companyId, int count) {
    Account account = accountRepository.findAccountByCompanyId(companyId);
    Assert.notNull(account, "Account not found for " + companyId);
    releaseSubscription(account, count, SPPlanType.IntelligentHiring);
  }
  
  /**
   * This method releases the hiring subscription.
   * 
   * @param account
   *          - account
   */
  public void releaseSubscription(Account account, SPPlanType spPlanType) {
    releaseSubscription(account, 1, spPlanType);
  }
  
  /**
   * This method releases the hiring subscription.
   * 
   * @param account
   *          - account
   * @param count
   *          - number of accounts to release
   * @param planType
   *          plantype for which hiring subrsciption is ot be reserved.
   */
  public void releaseSubscription(Account account, int count, SPPlanType planType) {
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      account = accountRepository.findValidatedAccountByAccountId(account.getId());
      
      SPPlan spPlan = account.getSpPlanMap().get(planType);
      spPlan.setNumMember(spPlan.getNumMember() + count);
      account.getSpPlanMap().put(planType, spPlan);
      
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * This method releases the hiring subscription.
   * 
   * @param accountId
   *          - id of the account.
   * @param count
   *          - number of accounts to release
   * @param planType
   *          planType for which hiring subscription to be reserved.
   */
  public void releaseSubscription(String accountId, int count, SPPlanType planType) {
    // get a lock to create an entry into the account lock map
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      
      // get the most updated account from the database
      account = accountRepository.findValidatedAccountByAccountId(account.getId());
      
      SPPlan spPlan = account.getSpPlanMap().get(planType);
      spPlan.setNumMember(spPlan.getNumMember() + count);
      account.getSpPlanMap().put(planType, spPlan);
      
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * Release a member subscriptions for the given company.
   * 
   * @param companyId
   *          - company id
   */
  public void releaseMemberSubscription(String companyId, SPPlanType planType) {
    Company company = accountRepository.findCompanyByIdValidated(companyId);
    
    releaseSubscription(company.getAccountId(), 1, planType);
  }
  
  /**
   * Release member subscription.
   * 
   * @param accountId
   *          - account id
   * @param count
   *          - count
   */
  @Deprecated
  public void releaseMemberSubscription(String accountId, int count) {
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(accountId);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      Account account = accountRepository.findValidatedAccountByAccountId(accountId);
      
      // add the hiring subscriptions
      account.setAvailableSubscriptions(account.getAvailableSubscriptions() + count);
      
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * Release member subscription.
   * 
   * @param accountId
   *          - account id
   * @param count
   *          - count
   * @param planTypes
   *          plantypes for member subscription is to be released.
   */
  public void releaseMemberSubscription(String accountId, int count, List<SPPlanType> planTypes) {
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(accountId);
    
    // get the lock for the given account
    // to process account update
    accountUpdateLock.lock();
    try {
      // get the most updated account from the database
      Account account = accountRepository.findValidatedAccountByAccountId(accountId);
      
      planTypes.stream().forEach(pt -> {
        SPPlan spPlan = account.getSpPlanMap().get(pt);
        
        spPlan.setNumMember(spPlan.getNumMember() + count);
        account.getSpPlanMap().put(pt, spPlan);
      });
      
      // update the account
      accountRepository.updateAccount(account);
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
  }
  
  /**
   * updateAccount method will updated the account.
   * 
   * @param account
   *          to be updated.
   * @param accountForm
   *          signup account form.
   * @param signupForm
   */
  public void updateAccount(Account account, SignupAccountForm accountForm, SignupForm signupForm) {
    
    /* update the new features in the company as well */
    Company company = accountRepository.findCompanybyAccountId(account.getId());
    
    company.setEnhancePasswordSecurity(accountForm.isEnhancedPasswordSecurity());
    
    /* clearing the roles and features as these will be updated with new plans */
    company.getFeatureList().clear();
    
    // get a lock to create an entry into the account lock map
    ReentrantLock accountUpdateLock = getAccountUpdateLock(account);
    
    // get the lock for the given account
    // to proc ess account update
    accountUpdateLock.lock();
    SPPlanType newPlanType = null;
    try {
      for (SPPlanForm spPlanForm : accountForm.getPlanForms()) {
        
        SPPlan spPlan = account.getSpPlanMap().get(spPlanForm.getPlanType());
        // Its a new spPlan, creating the new plan.
        if (spPlan == null) {
          spPlan = spPlanForm.getSPPlan();
          /* Make the splas as active in case */
          spPlan.setAgreementTerm(Integer.valueOf(spPlanForm.getAggreementTerm()));
          
          LocalDateTime now = LocalDateTime.now();
          LocalDateTime aggreementEndDate = now.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
              * Integer.valueOf(spPlanForm.getAggreementTerm()));
          spPlan.setAggreementEndDate(aggreementEndDate);
          accountHelper.setNextPaymentDate(spPlan);
          account.getSpPlanMap().put(spPlanForm.getPlanType(), spPlan);
          newPlanType = spPlanForm.getPlanType();
          if (newPlanType == SPPlanType.IntelligentHiring) {
            company.setPeopleAnalyticsDeactivated(false);
          } else {
            company.setErtiDeactivated(false);
          }
        }
        
        PaymentInstrument newPaymentInstrument = spPlanForm.createPaymentInstrument();
        PaymentInstrument paymentInstrument = paymentInstrumentRepository.findById(spPlan
            .getPaymentInstrumentId());
        
        updatePaymentInstrument(spPlan, paymentInstrument, newPaymentInstrument);
        /* New Payment Instrument Information */
        
        if (paymentInstrument == null) {
          paymentInstrument = newPaymentInstrument;
        }
        
        SPPlan newSPPlan = spPlanForm.getSPPlan();
        updateAccountPlan(account, company, newSPPlan, paymentInstrument, spPlanForm.isNewPlan());
        
        /* update the next billing cycle */
        
        if (spPlanForm.getTags() != null) {
          /* remove the existing tags and update the new tags */
          spPlan.getTagsKeywords().clear();
          spPlan.getTagsKeywords().addAll(spPlanForm.getTags());
        }
        
        spPlan.setName(spPlanForm.getName());
        // update the number accounts added
        
        accountHelper.updateAccount(account, spPlan);
        
        if (account.getStatus() != AccountStatus.VALID) {
          account.setStatus(AccountStatus.VALID);
        }
        if (spPlan.getPlanStatus() != PlanStatus.ACTIVE) {
          spPlan.setPlanStatus(PlanStatus.ACTIVE);
        }
        account.getSpPlanMap().put(spPlan.getPlanType(), spPlan);
        // update the account
        accountRepository.updateAccount(account);
        // accountRepository.updateCompany(company);
        companyFactory.updateCompany(company);
        companyFactory.updateUsersInSession(company.getId(), ActionType.UpdateNavigation);
        
      }
    } catch (Exception e) {
      LOG.fatal(e);
      throw new InvalidRequestException(e);
    } finally {
      accountUpdateLock.unlock();
    }
    
    /* create new user if its a new plan type created */
    if (newPlanType != null) {
      createNewUser(signupForm, account, company, account.getSpPlanMap().get(newPlanType));
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Account got updated ");
    }
  }
  
  /**
   * updateAccountPlan method will update the splan to the account
   * 
   * @param account
   *          to which plan is to be updated while doing the account update.
   * @param company
   *          for which plan is to be updated.
   * @param newPlan
   * @param newSPPlans
   *          new plan.
   */
  private void updateAccountPlan(Account account, Company company, SPPlan newSPPlan,
      PaymentInstrument paymentInstrument, boolean newPlan) {
    
    RoleType searchRole = newSPPlan.getPlanType() == SPPlanType.IntelligentHiring ? RoleType.Hiring
        : RoleType.User;
    List<User> users = userRepository.findByCompanyAndRole(company.getId(), searchRole);
    
    /* get the reason for the update and the differential plan */
    SPPlan differntialPlan = new SPPlan(newSPPlan);
    SPPlan spPlan = account.getSpPlanMap().get(newSPPlan.getPlanType());
    if (spPlan.getPlanStatus() != null) {
      differntialPlan.setNumAdmin(newSPPlan.getNumAdmin() - spPlan.getNumAdmin());
      if (newSPPlan.getPlanType() == SPPlanType.Primary) {
        differntialPlan.setNumMember(newSPPlan.getNumMember()
            - (spPlan.getNumMember() + users.size()));
      } else {
        differntialPlan.setNumMember(newSPPlan.getNumMember() - spPlan.getNumMember());
      }
    }
    differntialPlan.setNextPaymentDate(spPlan.getNextPaymentDate());
    differntialPlan.setBillingCycleStartDate(spPlan.getBillingCycleStartDate());
    double chargeAmount = 0;
    
    chargeAmount = chargeAmount
        + getUpdateAccountAndChargeAmount(account, company, newSPPlan, users);
    if (chargeAmount > 0) {
      boolean isCreditCard = ((spPlan.getPaymentType() == null) || (spPlan.getPaymentType() == PaymentType.CREDIT_CARD));
      String reason = getReasonDescription(newPlan ? PaymentReasonType.NEW_ACCOUNT
          : PaymentReasonType.UPDATE_ACCOUNT, account, differntialPlan, isCreditCard);
      
      PaymentGatewayRequest request = new PaymentGatewayRequest(reason, chargeAmount,
          paymentInstrument, spPlan.getCreditBalance());
      request.setCustomerId(spPlan.getCustomerProfileId());
      request.setAuthorizedNetProfileId(spPlan.getAuthorizedNetProfileId());
      PaymentGatewayResponse response = gatewayFactory.getPaymentGateway(
          paymentInstrument.getPaymentType()).process(request);
      
      if (!response.isSuccess()) {
        throw new PaymentProcessingException(response);
      }
      
      // set the last payment date
      PaymentRecord record = null;
      try {
        
        spPlan.setCreditBalance(spPlan.getCreditBalance() - chargeAmount);
        
        record = rechargeRepository.addPaymentRecord(account, spPlan, request, response);
        spPlan.setLastPaymentId(record.getId());
        
        if (account.getStatus() != AccountStatus.VALID) {
          account.setStatus(AccountStatus.VALID);
        }
        
        if (spPlan.getPlanStatus() != PlanStatus.ACTIVE) {
          spPlan.setPlanStatus(PlanStatus.ACTIVE);
        }
        /**
         * Update the payment instrument in the db as credit balance has been updated.
         */
        if (spPlan.getPaymentType() != null && spPlan.getPaymentType() != PaymentType.CREDIT_CARD) {
          paymentInstrumentRepository.save(paymentInstrument);
        }
      } catch (Exception e) {
        LOG.fatal("Exception occurred while updating the account", e);
        throw new InvalidRequestException(e);
      } finally {
      }
    }
    // return record;
    
  }
  
  private double getUpdateAccountAndChargeAmount(Account account, Company company,
      SPPlan newSPPlans, List<User> users) {
    double chargeAmount = 0;
    
    SPPlan existingSpPlan = account.getSpPlanMap().get(newSPPlans.getPlanType());
    int activeMembers = 0;
    
    /* check if this a new plan */
    if (existingSpPlan.getPlanStatus() == null && newSPPlans.isActive()) {
      chargeAmount = chargeAmount + getAddNewPlanChargeAmount(account, existingSpPlan, newSPPlans);
      
      // Update the user roles
      updateRoleInUser(newSPPlans, users);
      
      /* update the company roles for the newly added plan. */
      addCompanyRoleAndFeature(company, newSPPlans);
      
      /* update the plan to the account */
      existingSpPlan.setPlanStatus(PlanStatus.ACTIVE);
      account.getSpPlanMap().put(newSPPlans.getPlanType(), newSPPlans);
    } else if (existingSpPlan.getPlanStatus() != null) {
      
      /*
       * As the plan contains only the available members subscription, so we need to find the total
       * members while updating the plan.
       */
      if (existingSpPlan.getPlanType() == SPPlanType.Primary) {
        activeMembers = activeMembers + users.size();
        
      }
      if (newSPPlans.isActive()) {
        /*
         * find the new members added in the plan, as in the update account, the newly added members
         * will not come , rather total will come, so find the differential member added and set the
         * same in the new purchase plan, as the new members will be charged. Adding the active
         * members, becuase spPlan contains the no of available member subscription left in the plan
         * while in edit account, the new plan contains the total no of members subscription, so in
         * order to change we have to add the active members to exisitng account to find out the
         * change in the no .
         */
        
        if ((newSPPlans.getNumAdmin() != existingSpPlan.getNumAdmin())
            || (newSPPlans.getNumMember() != (existingSpPlan.getNumMember() + activeMembers))) {
          
          /*
           * Creating a clone for newPaln
           */
          
          SPPlan differntialPlan = new SPPlan(newSPPlans);
          differntialPlan.setNumAdmin(newSPPlans.getNumAdmin() - existingSpPlan.getNumAdmin());
          differntialPlan.setNumMember(newSPPlans.getNumMember()
              - (existingSpPlan.getNumMember() + activeMembers));
          /*
           * prcess the newly added members, by add member flow. Only charge amount will be
           * different.
           */
          account.getAccountUpdateDetailHistory().remove(existingSpPlan.getPlanType());
          chargeAmount = chargeAmount
              + getUpdateAccountCandidateAddChargeAmount(account, existingSpPlan, differntialPlan);
          
        } else if (newSPPlans.getOverrideAdminPrice().compareTo(
            existingSpPlan.getOverrideAdminPrice()) != 0
            || newSPPlans.getOverrideMemberPrice().compareTo(
                existingSpPlan.getOverrideMemberPrice()) != 0) {
          account.getAccountUpdateDetailHistory().remove(existingSpPlan.getPlanType());
          chargeAmount = chargeAmount + getAccountUpdatePriceChanged(account, newSPPlans);
        }
        
        /*
         * Find the features which are different in both the plans and remove these features from
         * the company and users.
         */
        Collection<SPFeature> featuresUpdated = CollectionUtils.disjunction(
            existingSpPlan.getFeatures(), newSPPlans.getFeatures());
        
        if (featuresUpdated.size() > 0) {
          /* update the role in the user */
          users.stream().forEach(usr -> {
            /* clear the user roles */
            featuresUpdated.stream().forEach(feat -> {
              for (RoleType role : feat.getRoles()) {
                usr.removeRole(role);
              }
            });
            if (usr.getRoles().contains(RoleType.AccountAdministrator)) {
              /* add all the roles to users */
              newSPPlans.getFeatures().stream().forEach(feat -> {
                for (RoleType role : feat.getRoles()) {
                  usr.addRole(role);
                }
                
              });
            }
            userRepository.updateUser(usr);
            
          });
          
          addCompanyRoleAndFeature(company, newSPPlans);
          // managementService.expireSessionForCompany(company.getId());
          
          // update logged in user
          /*
           * Map<String,Object> actionParams = new HashMap<String,Object>();
           * actionParams.put(Constants.DO_LOGOUT, false); actionParams.put(Constants.PARAM_COMPANY,
           * true); managementService.updateSessionForCompany(company.getId(),
           * UserUpdateAction.UpdatePermission,actionParams);
           */
        } else {
          addCompanyRoleAndFeature(company, existingSpPlan);
        }
        
      } else {
        /* update the existing plan, in case the plan is deactivated */
        existingSpPlan.setActive(newSPPlans.isActive());
        /*
         * update the company features and roles. This is done, intentionally, as during the plan
         * loop, some features might have got added during previous plan and company features are no
         * longer empty. So removing them explicitly in case they are present.
         */
        newSPPlans.getFeatures().stream().forEach(feat -> {
          company.getFeatureList().remove(feat);
          // if (!feat.isAdminRole()) {
          // company.getRoleList().remove(feat.getRoleType());
          // }
            
          });
        
        /* update and remove the role in the user */
        users.stream().forEach(usr -> {
          newSPPlans.getFeatures().stream().forEach(feat -> {
            for (RoleType role : feat.getRoles()) {
              if (role.isAdminRole()) {
                usr.removeRole(role);
                /* IN case of account administrator we will not be removing the roles */
                if (usr.getRoles().contains(RoleType.AccountAdministrator)) {
                  usr.addRole(role);
                }
                
              }
            }
            
          });
          
          userRepository.updateUser(usr);
        });
        
        // managementService.expireSessionForCompany(company.getId());
        // update logged in user
        /*
         * Map<String,Object> actionParams = new HashMap<String,Object>();
         * actionParams.put(Constants.DO_LOGOUT, false); actionParams.put(Constants.PARAM_COMPANY,
         * true); managementService.updateSessionForCompany(company.getId(),
         * UserUpdateAction.UpdatePermission,actionParams);
         */
      }
      if (existingSpPlan.getPlanType() == SPPlanType.Primary) {
        existingSpPlan.setNumMember(newSPPlans.getNumMember() - activeMembers);
        
        if (existingSpPlan.getAgreementTerm() != newSPPlans.getAgreementTerm()) {
          LocalDate localeDate = DateTimeUtil.getLocalDate(account.getStartDate());
          LocalDate newAgreementDate = localeDate.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
              * newSPPlans.getAgreementTerm());
          if (newAgreementDate.isBefore(LocalDate.now())) {
            newAgreementDate = localeDate.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
                * (newSPPlans.getAgreementTerm() + 1));
          }
          existingSpPlan.setAggreementEndDate(LocalDateTime.of(newAgreementDate, LocalTime.now()));
        }
        
      }
      // else {
      // SPPlan primaryPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      // if (newSPPlans.getAgreementTerm() == primaryPlan.getAgreementTerm()) {
      // existingSpPlan.setAggreementEndDate(primaryPlan.getAggreementEndDate());
      // } else {
      // LocalDateTime now = LocalDateTime.now();
      // LocalDateTime aggreementEndDate = now.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
      // * newSPPlans.getAgreementTerm());
      // existingSpPlan.setAggreementEndDate(aggreementEndDate);
      // }
      // }
      existingSpPlan.setNumAdmin(newSPPlans.getNumAdmin());
      existingSpPlan.setOverrideAdminPrice(newSPPlans.getOverrideAdminPrice());
      existingSpPlan.setOverrideMemberPrice(newSPPlans.getOverrideMemberPrice());
      existingSpPlan.setUnitAdminPrice(newSPPlans.getUnitAdminPrice());
      existingSpPlan.setUnitMemberPrice(newSPPlans.getUnitMemberPrice());
      existingSpPlan.setLicensePrice(newSPPlans.getLicensePrice());
      existingSpPlan.setAgreementTerm(newSPPlans.getAgreementTerm());
      existingSpPlan.setBundle(newSPPlans.isBundle());
      existingSpPlan.setFeatures(newSPPlans.getFeatures());
      existingSpPlan.setBillingCycle(newSPPlans.getBillingCycle());
    }
    
    return chargeAmount;
  }
  
  /**
   * addCompanyRoleAndFeature method will add the features and roles to the company.
   * 
   * @param company
   *          to which roles and features is to be added.
   * @param newPlan
   *          to be added.
   */
  private void addCompanyRoleAndFeature(Company company, SPPlan newPlan) {
    /* update the company features and roles. */
    newPlan.getFeatures().stream().forEach(feat -> {
      company.getFeatureList().add(feat);
    });
  }
  
  /**
   * <code>updateRoleInUser</code> method will update the roles in the user by adding to them.
   * 
   * @param newPlan
   *          plan which contains the features.
   * @param users
   *          whome role is to be updated.
   */
  private void updateRoleInUser(SPPlan newPlan, List<User> users) {
    /* update the role in the account administrator user account. */
    
    users.stream().filter(usr -> usr.getRoles().contains(RoleType.AccountAdministrator))
        .forEach(usr -> {
          newPlan.getFeatures().stream().forEach(feat -> {
            for (RoleType role : feat.getRoles()) {
              if (role.isAdminRole()) {
                usr.addRole(role);
              }
            }
          });
          userRepository.updateUser(usr);
        });
  }
  
  /**
   * <code>updatePaymentInstrument</code> method will update the payment instrument.
   * 
   * @param account
   *          for which payment instrument is to be updated.
   * @param paymentInstrument
   *          existing payment instrument which is to updated in case there is an update.
   * @param newPaymentInstrument
   *          which is to be used.
   */
  private void updatePaymentInstrument(SPPlan spPlan, PaymentInstrument paymentInstrument,
      PaymentInstrument newPaymentInstrument) {
    /* Check if payment instrument is updated */
    if (paymentInstrument == null
        || paymentInstrument.getPaymentType() != newPaymentInstrument.getPaymentType()) {
      
      /* update the payment instrument directly */
      paymentInstrumentRepository.save(newPaymentInstrument);
      spPlan.setPaymentType(newPaymentInstrument.getPaymentType());
      /* Update the new credit note in the account */
      spPlan.setPaymentInstrumentId(newPaymentInstrument.getId());
      if (paymentInstrument != null) {
        paymentInstrument.updatePaymentInstrument(newPaymentInstrument);
      } else {
        paymentInstrument = newPaymentInstrument;
      }
    }
  }
  
  public User createNewUser(SignupForm signupForm, Account account, Company company, SPPlan splan) {
    // create the user
    User user = null;
    final User existingUser = userRepository.findByEmail(signupForm.getEmail());
    if (signupForm.isExistingMember()) {
      user = new User();
      String oldCompanyId = existingUser.getCompanyId();
      BeanUtils.copyProperties(existingUser, user);
      // resetting the group associations
      userFactory.cleanUser(user, existingUser);
      
      user.setCompanyId(company.getId());
      
      /* remove the old user from PA from that company if exists */
      HiringUser userByEmail = hiringUserFactory.getUserByEmail(existingUser.getEmail(),
          oldCompanyId);
      if (userByEmail != null) {
        hiringUserFactory.delete(userByEmail);
      }
    } else {
      
      /* check if the user is existing user from same company and erti or not */
      if (existingUser != null) {
        user = existingUser;
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put(Constants.PARAM_USER_ID, existingUser.getId());
        paramsMap.put(Constants.PARAM_USER_EMAIL, existingUser.getEmail());
        paramsMap.put("validUser", user.getUserStatus() != UserStatus.PROFILE_INCOMPLETE);
        paramsMap.put(
            "planName",
            MessagesHelper.getMessage("plan." + splan.getPlanType().toString(),
                existingUser.getLocale()));
        // setting the token to the user
        NotificationType type = splan.getPlanType() == SPPlanType.IntelligentHiring ? NotificationType.AltBillingPAValidERTIUser
            : NotificationType.AltBillingERTIValidPAUser;
        notificationsProcessor.process(type, existingUser, existingUser, paramsMap);
        
      } else {
        user = new User();
        user.setEmail(signupForm.getEmail());
        user.setFirstName(signupForm.getFirstName());
        user.setLastName(signupForm.getLastName());
        user.setTitle(signupForm.getTitle());
        user.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
        user.setCreatedOn(LocalDate.now());
        user.setCompanyId(company.getId());
        
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put(Constants.PARAM_USER_ID, user.getId());
        paramsMap.put(Constants.PARAM_USER_EMAIL, user.getEmail());
        paramsMap.put("planName",
            MessagesHelper.getMessage("plan." + splan.getPlanType().toString(), user.getLocale()));
        Token token = tokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
            TokenProcessorType.ADD_MEMBER);
        // setting the token to the user
        user.setTokenUrl(token.getTokenUrl());
        NotificationType planSignup = splan.getPlanType() == SPPlanType.IntelligentHiring ? NotificationType.AltBillingPASubscribed
            : NotificationType.AltBillSignup;
        notificationsProcessor.process(planSignup, user, user, paramsMap);
      }
      
    }
    
    /*
     * in case tool added is people analytics, then migrate the old users from erti account it it
     * exists
     */
    if (splan.getPlanType() == SPPlanType.IntelligentHiring
        && account.getSpPlanMap().get(SPPlanType.Primary) != null) {
      
      /* get all the users from the erti account */
      List<User> users = userRepository.findUsers("companyId", company.getId());
      users.stream().filter(u -> u.getUserStatus() == UserStatus.VALID).forEach(usr -> {
        hiringUserFactory.addFromErti(usr, true);
      });
    }
    
    Set<RoleType> roles = new HashSet<RoleType>();
    roles.addAll(user.getRoles());
    if (splan.getPlanType() == SPPlanType.Primary) {
      roles.add(RoleType.User);
      roles.add(RoleType.AccountAdministrator);
    }
    
    company.getFeatureList().addAll(splan.getFeatures());
    splan.getFeatures().stream().forEach(feat -> {
      for (RoleType role : feat.getRoles()) {
        if (role.isAdminRole()) {
          roles.add(role);
        }
      }
    });
    
    // for business/individual first time registering, status will be assessment pending. Profile
    // incomplete
    user.setRoles(roles);
    
    userRepository.createUser(user);
    
    /* in case of existing member functionality, adding the hiring user back to the pa plna */
    if (splan.getPlanType() == SPPlanType.IntelligentHiring && signupForm.isExistingMember()) {
      HiringUser hiringUser = new HiringUser(user);
      hiringUser.addRole(RoleType.Hiring);
      hiringUserFactory.updateUser(hiringUser);
    }
    
    if (splan.getPlanType() == SPPlanType.Primary) {
      // remove one subscription for the newly added business user.
      account.reserveSubscritption(splan.getPlanType());
      accountRepository.updateAccount(account);
    }
    
    return user;
  }
  
}
