package com.sp.web.controller.tutorial;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller for the SP Tutorial user requests.
 */
@Controller
@RequestMapping("/tutorial")
public class SPTutorialController {
  
  @Autowired
  private SPTutorialControllerHelper helper;
  
  /**
   * The controller method for getting the current get started program details.
   * 
   * @param token
   *          - logged in user
   * @return
   *    the response to the get request
   */
  @RequestMapping(value = "/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse get(Authentication token) {
    return process(helper::get, token);
  }

  /**
   * Controller method to get the details for the given tutorial.
   * 
   * @param tutorialId
   *            - tutorial id
   * @param token
   *            - logged in user
   * @return
   *    the details for the tutorial
   */
  @RequestMapping(value = "/getDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDetails(@RequestParam String tutorialId, Authentication token) {
    return process(helper::getDetails, token, tutorialId);
  }

  /**
   * Controller method to mark the action in the tutorial step as completed.
   * 
   * @param tutorialId
   *          - tutorial id
   * @param stepId
   *          - step id
   * @param actionId
   *          - action id
   * @param token
   *          - logged in user
   * @return
   *    the response to the mark complete request
   */
  @RequestMapping(value = "/markComplete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse markComplete(@RequestParam String tutorialId, @RequestParam String stepId,
      @RequestParam String actionId, Authentication token) {
    return process(helper::markComplete, token, tutorialId, stepId, actionId);
  }
  
}
