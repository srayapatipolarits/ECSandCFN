package com.sp.web.service.hiring.match;

import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.repository.hiring.match.HiringPortraitRepository;
import com.sp.web.service.hiring.match.processor.HiringPortraitMatchProcessorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory cache class for the hiring portrait match.
 */
@Component
public class HiringPortraitMatchFactoryCache {
  
  @Autowired
  HiringPortraitRepository repo;
  
  @Autowired
  HiringPortraitMatchProcessorFactory processorFactory;

  public List<HiringPortrait> getAll() {
    return repo.findAll();
  }

  /**
   * Get the hiring portrait from cache or the DB.
   * 
   * @param id
   *        - portrait id
   * @return
   *      the hiring portrait or null
   */
  @Cacheable("portrait")
  public HiringPortraitDao get(String id) {
    final HiringPortrait findById = repo.findById(id);
    if (findById != null) {
      return new HiringPortraitDao(findById, processorFactory);
    }
    return null;
  }

  /**
   * Create the portrait with the given name.
   * 
   * @param create
   *          - create
   */
  public void create(HiringPortrait create) {
    HiringPortrait portrait = repo.findByNameIgnoreCase(create.getName());
    Assert.isNull(portrait, "Portrait already exists.");
    repo.save(create);
  }

  /**
   * Update the given portrait.
   * 
   * @param portrait
   *          - portrait to check
   */
  @CacheEvict(value = "portrait", key = "#portrait.id")
  public void update(HiringPortraitDao portrait) {
    repo.save(portrait);
  }

  /**
   * Update the given portrait and also check if a portait with the same name exists.
   * 
   * @param portrait
   *          - portrait to check
   */
  @CacheEvict(value = "portrait", key = "#portrait.id")
  public void adminUpdate(HiringPortraitDao portrait) {
    HiringPortrait existingPortrait = repo.findByNameIgnoreCase(portrait.getName());
    if (existingPortrait != null) {
      Assert.isTrue(portrait.getId().equalsIgnoreCase(existingPortrait.getId()), "Portrait already exists.");
    }
    repo.save(portrait);
  }
  
  @CacheEvict(value = "portrait", key = "#portrait.id")
  public void delete(HiringPortraitDao portrait) {
    repo.delete(portrait);
  }

  public List<HiringPortrait> getAllByCompanyId(String companyId) {
    return repo.findAllByCompanyId(companyId);
  }
  
}
