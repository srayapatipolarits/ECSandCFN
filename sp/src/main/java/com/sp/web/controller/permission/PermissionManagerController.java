package com.sp.web.controller.permission;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.RoleType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

/**
 * Permission Manager Controler will manager the permission for the user.
 * 
 * @author pradeepruhil
 *
 *
 */
@Controller
public class PermissionManagerController {
  
  @Autowired
  private PermissionManagerControllerHelper permissionManagerHelper;
  
  /** Access Manager User Listing View for Super Admin. */
  @RequestMapping(value = "/sysAdmin/accessManager", method = RequestMethod.GET)
  public String accessManager(Authentication token) {
    return "accessManager";
  }
  
  /** Update User Access View Super Admin. */
  @RequestMapping(value = "/sysAdmin/updateAccess", method = RequestMethod.GET)
  public String updateAccess(Authentication token) {
    return "updateAccess";
  }
  
  /**
   * Modal View For Selecting a Company and User
   */
  @RequestMapping(value = "/sysAdmin/accessManager/selectCompanyAndUser", method = RequestMethod.GET)
  public String selectCompanyAndUser(Authentication token) {
    return "selectCompanyAndUser";
  }
  
  /**
   * getUserPermissionList method will return the users which are having super administration
   * Functionalities roles.
   * 
   * @param authentication
   *          super administrator user.
   * @return the response.
   */
  @RequestMapping(value = "/sysAdmin/accessManager/getUserPermissionList", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserPermissionList(Authentication authentication) {
    return ControllerHelper.process(permissionManagerHelper::getUserAccessList, authentication);
  }
  
  /**
   * getSysPermissions request will return the sys admin permission for the user.
   * 
   * @param authentication
   *          super administrator user.
   * @return the response.
   */
  @RequestMapping(value = "/sysAdmin/accessManager/getSysPermissions", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getSysPermissions(@RequestParam(required = false) String email,
      Authentication authentication) {
    email =  StringUtils.trimWhitespace(email.toLowerCase());
    return ControllerHelper.process(permissionManagerHelper::getSysPermissions, authentication, email);
  }
  
  /**
   * SavePermissionToUser request will save the sys permission to the users.
   * 
   * @param email
   *          for which sys permission is to be udpated.
   * @param roles
   *          roles which are to be updated.
   * @param authentication
   *          super administration user.
   * @return the response.
   */
  @RequestMapping(value = "/sysAdmin/accessManager/savePermission", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse savePermissionToUser(@RequestParam String email,
      @RequestParam List<RoleType> roles, Authentication authentication) {
    return ControllerHelper.process(permissionManagerHelper::saveSysPermissions, authentication,
        email, roles);
  }
}
