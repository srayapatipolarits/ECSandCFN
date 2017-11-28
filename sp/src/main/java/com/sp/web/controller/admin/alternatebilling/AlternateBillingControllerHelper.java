package com.sp.web.controller.admin.alternatebilling;

import com.sp.web.Constants;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.account.ExpiryAccountHelper;
import com.sp.web.account.plan.AccountHelper;
import com.sp.web.controller.admin.account.AccountControllerHelper;
import com.sp.web.controller.admin.account.AccountRechargeControllerHelper;
import com.sp.web.controller.admin.account.BusinessAccountDTO;
import com.sp.web.controller.admin.member.MemberControllerHelper;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.AccountDTO;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.PaymentRecordDTO;
import com.sp.web.dto.alternatebilling.AccountAdminUserDTO;
import com.sp.web.dto.alternatebilling.AccountDetailsDTO;
import com.sp.web.dto.alternatebilling.SPPlanFeatureDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.PaymentProcessingException;
import com.sp.web.exception.SPException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.CompanyForm;
import com.sp.web.form.SPPlanForm;
import com.sp.web.form.SignupAccountForm;
import com.sp.web.form.SignupForm;
import com.sp.web.form.UserProfileForm;
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
import com.sp.web.model.ProductType;
import com.sp.web.model.ProductValidityType;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.account.BillingCycleType;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.payment.PaymentReasonType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signup.SignupHelper;
import com.sp.web.payment.PaymentGatewayFactory;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;
import com.sp.web.product.AccountFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.product.ProductRepository;
import com.sp.web.repository.payment.PaymentInstrumentRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.scheduler.AccountUpdationScheduler;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.pdf.PDFCreatorService;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>AlternateBillingControllerHelper</code> class provides helper methods for the alternate
 * billing code.
 * 
 * @author pradeepruhil
 */
@Component
public class AlternateBillingControllerHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(AlternateBillingControllerHelper.class);
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private SignupHelper signupHelper;
  
  ProductRepository productRepository;
  
  @Autowired
  private AccountControllerHelper accountControllerHelper;
  
  @Autowired
  private MemberControllerHelper memberControllerHelper;
  
  @Autowired
  AccountRechargeRepository accountRechargeRepository;
  
  @Autowired
  AccountFactory accountFactory;
  
  @Autowired
  private AccountRechargeControllerHelper accountRechargeControllerHelper;
  
  @Autowired
  private PaymentInstrumentRepository instrumentRepository;
  
  @Autowired
  @Qualifier("itextPdfService")
  private PDFCreatorService pdfCreatorService;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private PaymentGatewayFactory gatewayFactory;
  
  @Autowired
  private ExpiryAccountHelper expiryAccountHelper;
  
  @Autowired
  private AccountUpdationScheduler accountUpdationScheduler;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private AccountHelper accountHelper;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  /**
   * <code>getAllAccounts</code> method will return all the accounts requested presne tin the
   * system.
   * 
   * @param user
   *          system adminsitrator user
   * @param param
   *          contains the type of accounts to be fetched.
   * @return the json response;
   */
  public SPResponse getAllAccounts(User user, Object[] param) {
    
    AccountType accountType = (AccountType) param[0];
    
    if (accountType == null) {
      throw new InvalidParameterException("Account Type is not present");
    }
    boolean isImportExisting = (boolean) param[1];
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("AccountType for account details to be fetched are " + accountType);
    }
    
    List<AccountDetailsDTO> accountsDetails = accountType == AccountType.Business ? getAllBuisnessAccounts(isImportExisting)
        : getAllIndividualsAccount(isImportExisting);
    
    int totalAccounts = accountsDetails != null ? accountsDetails.size() : 0;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Total accouts found are :" + totalAccounts);
    }
    SPResponse spResponse = new SPResponse();
    spResponse.add("accountDetails", accountsDetails);
    spResponse.add("totalAccounts", totalAccounts);
    return spResponse;
  }
  
  /**
   * <code>getPlanFeatures</code> method will return all the features of the plan. system.
   * 
   * @param user
   *          system adminsitrator user
   * @return the json response;
   */
  public SPResponse getPlanFeatures(User user) {
    
    List<SPPlanType> spPlans = Arrays.asList(SPPlanType.values());
    
    List<SPPlanFeatureDTO> plansFeatures = spPlans.stream().map(SPPlanFeatureDTO::new)
        .collect(Collectors.toList());
    
    SPResponse spResponse = new SPResponse();
    spResponse.add("plansFeatures", plansFeatures);
    return spResponse;
  }
  
  /**
   * <code>getAllIndividualAccount</code> method will return all the individual accounts.
   * 
   * @param isImportExisting
   *          to import the existing account to alt billingaccount.
   * @return the list of individiual accounts find in Surepeople account.
   */
  private List<AccountDetailsDTO> getAllIndividualsAccount(boolean isImportExisting) {
    
    List<Account> individualsAccounts = accountRepository
        .getAllAccountForAccountType(AccountType.Individual);
    
    Predicate<Account> accoutFilter = isImportExisting ? existingAccounts() : altBillAccounts();
    
    List<AccountDetailsDTO> accountDetailsDtos = individualsAccounts
        .stream()
        .filter(accoutFilter)
        .map(
            ac -> {
              String accountId = ac.getId();
              User user = userRepository.findByAccountId(accountId);
              if (user == null) {
                return null;
              }
              List<String> products = ac.getProducts();
              List<Product> prductList = productRepository.findAllProductsById(products);
              if (CollectionUtils.isEmpty(prductList)) {
                return null;
              }
              ProductValidityType productValidityType = prductList.stream()
                  .map(pr -> pr.getValidityType()).findFirst().get();
              AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO(ac, user,
                  productValidityType);
              return accountDetailsDTO;
            }).filter(adto -> adto != null).collect(Collectors.toList());
    
    LOG.debug("Inidividual accounts details are " + accountDetailsDtos);
    return accountDetailsDtos;
  }
  
  /**
   * <code>getAllBuisnessAccounts</code> method will return all buisness account present in the
   * system.
   * 
   * @param isImportExisting
   *          whether to retrieve all the existing account details.
   * @return the list of account details.
   */
  private List<AccountDetailsDTO> getAllBuisnessAccounts(boolean isImportExisting) {
    List<Account> buisnessAccounts = accountRepository
        .getAllAccountForAccountType(AccountType.Business);
    // Predicate<Account> accoutFilter = isImportExisting ? existingAccounts() : altBillAccounts();
    List<AccountDetailsDTO> accountDetailsDtos = buisnessAccounts.stream().map(ac -> {
      String accountId = ac.getId();
      Company company = null;
      try {
        company = accountRepository.getCompanyForAccount(accountId);
      } catch (InvalidRequestException ex) {
        return null;
      }
      AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO(ac, company);
      
      return accountDetailsDTO;
    }).filter(adto -> adto != null).collect(Collectors.toList());
    
    LOG.debug("Inidividual accounts details are " + accountDetailsDtos);
    return accountDetailsDtos;
  }
  
  /**
   * <code>createBusinessAccount</code> will create the business account for the user.
   * 
   * @param user
   *          system administrator user.
   * @param param
   *          contains the buisness account details.
   * @return the spresponse.
   */
  public SPResponse createBusinessAccount(User user, Object[] param) {
    SPResponse resp = new SPResponse();
    try {
      boolean success = false;
      SignupForm signupForm = (SignupForm) param[1];
      SignupAccountForm accountForm = (SignupAccountForm) param[0];
      AddressForm addressForm = (AddressForm) param[2];
      HttpServletRequest request = (HttpServletRequest) param[3];
      if (signupForm != null && StringUtils.isNotEmpty(signupForm.getFirstName())) {
        
        User userSignup = userRepository.findByEmail(signupForm.getEmail());
        if (userSignup != null && !signupForm.isExistingMember()) {
          resp.addError("Duplicate_Email",
              MessagesHelper.getMessage("exception.duplicateEmail.signup"));
          throw new InvalidRequestException(
              MessagesHelper.getMessage("exception.duplicateEmail.signup"));
        } else {
          if (userSignup == null && signupForm.isExistingMember()) {
            resp.addError("UserNotFound", "User not found.");
            throw new InvalidRequestException(
                MessagesHelper.getMessage("profileCopy.verifyEmail.userNotFoundReset"));
          }
        }
        
        // TODO check for valid credit amount in case somebody by pass the
        // process */
        if (CollectionUtils.isEmpty(accountForm.getPlanForms())) {
          throw new InvalidRequestException("NO plans found for the creating account");
        }
        success = signupHelper.signupBusiness(signupForm, addressForm, accountForm,
            accountForm.getPlanForms(), request);
      }
      
      if (!success) {
        resp.addError("SP_SIGNUP_ERROR", "Could not signup account !!!");
      } else {
        if (StringUtils.isNotEmpty(accountForm.getAccId())) {
          resp.add("accountId", accountForm.getAccId());
        }
        resp.isSuccess();
      }
    } catch (SPException e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(e);
    } catch (Exception e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(new SPException(e));
    }
    return resp;
  }
  
  /**
   * The helper method to get the business account details.
   * 
   * @param user
   *          - the administrator user
   * @return the response to the get request
   */
  public SPResponse getBusinessAccountDetails(User user, Object[] param) {
    
    String accountId = (String) param[0];
    
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountId cannot be blank or contain white spaces");
    }
    
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    if (account == null) {
      throw new SPException("Account Not Found!!");
    }
    
    Company company = accountRepository.findCompanybyAccountId(account.getId());
    
    BusinessAccountDTO accountDTO = new BusinessAccountDTO(account);
    
    SPResponse resp = new SPResponse();
    
    resp.add(Constants.PARAM_COMPANY, new CompanyDTO(company));
    
    Boolean isEditAccount = (Boolean) param[1];
    updateAccountDetails(company.getId(), resp, account, accountDTO, isEditAccount);
    
    return resp;
    
  }
  
  /**
   * The helper method to get the individual account details.
   * 
   * @param user
   *          - the administrator user
   * @return the response to the get request
   */
  public SPResponse getIndividualAccountDetails(User user, Object[] param) {
    SPResponse resp = new SPResponse();
    String accountId = (String) param[0];
    
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountId cannot be blank or contain white spaces");
    }
    
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    if (account == null) {
      throw new SPException("No account present for the id passsed");
    }
    
    if (account.getAgreementTerm() == 0) {
      account.setAgreementTerm(1);
      
      Date startDate = account.getStartDate();
      Date aggreementEndDate = DateUtils.addDays(startDate, 360);
      account.setAggreementEndDate(aggreementEndDate);
      accountRepository.updateAccount(account);
    }
    User profileUser = userRepository.findByAccountId(account.getId());
    
    UserProfileForm profileForm = new UserProfileForm(profileUser);
    
    // add the user profile
    resp.add("profileForm", profileForm);
    // add the address for the user
    resp.add("address", profileUser.getAddress());
    // add the user group profile
    resp.add("groupList", profileUser.getGroupAssociationList());
    // add the tags
    resp.add("tagList", profileUser.getTagList());
    // add the user roles
    resp.add(Constants.PARAM_ROLES, profileUser.getRoles());
    // add the user summary
    resp.add("userSummary", new BaseUserDTO(profileUser));
    // add user certificate
    if (profileUser.hasRole(RoleType.IndividualAccountAdministrator)) {
      resp.add("userCertificate", "SP" + profileUser.getCertificateNumber());
      if (profileUser.getCreatedOn() != null) {
        resp.add("createdOn", MessagesHelper.formatDate(profileUser.getCreatedOn()));
      }
    }
    
    // create a new DTO object
    AccountDTO accountDTO = new AccountDTO(account);
    
    // update the rest of the information like products and payment information
    Map<SPPlanType, List<User>> adminUsersMap = accountRepository.findAdminsForPlans(account
        .getSpPlanMap().keySet(), user.getCompanyId());
    
    Map<SPPlanType, List<AccountAdminUserDTO>> adminUserDtoMap = new HashMap<>();
    Map<SPPlanType, Integer> adminUserCountMap = new HashMap<>();
    adminUsersMap.forEach((key, users) -> {
      List<AccountAdminUserDTO> adminUserDTOs = users.stream().map(AccountAdminUserDTO::new)
          .collect(Collectors.toList());
      adminUserDtoMap.put(key, adminUserDTOs);
      adminUserCountMap.put(key, users.size());
    });
    updateAccountDetails(null, resp, account, accountDTO, false);
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
   * @param adminUserCountMap
   */
  private void updateAccountDetails(String companyId, SPResponse resp, Account account,
      AccountDTO accountDTO, boolean isEditAccount) {
    // get the product info for the account
    // accountControllerHelper.updateProductInfo(accountDTO, account,
    // companyId);
    
    accountControllerHelper.updateSPPlanInfo(accountDTO, account, companyId, resp, isEditAccount);
    resp.add(Constants.PARAM_ACCOUNT, accountDTO);
  }
  
  /**
   * Adds the candidates for the given request.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return response to the add request
   */
  public SPResponse addMemberAccounts(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the promotion id if present
    SPPlanForm planForm = (SPPlanForm) params[0];
    
    String accountId = (String) params[1];
    
    // get the payment instrument from the account
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    if (account == null) {
      throw new InvalidRequestException("Account not found");
    }
    
    /*
     * check if account is valid to add members *
     */
    SPPlan spPlan = account.getSpPlanMap().get(planForm.getPlanType());
    if (account.getStatus() != AccountStatus.VALID
        && (spPlan != null && spPlan.getPlanStatus() == PlanStatus.ACTIVE)) {
      throw new InvalidRequestException("Account or Plan is not valid. Cannot add members ");
    }
    
    if (spPlan == null) {
      throw new InvalidRequestException("No plan found for the requested type :"
          + planForm.getPlanType());
    }
    
    SPPlan purchasedPlan = planForm.getSPPlan();
    if (!(spPlan.getUnitAdminPrice().compareTo(purchasedPlan.getUnitAdminPrice()) == 0 && spPlan
        .getUnitMemberPrice().compareTo(purchasedPlan.getUnitMemberPrice()) == 0)) {
      throw new InvalidRequestException(
          "Cannot change the unit price during the add member. Please use override amount feature ");
    }
    
    /**
     * sending theaddress as null, as in case of alternate billing no address is stored in the
     * payment gatway for.
     */
    PaymentRecord record = accountRechargeControllerHelper.processAddMemberRequest(planForm,
        planForm.getPlanType(), account);
    
    // add the newly created payment record to the response
    resp.add(Constants.PARAM_PAYMENT_RECORD, new PaymentRecordDTO(record));
    
    return resp;
  }
  
  /**
   * Cancel request for the users account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the cancel request
   */
  public SPResponse cancel(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    
    String accountId = (String) param[0];
    
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountId cannot be blank or empty");
    }
    // get the user account
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    
    if (account == null) {
      throw new InvalidRequestException("Account not found");
    }
    
    SPPlanType planType = (SPPlanType) param[1];
    SPPlan plan = account.getSpPlanMap() != null ? account.getSpPlanMap().get(planType) : null;
    
    // process the cancel request
    if (plan != null) {
      plan.setPlanStatus(PlanStatus.CANCEL);
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
  public SPResponse reActivate(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    String accountId = (String) param[0];
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountId cannot be blank or empty");
    }
    // get the user account
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    
    if (account == null) {
      throw new InvalidRequestException("Account not found");
    }
    
    SPPlanType planType = (SPPlanType) param[1];
    SPPlan plan = account.getSpPlanMap() != null ? account.getSpPlanMap().get(planType) : null;
    
    // process the cancel request
    if (plan == null || plan.getPlanStatus() != PlanStatus.CANCEL) {
      throw new InvalidRequestException("Plan must be cancelled first.");
    }
    
    plan.setPlanStatus(PlanStatus.ACTIVE);
    accountRepository.updateAccount(account);
    
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * <code>getProducts</code>Method will return the all the products belonging to the type.
   * 
   * @param user
   *          system administrator user.
   * @param param
   *          contains the productType
   * @return the SPResponse.
   */
  public SPResponse getProducts(User user, Object[] param) {
    ProductType productType = (ProductType) param[0];
    SPResponse spResponse = new SPResponse();
    List<Product> products = productRepository.findByType(productType);
    if (products != null && products.size() != 0) {
      spResponse.add("products", products);
    } else {
      spResponse.addError("Product_Not_Found", "No products found for type :" + productType);
    }
    return spResponse;
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
    AddressForm addressForm = (AddressForm) args[2];
    
    String companyId = (String) args[1];
    // get the company object
    CompanyDao company = companyFactory.getCompany(companyId);
    
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
   * <code>addCredit</code> method will add the credit to the user profile.
   * 
   * @param user
   *          logged in system adminstrator user.
   * @param params
   *          of the logged in user.
   * @return the SPRespnose for the credit added sccessfully.
   */
  public SPResponse addCredit(User user, Object[] params) {
    
    String accountId = (String) params[0];
    SPPlanType planType = (SPPlanType) params[4];
    ;
    
    Assert.notNull(planType, "Invalid Request, Plan Type is not present");
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    
    SPPlan spPlan = account.getSpPlanMap().get(planType);
    Assert.notNull(spPlan, "Plan is not subscribed by the user, cannot add credit");
    /** After mingration, credit card payment will no longed be supported. */
    
    // get the amount to charge
    PaymentType paymentType = (PaymentType) params[2];
    double creditAmount = (double) params[1];
    if (creditAmount <= 0) {
      throw new InvalidRequestException(
          "Invalid credit amount. Please enter value greater then zero");
    }
    
    if (paymentType == PaymentType.CREDIT_CARD) {
      String reason = "Add Credit via Credit Card";
      if (StringUtils.isEmpty(spPlan.getPreviousPaymentInstrumentId())) {
        throw new SPException(
            "Cannot add credit via Credit card, credit details not linked on account ");
      }
      PaymentInstrument oldPaymentInstrumentId = instrumentRepository.findById(spPlan
          .getPreviousPaymentInstrumentId());
      PaymentGatewayRequest request = new PaymentGatewayRequest(reason, creditAmount,
          oldPaymentInstrumentId, spPlan.getCreditBalance());
      request.setCustomerId(spPlan.getCustomerProfileId());
      request.setAuthorizedNetProfileId(spPlan.getAuthorizedNetProfileId());
      PaymentGatewayResponse response = gatewayFactory.getPaymentGateway(
          oldPaymentInstrumentId.getPaymentType()).process(request);
      
      if (!response.isSuccess()) {
        throw new PaymentProcessingException(response);
      }
    }
    
    String paymentInstrumentId = spPlan.getPaymentInstrumentId();
    
    CreditNotePaymentInstrument paymentInstrument = (CreditNotePaymentInstrument) instrumentRepository
        .findById(paymentInstrumentId);
    double creditBalance = paymentInstrument.getCreditBalance();
    
    LOG.debug("Previous credit balance is " + creditBalance);
    
    String comment = (String) params[3];
    CreditNotePaymentInstrument creditNotePaymentInstrument = new CreditNotePaymentInstrument(
        paymentInstrument);
    creditNotePaymentInstrument.setCreateTime(LocalDateTime.now());
    creditNotePaymentInstrument.setReferenceNo(UUID.randomUUID().toString());
    // creditNotePaymentInstrument.setCreditBalance(creditNotePaymentInstrument.getCreditBalance()
    // + creditAmount);
    creditNotePaymentInstrument.setComment(comment);
    
    spPlan.setCreditBalance(spPlan.getCreditBalance() + creditAmount);
    
    instrumentRepository.save(creditNotePaymentInstrument);
    
    /* Update the new credit note in the account */
    spPlan.setPaymentInstrumentId(creditNotePaymentInstrument.getId());
    
    accountRepository.updateAccount(account);
    SPResponse resp = new SPResponse();
    resp.isSuccess();
    return resp;
  }
  
  /**
   * <code>signupIndividualAccount</code> method will create the indvidual account for the user.
   * 
   * @param user
   *          individual account signup.
   * @param params
   *          contians the parameters.
   * @return
   */
  public SPResponse signUpIndividualAccount(User user, Object[] params) {
    SignupForm signupForm = (SignupForm) params[0];
    AddressForm addressForm = (AddressForm) params[1];
    SignupAccountForm accountForm = (SignupAccountForm) params[2];
    HttpServletRequest request = (HttpServletRequest) params[3];
    SPResponse resp = new SPResponse();
    try {
      boolean success = signupHelper
          .signupIndividual(signupForm, addressForm, accountForm, request);
      if (!success) {
        resp.addError("SP_INDIVIDUAL_SIGNUP_ERROR", "Could not signup account !!!");
      } else {
        resp.isSuccess();
      }
    } catch (SPException e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(e);
    } catch (Exception e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(new SPException(e));
    }
    return resp;
  }
  
  /**
   * Reactivate a cancelled account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the re-activate request
   */
  public SPResponse reactivateAccount(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    String accountId = (String) param[0];
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountId cannot be blank or empty");
    }
    
    SPPlanType spPlanType = null;
    if (param[1] != null) {
      spPlanType = (SPPlanType) param[1];
    }
    
    // get the user account
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    
    Company company = accountRepository.findCompanybyAccountId(accountId);
    
    if (account == null) {
      throw new InvalidRequestException("Account not found");
    }
    
    // process the deactivated request, validate the reactivation request is for account only.
    if (!account.isDeactivated() && spPlanType == null) {
      throw new InvalidRequestException("Account must be deactivated first.");
    }
    
    if (spPlanType == null) {
      
      /* reactivate all the plans for the account */
      Collection<SPPlan> plans = account.getSpPlanMap().values();
      for (SPPlan plan : plans) {
        reActivatePlan(account, company, plan);
      }
      account.setDeactivated(false);
      company.setDeactivated(false);
    } else {
      SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
      if (spPlan == null) {
        throw new InvalidRequestException("Plan is not subscribed by the company");
      }
      reActivatePlan(account, company, spPlan);
    }
    
    accountRepository.updateAccount(account);
    if (company != null) {
      companyFactory.updateCompany(company);
    }
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * ReactivatePlan method will reactivate the plan.
   * 
   * @param account
   *          for which plan is to reactivated.
   * @param company
   *          of the user.
   * @param plan
   *          to be reactivated.
   */
  public void reActivatePlan(Account account, Company company, SPPlan plan) {
    /* check the credit balance is available in the account to reactivate it aagain. */
    if (plan.getCreditBalance() < plan.getNextChargeAmount()) {
      throw new InvalidRequestException("Cannot activate plan!!. "
          + MessagesHelper.getMessage("plan." + plan.getPlanType().toString())
          + ", Please add credit balance before activate the account ");
    }
    
    plan.setDeactivated(false);
    plan.setActive(true);
    plan.setPlanStatus(PlanStatus.ACTIVE);
    account.setStatus(AccountStatus.VALID);
    if (company != null) {
      company.setDeactivated(false);
      if (plan.getPlanType() == SPPlanType.IntelligentHiring) {
        company.setPeopleAnalyticsDeactivated(false);
        
      } else {
        company.setErtiDeactivated(false);
        company.setBlockAllMembers(false);
        /*
         * Set the in Erti flag to true for all the erti members in case company has erti account as
         * well.
         */
        
        if (account.getSpPlanMap().containsKey(SPPlanType.Primary)) {
          
          /* get all the erti members and check against the pa emoloyess and set the erti flag. */
          
          List<User> findUsers = userRepository.findUsers("companyId", company.getId());
          
          Collection<? extends HiringUser> allArchivedUsers = hiringUserFactory
              .getAllArchivedUsers(company.getId());
          allArchivedUsers
              .stream()
              .filter(
                  hau -> {
                    boolean userPresent = false;
                    for (User usr : findUsers) {
                      if (usr.getEmail().equalsIgnoreCase(usr.getEmail())
                          && !(usr.hasRole(RoleType.Hiring) && usr.getRoles().size() == 1)) {
                        userPresent = true;
                      }
                    }
                    return userPresent;
                  }).forEach(hau -> {
                hau.setInErti(true);
                hiringUserFactory.updateUser(hau);
              });
          
          List<HiringUser> users = hiringUserFactory.getAll(company.getId(), UserType.Member);
          users
              .stream()
              .filter(
                  hu -> {
                    boolean userPresent = false;
                    for (User usr : findUsers) {
                      if (usr.getEmail().equalsIgnoreCase(hu.getEmail())
                          && !(usr.hasRole(RoleType.Hiring) && usr.getRoles().size() == 1)) {
                        userPresent = true;
                      }
                    }
                    return userPresent;
                  }).forEach(hu -> {
                hu.setInErti(true);
                hiringUserFactory.updateUser(hu);
              });
          
        }
      }
      
    } else {
      User individualUser = userRepository.findByAccountId(account.getId());
      individualUser.setDeactivated(false);
      userRepository.updateGenericUser(individualUser);
    }
    
    if (LocalDate.fromDateFields(plan.getNextPaymentDate()).isBefore(LocalDate.now())) {
      accountUpdationScheduler.processRecharge(account, plan);
      
      DateTime start = new DateTime(plan.getBillingCycleStartDate());
      plan.setExpiresTime(start.plusDays(
          Constants.DAYS_OF_MONTHLY_BILLING * 12 * plan.getAgreementTerm()).toDate());
      // blocking all the users of the company
      if (company != null) {
        company.setBlockAllMembers(false);
      }
    }
    
    plan.setActive(true);
    
    if (company != null) {
      /* update the existing plan, in case the plan is deactivated */
      /* update the company features and roles. */
      plan.getFeatures().stream().forEach(feat -> {
        company.getFeatureList().add(feat);
      });
      
    }
  }
  
  /**
   * Reactivate a cancelled account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the re-activate request
   */
  public SPResponse deactivateAccount(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    String accountId = (String) param[0];
    SPPlanType spPlanType = null;
    if (param[1] != null) {
      spPlanType = (SPPlanType) param[1];
    }
    
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountId cannot be blank or empty");
    }
    // get the user account
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    
    Company company = accountRepository.findCompanybyAccountId(accountId);
    
    if (company != null) {
      
      List<User> users = userRepository.findUsers("companyId", company.getId());
      
      if (account == null) {
        throw new InvalidRequestException("Account not found");
      }
      List<SPFeature> removeFeatures = new ArrayList<SPFeature>();
      if (spPlanType == null) {
        account.setDeactivated(true);
        company.setDeactivated(true);
        
        /* request is to deactivate account, Deactivating all the plans users has access to */
        Collection<SPPlan> plans = account.getSpPlanMap().values();
        for (SPPlan plan : plans) {
          if (plan != null) {
            plan.setActive(false);
            
            /* update the existing plan, in case the plan is deactivated */
            /* update the company features and roles. */
            plan.getFeatures().stream().forEach(feat -> {
              company.getFeatureList().remove(feat);
            });
            removeFeatures.addAll(plan.getFeatures());
          }
        }
        companyFactory.updateCompany(company);
        
      } else {
        SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
        if (spPlan == null) {
          throw new SPException("Plan not subscribed forthe company. cann't deactivate.");
        }
        spPlan.setActive(false);
        if (spPlanType == SPPlanType.IntelligentHiring) {
          company.setPeopleAnalyticsDeactivated(true);
          removeFeatures.addAll(Arrays.asList(SPPlanType.IntelligentHiring.getFeatures()));
          /* update the role in the user */
          users
              .stream()
              .filter(usr -> (usr.hasRole(RoleType.Hiring) && usr.getRoles().size() > 1))
              .forEach(
                  usr -> {
                    Arrays.stream(SPPlanType.IntelligentHiring.getFeatures())
                        .flatMap(feature -> Arrays.stream(feature.getRoles()))
                        .filter(r -> r.isAdminRole()).forEach(usr::removeRole);
                    userRepository.updateUser(usr);
                    HiringUser userByEmail = hiringUserFactory.getUserByEmail(usr.getEmail(),
                        company.getId());
                    userByEmail.removeRole(RoleType.Hiring);
                    hiringUserFactory.updateUser(userByEmail);
                  });
          
        } else {
          company.setErtiDeactivated(true);
          removeFeatures.addAll(Arrays.asList(SPPlanType.Primary.getFeatures()));
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
          
          /* update the role in the user */
          users.stream().forEach(
              usr -> {
                removeFeatures.stream().flatMap(feature -> Arrays.stream(feature.getRoles()))
                    .filter(r -> r.isAdminRole()).forEach(usr::removeRole);
                usr.removeRole(SPPlanType.Primary.getAdminRoleForPlan());
                userRepository.updateUser(usr);
              });
        }
        
      }
    } else {
      SPPlan plan = account.getSpPlanMap() != null ? account.getSpPlanMap().get(SPPlanType.Primary)
          : null;
      
      account.setDeactivated(true);
      User individualUser = userRepository.findByAccountId(account.getId());
      individualUser.setDeactivated(true);
      userRepository.updateUser(individualUser);
      if (plan != null) {
        plan.setActive(false);
      }
    }
    
    accountRepository.updateAccount(account);
    companyFactory.updateCompany(company);
    
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Get the payment history for the account.
   * 
   * @param user
   *          - logged in user
   * @return the payment history response
   */
  public SPResponse getPaymentHistory(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String accountId = (String) params[0];
    SPPlanType spPlanType = (SPPlanType) params[1];
    // get the account and also the payment history
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    
    List<PaymentRecord> paymentRecordList = accountFactory.getPaymentHistory(account, spPlanType);
    List<PaymentRecordDTO> paymentRespList = paymentRecordList.stream().map(PaymentRecordDTO::new)
        .collect(Collectors.toList());
    resp.add(Constants.PARAM_PAYMENT_HISTORY, paymentRespList);
    return resp;
  }
  
  /**
   * Return the pdf for the payment history.s
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameters.
   * @return the SPResponse.
   */
  public SPResponse getPaymentHistoryInvoicePdf(User user, Object[] params) {
    
    String paymentRecordId = (String) params[0];
    
    final HttpServletResponse response = (HttpServletResponse) params[1];
    
    if (StringUtils.isBlank(paymentRecordId)) {
      throw new InvalidRequestException("Payment record id missing ");
    }
    
    PaymentRecord paymentRecord = accountRechargeRepository.findPaymentRecordById(paymentRecordId);
    
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    
    String accountId = (String) params[2];
    Company company = accountRepository.findCompanybyAccountId(accountId);
    String error = null;
    /* check the user type, in case of buisness user fetch the company */
    if (company != null) {
      String companyName = company.getName();
      Address address = company.getAddress();
      paramsMap.put("name", companyName);
      paramsMap.put("address", address);
      paramsMap.put("isBuisness", true);
    } else {
      /* fetch the user associated with the account */
      User individualUser = userRepository.findByAccountId(accountId);
      if (individualUser == null) {
        error = "Account not found.";
      } else {
        paramsMap.put(
            "name",
            MessagesHelper.getMessage("user.name", individualUser.getFirstName(),
                individualUser.getLastName()));
        paramsMap.put("isBuisness", false);
      }
    }
    
    paramsMap.put(Constants.PAYMENT_RECORD, paymentRecord);
    
    if (paymentRecord != null) {
      LOG.info("pyament amount" + paymentRecord.getAmount());
      paramsMap.put("formattedAmount", String.format("%.2f", paymentRecord.getAmount()));
      PaymentType paymentType = paymentRecord.getPaymentInstrument().getPaymentType();
      String paymentTypeFormatted = null;
      if (paymentType == null) {
        paymentTypeFormatted = MessagesHelper.getMessage("admin.account.CREDIT_CARD");
      } else {
        paymentTypeFormatted = MessagesHelper.getMessage("admin.account." + paymentType);
      }
      paramsMap.put(Constants.PARAM_PAYMENT_TYPE, paymentTypeFormatted);
      
    } else {
      error = "Transaction record not found.";
    }
    paramsMap.put(Constants.FEDRAL_TAX_ID, enviornment.getProperty("sp.fedral.taxId"));
    
    ByteArrayOutputStream baos = null;
    if (error != null) {
      baos = new ByteArrayOutputStream();
      try {
        baos.write(error.getBytes());
      } catch (IOException e) {
        LOG.error("Error writing the error message :" + error, e);
      }
    } else {
      baos = pdfCreatorService.createPDF("/templates/email/payment/historyReceipt.stg", paramsMap,
          "default", PaymentReasonType.HISTORY_RECIEPT.toString());
    }
    response.setContentLength(baos.size());
    response.setContentType("application/pdf");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    response.setHeader("Pragma", "public");
    // write ByteArrayOutputStream to the ServletOutputStream
    
    try {
      OutputStream os = response.getOutputStream();
      baos.writeTo(os);
      os.flush();
      os.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
    
  }
  
  /**
   * Filter to return the existing accounts.
   * 
   * @return the existing accounts
   */
  private Predicate<Account> existingAccounts() {
    return ac -> (ac.getPaymentType() == null || ac.getPaymentType() == PaymentType.CREDIT_CARD);
  }
  
  /**
   * Filter the alternate billing accounts.
   * 
   * @return the altterate billing accounts.
   */
  private Predicate<Account> altBillAccounts() {
    return ac -> (ac.getPaymentType() != null && ac.getPaymentType() != PaymentType.CREDIT_CARD);
  }
  
  /**
   * <code>importExistingAccount</code> method will import the existing account.
   * 
   * @param user
   *          administrator
   * @param param
   *          paramters
   * @return response.
   */
  public SPResponse importExistingAccount(User user, Object[] param) {
    
    SPPlanForm singupForm = (SPPlanForm) param[0];
    String accountId = (String) param[1];
    
    if (StringUtils.isBlank(accountId)) {
      throw new InvalidParameterException("AccountID cannot be blank");
    }
    
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    if (account == null) {
      throw new InvalidRequestException("No account found for the account Id passed");
    }
    
    /* update the payment type */
    /* get the spplan */
    SPPlan spPlan = account.getSpPlanMap().get(singupForm.getPlanType());
    if (spPlan != null) {
      spPlan.setPaymentType(singupForm.getPaymentType());
      PaymentInstrument newPaymentInstrumnet = singupForm.createPaymentInstrument();
      accountRepository.createPaymentInstrument(newPaymentInstrumnet);
      
      spPlan.setPaymentInstrumentId(newPaymentInstrumnet.getId());
    }
    
    /* udpate the accont */
    accountRepository.updateAccount(account);
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    return spResponse;
    
  }
  
  /**
   * <code>addAdminMember</code> method will return all the accounts requested presne tin the
   * system.
   * 
   * @param user
   *          system adminsitrator user
   * @param param
   *          contains the type of accounts to be fetched.
   * @return the json response;
   */
  public SPResponse addAdminMember(User user, Object[] param) {
    
    String userEmail = (String) param[0];
    String companyId = (String) param[1];
    SPPlanType planType = (SPPlanType) param[2];
    SPResponse response = new SPResponse();
    ;
    switch (planType) {
    case IntelligentHiring:
      HiringUser hiringUser = getValidEmployee(companyId, userEmail);
      
      Assert.isTrue(!hiringUser.hasRole(RoleType.Hiring), "User already administrator.");
      
      memberControllerHelper.addHiringAdmin(hiringUser, hiringUser);
      
      break;
    case Primary:
      response = memberControllerHelper.addAdminMember(userEmail, companyId, planType);
      
    default:
      break;
    }
    return response.isSuccess();
    
  }
  
  /**
   * <code>removeAdminMember</code> method will return all the accounts requested presne tin the
   * system.
   * 
   * @param user
   *          system adminsitrator user
   * @param param
   *          contains the type of accounts to be fetched.
   * @return the json response;
   */
  public SPResponse removeAdminMember(User user, Object[] param) {
    
    String userEmail = (String) param[0];
    String companyId = (String) param[1];
    SPPlanType planType = (SPPlanType) param[2];
    switch (planType) {
    case IntelligentHiring:
      HiringUser hiringUser = hiringUserFactory.getUserByEmail(userEmail, companyId);
      Assert.notNull(hiringUser, "Hiring user not found.");
      Assert.isTrue(hiringUser.hasRole(RoleType.Hiring), "User not an administrator.");
      
      memberControllerHelper.removeHiringAdmin(hiringUser);
      break;
    case Primary:
      return memberControllerHelper.removeAdminMember(userEmail, companyId, planType);
    default:
      break;
    }
    return new SPResponse().isSuccess();
    
  }
  
  /**
   * This the helper method to retrieve the list of members in the company the user is part of.
   * 
   * @param user
   *          - admin user
   * @return the response for the get member request
   */
  public SPResponse getMembers(User user, Object[] param) {
    String companyId = (String) param[0];
    SPPlanType planType = (SPPlanType) param[1];
    return memberControllerHelper.getMembersForAdmin(companyId, planType);
  }
  
  /**
   * Helper method to get all the members for the given company.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the list of members for the company
   */
  public SPResponse getAllCompanyMembers(User user, Object[] params) {
    String companyId = (String) params[0];
    return memberControllerHelper.getAllMembers(companyId);
  }
  
  /**
   * Update account method will update the account.
   * 
   * @param user
   *          system administrator user
   * @param param
   *          contains the account id and account for parametres.
   * @return the udpate account.
   */
  public SPResponse updateAccount(User user, Object[] param) {
    SPResponse response = new SPResponse();
    String accountId = (String) param[0];
    SignupAccountForm accountForm = (SignupAccountForm) param[1];
    SignupForm signupForm = (SignupForm) param[2];
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    if (account == null) {
      throw new InvalidRequestException("No account found for the account Id passed");
    }
    User userSignup = null;
    if (signupForm != null && signupForm.getEmail() != null) {
      userSignup = userRepository.findByEmail(signupForm.getEmail());
      if (userSignup != null && !signupForm.isExistingMember()) {
        
        /* check if the user belongs to the same ocmpnay or not */
        
        /* get the company for the account id passed */
        Company findCompanybyAccountId = accountRepository.findCompanybyAccountId(accountId);
        if (!userSignup.getCompanyId().equalsIgnoreCase(findCompanybyAccountId.getId())) {
          response.addError("Duplicate_Email",
              MessagesHelper.getMessage("exception.duplicateEmail.signup"));
          throw new InvalidRequestException(
              MessagesHelper.getMessage("exception.duplicateEmail.signup"));
        }
        
      } else {
        if (userSignup == null && signupForm.isExistingMember()) {
          response.addError("UserNotFound", "User not found.");
          throw new InvalidRequestException(MessagesHelper.getMessage("User not found."));
        }
      }
      
    }
    
    validateAccountUpdateRequest(account, accountForm, signupForm);
    
    accountFactory.updateAccount(account, accountForm, signupForm);
    
    return response.isSuccess();
    
  }
  
  private void validateAccountUpdateRequest(Account account, SignupAccountForm signupAccountForm,
      SignupForm signupForm) {
    
    /* get the new plans updated data */
    signupAccountForm.getPlanForms().forEach(
        newPlanForm -> {
          /*
           * check if validity is updated, if updated it can be updated only last month of billing
           * cycle.
           */
          
          BillingCycleType newCycleType = newPlanForm.getBillingCycleType();
          SPPlan spPlan = account.getSpPlanMap().get(newPlanForm.getPlanType());
          if (spPlan != null) {
            if (newCycleType != spPlan.getBillingCycle().getBillingCycleType()
                && newCycleType.getMonths() != spPlan.getBillingCycle().getNoOfMonths()) {
              
              if (LOG.isDebugEnabled()) {
                LOG.debug("Billing Cycle is changed " + newPlanForm.getBillingCycleType());
              }
              /* check if the account next payment date is within next month */
              LocalDate nextPaymentDate = LocalDate.fromDateFields(spPlan.getNextPaymentDate());
              int days = Days.daysBetween(LocalDate.now(), nextPaymentDate).getDays();
              if (days > 30) {
                throw new InvalidRequestException(
                    "Cannot change the billing cycle now. Billing Cycle can be changed during"
                        + " last month of billing payment date ");
              } else {
                spPlan.getBillingCycle().setBillingCycleType(newCycleType);
                spPlan.getBillingCycle().setNoOfMonths(newPlanForm.getNoOfMonths());
              }
              
            }
            
            /*
             * check if plans available no of admins are not less then the existing avalable
             * subscription for both admin and members
             */
            
            SPPlan plan = account.getSpPlanMap().get(newPlanForm.getPlanType());
            if (plan != null && newPlanForm.getPlanType() == plan.getPlanType()) {
              if (plan.getNumAdmin() > Integer.valueOf(newPlanForm.getNumAdmin())
                  || plan.getNumMember() > Integer.valueOf(newPlanForm.getNumCandidates())) {
                throw new InvalidRequestException(
                    "Member added cannot be less then the availble subscriptio for plan "
                        + newPlanForm.getPlanType());
              }
            }
            
          }
        });
    
  }
  
  /**
   * ValidAggreement Terms method will validate the agrreement terms of the account with the primary
   * plan. Primary plan agreement alwayz be more that the other plans.
   * 
   * @param spForms
   *          list of plans.
   */
  @Deprecated
  private void validateAgreementTerm(List<SPPlanForm> spForms) {
    
    if (!CollectionUtils.isEmpty(spForms)) {
      
      SPPlanForm primaryPlan = spForms.stream()
          .filter(sp -> sp.getPlanType() == SPPlanType.Primary).findFirst().get();
      for (SPPlanForm spPlan : spForms) {
        
        if (Integer.valueOf(primaryPlan.getAggreementTerm()) < Integer.valueOf(spPlan
            .getAggreementTerm())) {
          throw new InvalidRequestException("Agreement Term of plan " + spPlan.getPlanType()
              + ", cannot be less then primary Account");
        }
      }
    }
    
  }
  
  /**
   * Get a valid employee from the same company.
   * 
   * @param user
   *          - user
   * @param userId
   *          - user id
   * @return the employee if found
   */
  private HiringUser getValidEmployee(String companyId, String email) {
    HiringUser hiringUser = getValidHiringUser(email, companyId);
    Assert.isTrue(hiringUser.getType() == UserType.Member, "Not a member.");
    return hiringUser;
  }
  
  /**
   * Get the hiring user for the given user id.
   * 
   * @param userId
   *          - user id
   * @param user
   *          - user
   * @return the hiring user
   */
  private HiringUser getValidHiringUser(String email, String companyId) {
    HiringUser hiringUser = hiringUserFactory.getUserByEmail(email, companyId);
    Assert.notNull(hiringUser, "User not found.");
    Assert.notNull(hiringUser.getAnalysis(), "Assessment pending. " + hiringUser.getEmail());
    return hiringUser;
  }
  
}
