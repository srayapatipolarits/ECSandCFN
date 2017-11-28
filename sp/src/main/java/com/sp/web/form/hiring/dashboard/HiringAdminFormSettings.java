package com.sp.web.form.hiring.dashboard;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HiringAdminFormSettings implements GenericForm<HiringDashboardSettings> {
  
  private String id;
  
  private Map<SPFeature, String> mediaSettings;
  
  private List<String> artilces;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Map<SPFeature, String> getMediaSettings() {
    return mediaSettings;
  }
  
  public void setMediaSettings(Map<SPFeature, String> mediaSettings) {
    this.mediaSettings = mediaSettings;
  }
  
  public List<String> getArtilces() {
    return artilces;
  }
  
  public void setArtilces(List<String> artilces) {
    this.artilces = artilces;
  }
  
  @Override
  public void validate() {
  }
  
  private void validateId() {
    Assert.hasText(id, "ID is required.");
  }
  
  @Override
  public void validateUpdate() {
    validateId();
    validate();
  }
  
  @Override
  public void validateGet() {
    validateId();
    
  }
  
  @Override
  public HiringDashboardSettings create(User user) {
    
    HiringDashboardSettings dashboardSettings = new HiringDashboardSettings();
    UserMarkerDTO createdBy = new UserMarkerDTO(user);
    dashboardSettings.setCreatedBy(createdBy);
    dashboardSettings.setUpdatedBy(createdBy);
    LocalDateTime createdOn = LocalDateTime.now();
    dashboardSettings.setUpdatedOn(createdOn);
    if (MapUtils.isNotEmpty(mediaSettings)) {
      dashboardSettings.setImages(getMediaSettings());
    }
    return dashboardSettings;
  }
  
  @Override
  public void update(User user, HiringDashboardSettings instanceToUpdate) {
    instanceToUpdate.setUpdatedBy(new UserMarkerDTO(user));
    instanceToUpdate.setUpdatedOn(LocalDateTime.now());
    instanceToUpdate.setArtilces(getArtilces());
    if (!MapUtils.isEmpty(mediaSettings)) {
      instanceToUpdate.setImages(getMediaSettings());
    }
    
  }
  
}
