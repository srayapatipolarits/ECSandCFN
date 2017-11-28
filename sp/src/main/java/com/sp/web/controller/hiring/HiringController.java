package com.sp.web.controller.hiring;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.assessment.personality.RangeType;
import com.sp.web.form.hiring.user.HireCandidateForm;
import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.GenericUtils;

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
 * @author Dax Abraham
 *
 *         The hiring controller.
 */
@Controller
public class HiringController {
  
  @Autowired
  HiringControllerHelper helper;
  
  /**
   * Gets the number of hiring subscriptions available for the account.
   * 
   * @param token
   *          - the logged in user
   * @return the response to the get request
   */
  
  @RequestMapping(value = "/hiring/importList", method = RequestMethod.GET)
  public String importListView() {
    return "importList";
  }
  
  @RequestMapping(value = "/hiring/validateList", method = RequestMethod.GET)
  public String validateListView() {
    return "validateList";
  }
  
  @RequestMapping(value = "/pa/tools", method = RequestMethod.GET)
  public String getpaHiringHomeView() {
    return "paTools";
  }
  
  @RequestMapping(value = "/pa/spectrum", method = RequestMethod.GET)
  public String getSpectrum() {
    return "paSpectrum";
  }
  
  @RequestMapping(value = "/pa/ideal-portrait", method = RequestMethod.GET)
  public String getPortraits() {
    return "idealPortrait";
  }
  
  @RequestMapping(value = "/hiring/archives", method = RequestMethod.GET)
  public String getAllArchivedCandidatesView() {
    return "getAllArchivedCandidates";
  }
  
  @RequestMapping(value = "/hiring/profile", method = RequestMethod.GET)
  public String getAvailableSubscriptionsView() {
    return "getAvailableSubscriptions";
  }
  
  @RequestMapping(value = "/hiring/profile/details", method = RequestMethod.GET)
  public String archiveCandidateView() {
    return "archiveCandidate";
  }
  
  @RequestMapping(value = "/hiring/compare", method = RequestMethod.GET)
  public String compareCandidateView() {
    return "compareCandidate";
  }
  
  @RequestMapping(value = "/hiring/advisor", method = RequestMethod.GET)
  public String candidateAdvisorView() {
    return "candidateAdvisor";
  }
  
  /**
   * Thank you page.
   * 
   * @param token
   *          - logged in user
   * @return the view to the thank you page
   */
  @RequestMapping(value = "/thankYou", method = RequestMethod.GET)
  public String candidateThankYouView(Authentication token) {
    User userFromAuthentication = GenericUtils.getUserFromAuthentication(token);
    if (userFromAuthentication instanceof HiringUser) {
      
      if (userFromAuthentication.getType() == UserType.Member) {
        return "employeeThankYou";
      } else {
        
        if (userFromAuthentication.hasRole(RoleType.PartnerCandidate)) {
          return "partnerThankYou";
        } else {
          return "candidateThankYou";
        }
      }
    } else {
      return "ertiMemberThankyou";
    }
  }
  
  @RequestMapping(value = "/pa/relationship-advisor", method = RequestMethod.GET)
  public String getMembersView() {
    return "paCompare";
  }
  
  /**
   * Add candidate view.
   * 
   * @return the view
   */
  @RequestMapping(value = "/hiring/add/candidate", method = RequestMethod.GET)
  public String addCandidateView() {
    return "addCandidate";
  }
  
  /**
   * Gets the available subscriptions.
   * 
   * @param token
   *          - logged in user
   * @return the available subscriptions
   */
  @RequestMapping(value = "/hiring/getAvailableSubscriptions", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAvailableSubscriptions(Authentication token) {
    return process(helper::getAvailableSubscriptions, token);
  }
  
  /**
   * Gets the available subscriptions.
   * 
   * @param token
   *          - logged in user
   * @return the available subscriptions
   */
  @RequestMapping(value = "/hiring/getAvailableAdminSubscriptions", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAvailableAdminSubscriptions(Authentication token) {
    return process(helper::getAvailableAdminSubscriptions, token);
  }
  
  /**
   * Controller method to get all the hiring candidates for the company.
   * 
   * @param type
   *          - user type
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(@RequestParam UserType type, Authentication token) {
    return process(helper::getAll, token, type);
  }
  
  /**
   * Controller method to get all the members who are applicable for adding as administrators.
   * 
   * @param type
   *          - user type
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getMembersForAdmin", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getMembersForAdmin(Authentication token) {
    return process(helper::getMembersForAdmin, token);
  }
  
  /**
   * Controller method to get all the hiring candidates for the company.
   * 
   * @param validOnly
   *          - flag to indicate only valid users
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getAllMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllMembers(@RequestParam(defaultValue = "false") boolean validOnly,
      Authentication token) {
    return process(helper::getAllMembers, token, validOnly);
  }
  
  /**
   * Helper method to get all the archived candidates for the company.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/archiveGetAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse archiveGetAll(Authentication token) {
    return process(helper::archiveGetAll, token);
  }
  
  /**
   * The controller method to delete the candidate.
   * 
   * @param userIds
   *          - the id of the candidates/employee to delete
   * @param token
   *          - the logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/hiring/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam List<String> userIds, Authentication token) {
    return process(helper::delete, token, userIds);
  }
  
  /**
   * The controller method to delete the candidate.
   * 
   * @param userIds
   *          - the list of hiring user id's
   * @param token
   *          - the logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/hiring/deleteArchiveUser", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteHiringArchiveUser(@RequestParam List<String> userIds, Authentication token) {
    return process(helper::deleteHiringArchiveUser, token, userIds);
  }
  
  /**
   * Controller method to archive candidates/employee.
   * 
   * @param userIds
   *          - candidate/employee id's to archive
   * @param isHired
   *          - flag indicating if the candidate(s) were hired
   * @param token
   *          - logged in user
   * @return the response to the archive request
   */
  @RequestMapping(value = "/hiring/archiveUser", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse archiveUser(@RequestParam List<String> userIds,
      @RequestParam(defaultValue = "false") boolean isHired,
      @RequestParam(required = false) List<String> tagList, Authentication token) {
    return process(helper::archiveUser, token, userIds, isHired, tagList);
  }
  
  /**
   * Controller method to check if the candidate already exists in the system as a hiring candidate
   * or as a surepeople individual.
   * 
   * @param candidateEmail
   *          - list of candidate email's
   * @param token
   *          - logged in user
   * @return the response to the check request
   */
  @RequestMapping(value = "/hiring/checkCandidate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse checkCandidate(@RequestParam List<String> candidateEmail, Authentication token) {
    return process(helper::checkCandidate, token, candidateEmail);
  }
  
  /**
   * Controller method to add a single candidate.
   * 
   * @param hiringAddForm
   *          - hiring user form
   * @param token
   *          - logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/hiring/add", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse add(@RequestBody HiringAddForm hiringAddForm, Authentication token) {
    return process(helper::add, token, hiringAddForm);
  }
  
  /**
   * Controller method to get the analysis of the the hiring candidate and a member from the
   * company.
   * 
   * @param candidateEmail
   *          - candidate email
   * @param memberEmail
   *          - member email
   * @param token
   *          - logged in user
   * @return the response for the compare request
   */
  @RequestMapping(value = "/hiring/compareProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse compareProfile(@RequestParam String user1Id, @RequestParam String user2Id,
      Authentication token) {
    return process(helper::compareProfile, token, user1Id, user2Id);
  }
  
  /**
   * Controller method to compare the relationship between candidate and existing member.
   * 
   * @param user1Id
   *          - user 1 id
   * @param user2Id
   *          - user 2 id
   * @param user1PersonalityType
   *          - user 1 personality type
   * @param user2PersonalityType
   *          - user 2 personality type
   * @param token
   *          - logged in user
   * @return the response to the relationship check
   */
  @RequestMapping(value = "/hiring/compareRelation", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse compareRelation(@RequestParam String user1Id, @RequestParam String user2Id,
      @RequestParam(defaultValue = "Primary") RangeType user1PersonalityType,
      @RequestParam(defaultValue = "Primary") RangeType user2PersonalityType, Authentication token) {
    return process(helper::compareRelation, token, user1Id, user2Id, user1PersonalityType,
        user2PersonalityType);
  }
  
  /**
   * The controller method to move the archive candidate to the hiring candidate list.
   * 
   * @param userIds
   *          - archive user id's
   * @param token
   *          - logged in user
   * @return the response to the move request
   */
  @RequestMapping(value = "/hiring/archivePutBack", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse archivePutBack(@RequestParam List<String> userIds, Authentication token) {
    return process(helper::archivePutBack, token, userIds);
  }
  
  /**
   * Controller method to get the roles and tags for the company.
   * 
   * @param token
   *          - logged in user
   * @return the response with the roles and tags
   */
  @RequestMapping(value = "/hiring/getRolesAndTags", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getRolesAndTags(Authentication token) {
    return process(helper::getRolesAndTags, token);
  }
  
  /**
   * Controller method to update the status of the hiring archived candidate to hired and moved to
   * member.
   * 
   * @param form
   *          - the form for the hire candidate details
   * @param token
   *          - logged in user
   * @return the response to the update status request
   */
  @RequestMapping(value = "/hiring/hireCandidate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse hireCandidate(HireCandidateForm form, Authentication token) {
    return process(helper::hireCandidate, token, form);
  }
  
  /**
   * Controller method to add the hiring employee to the ERTi tool.
   * 
   * @param userId
   *          - the user id of the employee
   * @param token
   *          - logged in user
   * @return the response to the update status request
   */
  @RequestMapping(value = "/hiring/addToErti", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addToErti(@RequestParam String userId, Authentication token) {
    return process(helper::addToErti, token, userId);
  }
  
  /**
   * Controller method to add an administrator from the list of employees.
   * 
   * @param userId
   *          - the user id of the employee
   * @param token
   *          - logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/hiring/addAdministrator", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addAdministrator(@RequestParam String userId, Authentication token) {
    return process(helper::addAdministrator, token, userId);
  }
  
  /**
   * Controller method to remove an administrator from the list of employees.
   * 
   * @param userId
   *          - the user id of the employee
   * @param token
   *          - logged in user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/hiring/removeAdministrator", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeAdministrator(@RequestParam String userId, Authentication token) {
    return process(helper::removeAdministrator, token, userId);
  }
  
  /**
   * Controller method to check if the company has ERTi or not.
   * 
   * @param token
   *          - logged in user
   * @return the response to check request
   */
  @RequestMapping(value = "/hiring/companyHasErti", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse hasErti(Authentication token) {
    return process(helper::hasErti, token);
  }
  
}
