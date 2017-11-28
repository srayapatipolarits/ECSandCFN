package com.sp.web.controller.hiring.match;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.hiring.match.HiringPortraitMatchForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller for the hiring portrait match controller.
 */
@Controller
@RequestMapping("/hiring/match")
public class HiringPortraitMatchController {
  
  @Autowired
  HiringPortraitMatchControllerHelper helper;
  
  /**
   * Controller method to get all portraits for the given company.
   * 
   * @param basePortrait
   *          - flag to indicate if only base portrait
   * @param token
   *          - logged in user
   * @return the response to the assign request
   */
  @RequestMapping(value = "/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(@RequestParam(defaultValue = "false") boolean basePortrait,
      Authentication token) {
    return process(helper::getAll, token, basePortrait);
  }
  
  /**
   * Controller method to get details for portrait for the given company.
   * 
   * @param portraitId
   *          - portrait id
   * @param token
   *          - logged in user
   * @return the response to the assign request
   */
  @RequestMapping(value = "/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse get(@RequestParam String portraitId, Authentication token) {
    return process(helper::get, token, portraitId);
  }
  
  /**
   * Controller method to get details for portrait for the given company.
   * 
   * @param form
   *          - match portrait form
   * @param token
   *          - logged in user
   * @return the response to the assign request
   */
  @RequestMapping(value = "/matchPortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse matchPortrait(HiringPortraitMatchForm form, Authentication token) {
    return process(helper::matchPortrait, token, form);
  }
}
