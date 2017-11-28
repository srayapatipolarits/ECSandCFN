package com.sp.web.controller.hiring.role;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.hiring.role.HiringRoleDTO;
import com.sp.web.dto.hiring.role.HiringRoleListingDTO;
import com.sp.web.form.hiring.role.HiringRoleForm;
import com.sp.web.model.hiring.role.HiringRole;
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
 *         The controller for the people analytics roles.
 */
@Controller
@RequestMapping("/hiring/role")
public class HiringRoleController
    extends
    GenericController<HiringRole, HiringRoleListingDTO, HiringRoleDTO, HiringRoleForm, HiringRoleControllerHelper> {
  
  /**
   * Constructor.
   * 
   * @param helper
   *          - hiring role controller helper
   */
  @Inject
  public HiringRoleController(HiringRoleControllerHelper helper) {
    super(helper);
  }
  
  /**
   * Controller method to assign portrait to role.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return
   *    the response to the assign request
   */
  @RequestMapping(value = "/assignPortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse assignPortrait(@RequestBody HiringRoleForm form, Authentication token) {
    return process(helper::assignPortrait, token, form);
  }
  
  /**
   * Controller method to remove portrait from role.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return
   *    the response to the remove request
   */
  @RequestMapping(value = "/removePortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removePortrait(@RequestBody HiringRoleForm form, Authentication token) {
    return process(helper::removePortrait, token, form);
  }
  
  /**
   * Controller method to get the role details and portrait match for the given user.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return
   *    the response to the get request
   */
  @RequestMapping(value = "/getUserRolesAndMatch", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserRolesAndMatch(@RequestParam String userId, Authentication token) {
    return process(helper::getUserRolesAndMatch, token, userId);
  }  
  
  /**
   * Controller method to get the roles without any assigned portraits.
   * 
   * @param token
   *          - logged in user
   * @return
   *    the response to the get request
   */
  @RequestMapping(value = "/getRolesWithoutPortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getRolesWithoutPortrait(Authentication token) {
    return process(helper::getRolesWithoutPortrait, token);
  } 
  
  /**
   * Controller method to add the users to the given role.
   * 
   * @param form
   *          - request form
   * @param token
   *          - logged in user
   * @return
   *    the response to the add request
   */
  @RequestMapping(value = "/addToRole", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addToRole(@RequestBody HiringRoleForm form, Authentication token) {
    return process(helper::addToRole, token, form);
  }   
}
