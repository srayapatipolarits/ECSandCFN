package com.sp.web.controller.admin.competency;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.competency.CompetencyProfileForm;
import com.sp.web.form.competency.ManageCompetencyForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>AdminCompetencyController</code> define the interfaces to manage the competencies for Admin
 * SP.
 * 
 * @author Prasanna Venkatesh
 *
 */
@Controller
public class AdminCompetencyController {
  
  /**
   * AdminCompetencyControllerHelper is the controller helper class.
   */
  @Autowired
  private AdminCompetencyControllerHelper helper;
  
  /**
   * Competency Landing Page for System Admin.
   */
  @RequestMapping(value = "/sysAdmin/competency/home", method = RequestMethod.GET)
  public String competencyHome(Authentication token) {
    return "competencyHome";
  }
  
  /**
   * Competency Create Page for System Admin.
   */
  @RequestMapping(value = "/sysAdmin/competency/create", method = RequestMethod.GET)
  public String createCompetencyProfile(Authentication token) {
    return "createCompetencyProfile";
  }
  
  /**
   * Competency Update Page for System Admin.
   */
  @RequestMapping(value = "/sysAdmin/competency/update", method = RequestMethod.GET)
  public String updateCompetencyProfile(Authentication token) {
    return "updateCompetencyProfile";
  }
  
  /**
   * Dialog View For Selecting a Company.
   */
  @RequestMapping(value = "/sysAdmin/competency/selectCompany", method = RequestMethod.GET)
  public String validateselectCompany(Authentication token) {
    return "selectCompanyCompetency";
  }
  
  /**
   * Dialog View For Clone Competency Profile.
   */
  @RequestMapping(value = "/sysAdmin/competency/clone", method = RequestMethod.GET)
  public String cloneCompetencyProfile(Authentication token) {
    return "cloneCompetencyProfile";
  }
  
  /**
   * Dialog View For Clone Competency Profile.
   */
  @RequestMapping(value = "/admin/competency/assignRemoveModal", method = RequestMethod.GET)
  public String assignRemoveModal(Authentication token) {
    return "adminAssignRemoveModal";
  }
  
  /**
   * Competency Manager Listing Page. User with role CompetencyAdmin will see this page
   */
  @RequestMapping(value = "/admin/competency/manager", method = RequestMethod.GET)
  public String competencyManager(Authentication token) {
    return "competencyManager";
  }
  
  /** Competency Manager Rating Preview Modal. */
  @RequestMapping(value = "/admin/competency/rating/preview", method = RequestMethod.GET)
  public String previewRating(Authentication token) {
    return "previewRating";
  }
  
  /** Activate Evaluation Modal. */
  @RequestMapping(value = "/admin/competency/activateEvaluation/modal", method = RequestMethod.GET)
  public String activateEvaluationView(Authentication token) {
    return "activateEvaluation";
  }
  
  /** Change Manager Modal. */
  @RequestMapping(value = "/admin/competency/changeManager/modal", method = RequestMethod.GET)
  public String changeManagerModal(Authentication token) {
    return "changeManagerModal";
  }
  
  /**
   * Method to get all the companies who have purchased the feature.
   * 
   * @param token
   *          - logged in user
   * 
   * @return the list of Companies
   */
  @RequestMapping(value = "/sysAdmin/competency/getCompanies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanies(Authentication token) {
    return process(helper::getCompanies, token);
  }
  
  /**
   * Method to get all the companies who have purchased the feature.
   * 
   * @param token
   *          - logged in user
   * 
   * @return the list of Companies
   */
  @RequestMapping(value = "/sysAdmin/competency/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    return process(helper::getAll, token);
  }
  
  /**
   * Controller method to create or update a competency profile.
   * 
   * @param form
   *          - competency profile
   * @param token
   *          - logged in user
   * @return the response to the create or update request
   */
  @RequestMapping(value = "/sysAdmin/competency/createUpdate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createUpdate(@RequestBody CompetencyProfileForm form, Authentication token) {
    return process(helper::createUpdate, token, form);
  }
  
  /**
   * Method to get the competency profile details.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = { "/sysAdmin/competency/get", "/admin/competency/get" }, method = RequestMethod.POST)
  @ResponseBody
  public SPResponse get(@RequestParam String competencyProfileId, Authentication token) {
    return process(helper::get, token, competencyProfileId);
  }
  
  /**
   * Controller method to delete the competency.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/sysAdmin/competency/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam String competencyProfileId, Authentication token) {
    return process(helper::delete, token, competencyProfileId);
  }
  
  /**
   * Controller method to get all the articles that are applicable for the given company.
   * 
   * @param companyId
   *          - company id
   * @param token
   *          - logged in user
   * @return the list of articles
   */
  @RequestMapping(value = "/sysAdmin/competency/getArticles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getArticles(@RequestParam String companyId, Authentication token) {
    return process(helper::getArticles, token, companyId);
  }
  
  /**
   * Controller method to get all the users of the company along with the competency profiles
   * assigned to them.
   * 
   * @param groupId
   *          - optional group id
   * @param token
   *          - logged in user
   * @return the list of users
   */
  @RequestMapping(value = "/admin/competency/getUserCompetency", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserCompetency(@RequestParam(required = false) String groupId,
      Authentication token) {
    return process(helper::getUserCompetency, token, groupId);
  }
  
  /**
   * Controller method to assign competency to a particular set of members.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param memberIdList
   *          - member id list
   * @param token
   *          - logged in user
   * @return the response to the assign request
   */
  @RequestMapping(value = "/admin/competency/assignCompetency", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse assignCompetency(@RequestBody ManageCompetencyForm competencyForm, Authentication token) {
    return process(helper::assignCompetency, token, competencyForm);
  }
  
  /**
   * Controller method to remove the competency profile for the members.
   * 
   * @param memberIdList
   *          - member id list
   * @param token
   *          - logged in user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/admin/competency/removeCompetency", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeCompetency(@RequestBody ManageCompetencyForm competencyForm, Authentication token) {
    return process(helper::removeCompetency, token, competencyForm);
  }
    
  /**
   * Controller method to get the listing for the competency dashboard.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get all request
   */
  @RequestMapping(value = "/admin/competency/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse adminGetAll(Authentication token) {
    return process(helper::adminGetAll, token);
  }
  
  /**
   * Controller method to get the company statistics.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/admin/competency/getStats", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getStats(Authentication token) {
    return process(helper::getStats, token);
  }
  
}
