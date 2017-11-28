package com.sp.web.service.hiring.dasboard;

import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dto.hiring.dashboard.HiringAdminDashboardSettingsDTO;
import com.sp.web.dto.hiring.dashboard.HiringAdminDashboardSettingsListingDTO;
import com.sp.web.dto.hiring.dashboard.HiringDashboardBaseSettingsDTO;
import com.sp.web.form.hiring.dashboard.HiringAdminFormSettings;
import com.sp.web.model.User;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.repository.hiring.dashboard.HiringDashboardRepository;
import com.sp.web.repository.library.ArticlesFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author pruhil
 * 
 *         The factory class for the People Analytics Dashboard page.
 */
@Component
public class AdminHiringDashboardFactory
    implements
    GenericFactory<HiringAdminDashboardSettingsListingDTO, HiringAdminDashboardSettingsDTO, HiringAdminFormSettings> {
  
  @Autowired
  private HiringDashboardRepository repository;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Override
  public List<HiringAdminDashboardSettingsListingDTO> getAll(User user) {
    return repository.findAll().stream()
        .map(settings -> new HiringAdminDashboardSettingsListingDTO(settings, articlesFactory))
        .collect(Collectors.toList());
  }
  
  @Override
  public HiringAdminDashboardSettingsDTO get(User user, HiringAdminFormSettings form) {
    HiringDashboardSettings findById = repository.findById(form.getId());
    Assert.notNull(findById, "Hiring Dashboard Settings not found.");
    return new HiringAdminDashboardSettingsDTO(findById, articlesFactory);
  }
  
  @Override
  public HiringAdminDashboardSettingsDTO create(User user, HiringAdminFormSettings form) {
    HiringDashboardSettings settings = form.create(user);
    repository.save(settings);
    return new HiringAdminDashboardSettingsDTO(settings, articlesFactory);
  }
  
  @Override
  public HiringAdminDashboardSettingsDTO update(User user, HiringAdminFormSettings form) {
    HiringDashboardSettings dashboardSettings = repository.findById(form.getId());
    Assert.notNull(dashboardSettings, "Dashboard Settings for People Analytics not found.");
    form.update(user, dashboardSettings);
    repository.save(dashboardSettings);
    HiringAdminDashboardSettingsDTO tutorialDTO = new HiringAdminDashboardSettingsDTO(
        dashboardSettings, articlesFactory);
    
    return tutorialDTO;
  }
  
  @Override
  public void delete(User user, HiringAdminFormSettings form) {
    repository.delete(repository.findById(form.getId()));
  }
  
  /**
   * getAllDashboard will return the dashboard DTO data.
   * 
   * @param user
   *          is the logged in user.
   * @return return list of dashboard settings data.
   */
  public List<HiringDashboardBaseSettingsDTO> getAllDashboard(User user) {
    return repository.findAll().stream()
        .map(settings -> new HiringDashboardBaseSettingsDTO(settings, articlesFactory))
        .collect(Collectors.toList());
  }
}
