package com.sp.web.service.tutorial;

import com.sp.web.Constants;
import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.dto.tutorial.SPTutorialDTO;
import com.sp.web.dto.tutorial.SPTutorialListingDTO;
import com.sp.web.form.tutorial.SPTutorialForm;
import com.sp.web.model.User;
import com.sp.web.service.badge.BadgeFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 * 
 *         The factory class for the SP tutorial entity.
 */
@Component
public class AdminSPTutorialFactory implements
    GenericFactory<SPTutorialListingDTO, SPTutorialDTO, SPTutorialForm> {
  
  @Autowired
  private SPTutorialFactoryCache factoryCache;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  @Override
  public List<SPTutorialListingDTO> getAll(User user) {
    return factoryCache.getAll().stream().map(SPTutorialListingDTO::new)
        .collect(Collectors.toList());
  }
  
  @Override
  public SPTutorialDTO get(User user, SPTutorialForm form) {
    return new SPTutorialDTO(factoryCache.get(form.getId(), Constants.DEFAULT_LOCALE));
  }
  
  @Override
  public SPTutorialDTO create(User user, SPTutorialForm form) {
    return new SPTutorialDTO(factoryCache.createUpdate(form.create(user), Constants.DEFAULT_LOCALE));
  }
  
  @Override
  public SPTutorialDTO update(User user, SPTutorialForm form) {
    SPTutorialDao tutorialDao = factoryCache.get(form.getId(), Constants.DEFAULT_LOCALE);
    Assert.notNull(tutorialDao, "Tutorial not found.");
    form.update(user, tutorialDao);
    SPTutorialDTO tutorialDTO = new SPTutorialDTO(factoryCache.createUpdate(tutorialDao,
        Constants.DEFAULT_LOCALE));
    if (form.hasStepsToRemove()) {
      factoryCache.removeSteps(form.getStepIdsToRemove());
    }
    return tutorialDTO;
  }
  
  @Override
  public void delete(User user, SPTutorialForm form) {
    factoryCache.delete(form.getId(), Constants.DEFAULT_LOCALE);
    
    /* delete the badge  assigned to the users */
    badgeFactory.deleteBadgeProgress(form.getId());
  }
  
}
