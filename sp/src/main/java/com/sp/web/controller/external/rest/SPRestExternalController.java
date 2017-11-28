package com.sp.web.controller.external.rest;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.external.rest.UserForm;
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
 * SPRestController is the rest controller for providing exposing SurePeople services to external
 * partner.
 * 
 * @author pradeepruhil
 *
 */
@Controller
@RequestMapping("/rest")
public class SPRestExternalController {
  
  /**
   * Helper for rest external controller.
   */
  @Autowired
  private SPRestControllerHelper helper;
  
  /**
   * create User method will create the user in the SurePeople plateform.
   * 
   * @param form
   *          is the form of the user.
   * @param token
   *          user who is logged in.
   * @return is the
   */
  @RequestMapping(value = "/user/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createUser(@RequestBody UserForm form, Authentication token) {
    return ControllerHelper.process(helper::createUser, token, form);
    
  }
  
  /**
   * delete User method will remove the user in the SurePeople plateform.
   * 
   * @param form
   *          is the form of the user.
   * @param token
   *          user who is logged in.
   * @return is the
   */
  @RequestMapping(value = "/user/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteUser(@RequestParam String email, Authentication token) {
    return ControllerHelper.process(helper::deleteUser, token, email);
    
  }
}
