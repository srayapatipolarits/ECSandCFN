package com.sp.web.controller.admin.alternatebilling;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.response.UserResponse;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.CompanyForm;
import com.sp.web.form.SPPlanForm;
import com.sp.web.form.SignupAccountForm;
import com.sp.web.form.SignupForm;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.AccountType;
import com.sp.web.model.PaymentType;
import com.sp.web.model.ProductType;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 *
 * <code>AlternateBillingCOntorllelr</code> class provides the intefaces for creating accounts in sp
 * alternate to credit card.
 * 
 * @author pradeepruhil
 *
 */
@Controller
@Scope("session")
@Audit(type = ServiceType.ALTERNATE_BILLING)
public class AlternateBillingController {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(AlternateBillingController.class);
  
  /** Alternate billing helper class. */
  @Autowired
  private AlternateBillingControllerHelper alternateBillingControllerHelper;
  
  @Autowired
  private UserRepository userRepository;
  
  public AddressForm getAddressForm() {
    return addressForm;
  }
  
  public void setAddressForm(AddressForm addressForm) {
    this.addressForm = addressForm;
  }
  
  private AddressForm addressForm;
  
  /**
   * getAllAccounts method will return all the account information of the user.
   * 
   * @param accountType
   *          for which detail is to be returend.
   * @param token
   *          of the user.
   * @return the
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/getAllAccounts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllAccounts(@RequestParam AccountType accountType,
      @RequestParam(defaultValue = "false", required = false) boolean isExistingAccount,
      Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::getAllAccounts, token,
        accountType, isExistingAccount);
  }
  
  /**
   * getPlanFeatures method will return all the spFeatures associated with the plan.
   * 
   * @param token
   *          of the user.
   * @return the
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/getPlansFeature", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPlanFeatures(Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::getPlanFeatures, token);
  }
  
  /**
   * getPlanFeatures method will return all the spFeatures associated with the plan.
   * 
   * @param token
   *          of the user.
   * @return the
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/addAdminMember", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addAdminMember(@RequestParam String userEmail, @RequestParam String companyId,
      @RequestParam SPPlanType planType, Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::addAdminMember, token,
        userEmail, companyId, planType);
  }
  
  /**
   * getPlanFeatures method will return all the spFeatures associated with the plan.
   * 
   * @param token
   *          of the user.
   * @return the
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/removeAdminMember", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeAdminMember(@RequestParam String userEmail,
      @RequestParam String companyId, @RequestParam SPPlanType planType, Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::removeAdminMember, token,
        userEmail, companyId, planType);
  }
  
  /**
   * Method to sign up the business user step 1.
   * 
   * @param signupForm
   *          - the sign up form for user and company details
   * @param addressForm
   *          - the company address
   * @param bindingResult
   *          - if there are any validation errors
   * @return the json response for the sign up request
   */
  @Audit("")
  @RequestMapping(value = "/sysAdmin/alternateBilling/signup/business", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupStep1(@Valid AddressForm addressForm, BindingResult bindingResult) {
    LOG.debug("The sign up form received :" + addressForm + ":" + bindingResult);
    SPResponse spResponse = new SPResponse();
    
    if (bindingResult.hasErrors()) {
      for (FieldError err : bindingResult.getFieldErrors()) {
        spResponse.addError(err);
      }
    } else {
      // validate the email
      
      this.addressForm = addressForm;
      spResponse.isSuccess();
    }
    LOG.debug("Sending response :" + spResponse);
    return spResponse;
  }
  
  /**
   * The controller method to signup business account.
   * 
   * @param accountForm
   *          - the account details form
   * @param creditAmount
   *          credit amount which is to be created.
   * @return - response for the signup
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/signup/business/account", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupBusinessAccountStep2(@Valid SignupAccountForm accountForm,
      @Valid SignupForm signupForm, HttpServletRequest request, Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::createBusinessAccount, token,
        accountForm, signupForm, addressForm, request);
  }
  
  /**
   * Gets the account details for the business account associated with the given user.
   * 
   * @param token
   *          - logged in user
   * @return - the response to the get business account details
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/businessAccountDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBusinessAccountDetails(Authentication token, @RequestParam String accountId,
      @RequestParam(defaultValue = "false", required = false) boolean isEditAccountRequest) {
    
    // process the add single user from user controller helper
    return process(alternateBillingControllerHelper::getBusinessAccountDetails, token, accountId,
        isEditAccountRequest);
  }
  
  /**
   * Controller method to get the individual members account details.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get individual account details
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/individualAccountDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getIndividualAccountDetails(Authentication token, @RequestParam String accountId) {
    return process(alternateBillingControllerHelper::getIndividualAccountDetails, token, accountId);
  }
  
  /**
   * Controller method to add the member accounts.
   * 
   * @param productId
   *          - product id
   * @param promotionId
   *          - promotion id
   * @param numAccounts
   *          - num of accounts to add
   * @param token
   *          logged in user
   * @return the response to the add request
   */
  // @RequestMapping(value = "/sysAdmin/alternateBilling/account/addMemberAccounts", method =
  // RequestMethod.POST)
  // @ResponseBody
  // public SPResponse addMemberAccounts(@RequestParam String productId,
  // @RequestParam(required = false) String promotionId, @RequestParam int numAccounts,
  // @RequestParam String accountId, Authentication token) {
  //
  // // process the request
  // return process(alternateBillingControllerHelper::addCandidates, token, productId, promotionId,
  // numAccounts, accountId);
  // }
  
  /**
   * Controller method to add the member accounts.
   * 
   * @param productId
   *          - product id
   * @param promotionId
   *          - promotion id
   * @param numAccounts
   *          - num of accounts to add
   * @param token
   *          logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/addMemberAccounts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addMemberAccounts(@Valid SPPlanForm spPlanForm, @RequestParam String accountId,
      Authentication token) {
    
    // process the request
    return process(alternateBillingControllerHelper::addMemberAccounts, token, spPlanForm,
        accountId);
  }
  
  /**
   * Controller method to cancel the account.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/cancel", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse cancel(Authentication token, @RequestParam String accountId,
      @RequestParam SPPlanType planType) {
    
    // process the add single user from user controller helper
    return ControllerHelper.process(alternateBillingControllerHelper::cancel, token, accountId,
        planType);
  }
  
  /**
   * Controller method to cancel the account.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/reActivate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse reActivate(Authentication token, @RequestParam String accountId,
      @RequestParam SPPlanType planType) {
    
    // process the add single user from user controller helper
    return ControllerHelper.process(alternateBillingControllerHelper::reActivate, token, accountId,
        planType);
  }
  
  /**
   * Controller method for the get products.
   * 
   * @param productType
   *          - the product type
   * @return the response to the get product request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/signup/getProducts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProducts(Authentication token, @RequestParam ProductType productType) {
    return ControllerHelper.process(alternateBillingControllerHelper::getProducts, token,
        productType);
  }
  
  /**
   * Controller method to update the company details.
   * 
   * @param companyForm
   *          - company details
   * @param addressForm
   *          - address details of company
   * @param token
   *          - logged in user
   * @return the response to the update company details request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/updateCompany", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateCompany(@Valid CompanyForm companyForm, @Valid AddressForm addressForm,
      @RequestParam String companyId, Authentication token) {
    
    // process the add single user from user controller helper
    return ControllerHelper.process(alternateBillingControllerHelper::updateCompany, token,
        companyForm, companyId, addressForm);
  }
  
  /**
   * This controller method updates the user profile with the given data.
   * 
   * @param userProfile
   *          - the user profile to update
   * @param address
   *          - the address to update
   * @param token
   *          - token to get currently logged in user
   * @return the response to the administrator update action
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/updateIndividualProfile", method = RequestMethod.POST)
  @ResponseBody
  public UserResponse updateUserProfile(UserProfileForm userProfile, AddressForm address,
      Authentication token) {
    
    UserResponse response = new UserResponse();
    
    try {
      
      User userToUpdate = userRepository.findByEmail(userProfile.getEmail());
      if (userToUpdate == null) {
        throw new InvalidRequestException("User not found :" + userProfile.getEmail());
      }
      
      // perform the update action
      userRepository.updateUserProfile(userProfile, address);
      
      // adding the updated user to the response
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    // the response
    return response;
  }
  
  /**
   * Controller method to process the add credit to the user account
   * 
   * @param accountId
   *          - the accountId id
   * @param chargeAmount
   *          charge amount
   * @param tokentoken
   * @param spPlanType
   *          is the plan type for which credit is be added. .
   * @return the respon for the add credit result.
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/addCredit", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addCredit(@RequestParam String accountId, @RequestParam SPPlanType spPlanType,
      @RequestParam double chargeAmount, @RequestParam PaymentType paymentType,
      @RequestParam String comment, Authentication token) {
    // process the request
    return ControllerHelper.process(alternateBillingControllerHelper::addCredit, token, accountId,
        chargeAmount, paymentType, comment, spPlanType);
  }
  
  /**
   * Method to sign up the individual user step 1.
   * 
   * @param signupForm
   *          - the sign up form for user and company details
   * @param addressForm
   *          - the company address
   * @param bindingResult
   *          - if there are any validation errors
   * @return the json response for the sign up request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/signup/individual", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupIndividualStep1(@Valid SignupForm signupForm,
      @Valid AddressForm addressForm, BindingResult bindingResult) {
    LOG.debug("The sign up form received :" + signupForm + ":" + addressForm + ":" + bindingResult);
    SPResponse spResponse = new SPResponse();
    
    if (bindingResult.hasErrors()) {
      bindingResult.getAllErrors().forEach(err -> spResponse.addError((FieldError) err));
    } else {
      // validate the email
      User user = userRepository.findByEmail(signupForm.getEmail());
      if (user != null) {
        spResponse.addError("Duplicate_Email",
            MessagesHelper.getMessage("exception.duplicateEmail.signup"));
      } else {
        this.addressForm = addressForm;
        spResponse.isSuccess();
      }
    }
    LOG.debug("Sending response :" + spResponse);
    return spResponse;
  }
  
  /**
   * This method signs up an individual user.
   * 
   * @param accountForm
   *          - the account details to signup
   * @return - SPResponse indicating success or failure of operation
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/signup/individual/account", method = RequestMethod.POST)
  @ResponseBody
  @Deprecated
  public SPResponse signupIndividualAccount(@Valid SignupAccountForm accountForm,
      @Valid SignupForm signupForm, HttpServletRequest request, Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::signUpIndividualAccount,
        token, signupForm, addressForm, accountForm, request);
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/business", method = RequestMethod.GET)
  public String anBusinessView() {
    return "businessAB";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/individual", method = RequestMethod.GET)
  public String anindividualView() {
    return "individualAB";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/businessStep1Form", method = RequestMethod.GET)
  public String businessStep1() {
    return "businessStep1";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/individualStep1Form", method = RequestMethod.GET)
  public String individualStep1() {
    return "individualStep1";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/businessStep2Form", method = RequestMethod.GET)
  public String businessStep2() {
    if (addressForm == null) {
      return "redirect:businessStep1Form";
    } else {
      return "businessStep2";
    }
    
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/individualStep2Form", method = RequestMethod.GET)
  public String individualStep2() {
    return "individualStep2";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/viewDetailAccount", method = RequestMethod.GET)
  public String viewDetailAccount() {
    return "viewDetailAccount";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/viewDetailAccountIndividual", method = RequestMethod.GET)
  public String viewDetailAccountIndividual() {
    return "viewDetailAccountIndividual";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/importContacts", method = RequestMethod.GET)
  public String abImportContacts() {
    return "abImportContacts";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/archivedBusinessContacts", method = RequestMethod.GET)
  public String archivedBusinessContacts() {
    return "archivedBusinessContacts";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/archivedIndividualContacts", method = RequestMethod.GET)
  public String archivedIndividualContacts() {
    return "archivedIndividualContacts";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/importContactsIndividual", method = RequestMethod.GET)
  public String abImportContactsIndi() {
    return "abImportContactsIndi";
  }
  
  /**
   * Controller method to deactivate the account.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/deactivateAccount", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deactivateAccount(Authentication token, @RequestParam String accountId,
      @RequestParam(required = false) SPPlanType spPlanType) {
    
    // process the add single user from user controller helper
    return ControllerHelper.process(alternateBillingControllerHelper::deactivateAccount, token,
        accountId, spPlanType);
  }
  
  /**
   * Controller method to reactiavte the account.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/reActivateAccount", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse reActivateAccont(Authentication token, @RequestParam String accountId,
      @RequestParam(required = false) SPPlanType spPlanType) {
    
    // process the add single user from user controller helper
    return ControllerHelper.process(alternateBillingControllerHelper::reactivateAccount, token,
        accountId, spPlanType);
  }
  
  /**
   * Get the payment history for the account.
   * 
   * @param token
   *          - logged in user
   * @param accountId
   *          of the account.
   * @param spPlanType
   *          for which plan the paymentHistory is to be find.
   * @return the response to the payment history
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/paymentHistory", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPaymentHistory(Authentication token, @RequestParam String accountId,
      @RequestParam SPPlanType spPlanType) {
    // process the request
    return process(alternateBillingControllerHelper::getPaymentHistory, token, accountId,
        spPlanType);
  }
  
  /**
   * <code>getPaymentHistoryInvoicePdf</code> method wil download the pdf invoice the service.
   * 
   * @param paymentId
   *          for which pdf to be downloaded.
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/paymentHistory/invoice/{paymentId}/{accountId}")
  public void getPaymentHistoryInvoicePdf(@PathVariable String paymentId, Authentication token,
      HttpServletResponse httpServletResponse, @PathVariable String accountId) {
    process(alternateBillingControllerHelper::getPaymentHistoryInvoicePdf, token, paymentId,
        httpServletResponse, accountId);
  }
  
  /**
   * <code>getPaymentHistoryInvoicePdf</code> method wil download the pdf invoice the service.
   * 
   * @param paymentId
   *          for which pdf to be downloaded.
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/importExisting", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPaymentHistoryInvoicePdf(SPPlanForm accountForm,
      @RequestParam String accountId, Authentication token) {
    return process(alternateBillingControllerHelper::importExistingAccount, token, accountForm,
        accountId);
  }
  
  /**
   * Controller method to return the list of members for the company.
   * 
   * @param token
   *          - logged in user
   * @return the response for the get member list request
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/members", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getMembers(@RequestParam String companyId, @RequestParam SPPlanType planType,
      Authentication token) {
    
    // process the list of members from user controller helper
    return process(alternateBillingControllerHelper::getMembers, token, companyId, planType);
  }
  
  /**
   * Method to get all the member for the given company.
   * 
   * @param companyId
   *          - company id
   * @param token
   *          - logged in user
   * @return the list of members for that company
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/allCompanyMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllCompanyMembers(@RequestParam String companyId, Authentication token) {
    // process the list of members from user controller helper
    return process(alternateBillingControllerHelper::getAllCompanyMembers, token, companyId);
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/updateAccount", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateAccount(@RequestParam String accountId,
      @Valid SignupAccountForm accountForm, SignupForm signupForm,Authentication token) {
    return ControllerHelper.process(alternateBillingControllerHelper::updateAccount, token,
        accountId, accountForm,signupForm);
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/add/memberalternativeaccounts", method = RequestMethod.GET)
  public String addAlternativeHiringCandidates() {
    return "addAlternativeHiringCandidates";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/add/candidatealternativeaccounts", method = RequestMethod.GET)
  public String getAlternateHiringProductInfo() {
    return "getAlternateHiringProductInfo";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/account/add/addCredit", method = RequestMethod.GET)
  public String addCredit() {
    return "addCredit";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/alternateAccount/payment/historybusiness", method = RequestMethod.GET)
  public String getPaymenthistorybusinessView() {
    return "getPaymentHistoryalternate";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/alternateAccount/payment/historyindividual", method = RequestMethod.GET)
  public String getPaymentHistoryalternateView() {
    return "getPaymentHistoryalternate";
  }
  
  @RequestMapping(value = "/sysAdmin/alternateBilling/editAccount", method = RequestMethod.GET)
  public String editAccount() {
    return "businessStep2";
  }
  
}
