package com.sp.web.dto.hiring.dashboard;

import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.repository.library.ArticlesFactory;

/**
 * HirignAdminListingDTO for the settings for admin.
 * 
 * @author pradeepruhil
 *
 */
public class HiringAdminDashboardSettingsListingDTO extends HiringAdminDashboardSettingsDTO {
  
  public HiringAdminDashboardSettingsListingDTO(HiringDashboardSettings dasboardSettings,
      ArticlesFactory articlesFactory) {
    super(dasboardSettings, articlesFactory);
  }
  
  private static final long serialVersionUID = 5329520170535802534L;
  
}
