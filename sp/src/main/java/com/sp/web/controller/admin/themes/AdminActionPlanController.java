package com.sp.web.controller.admin.themes;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.goal.ActionPlanForm;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <code>Action Plan Controller</code> define the interfaces to manage the organization action plans
 * SP.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class AdminActionPlanController {
  
  /**
   * AdminActionPlanControllerHelper is the controller helper class.
   */
  @Autowired
  private AdminActionPlanControllerHelper helper;
  
  /**
   * Listing View For Organization.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/adminActionPlan", method = RequestMethod.GET)
  public String validateadminActionPlan(Authentication token) {
    return "adminActionPlan";
  }
  
  /**
   * Create Org Plan For Organization.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/createOrgPlan", method = RequestMethod.GET)
  public String validatecreateOrgPlan(Authentication token) {
    return "createOrgPlan";
  }
  
  /**
   * Publish Org Plan For Learning Program.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/publishPlan", method = RequestMethod.GET)
  public String publishPlan(Authentication token) {
    return "publishPlan";
  }
  
  /**
   * Publish Org Plan For Account Learning Program.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/publishPlanAccount", method = RequestMethod.GET)
  public String publishPlanAccount(Authentication token) {
    return "publishPlanAccount";
  }
  
  /**
   * AddFromSurePeople.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/addFromSurePeople", method = RequestMethod.GET)
  public String addFromSurePeople(Authentication token) {
    return "addFromSurePeople";
  }
  
  /**
   * updateOrgPlan Plan For Organization.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/updateOrgPlan", method = RequestMethod.GET)
  public String validateupdateOrgPlan(Authentication token) {
    return "updateOrgPlan";
  }
  
  /**
   * Dialog View For Company.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/selectCompany", method = RequestMethod.GET)
  public String validateselectCompany(Authentication token) {
    return "selectCompany";
  }
  
  /**
   * Dialog View For Company.
   */
  @RequestMapping(value = "/admin/videoPop", method = RequestMethod.GET)
  public String validatevideoPop(Authentication token) {
    return "videoPop";
  }
  
  /**
   * Dialog View For Company.
   */
  @RequestMapping(value = "/admin/selectGroup", method = RequestMethod.GET)
  public String validateselectGroup(Authentication token) {
    return "selectGroup";
  }
  
  /**
   * Dialog View For Clone Account Program.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/clonePlan", method = RequestMethod.GET)
  public String validateclonePlan(Authentication token) {
    return "clonePlan";
  }
  
  /**
   * Dialog View For Clone for SP.
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/clonePlanSP", method = RequestMethod.GET)
  public String validateclonePlanSP(Authentication token) {
    return "clonePlanSP";
  }
  
  /**
   * Method to get the all the action plans.
   * 
   * @param token
   *          - logged in user
   * @return the list of action plans
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(@RequestParam ActionPlanType type, Authentication token) {
    return process(helper::getAll, token, type);
  }
  
  /**
   * Method to get all the companies who have Organizational Plan Feature.
   * 
   * @param token
   *          - logged in user
   * @return the list of Companies
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/getCompanies", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getCompanies(Authentication token) {
    return process(helper::getCompanies, token);
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
  @RequestMapping(value = "/sysAdmin/actionPlan/getDetails", method = RequestMethod.POST)
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
  @RequestMapping(value = "/sysAdmin/actionPlan/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse create(@RequestBody ActionPlanForm ationPlanForm, Authentication token) {
    return process(helper::create, token, ationPlanForm);
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
  @RequestMapping(value = "/sysAdmin/actionPlan/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse update(@RequestBody ActionPlanForm ationPlanForm, Authentication token) {
    return process(helper::update, token, ationPlanForm);
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
  @RequestMapping(value = "/sysAdmin/actionPlan/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam String actionPlanId,
      @RequestParam(required = false) String practiceAreaId, Authentication token) {
    return process(helper::delete, token, actionPlanId, practiceAreaId);
  }
  
  /**
   * Controller method to enable disable admin access to the organization plans for the companies.
   * 
   * @param companyId
   *          - company id
   * @param enableAdminAccess
   *          - flag to enable or disable the admin access
   * @param token
   *          - logged in user
   * @return the status for the update request
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/adminEnable", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse adminEnable(@RequestParam String companyId, boolean enableAdminAccess,
      Authentication token) {
    return process(helper::adminEnable, token, companyId, enableAdminAccess);
  }
  
  /**
   * Controller method to publish the action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param allCompanies
   *          - all companies flag
   * @param allMembers
   *          - all members flag
   * @param token
   *          - user
   * @return the response to the publish request
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/publish", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse publish(@RequestParam String actionPlanId,
      @RequestParam(defaultValue = "false") boolean allCompanies,
      @RequestParam(defaultValue = "false") boolean allMembers, Authentication token) {
    return process(helper::publish, token, actionPlanId, allCompanies, allMembers);
  }
  
  /**
   * Controller method to modify the edit access for the action plans.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param enable
   *          - flag to enable or disable
   * @param token
   *          - user
   * @return the response to the edit access request
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/modify/editAccess", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse editAccess(@RequestParam String actionPlanId, @RequestParam boolean enable,
      Authentication token) {
    return process(helper::editAccess, token, actionPlanId, enable);
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
  @RequestMapping(value = "/sysAdmin/actionPlan/modify/allCompanies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse modifyAllCompanies(@RequestParam String actionPlanId,
      @RequestParam boolean enable, Authentication token) {
    return process(helper::modifyAllCompanies, token, actionPlanId, enable);
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
  @RequestMapping(value = "/sysAdmin/actionPlan/modify/allMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse modifyAllMembers(@RequestParam String actionPlanId,
      @RequestParam(required = false) String companyId, @RequestParam boolean enable,
      Authentication token) {
    return process(helper::modifyAllMembers, token, actionPlanId, enable, companyId);
  }
  
  /**
   * Controller method to add a company to the action plan.
   * 
   * @param actionPlanIds
   *          - list of action plan ids
   * @param companyId
   *          - company id
   * @param token
   *          - user
   * @return the response to the add request
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/addCompany", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addCompany(@RequestParam List<String> actionPlanIds,
      @RequestParam String companyId, Authentication token) {
    return process(helper::addCompany, token, actionPlanIds, companyId);
  }
  
  /**
   * Controller method to remove a company to the action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param companyId
   *          - company id
   * @param token
   *          - user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/removeCompany", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeCompany(@RequestParam String actionPlanId,
      @RequestParam String companyId, Authentication token) {
    return process(helper::removeCompany, token, actionPlanId, companyId);
  }
  
  /**
   * Controller method to get SP programs for a company.
   * 
   * @param companyId
   *          - company id
   * @param token
   *          - user
   * @return the response to the get request
   */
  @RequestMapping(value = "/sysAdmin/actionPlan/getGlobalPrograms", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGlobalPrograms(@RequestParam String companyId, Authentication token) {
    return process(helper::getGlobalPrograms, token, companyId);
  }
}
