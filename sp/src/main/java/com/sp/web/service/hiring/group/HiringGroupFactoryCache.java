package com.sp.web.service.hiring.group;

import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.repository.hiring.group.HiringGroupRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         The factory cache class to enable caching of repository instances if required.
 */
@Component
public class HiringGroupFactoryCache {

  private static final Logger log = Logger.getLogger(HiringGroupFactoryCache.class);
  
  @Autowired
  HiringGroupRepository groupRepository;

  public List<HiringGroup> getAll(String companyId) {
    return groupRepository.findAllByCompanyId(companyId);
  }

  public HiringGroup get(String id) {
    return groupRepository.findById(id);
  }

  /**
   * Saving the group.
   * 
   * @param group
   *          - group to save.
   * @return
   *      the updatd group
   */
  public HiringGroup save(HiringGroup group) {
    try {
      groupRepository.save(group);
    } catch (Exception e) {
      log.warn("Error saving the people analytics group.", e);
      throw new IllegalArgumentException("Error saving group.");
    }
    return group;
  }

  /**
   * Remove the hiring group.
   * 
   * @param group
   *          - group
   */
  public void remove(HiringGroup group) {
    groupRepository.delete(group);
  }

  public List<HiringGroup> getGroupsForUser(String companyId, String userId) {
    return groupRepository.findGroupsForUser(companyId, userId);
  }

  public HiringGroup findByNameIgnoreCase(String companyId, String name) {
    return groupRepository.findByNameIgnoreCase(companyId, name);
  }
}
