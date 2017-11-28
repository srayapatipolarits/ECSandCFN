package com.sp.web.controller.bm.ta;

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
 *         The controller for all the dashboard related functionalities.
 */
@Controller
public class ToneAnalysisController {
  
  @Autowired
  ToneAnalysisControllerHelper helper;
  
  /** Tone Analyzer company listing view for Super Admin. */
  @RequestMapping(value = "/sysAdmin/toneAnalyze/home", method = RequestMethod.GET)
  public String accessManager(Authentication token) {
    return "home";
  }
  
  /** Tone Analyzer detail View for Super Admin. */
  @RequestMapping(value = "/sysAdmin/toneAnalyze/detail", method = RequestMethod.GET)
  public String updateAccess(Authentication token) {
    return "detail";
  }
  
  /**
   * Controller method to get all the aggregate tone analysis for the given company.
   * 
   * @param companyId
   *          - company id
   * @param token
   *          - logged in user
   * @return the response to the get all request
   */
  @RequestMapping(value = "/toneAnalysis/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(@RequestParam String companyId, Authentication token) {
    return process(helper::getAll, token, companyId);
  }

  /**
   * Controller method to get all the user tone analysis records.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return
   *    the response to the get all request
   */
  @RequestMapping(value = "/toneAnalysis/getAllUserToneAnalysis", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllUserToneAnalysis(@RequestParam String userId, Authentication token) {
    return process(helper::getAllUserToneAnalysis, token, userId);
  }
  
}
