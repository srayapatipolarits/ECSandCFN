package com.sp.web.dto.hiring.dashboard;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.repository.library.ArticlesFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HiringAdminDashboardSettingsDTO extends HiringDashboardBaseSettingsDTO implements
    Serializable {
  
  private static final long serialVersionUID = -9081251962627248892L;
  
  private UserMarkerDTO updatedBy;
  
  private LocalDateTime updatedOn;
  
  private String id;
  
  /**
   * Created Hiring Admin Dashboard Settings for People Analytics.
   * 
   * @param hiringDashboardSetting
   *          hiring dashboard settings
   * @param articlesFactory
   *          articles factory.
   */
  public HiringAdminDashboardSettingsDTO(HiringDashboardSettings hiringDashboardSetting,
      ArticlesFactory articlesFactory) {
    super(hiringDashboardSetting, articlesFactory);
    
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}
