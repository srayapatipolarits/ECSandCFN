package com.sp.web.controller.admin.themes;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.goal.PracticeAreaForm;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * <code>Practice Area Controller</code> define the interfaces to manage the practice areas in the
 * SP.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class AdminPracticeAreaController {
  
  /**
   * AdminGoalsControllerHelper is the controller helper class for the admin.
   */
  @Autowired
  private AdminPracticeAreaControllerHelper helper;
  
  /**
   * Method to get the all the practice areas.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the list of practice areas
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllGoals(Authentication token) {
    return process(helper::getAll, token);
  }

  /**
   * Controller method to update the practice area status.
   * 
   * @param practiceAreaId
   *          - practice area id to update
   * @param status
   *          - new status 
   * @param token
   *          - logged in user
   * @return
   *      the response to the update request
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/activateDeactivate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse activateDeactivate(@RequestParam String practiceAreaId,
      @RequestParam GoalStatus status, Authentication token) {
    return process(helper::activateDeactivate, token, practiceAreaId, status);
  }
  
  /**
   * Controller method to create a new practice area.
   * 
   * @param form
   *          - form for the create request
   * @param token
   *          - logged in user
   * @return
   *    status for the create request
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse create(@Valid PracticeAreaForm form, Authentication token) {
    return process(helper::create, token, form);
  }

  /**
   * Controller method to update the practice areas.
   * 
   * @param practiceAreaId
   *            - practice area id to update
   * @param form
   *            - practice area data to update
   * @param token
   *            - logged in user 
   * @return
   *    the status for the update request
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse update(@RequestParam String practiceAreaId, @Valid PracticeAreaForm form,
      Authentication token) {
    return process(helper::update, token, form, practiceAreaId);
  }
}
