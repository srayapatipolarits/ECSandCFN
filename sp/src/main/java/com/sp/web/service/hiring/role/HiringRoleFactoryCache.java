package com.sp.web.service.hiring.role;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.repository.hiring.role.HiringRoleRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The cache class for the hiring role factory.
 */
@Component
public class HiringRoleFactoryCache {
  
  private static final Logger log = Logger.getLogger(HiringRoleFactoryCache.class);
  
  @Autowired
  HiringRoleRepository repo;

  public List<HiringRole> getAll(String companyId) {
    return repo.findAllByCompanyId(companyId);
  }

  public HiringRole get(String roleId) {
    return repo.findById(roleId);
  }

  /**
   * Save the given hiring role.
   * 
   * @param role
   *        - role
   */
  public void save(HiringRole role) {
    try {
      repo.save(role);
    } catch (Exception e) {
      log.warn("Error saving role.", e);
      throw new InvalidRequestException("Error saving role.", e);
    }
  }

  public void delete(HiringRole role) {
    repo.delete(role);
  }

  public List<HiringRole> getByPortrait(String id) {
    return repo.findByPortraitId(id);
  }

  public List<HiringRole> getByPortrait(String portraitId, String companyId) {
    return repo.findByPortraitId(portraitId, companyId);
  }

  public HiringRole findByNameIgnoreCase(String companyId, String name) {
    return repo.findByNameIgnoreCase(companyId, name);
  }
}
