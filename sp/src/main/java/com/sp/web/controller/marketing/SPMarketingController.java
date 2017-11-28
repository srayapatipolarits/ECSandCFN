package com.sp.web.controller.marketing;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 * @author Prasanna Venkatesh
 *
 *   The controller for the A/B Marketing tests
 */

@Controller
@RequestMapping("/marketing")
public class SPMarketingController {
  
  @Autowired
  private SPMarketingControllerHelper helper;
  
  /**
   * The controller method for retrieving the marketing A/B version for a user.
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
   * The controller method to save  the marketing A/B version for a user.
   * 
   * @param marketing version
   *          - String 
   * @param token
   *          - logged in user        
   * @return
   *     the response 
   */
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse save(Authentication token, String version) {
    return ControllerHelper.process(helper::save, token, version);
  }
}
