package com.sp.web.controller.hiring.dashboard;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * HiringDashbaordController class will handle the dashboard request for the people analytics
 * plateform.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class HiringDashboardController {
  
  @Autowired
  private HiringDashboardControllerHelper helper;
  
  /**
   * getEmployeeCandidateStat request will give all the statistics for the employee and candidate
   * section for People Analytics dashboard.
   * 
   * @param auth
   *          People Analytics administrator.
   * @return response.
   */
  @RequestMapping(value = "/hiring/dashboard/getEmployeeCandidateStats", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getEmployeCandidateStats(Authentication auth) {
    return ControllerHelper.process(helper::getEmployeeCandidate, auth);
  }
  
  /**
   * getDashboardSettings method will return the dasboardSettings for the People analytics.
   * 
   * @param auth
   *          logged in user.
   * @return the response.
   */
  @RequestMapping(value = "/hiring/dashboard/getDashboardSettingsDetail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDashboardSettingsDetail(Authentication auth) {
    return ControllerHelper.process(helper::getHiringDashboardSettings, auth);
  }
  
  @RequestMapping(value = "/hiring/dashboard/visitedDashboard", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse visitedDashboard(Authentication auth) {
    return ControllerHelper.process(helper::visitedDashboard, auth);
  }
  
  /**
   * Dashboard People Analytics User Side
   */
  @RequestMapping(value = "/pa/dashboard", method = RequestMethod.GET)
  public String getPADashBoard() {
    return "paDashBoard";
  }
}
