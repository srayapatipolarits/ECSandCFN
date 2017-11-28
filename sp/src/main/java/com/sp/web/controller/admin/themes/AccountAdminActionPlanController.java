package com.sp.web.controller.admin.themes;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.response.UserResponse;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.SPException;
import com.sp.web.form.goal.ActionPlanForm;
import com.sp.web.form.goal.ManageActionPlanForm;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>AccountActionPlanController</code> class will fetch the organization ActionPlans for
 * Account Admin.
 * 
 * @author Prasanna Venkatesh
 *
 */

@Controller
public class AccountAdminActionPlanController {
  
  private static final Logger log = Logger.getLogger(AccountAdminActionPlanController.class);
  
  @Autowired
  private AdminActionPlanControllerHelper helper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * View for Account Admin Organization Landing Page.
   * 
   */
  @RequestMapping(value = "/admin/organizationMemberModal", method = RequestMethod.GET)
  public String getOrganizationMemberModal() {
    return "organizationMemberModal";
  }
  
  /**
   * View for Account Admin Organization Landing Page.
   * 
   */
  @RequestMapping(value = "/admin/account/actionPlan/home", method = RequestMethod.GET)
  public String getActionPlanHomeView() {
    return "getActionPlanHome";
  }
  
  /**
   * View for Account Admin Create ActionPlan Page.
   * 
   */
  @RequestMapping(value = "/admin/account/actionPlan/createPage", method = RequestMethod.GET)
  public String getActionPlanCreateView() {
    return "getActionPlanCreate";
  }
  
  /**
   * View for Account Admin Update ActionPlan Page.
   * 
   */
  @RequestMapping(value = "/admin/account/actionPlan/updatePage", method = RequestMethod.GET)
  public String getActionPlanUpdateView() {
    return "getActionPlanUpdate";
  }
  
  /**
   * Method to get the all the action plans for a company..
   * 
   * @param token
   *          - logged in user
   * @return the list of action plans
   */
  @RequestMapping(value = "/admin/account/actionPlan/getCompanyActionPlans", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanyActionPlans(
      @RequestParam(defaultValue = "false") boolean accountDashboardRequest,
      @RequestParam(defaultValue = "false") boolean includeUserId, Authentication token) {
    return process(helper::getCompanyActionPlans, token, accountDashboardRequest, includeUserId);
  }
  
  /**
   * This is the controller method to get the details for the given action plan.
   * 
   * @param actionPlanId
   *          - the action plan id
   * @param token
   *          - logged in user
   * @return the details for the given action plan id
   */
  @RequestMapping(value = "/admin/account/actionPlan/getDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDetails(@RequestParam String actionPlanId,
      @RequestParam(defaultValue = "false") boolean accountDashboard,
      @RequestParam(defaultValue = "false") boolean isEdit, Authentication token) {
    return process(helper::getDetails, token, actionPlanId, accountDashboard, isEdit);
  }
  
  /**
   * Controller method to create a new action plan.
   * 
   * @param ationPlanForm
   *          - action plan data
   * @param token
   *          - logged in user
   * @return the response to the create request
   */
  @RequestMapping(value = "/admin/account/actionPlan/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse create(@RequestBody ActionPlanForm ationPlanForm, Authentication token) {
    try {
      authoriseRequestAndAddCompany(ationPlanForm, token);
      return process(helper::create, token, ationPlanForm);
    } catch (Exception e) {
      log.warn("Error processing create request.", e);
      return new UserResponse(new SPException(e.getMessage(), e));
    }
  }
  
  /**
   * Controller method to update the action plan.
   * 
   * @param ationPlanForm
   *          - action plan data to update
   * @param token
   *          - the logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/admin/account/actionPlan/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse update(@RequestBody ActionPlanForm ationPlanForm, Authentication token) {
    try {
      authoriseRequestAndAddCompany(ationPlanForm, token);
      return process(helper::update, token, ationPlanForm);
    } catch (Exception e) {
      log.warn("Error processing update request.", e);
      return new UserResponse(new SPException(e.getMessage(), e));
    }
  }
  
  /**
   * Controller method to publish the action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param allMembers
   *          - flag for all members
   * @param token
   *          - the logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/admin/account/actionPlan/publish", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse publish(@RequestParam String actionPlanId,
      @RequestParam(defaultValue = "false") boolean allMembers, Authentication token) {
    try {
      authoriseRequest(token);
      return process(helper::publish, token, actionPlanId, false, allMembers);
    } catch (Exception e) {
      log.warn("Error processing update request.", e);
      return new UserResponse(new SPException(e.getMessage(), e));
    }
  }
  
  /**
   * Controller method to delete the action plan or org plan step.
   * 
   * @param ationPlanForm
   *          - action plan data to update
   * @param token
   *          - the logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/admin/account/actionPlan/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam String actionPlanId,
      @RequestParam(required = false) String practiceAreaId, Authentication token) {
    return process(helper::delete, token, actionPlanId, practiceAreaId);
  }
  
  /**
   * Controller method to assign action plans to users.
   * 
   * @param actionPlanForm
   *          - action plan form
   * @param token
   *          - logged in user
   * @return the response to the assign request
   */
  @RequestMapping(value = "/admin/account/actionPlan/assignActionPlan", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse assignActionPlan(@RequestBody ManageActionPlanForm actionPlanForm,
      Authentication token) {
    return process(helper::assignActionPlan, token, actionPlanForm);
  }
  
  /**
   * Controller method to remove the action plan from users.
   * 
   * @param actionPlanForm
   *          - action plan form
   * @param token
   *          - logged in user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/admin/account/actionPlan/removeActionPlan", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeActionPlan(@RequestBody ManageActionPlanForm actionPlanForm,
      Authentication token) {
    return process(helper::removeActionPlan, token, actionPlanForm);
  }
  
  /**
   * Activate and deactivate action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param onHold
   *          - flag to activate or deactivate
   * @param token
   *          - logged in user
   * @return the status of the activate deactivate request
   */
  @RequestMapping(value = "/admin/account/actionPlan/activateDeactivate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse activateDeactivate(@RequestParam String actionPlanId,
      @RequestParam boolean onHold, @RequestParam(required = false) String companyId,
      Authentication token) {
    return process(helper::activateDeactivate, token, actionPlanId, onHold, companyId);
  }
  
  /**
   * Controller method to update the action plan all companies access.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param enable
   *          - all companies
   * @param token
   *          - user
   * @return the response to modify all companies
   */
  @RequestMapping(value = "/admin/account/actionPlan/modify/allMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse modifyAllMembers(@RequestParam String actionPlanId,
      @RequestParam boolean enable, Authentication token) {
    User user = GenericUtils.getUserFromAuthentication(token);
    return process(helper::modifyAllMembers, token, actionPlanId, enable, user.getCompanyId());
  }
  
  /**
   * Authorize the edit request and add the company id to the form.
   * 
   * @param ationPlanForm
   *          - action plan form
   * @param token
   *          - user
   */
  private void authoriseRequestAndAddCompany(ActionPlanForm ationPlanForm, Authentication token) {
    ationPlanForm.setCreatedByCompanyId(authoriseRequest(token).getCompanyId());
  }
  
  /**
   * Authorize the request if edit allowed for the company.
   * 
   * @param token
   *          - user
   * @return user from token
   */
  private User authoriseRequest(Authentication token) {
    User user = GenericUtils.getUserFromAuthentication(token);
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    Assert.isTrue(company.isActionPlanAdminEnabled(), "Unauthorized access.");
    return user;
  }
  
}
