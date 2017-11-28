package com.sp.web.controller.competency;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.admin.competency.AdminCompetencyControllerHelper;
import com.sp.web.form.competency.CompetencyRequestForm;
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
 * @author Dax Abraham
 * 
 *         The controller for all user competency related tasks.
 */
@Controller
public class CompetencyController {
  
  /**
   * CompetencyControllerHelper is the controller helper class.
   */
  @Autowired
  private CompetencyControllerHelper helper;
  
  /**
   * AdminCompetencyControllerHelper is the Admin Controller helper class.
   */
  @Autowired
  private AdminCompetencyControllerHelper adminHelper;
  
  /**
   * Competency Profiles User Page.
   */
  @RequestMapping(value = "/competency/home", method = RequestMethod.GET)
  public String competencyDetailsHome(Authentication token) {
    return "competencyDetailsHome";
  }
  
  /** Start Evaluation Rating Modal. */
  @RequestMapping(value = "/competency/evaluation/feedback/ratingModal", method = RequestMethod.GET)
  public String ratingModal(Authentication token) {
    return "ratingModal";
  }
  
  /** Evaluation Details Modal. */
  @RequestMapping(value = "/competency/evaluation/feedback/getEvaluationDetailsModal", method = RequestMethod.GET)
  public String evaluationDetailsModal(Authentication token) {
    return "evaluationDetailsModal";
  }
  
  /** Initiate Evaluation Modal. */
  @RequestMapping(value = "/competency/evaluation/feedback/initiateEvaluationModal", method = RequestMethod.GET)
  public String initiateEvaluationModal(Authentication token) {
    return "initiateEvaluationModal";
  }
  
  /** Send Reminder Modal. */
  @RequestMapping(value = "/competency/evaluation/feedback/sendReminder/modal", method = RequestMethod.GET)
  public String sendReminderModal(Authentication token) {
    return "sendReminderModal";
  }
  
  /**
   * Method to initiate the evaluation.
   * 
   * @param form
   *          - the initiate evaluation form
   * @param token
   *          - logged in user
   * 
   * @return the response to the initiate evaluation request
   */
  @RequestMapping(value = "/competency/evaluation/feedback/initiate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse initiateEvaluation(@RequestBody CompetencyRequestForm form, Authentication token) {
    return process(helper::initiateEvaluation, token, form);
  }
  
  /**
   * Controller method to get the competency profile information for user.
   * 
   * @param token
   *          - logged in user
   * @return the response for the get request
   */
  @RequestMapping(value = "/competency/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse get(@RequestParam(required = false) String memberEmail, Authentication token) {
    return process(helper::get, token, memberEmail);
  }
  
  /**
   * Controller method to get all the evaluation requests.
   * 
   * @param token
   *          - logged in user
   * @return
   *    the response to the get requests
   */
  @RequestMapping(value = "/competency/evaluation/feedback/requests", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getEvaluationRequests(Authentication token) {
    return process(helper::getEvaluationRequests, token);
  }
  
  /**
   * Controller method to get the details to start an evaluation.
   * 
   * @param memberId
   *          - member to start the evaluation for
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/competency/evaluation/feedback/start", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse evaluationStart(@RequestParam(required = false) String memberId,
      Authentication token) {
    return process(helper::evaluationStart, token, memberId);
  }
  
  /**
   * Controller method to store the assessment data.
   * 
   * @param memberId
   *          - member id
   * @param scoreArray
   *          - score list
   * @param token
   *          - logged in user
   * @return the response to the save request
   */
  @RequestMapping(value = "/competency/evaluation/feedback/save", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse evaluationSave(@RequestParam(required = false) String memberId,
      @RequestParam double[] scoreArray, Authentication token) {
    return process(helper::evaluationSave, token, memberId, scoreArray);
  }
  
  /**
   * Controller method to get the competency evaluation details.
   * 
   * @param scoreDetailsId
   *          - score details id
   * @param token
   *          - logged in user
   * @return the response to the get evaluation details request
   */
  @RequestMapping(value = "/competency/evaluation/feedback/getEvaluationDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getEvaluationDetails(@RequestParam String scoreDetailsId, Authentication token) {
    return process(helper::getEvaluationDetails, token, scoreDetailsId);
  }
  
  /**
   * Controller method to send reminder for competency evaluation.
   * 
   * @param userId
   *          - user id
   * @param isSelf
   *          - is self evaluation reminder
   * @param reviewUserId
   *          - id of review user
   * @param token
   *          - logged in user
   * @return the response to the send reminder request
   */
  @RequestMapping(value = "/competency/evaluation/feedback/sendReminder", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse sendReminder(@RequestParam String userId,
      @RequestParam(defaultValue = "false") boolean isInitiate,
      @RequestParam(defaultValue = "false") boolean isSelf,
      @RequestParam(required = false) String reviewUserId, Authentication token) {
    return process(adminHelper::sendReminder, token, userId, isInitiate, isSelf, reviewUserId);
  }
  
  /**
   * Controller method to get the competency profile information for user.
   * 
   * @param token
   *          - logged in user
   * @return the response for the get request
   */
  @RequestMapping(value = "/competency/getPreviousEvaluations", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPreviousEvaluations(Authentication token) {
    return process(helper::getPreviousEvaluations, token);
  }
  
  /**
   * View for Member Modal.
   * 
   */
  @RequestMapping(value = "/admin/competencyMemberModal", method = RequestMethod.GET)
  public String getcompetencyMemberModal() {
    return "competencyMemberModal";
  }
}
