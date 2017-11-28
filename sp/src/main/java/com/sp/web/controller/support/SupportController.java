/**
 * 
 */
package com.sp.web.controller.support;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <code>SupportController</code> provides functioanlty for the support pages.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class SupportController {

  /**
   * <code>getSupportHome</code> method return the support home.
   * 
   * @return
   */
  @RequestMapping(value = { "/support" }, method = RequestMethod.GET)
  public String getSupportHome() {
    return "supportHome";
  }
  
  /**
   * <code>getSupportHome</code> method return the support home.
   * 
   * @return
   */
  @RequestMapping(value = "/pa/support", method = RequestMethod.GET)
  public String getPASupportHome() {
    return "paSupportHome";
  }
  
  /**
   * View For add member.
   * 
   */
  @RequestMapping(value = "/support/videoPopUp", method = RequestMethod.GET)
  public String validateaddMemberScreen(Authentication token) {

    return "videoPopUp";
  }

}
