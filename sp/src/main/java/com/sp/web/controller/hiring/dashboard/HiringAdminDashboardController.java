package com.sp.web.controller.hiring.dashboard;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.hiring.dashboard.HiringAdminDashboardSettingsDTO;
import com.sp.web.form.hiring.dashboard.HiringAdminFormSettings;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@Controller
@RequestMapping("/sysAdmin/hiring/dashboard")
public class HiringAdminDashboardController
    extends
    GenericController<HiringDashboardSettings, HiringAdminDashboardSettingsDTO, HiringAdminDashboardSettingsDTO, HiringAdminFormSettings, HiringAdminDashboardControllerHelper> {
  
  @Inject
  public HiringAdminDashboardController(HiringAdminDashboardControllerHelper helper) {
    super(helper);
  }
  
}
