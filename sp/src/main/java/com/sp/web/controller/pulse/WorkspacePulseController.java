package com.sp.web.controller.pulse;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.PulseAssessmentForm;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.DateTimeUtil;

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
 *         The controller class for the workspace pulse.
 */
@Controller
public class WorkspacePulseController {

  @Autowired
  WorkspacePulseControllerHelper helper;
  
  /**
   * The controller method to start the pulse.
   * 
   * @param startDate
   *          - start date
   * @param endDate
   *          - end date
   * @param token
   *          - the logged in user
   * @return
   *      the response to the start request
   */
  @RequestMapping(value = "/pulse/start", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse startPulse(@RequestParam String startDate,
      @RequestParam  String endDate,
      @RequestParam(required = false) String pulseQuestionSetId,
      Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::startPulse, token, DateTimeUtil.getLocalDate(startDate),
        DateTimeUtil.getLocalDate(endDate), pulseQuestionSetId);
  }

  /**
   * Send a reminder to all the people who have not taken the pulse.
   * 
   * @param pulseRequestId
   *          - the pulse request id
   * @param token
   *          - logged in user
   * @return
   *      the response to the send reminder request
   */
  @RequestMapping(value = "/pulse/sendReminder", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse sendReminder(@RequestParam String pulseRequestId,
      Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::sendReminder, token, pulseRequestId);
  }
  
  /**
   * The list of the completed pulse requests for the given company.
   * 
   * @param pulseQuestionSetId
   *          - question set 
   * @param token
   *          - the logged in user
   * @return
   *    the pulse requests
   */
  @RequestMapping(value = "/pulse/getRequests", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getPulseRequests(@RequestParam(required = false) String pulseQuestionSetId,
      Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getPulseRequests, token, pulseQuestionSetId);
  }

  /**
   * Get the various pulse questions that are applicable for the given company.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the list of pulse questions 
   */
  @RequestMapping(value = "/pulse/getQuestionSets", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getPulseQuestionSets(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getPulseQuestionSets, token);
  }
  
  /**
   * The controller method to get the pulse result.
   * 
   * @param pulseResultId
   *          - result id
   * @param groupName
   *          - optional group name
   * @param token
   *          - logged in user
   * @return
   *    the response to the get request
   */
  @RequestMapping(value = "/pulse/getScore", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getPulseResult(@RequestParam String pulseResultId,
      @RequestParam(required = false) String groupName, Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getPulseResult, token, pulseResultId, groupName);
  }

  /**
   * Controller method to get the question set for the given question set id. If the 
   * question set is not sent then the default question set will be sent in the response.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set
   * @param token
   *          - logged in user
   * @return
   *      the response to the get question request 
   */
  @RequestMapping(value = "/pulse/getQuestions", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getPulseQuestions(@RequestParam(required = false) String pulseQuestionSetId,
      @RequestParam(required = false) String pulseRequestId,
      Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getPulseQuestions, token, pulseQuestionSetId, pulseRequestId);
  }

  /**
   * Controller method to cancel a currently executing pulse.
   * 
   * @param pulseRequestId
   *            - pulse request id
   * @param token
   *            - logged in user
   * @return
   *      the response to the cancel pulse request
   */
  @RequestMapping(value = "/pulse/cancelPulse", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse cancelPulse(@RequestParam String pulseRequestId,
      @RequestParam(defaultValue = "false") boolean saveResults,
      Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::cancelPulse, token, pulseRequestId, saveResults);
  }
  
  /**
   * The controller method to store the pulse assessment.
   * 
   * @param pulseRequestId
   *            - pulse request 
   * @param pulseAssessmentResponse
   *            - the pulse assessment response
   * @param token
   *            - logged in user
   * @return
   *      the response to the save request 
   */
  @RequestMapping(value = "/pulseUser/saveAssessment", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse savePulseAssessment(@RequestParam(required = false) String pulseRequestId,
      @RequestBody List<PulseAssessmentForm> pulseAssessmentResponse, Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::savePulseAssessment, token, pulseRequestId, pulseAssessmentResponse);
  }
  
  /**
   * View For Pulse.
   * 
   */
  @RequestMapping(value = "/pulse", method = RequestMethod.GET)
  public String validatePulse(Authentication token,
      @RequestParam(required = false) String theme) {
    return "pulseListing";
  }
  
  /**
   * View For startNewPulse.
   * 
   */
  @RequestMapping(value = "/pulse/startNewPulse", method = RequestMethod.GET)
  public String validateStartNewPulse(Authentication token,
      @RequestParam(required = false) String theme) {
    return "startNewPulse";
  }
  /**
   * View For Reminder.
   * 
   */
  
  @RequestMapping(value = "/pulse/pulseReminder", method = RequestMethod.GET)
  public String validateReminer(Authentication token,
      @RequestParam(required = false) String theme) {
    return "pulseReminder";
  }
  /**
   * View For cancel.
   * 
   */
  
  @RequestMapping(value = "/pulse/cancelSP", method = RequestMethod.GET)
  public String validateCancelSP(Authentication token,
      @RequestParam(required = false) String theme) {
    return "cancelSP";
  }
  /**
   * View For Error.
   * 
   */
  
  @RequestMapping(value = "/linkExpired", method = RequestMethod.GET)
  public String validatePulseError(Authentication token,
      @RequestParam(required = false) String theme) {
    return "pulseErr";
  }
 
    
  /**
   * View For pulseQuestion.
   * 
   */
  @RequestMapping(value = "/pulseUser/pulseStartAssessment", method = RequestMethod.GET)
  public String validatepulseQuestione(Authentication token,
      @RequestParam(required = false) String theme) {
    return "pulseStartAssessment";
  }
}
