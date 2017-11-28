package com.sp.web.controller.systemadmin.hiring.match;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author Dax Abraham
 * 
 *         The view mapping for the system admin screens for portrait match.
 */
@Controller
public class SystemAdminPortraitMatchController {
  /**
   * Create Ideal Portrait.
   */
  @RequestMapping(value = "/sysAdmin/idealPortrait/create", method = RequestMethod.GET)
  public String createIdealPortrait(Authentication token) {
    return "createIdealPortrait";
  }
  
  /**
   * Update Ideal Portrait.
   */
  @RequestMapping(value = "/sysAdmin/idealPortrait/update", method = RequestMethod.GET)
  public String updateIdealPortrait(Authentication token) {
    return "updateIdealPortrait";
  }
  
  /**
   * Clone Ideal Portrait.
   */
  @RequestMapping(value = "/sysAdmin/idealPortrait/clone", method = RequestMethod.GET)
  public String cloneIdealPortrait(Authentication token) {
    return "cloneIdealPortrait";
  }
  
  /**
   * Listing Ideal Portrait.
   */
  @RequestMapping(value = "/sysAdmin/idealPortrait", method = RequestMethod.GET)
  public String listingIdealPortrait(Authentication token) {
    return "listingIdealPortrait";
  }
}
