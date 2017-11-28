package com.sp.web.controller.utility;

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
 * @author Dax Abraham
 * 
 *         The generic controller for utility services.
 */
@Controller
public class UtilitiesController {
  
  @Autowired
  UtilitiesControllerHelper helper;
  
  /**
   * Controller method to get open graph details.
   * 
   * @param url
   *          - URL to check
   * @param token
   *          - token
   * @return the view to redirect to
   */
  @RequestMapping(value = "/utility/og", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse tokenRequest(@RequestParam String url, Authentication token) {
    return process(helper::processOG, token, url);
  }
}