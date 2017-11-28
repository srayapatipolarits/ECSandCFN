package com.sp.web.mvc.signup;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.SignupNotificationsProcessor;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.exception.SignupException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.SPPlanForm;
import com.sp.web.form.SignupAccountForm;
import com.sp.web.form.SignupAssistedAccountForm;
import com.sp.web.form.SignupForm;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.AssistedAccount;
import com.sp.web.model.AssistedAccountStatus;
import com.sp.web.model.Company;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Password;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.PaymentType;
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
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.payment.PaymentGatewayFactory;
import com.sp.web.product.AccountFactory;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author daxabraham This is the helper class for signing up the users.
 */
@Component
public class SignupHelper implements Serializable {
  
  private static final long serialVersionUID = -7940983883288561670L;

  private static final Logger LOG = Logger.getLogger(SignupHelper.class);
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  /**
   * Password Encoder holding the {@link BCryptPasswordEncoder} class reference.
   */
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private AccountFactory accountFactory;
  
  @Autowired
  @Qualifier("signupNotificationsProcessor")
  SignupNotificationsProcessor notificationsProcessor;
  
  @Autowired
  CommunicationGateway emailGateway;
  
  @Autowired
  Environment env;
  
  @Autowired
  private TokenRepository tokenRepository;
  
  @Autowired
  private PaymentGatewayFactory gatewayFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  /**
   * Method to get the user from the signup form.
   * 
   * @param signupForm
   *          - the signup form
   * @param existingUser
   *          - if user is already existing in the system
   * @return - the user
   */
  private User getUser(SignupForm signupForm, User existingUser) {
    User user = new User();
    if (signupForm.isExistingMember()) {
      BeanUtils.copyProperties(existingUser, user);
      // resetting the group associations
      userFactory.cleanUser(user, existingUser);
      /* remove the old user from PA from that company if exists */
      HiringUser userByEmail = hiringUserFactory.getUserByEmail(existingUser.getEmail(),
          existingUser.getCompanyId());
      if (userByEmail != null) {
        hiringUserFactory.delete(userByEmail);
      }
      
    } else {
      user.setEmail(signupForm.getEmail());
      user.setFirstName(signupForm.getFirstName());
      user.setLastName(signupForm.getLastName());
      if (StringUtils.isNotBlank(signupForm.getPassword())) {
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
      }
      user.setTitle(signupForm.getTitle());
      user.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
      user.setCreatedOn(LocalDate.now());
    }
    return user;
  }
  
  /**
   * Get the company bean for signup.
   * 
   * @param signupForm
   *          - creates the company bean from the form
   * @return - the newly create company bean
   */
  private Company getCompanyBean(SignupForm signupForm, AddressForm addressForm) {
    Company company = new Company();
    company.setName(addressForm.getCompany());
    addressForm.update(company);
    company.setIndustry(addressForm.getIndustry());
    company.setNumberOfEmployees(addressForm.getNumEmp());
    company.setPhoneNumber(addressForm.getPhone());
    return company;
  }
  
  /**
   * Signup the business account.
   * 
   * @param signupForm
   *          - the sign up form to get the Company and User details
   * @param addressForm
   *          - address form for the company address
   * @param accountForm
   *          - account details for sign up
   * @return - true on signup success
   * 
   * @throws SignupException
   *           - Exception while signing up
   */
  public boolean signupBusiness(SignupForm signupForm, AddressForm addressForm,
      SignupAccountForm accountForm, HttpServletRequest request) throws SignupException {
    
    return process(signupForm, addressForm, accountForm, null, true);
  }
  
  /**
   * <code>signupBusines</code> method will signup the buisness account.
   * 
   * @param signupForm
   *          contains the initial information for user, address/name/ title etc.
   * @param addressForm
   *          contains the address of the company.
   * @param accountForm
   *          contains the agreement term, payment type other information related ot account.
   * @param planForm
   *          contains the planForm for the user.
   * @param request
   *          http servlet request ot fetch detail from the session.
   * @return the true or false in case signup is success.
   */
  public boolean signupBusiness(SignupForm signupForm, AddressForm addressForm,
      SignupAccountForm accountForm, List<SPPlanForm> planForm, HttpServletRequest request) {
    return process(signupForm, addressForm, accountForm, planForm, true);
  }
  
  /**
   * Signup the business account.
   * 
   * @param signupForm
   *          - the sign up form to get the Company and User details
   * @param addressForm
   *          - address form for the company address
   * @param accountForm
   *          - account details for sign up
   * @param request
   *          - http request
   * @return - true on signup success
   * 
   * @throws SignupException
   *           - Exception while signing up
   */
  public boolean signupIndividual(SignupForm signupForm, AddressForm addressForm,
      SignupAccountForm accountForm, HttpServletRequest request) throws SignupException {
    return process(signupForm, addressForm, accountForm, null, false);
  }
  
  /**
   * Signup the business account.
   * 
   * @param signupForm
   *          - the sign up form to get the Company and User details
   * @param addressForm
   *          - address form for the company address
   * @param accountForm
   *          - account details for sign up
   * @param planForm
   * @param isBusiness
   *          - if this is a business account registration
   * @param request
   *          - the http request
   * @return - true on signup success
   * 
   * @throws SignupException
   *           - Exception while signing up
   */
  private boolean process(SignupForm signupForm, AddressForm addressForm,
      SignupAccountForm accountForm, List<SPPlanForm> planForm, boolean isBusiness) {
    
    // check if the user already exists to account for the back button
    String userEmail = signupForm.getEmail();
    final User findByEmail = userRepository.findByEmail(userEmail);
    if (signupForm.isExistingMember()) {
      if (findByEmail == null) {
        throw new InvalidRequestException("Existing user not found.");
      }
    } else {
      if (findByEmail != null) {
        throw new InvalidRequestException("User already exists in the system.");
      }
    }
    
    Account account = new Account();
    
    PaymentInstrument paymentInstrument = null;
    Date startDate = DateTime.now().toDate();
    account.setStartDate(startDate);
    
    if (isBusiness) {
      account.setType(AccountType.Business);
    } else {
      account.setType(AccountType.Individual);
    }
    
    account.setReferSource(signupForm.getReferSource());
    String accountNo = accountRepository.getNextAccountNumber();
    
    account.setAccountNumber(accountNo);
    account.setCustomerProfileId(accountNo);
    
    /* only 1 plan can be activated in 1 time */
    if (planForm.size() != 1) {
      throw new SPException("Only 1 plan can be created");
    }
    SPPlanForm spForm = accountForm.getPlanForms().get(0);
    SPPlan spPlan = spForm.getSPPlan();
    /* Make the splas as active in case */
    spPlan.setAgreementTerm(Integer.valueOf(spForm.getAggreementTerm()));
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime aggreementEndDate = now.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12
        * Integer.valueOf(spForm.getAggreementTerm()));
    spPlan.setAggreementEndDate(aggreementEndDate);
    if (spForm.getPaymentType() == null) {
      spPlan.setPaymentType(PaymentType.CREDIT_CARD);
    } else {
      spPlan.setPaymentType(spForm.getPaymentType());
    }
    
    if (Boolean.valueOf(spForm.getIsTrial())) {
      spPlan.setPlanStatus((PlanStatus.TRIAL));
    } else {
      spPlan.setPlanStatus((PlanStatus.NEW));
    }
    /* check for mandatory admin */
    if (spPlan.getNumAdmin() <= 0) {
      spPlan.setNumAdmin(1);
    }
    spPlan.setTagsKeywords(spForm.getTags());
    spPlan.setCreditBalance(spForm.getCreditAmount());
    account.getSpPlanMap().put(spPlan.getPlanType(), spPlan);
    PaymentRecord paymentRecord = accountFactory.processNewAccount(account, spPlan,
        spForm.createPaymentInstrument());
    
    /*
     * this is not required as the validation no longer holds for agreement. Both the plans can co
     * exist independently.
     */
    
    /* validate the agreement Term for the spplan */
    // validateAgreementTerm(account);
    
    Company company = null;
    
    try {
      
      if (isBusiness) {
        // create company
        company = getCompanyBean(signupForm, addressForm);
        company.setAccountId(account.getId());
        company.setSharePortrait(true);
        company.setEnhancePasswordSecurity(accountForm.isEnhancedPasswordSecurity());
        for (SPPlan sp : account.getSpPlanMap().values()) {
          if (spPlan.getPlanType() == SPPlanType.IntelligentHiring) {
            company.getFeatureList().add(SPFeature.Hiring);
            company.setPeopleAnalyticsDeactivated(false);
          } else {
            company.getFeatureList().addAll(sp.getFeatures());
            company.setErtiDeactivated(false);
          }
          
        }
        accountRepository.createCompany(company);
      }
      
      // Create user
      User user = createNewUser(signupForm, addressForm, isBusiness, findByEmail, account, company,
          spPlan);
      
      if (isBusiness && spPlan.getPlanType() == SPPlanType.Primary) {
        // remove one subscription for the newly added business user.
        account.reserveSubscritption(spPlan.getPlanType());
        accountRepository.updateAccount(account);
      }
      
      /* We don;t need to login the ser when the user is created by admin */
      if (account.getPaymentType() == PaymentType.CREDIT_CARD) {
        loginHelper.authenticateUserAndSetSession(user.getEmail(), signupForm.getPassword());
        
        // send the email notification to the user
        notificationsProcessor.process(NotificationType.Signup, user, account, paymentRecord);
      } else {
        // create the token and get the token url
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put(Constants.PARAM_USER_ID, user.getId());
        paramsMap.put(Constants.PARAM_USER_EMAIL, user.getEmail());
        Token token;
        if (isBusiness) {
          token = tokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
              TokenProcessorType.ADD_MEMBER);
        } else {
          token = tokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
              TokenProcessorType.ADD_INDIVIDUAL_MEMBER);
        }
        // setting the token to the user
        user.setTokenUrl(token.getTokenUrl());
        userRepository.updateUser(user);
        NotificationType notificationType = spPlan.getPlanType() == SPPlanType.Primary ? NotificationType.AltBillSignup
            : NotificationType.AltBillingPASubscribed;
        notificationsProcessor.process(notificationType, user, account, paymentRecord);
      }
      
      try {
        EmailParams emailParams = new EmailParams("templates/email/sf/salesForceSignup.stg",
            env.getProperty("salesforce.email", "support@surepeople.com"),
            MessagesHelper.getMessage("salesForce.email.subject"), Constants.DEFAULT_LOCALE);
        emailParams.addParam(Constants.PARAM_USER, user);
        emailParams.addParam(Constants.PARAM_ACCOUNT, account);
        if (company != null) {
          emailParams.addParam(Constants.PARAM_COMPANY, company);
        }
        emailParams.addParam(Constants.PARAM_SPPLANS, account.getSpPlanMap().values());
        emailParams.addParam(Constants.PARAM_NOTIFICATION_TYPE,
            NotificationType.SalesForceNewAccount);
        emailParams.addParam(Constants.PARAM_PAYMENT_RECORD, paymentRecord);
        // emailParams.addParam(Constants.PARAM_PROMOTION, accountForm.getPromotionId());
        emailGateway.sendMessage(emailParams);
      } catch (Exception e) {
        LOG.warn("Error registering with sales force.", e);
      }
    } catch (SPException spe) {
      rollback(account, paymentInstrument, company);
      throw spe;
    } catch (Exception e) {
      LOG.fatal("Sign up failed !!!", e);
      rollback(account, paymentInstrument, company);
      throw new SignupException("Sign up failed !!!", e);
    }
    /* set the accountId to return for the front end for the newly created account */
    accountForm.setAccId(account.getId());
    return true;
  }
  
  public User createNewUser(SignupForm signupForm, AddressForm addressForm, boolean isBusiness,
      final User findByEmail, Account account, Company company, SPPlan splan) {
    // create the user
    User user = getUser(signupForm, findByEmail);
    if (isBusiness && company != null) {
      user.setCompanyId(company.getId());
    } else {
      signupForm.updateIndividualUserDetails(user, addressForm);
    }
    Set<RoleType> roles = new HashSet<RoleType>();
    if (splan.getPlanType() == SPPlanType.Primary) {
      roles.add(RoleType.User);
      roles.add(RoleType.AccountAdministrator);
    }
    if (isBusiness && company != null) {
      
      company.getFeatureList().addAll(splan.getFeatures());
      splan.getFeatures().stream().forEach(feat -> {
        for (RoleType role : feat.getRoles()) {
          if (role.isAdminRole()) {
            roles.add(role);
          }
        }
      });
    } else {
      roles.add(RoleType.IndividualAccountAdministrator);
      
      if (account.getPaymentType() == PaymentType.CREDIT_CARD || account.getPaymentType() == null) {
        user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      } else {
        if (!signupForm.isExistingMember()) {
          // For individual users created by system adminstrator will not have password to therie
          // profile, so adding them to this flow */
          user.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
        }
      }
      
      user.setAccountId(account.getId());
    }
    // for business/individual first time registering, status will be assessment pending. Profile
    // incomplete
    user.setRoles(roles);
    
    userRepository.createUser(user);
    
    /*
     * in case it is an existing user functioanltiy and user has already completed his assessment,
     * then create an PA employe for the user
     */
    
    if (SPPlanType.IntelligentHiring == splan.getPlanType() && signupForm.isExistingMember()) {
      
      if (user.getAnalysis() != null) {
        HiringUser hiringUser = new HiringUser(user);
        hiringUser.addRole(RoleType.Hiring);
        hiringUserFactory.updateUser(hiringUser);
      }
    }
    return user;
  }
  
  private void rollback(Account account, PaymentInstrument paymentInstrument, Company company) {
    if (account != null && account.getId() != null) {
      accountRepository.removeAccount(account);
    }
    
    if (paymentInstrument != null && paymentInstrument.getId() != null) {
      accountRepository.removePaymentInstrument(paymentInstrument);
    }
    
    if (company != null && company.getId() != null) {
      accountRepository.removeCompany(company);
    }
  }
  
  /**
   * Sign up the assisted business account.
   * 
   * @param signupForm
   *          - user details
   * @param assistedForm
   *          - address form
   * @return true if request created successfully
   */
  public boolean signupAssisted(SignupForm signupForm, SignupAssistedAccountForm assistedForm) {
    
    try {
      // validate the user information
      AssistedAccount account = getAssistedAccount(assistedForm, signupForm);
      accountRepository.createAssistedAccount(account);
    } catch (SignupException e) {
      throw e;
    } catch (Exception e) {
      LOG.warn("Error signing up assisted business account !!!", e);
      throw new SignupException("Error signing up assisted business account !!!", e);
    }
    
    return true;
  }
  
  /**
   * Manually validate the details for the assisted account creation.
   * 
   * @param assistedForm
   *          - the account form for the details of the user
   * @param signupForm
   *          - the signup form
   * @return the validated assisted account
   * 
   * @throws SignupException
   *           - error while signing up the assisted user
   */
  private AssistedAccount getAssistedAccount(SignupAssistedAccountForm assistedForm,
      SignupForm signupForm) throws SignupException {
    AssistedAccount account = new AssistedAccount();
    account.setName(assistedForm.getName());
    account.setEmail(signupForm.getEmail());
    account.setNumEmployees(assistedForm.getNumEmp());
    account.setPhone(assistedForm.getPhone());
    account.setCompany(assistedForm.getCompany());
    account.setStatus(AssistedAccountStatus.New);
    account.setCreatedOn(DateTime.now().toDate());
    return account;
  }
  
  /**
   * <code>singUpMember</code> method will signup the user.
   * 
   * @param param
   *          contains the profile and address info
   * @return the the SPResponse.
   */
  public SPResponse signupMember(Object[] param) {
    
    SPResponse resp = new SPResponse();
    UserProfileForm userProfileForm = (UserProfileForm) param[0];
    AddressForm addressForm = (AddressForm) param[1];
    String orgPassword = (String) param[2];
    HttpSession session = (HttpSession) param[3];
    String tokenStr = (String) param[4];
    
    Token token = (Token) session.getAttribute(Constants.PARAM_TOKEN);
    if (token == null) {
      /* fetch the token from the repository */
      token = tokenRepository.findTokenById(tokenStr);
    }
    
    if (token != null && token.isValid()) {
      // check if the view to redirect is same as the one set in the
      // token
      if (token.getRedirectToView().equalsIgnoreCase(Constants.VIEW_ADD_MEMBER)) {
        // updating the user profile
        User user = userRepository.updateUserProfile(userProfileForm, addressForm);
        // update the password
        
        String encodedPassword = passwordEncoder.encode(orgPassword);
        
        Password newPassword = Password.newPassword(encodedPassword, encodedPassword);
        user.setPassword(encodedPassword);
        user.setPasswords(newPassword);
        userRepository.updateUser(user);
        
        // invalidate the token and persist to the database
        // token.invalidate("Request completed successfully !!!");
        // tokenFactory.persistToken(token);
        
        loginHelper.authenticateUserAndSetSession(user.getEmail(), orgPassword);
        resp.isSuccess();
      } else {
        resp.addError("error", "Token expected for add member but incorrect destination set.");
        // token.invalidate("Incorrect request redirect expected " + Constants.VIEW_ADD_MEMBER
        // + " but found :" + token.getRedirectToView());
        // tokenFactory.persistToken(token);
      }
    } else {
      resp.addError("error", "Unauthorized request !!!");
    }
    return resp;
    
  }
  
  /**
   * <code>singUpMember</code> method will signup the user.
   * 
   * @param param
   *          contains the profile and address info
   * @return the the SPResponse.
   */
  public SPResponse signupIndividualMember(Object[] param) {
    
    SPResponse resp = new SPResponse();
    UserProfileForm userProfileForm = (UserProfileForm) param[0];
    AddressForm addressForm = (AddressForm) param[1];
    String orgPassword = (String) param[2];
    HttpSession session = (HttpSession) param[3];
    String tokenStr = (String) param[4];
    
    Token token = (Token) session.getAttribute(Constants.PARAM_TOKEN);
    if (token == null) {
      /* fetch the token from the repository */
      token = tokenRepository.findTokenById(tokenStr);
    }
    
    if (token != null && token.isValid()) {
      // check if the view to redirect is same as the one set in the
      // token
      if (token.getRedirectToView().equalsIgnoreCase(Constants.VIEW_ADD_INDIVIDUAL_MEMBER)) {
        // updating the user profile
        User user = userRepository.updateUserProfile(userProfileForm, addressForm);
        // update the password
        String encodedPassword = passwordEncoder.encode(orgPassword);
        userRepository.updatePassword(user.getEmail(), encodedPassword);
        // invalidate the token and persist to the database
        token.invalidate("Request completed successfully !!!");
        tokenFactory.persistToken(token);
        
        loginHelper.authenticateUserAndSetSession(user.getEmail(), orgPassword);
        resp.isSuccess();
      } else {
        resp.addError("error",
            "Token expected for add individual member but incorrect destination set.");
        token.invalidate("Incorrect request redirect expected "
            + Constants.VIEW_ADD_INDIVIDUAL_MEMBER + " but found :" + token.getRedirectToView());
        tokenFactory.persistToken(token);
      }
    } else {
      resp.addError("error", "Unauthorized request !!!");
    }
    return resp;
    
  }
}
