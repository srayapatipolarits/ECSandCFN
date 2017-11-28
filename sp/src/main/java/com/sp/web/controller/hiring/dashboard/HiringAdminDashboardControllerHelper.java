package com.sp.web.controller.hiring.dashboard;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.hiring.dashboard.HiringAdminDashboardSettingsDTO;
import com.sp.web.dto.hiring.dashboard.HiringAdminDashboardSettingsListingDTO;
import com.sp.web.form.hiring.dashboard.HiringAdminFormSettings;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.service.hiring.dasboard.AdminHiringDashboardFactory;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class HiringAdminDashboardControllerHelper
    extends
    GenericControllerHelper<HiringDashboardSettings, HiringAdminDashboardSettingsListingDTO, HiringAdminDashboardSettingsDTO, HiringAdminFormSettings, AdminHiringDashboardFactory> {
  
  private static final String MODULE_NAME = "hiringDashboard";
  
  @Inject
  public HiringAdminDashboardControllerHelper(AdminHiringDashboardFactory factory) {
    super(MODULE_NAME, factory);
  }
  
}
