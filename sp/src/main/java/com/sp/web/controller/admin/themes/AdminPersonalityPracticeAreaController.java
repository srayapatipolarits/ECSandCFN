package com.sp.web.controller.admin.themes;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.form.admin.personality.PersonalitySwotForm;
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
 * <code>Practice Area Controller</code> define the interfaces to manage the practice areas in the
 * SP.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class AdminPersonalityPracticeAreaController {
  
  /**
   * AdminGoalsControllerHelper is the controller helper class for the admin.
   */
  @Autowired
  private AdminPersonalityPracticeAreaControllerHelper helper;
  
  /**
   * The controller method to get all the personality mapping.
   * 
   * @param token
   *          - logged in user
   * @return the list of personality mapping
   */
  
  @RequestMapping(value = "/sysAdmin/personalityMapping", method = RequestMethod.GET)
  public String importgoalsPersonalityMappingView() {
    return "goalsPersonalityMapping";
  }
  
  @RequestMapping(value = "/sysAdmin/personalityMapping/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    return process(helper::getAll, token);
  }
  
  /**
   * Controller method to update the personality practice area.
   * 
   * @param personalityType
   *          - personality type
   * @param goalIds
   *          - list of goal id's
   * @param token
   *          - logged in user
   * @return the status of the update request
   */
  @RequestMapping(value = "/sysAdmin/personalityMapping/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse update(@RequestParam PersonalityType personalityType,
      @RequestParam List<String> goalIds, Authentication token) {
    return process(helper::update, token, personalityType, goalIds);
  }
  
  /**
   * Controller method to update the Swot profile details.
   * 
   * @param personalityType
   *          - personality type
   * @param mappingSwotForm contains the swot mapping.
   * @param token
   *          - logged in user
   * @return the status of the update request
   */
  @RequestMapping(value = "/sysAdmin/personalityMapping/swot/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateSwot(@RequestBody PersonalitySwotForm swotForm,
      Authentication token) {
    return process(helper::updateSwot, token, swotForm);
  }
}
