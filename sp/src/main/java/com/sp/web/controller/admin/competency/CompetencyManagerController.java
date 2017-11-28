package com.sp.web.controller.admin.competency;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.competency.CompetencyEvaluationForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.validation.Valid;

/**
 * <code>CompetencyManagerController</code> defines all the competency manager services.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class CompetencyManagerController {
  
  /**
   * AdminCompetencyControllerHelper is the controller helper class.
   */
  @Autowired
  private CompetencyManagerControllerHelper helper;
  
  /**
   * Competency Manager View Page from email.
   */
  @RequestMapping(value = "/competency/evaluation/manager/view", method = RequestMethod.GET)
  public String competencyManagerView(Authentication token) {
    return "competencyManagerResult";
  }
  
  /**
   * Method to get all the companies who have purchased the feature.
   * 
   * @param token
   *          - logged in user
   * 
   * @param evaluationId
   *          - competency evaluation id
   * 
   * @return the list of Companies
   */
  @RequestMapping(value = "/competency/manager/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse get(@RequestParam(required = false) String evaluationId, Authentication token) {
    return process(helper::get, token, evaluationId);
  }
  
  /**
   * Controller method to start an evaluation.
   * 
   * @param endDate
   *          - end date for evaluation
   * @param evaluationTypeList
   *          - evaluation type list
   * @param token
   *          - logged in user
   * @return the response to the activate evaluation request
   */
  @RequestMapping(value = "/competency/manager/activateEvaluation", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse activateEvaluation(@Valid CompetencyEvaluationForm form, Authentication token) {
    return process(helper::activateEvaluation, token, form);
  }
  
  /**
   * Controller method to add a user to an evaluation.
   * 
   * @param userIds
   *          - list of users to add
   * @param token
   *          - logged in user
   * @return the response to the add to evaluation request
   */
  @RequestMapping(value = "/competency/manager/addUserToEvaluation", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addUserToEvaluation(@RequestParam List<String> userIds, Authentication token) {
    return process(helper::addUserToEvaluation, token, userIds);
  }
  
  /**
   * Controller method to remove a user to an evaluation.
   * 
   * @param userId
   *          - user id to remove
   * @param token
   *          - logged in user
   * @return the response to remove from evaluation request
   */
  @RequestMapping(value = "/competency/manager/removeUserFromEvaluation", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeUserFromEvaluation(@RequestParam String userId, Authentication token) {
    return process(helper::removeUserFromEvaluation, token, userId);
  }
  
  /**
   * Controller method to end the evaluation currently executing.
   * 
   * @param token
   *          - logged in user
   * @return the response to the end evaluation request
   */
  @RequestMapping(value = "/competency/manager/endEvaluation", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse endEvaluation(Authentication token) {
    return process(helper::endEvaluation, token);
  }
  
  /**
   * Gets the list of users along with group information and competency profile information.
   * 
   * @param token
   *          - logged in user
   * @return the list of users along with competency profile and group
   */
  @RequestMapping(value = "/competency/manager/getUsers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUsers(Authentication token) {
    return process(helper::getUsers, token);
  }
  
  /**
   * Gets the list of past competency evaluation details.
   * 
   * @param token
   *          - logged in user
   * @return the list of previous competency evaluations
   */
  @RequestMapping(value = "/competency/manager/getEvaluationList", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getEvaluationList(Authentication token) {
    return process(helper::getEvaluationList, token);
  }
  
  /**
   * Controller method to get the data for the View By Filters.
   * 
   * @param token
   *          - logged in user
   * @return the data
   */
  @RequestMapping(value = "/competency/manager/getViewByCompetencyProfiles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getViewByCompetencyProfiles(Authentication token) {
    return process(helper::getViewByCompetencyProfiles, token);
  }
  
  /**
   * Controller method to get the details for the given competency profile.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param token
   *          - logged in user
   * @return the response to the get details request
   */
  @RequestMapping(value = "/competency/manager/getCompetencyProfileDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompetencyProfileDetails(@RequestParam String competencyProfileId,
      Authentication token) {
    return process(helper::getCompetencyProfileDetails, token, competencyProfileId);
  }
  
  /**
   * Controller method to get the user details for the given competency profile and competency
   * evaluation.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param token
   *          - logged in user
   * @return the response to the get user details request
   */
  @RequestMapping(value = "/competency/manager/getCompetencyProfileEvaluationUsers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompetencyProfileEvaluationUsers(@RequestParam String competencyProfileId,
      @RequestParam String competencyEvaluationId, Authentication token) {
    return process(helper::getCompetencyProfileEvaluationUsers, token, competencyProfileId,
        competencyEvaluationId);
  }
  
  /**
   * Controller method to get the list of members for the view by members filter.
   * 
   * @param token
   *          - logged in user
   * @return the list of valid members
   */
  @RequestMapping(value = "/competency/manager/getViewByMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getViewByMembers(Authentication token) {
    return process(helper::getViewByMembers, token);
  }
  
  /**
   * Controller method to get the last competency evaluation details for the user id's passed.
   * 
   * @param userIds
   *          - user id's
   * @param token
   *          - logged in user
   * @return the list of user competency evaluations
   */
  @RequestMapping(value = "/competency/manager/getUserCompetencies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserCompetencies(@RequestParam List<String> userIds, Authentication token) {
    return process(helper::getUserCompetencies, token, userIds);
  }
  
  /**
   * Get all the past competency evaluations for a user.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/competency/manager/getUserCompetencyEvaluations", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserCompetencyEvaluations(@RequestParam String userId, Authentication token) {
    return process(helper::getUserCompetencyEvaluations, token, userId);
  }
  
  /**
   * Controller method to get all the peer requests for the given user along with the evaluations if
   * completed.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = { "/competency/manager/getCompetencyEvaluationPeerRequests",
      "/competency/evaluation/manager/peerDetails", 
      "/competency/evaluation/feedback/peerDetails" }, method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompetencyEvaluationPeerRequests(
      @RequestParam String competencyEvaluationId, @RequestParam String userId, Authentication token) {
    return process(helper::getCompetencyEvaluationPeerRequests, token, competencyEvaluationId,
        userId);
  }
  
  /**
   * Get the competency evaluation of all the users for the given manager.
   * 
   * @param tokenId
   *          - token id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/competency/evaluation/manager/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getManagerResult(@RequestParam String tokenId, Authentication token) {
    return process(helper::getManagerResult, token, tokenId);
  }
}
