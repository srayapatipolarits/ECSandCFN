package com.sp.web.controller.systemadmin.hiring.dashboard;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author Dax Abraham
 * 
 *         The controller mappings for the dashboard settings for system administrator.
 */
@Controller
public class SystemAdminHiringDashboardSettings {
  
  /**
   * Dashboard People Analytics settings.
   */
  @RequestMapping(value = "/sysAdmin/paDashboardSettings", method = RequestMethod.GET)
  public String paDashboardSettings(Authentication token) {
    return "paDashboardSettings";
  }
}
