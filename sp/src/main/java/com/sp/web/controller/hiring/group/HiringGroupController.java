package com.sp.web.controller.hiring.group;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.hiring.group.HiringGroupDTO;
import com.sp.web.dto.hiring.group.HiringGroupListingDTO;
import com.sp.web.form.hiring.group.HiringGroupForm;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 * 
 *         This is the controller for managing all the services for the People Analytics Groups.
 */
@Controller
@RequestMapping("/hiring/group")
public class HiringGroupController
    extends
    GenericController<HiringGroup, HiringGroupListingDTO, HiringGroupDTO, 
                      HiringGroupForm, HiringGroupControllerHelper> {
  
  @Inject
  public HiringGroupController(HiringGroupControllerHelper helper) {
    super(helper);
  }
  
  /**
   * Controller method to add users to the group.
   *  
   * @param form
   *        - form for the data
   * @param token
   *        - logged in user
   * @return
   *      the response to the add request
   */
  @RequestMapping(value = "/addUsers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addUsers(@RequestBody HiringGroupForm form, Authentication token) {
    return process(helper::addUsers, token, form);
  }
  
  /**
   * Controller method to remove the users from the group.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return
   *    the response to the remove request
   */
  @RequestMapping(value = "/removeUsers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeUsers(@RequestBody HiringGroupForm form, Authentication token) {
    return process(helper::removeUsers, token, form);
  }

  /**
   * Controller method to get the user's groups.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return
   *    the response to the get user groups request
   */
  @RequestMapping(value = "/userGroups", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse userGroups(@RequestParam String userId, Authentication token) {
    return process(helper::userGroups, token, userId);
  }
  
  /**
   * Controller method to get the portraits for all the users in the group.
   *  
   * @param form
   *        - form for the data
   * @param token
   *        - logged in user
   * @return
   *      the response to the get request
   */
  @RequestMapping(value = "/getPortraits", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPortraits(HiringGroupForm form, Authentication token) {
    return process(helper::getPortraits, token, form);
  }
  
}
