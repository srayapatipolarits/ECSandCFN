package com.sp.web.controller.systemadmin.pulse;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.pulse.CreatePulseForm;
import com.sp.web.model.pulse.PulseQuestionSetStatus;
import com.sp.web.model.pulse.QuestionSetType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Dax Abraham
 *
 *         The Controller for the administrator interface for pulse.
 */
@Controller
@Scope("session")
public class PulseAdminController {
  
  @Autowired
  PulseAdminControllerHelper helper;
  
  /**
   * The method to get all the configured question sets.
   * 
   * @param token
   *          - logged in user
   * @return the list of all questions
   */
  @RequestMapping(value = "/sysAdmin/pulse/createPulse", method = RequestMethod.GET)
  public String createPulseView() {
    return "createPulse";
  }
  
  @RequestMapping(value = "/sysAdmin/pulse/pulseListings", method = RequestMethod.GET)
  public String pulseListingsView() {
    return "pulseListings";
  }
  
  @RequestMapping(value = "/sysAdmin/pulse/pulseDetails", method = RequestMethod.GET)
  public String pulseDetailsView() {
    return "pulseDetails";
  }
  
  @RequestMapping(value = "/sysAdmin/pulse/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getAll, token);
  }
  
  /**
   * Controller method to update the pulse question set status.
   * 
   * @param status
   *          - status
   * @param token
   *          - logged in user
   * @return the response to the update status
   */
  @RequestMapping(value = "/sysAdmin/pulse/updateStatus", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateStatus(@RequestParam String pulseQuestionSetId,
      @RequestParam PulseQuestionSetStatus status, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::updateStatus, token, pulseQuestionSetId, status);
  }
  
  /**
   * The controller method to store the pulse form information.
   * 
   * @param pulseForm
   *          - the pulse form
   * @param token
   *          - logged in user
   * @return the response to the crate pulse
   */
  @RequestMapping(value = "/sysAdmin/pulse/createPulse", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createPulse(@Valid CreatePulseForm pulseForm, Authentication token) {
    // process the get hiring subscriptions request
    return process(helper::createPulse, token, pulseForm);
  }
  
  /**
   * The create request with the file.
   * 
   * @param pulseQuestionSet
   *          - pulse question set
   * @param token
   *          - logged in user
   * @return the response to the create file request
   */
  @RequestMapping(value = "/sysAdmin/pulse/createPulseFile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createPulseFile(@RequestParam MultipartFile pulseQuestionSet,
      Authentication token) {
    // process the get hiring subscriptions request
    return process(helper::createPulseFile, token, pulseQuestionSet);
  }
  
  /**
   * Controller method to get the details for the pulse question set.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set id
   * @param token
   *          - logged in user
   * @return the details for the given pulse question set
   */
  @RequestMapping(value = "/sysAdmin/pulse/getDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDetails(@RequestParam String pulseQuestionSetId, Authentication token) {
    return process(helper::getDetails, token, pulseQuestionSetId);
  }
  
  /**
   * Controller method to get the question set as a file.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set id
   * @param token
   *          - logged in user
   * @param response
   *          - response
   */
  @RequestMapping(value = "/sysAdmin/pulse/downloadJson", method = RequestMethod.GET)
  public void downloadJson(@RequestParam String pulseQuestionSetId, Authentication token,
      HttpServletResponse response) {
    process(helper::downloadJson, token, pulseQuestionSetId, response, true);
  }
  
  /**
   * Controller method to get the question set as a file.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set id
   * @param token
   *          - logged in user
   * @param response
   *          - response
   */
  @RequestMapping(value = "/sysAdmin/pulse/previewJson", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse previewJson(@RequestParam String pulseQuestionSetId, Authentication token,
      HttpServletResponse response) {
    return process(helper::downloadJson, token, pulseQuestionSetId, response, false);
  }
  
  /**
   * Controller method to update the pulse question set.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set
   * @param type
   *          - type
   * @param companyIds
   *          - company id's
   * @param token
   *          - logged in user
   */
  @RequestMapping(value = "/sysAdmin/pulse/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse update(@RequestParam String pulseQuestionSetId,
      @RequestParam(required = false) String name, @RequestParam QuestionSetType type,
      @RequestParam(required = false) List<String> companyIds,
      @RequestParam(defaultValue = "false") boolean isForAll, Authentication token) {
    return process(helper::update, token, pulseQuestionSetId, type, companyIds, name, isForAll);
  }
  
}
