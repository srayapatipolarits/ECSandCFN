package com.sp.web.controller.admin.account;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.AddressForm;
import com.sp.web.form.CompanyForm;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @author Dax Abraham
 * 
 *         The controller for account management.
 */
@Controller
public class AccountController {

  @Autowired
  AccountControllerHelper helper;

  /**
   * Gets the available subscriptions for the given users company account.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get available subscriptions request
   */
  @RequestMapping(value = "/admin/account/individual", method = RequestMethod.GET)
  public String getIndividualAccountDetailsView() {
    return "getIndividualAccountDetails";
  }
  
  @RequestMapping(value = "/admin/account/business", method = RequestMethod.GET)
  public String getBusinessAccountDetailsView() {
    return "getBusinessAccountDetails";
  }
  
  @RequestMapping(value = "/admin/manageAccount", method = RequestMethod.GET)
  public String newManageAccountView() {
    return "newManageAccount";
  }  

  /**
   * Gets the account details for the business account associated with the given user.
   * 
   * @param token
   *          - logged in user
   * @return
   *          - the response to the get business account details
   */
  @RequestMapping(value = "/admin/account/businessAccountDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBusinessAccountDetails(@RequestParam SPPlanType planType, Authentication token) {

    // process the add single user from user controller helper
    return process(helper::getBusinessAccountDetails, token, planType);
  }

  /**
   * Controller method to get the individual members account details.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the get individual account details
   */
  @RequestMapping(value = "/admin/account/individualAccountDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getIndividualAccountDetails(Authentication token) {

    return process(helper::getIndividualAccountDetails, token);
  }
  
  
  /**
   * Controller method to get the company details.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the get company details request
   */
  @RequestMapping(value = "/admin/account/getCompanyDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanyDetails(Authentication token) {

    // process the add single user from user controller helper
    return process(helper::getCompanyDetails, token);
  }

  /**
   * Controller method to block all the users.
   * 
   * @param isBlockAllUsers
   *            - flag to set block all user
   * @param token
   *            - logged in user
   * @return
   *      the response to the block all user request
   */
  @RequestMapping(value = "/admin/account/blockAllMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse blockAllUsers(@RequestParam boolean isBlockAllUsers,
      Authentication token) {

    // process the add single user from user controller helper
    return process(helper::blockAllUsers, token, isBlockAllUsers);
  }

  /**
   * Controller method to update the company details.
   * 
   * @param companyForm
   *            - company details
   * @param addressForm
   *            - address details of company
   * @param token
   *            - logged in user
   * @return
   *        the response to the update company details request
   */
  @RequestMapping(value = "/admin/account/updateCompany", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateCompany(@Valid CompanyForm companyForm, @Valid AddressForm addressForm,
      Authentication token) {

    // process the add single user from user controller helper
    return process(helper::updateCompany, token, companyForm, addressForm);
  }

  /**
   * Controller method to cancel the account.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the cancel request
   */
  @RequestMapping(value = "/admin/account/cancel", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse cancel(@RequestParam SPPlanType spPlanType,Authentication token) {

    // process the add single user from user controller helper
    return process(helper::cancel, token,spPlanType);
  }

  /**
   * Controller method to cancel the account.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the cancel request
   */
  @RequestMapping(value = "/admin/account/reActivate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse reActivate(@RequestParam SPPlanType spPlanType, Authentication token) {

    // process the add single user from user controller helper
    return process(helper::reActivate, token, spPlanType);
  }
  
  /**
   * Controller method to update the individual profile settings.
   * 
   * @param isHiringAccessAllowed
   *          - is hiring tool access allowed
   * @param is360ProfileAllowed
   *          - public 360 profile allowed
   * @param token
   *          - logged in user
   * @return
   *        the response to the update profile request
   */
  @RequestMapping(value = "/admin/account/updateIndividualProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateIndividualProfile(@RequestParam boolean isHiringAccessAllowed,
      @RequestParam boolean is360ProfileAllowed, Authentication token) {

    // process the add single user from user controller helper
    return process(helper::updateIndividualProfile, token, isHiringAccessAllowed, is360ProfileAllowed);
  }

  /**
   * The controller method to set the certificate public profile view on or off.
   * 
   * @param isCertificateProfilePublicViewAllowed
   *            - public profile view flag
   * @param token
   *            - logged in user
   * @return
   *    the response to the update request
   */
  @RequestMapping(value = "/admin/account/updateIndividualProfile/spCertificate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateIndividualProfileSpCertificate(
      @RequestParam boolean isCertificateProfilePublicViewAllowed,
      Authentication token) {

    // process the add single user from user controller helper
    return process(helper::updateIndividualProfileSpCertificate, token, isCertificateProfilePublicViewAllowed);
  }
  
  /**
   * Controller method to generate a new token for the individual profile settings.
   * 
   * @param token
   *          - logged in user
   * @return
   *        - the response to the generate request
   */
  @RequestMapping(value = "/admin/account/individualGenerateToken", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse generateProfileToken(Authentication token) {

    // process the add single user from user controller helper
    return process(helper::generateProfileToken, token);
  }
  
  /**
   * Controller method to restrict relationship advisor
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the cancel request
   */
  @RequestMapping(value = "/admin/account/restrictRelationshipAdvisor", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse restrictRelationshipAdvisor(@RequestParam boolean restrictAdvisor, Authentication token) {

    // process the add single user from user controller helper
    return process(helper::restrictRelationshipAdvisor, token,restrictAdvisor);
  }
  
  /**
   * Controller method to get the available member subscriptions.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the number of member subscriptions available
   */
  @RequestMapping(value = "/admin/member/availalbeMemberSubscriptions", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAvailableMemberSubscriptions(@RequestParam SPPlanType spPlanType,Authentication token) {

    // process the add single user from user controller helper
    return process(helper::getAvailableAccounts, token, spPlanType);
  }
  
  /**
   * Controller method to enable the share portrait.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the cancel request
   */
  @RequestMapping(value = "/admin/account/enableSharePortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse enableSharePortrait(@RequestParam boolean enableSharePortrait, Authentication token) {

    // process the add single user from user controller helper
    return process(helper::enableSharePortrait, token,enableSharePortrait);
  }
  
}
