package com.sp.web.controller.hiring.spectrum;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * HiringSpectrumController class handles all the spectrum request for the people analtyics.
 * 
 * @author pradeepruhil
 *
 */
@Controller
@RequestMapping("/hiring/spectrum")
public class HiringSpectrumController {
  
  @Autowired
  private HiringSpectrumControllerHelper helper;
  
  /**
   * getProfileBalance request will return the personality balance of the user.
   * 
   * @param token
   *          is the logged in user.
   * @return the getProfileBalance.
   */
  @RequestMapping(value = "/personalityBalance", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProfileBalance(Authentication token) {
    return ControllerHelper.process(helper::getPersonalityBalance, token);
  }
  
  @RequestMapping(value = "/ertiInsights", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getErtiInsights(Authentication token) {
    return ControllerHelper.process(helper::getErtiInsights, token);
  }
  
  @RequestMapping(value = "/ertiAnalytics", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getErtiAnalytics(Authentication token) {
    return ControllerHelper.process(helper::getErtiAnalytics, token);
  }
}
