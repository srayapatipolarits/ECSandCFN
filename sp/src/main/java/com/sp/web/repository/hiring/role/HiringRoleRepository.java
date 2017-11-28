package com.sp.web.repository.hiring.role;

import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The repository interface for the hiring roles.
 */
public interface HiringRoleRepository extends GenericMongoRepository<HiringRole> {

  /**
   * Get all the hiring roles by the given portrait id.
   * 
   * @param portraitId
   *          - portrait id
   * @return
   *    the list of hiring roles
   */
  List<HiringRole> findByPortraitId(String portraitId);

  /**
   * Get all the roles for the given portrait id in the company.
   * 
   * @param portraitId
   *          - portrait id
   * @param companyId
   *          - company id
   * @return
   *    the list of roles
   */
  List<HiringRole> findByPortraitId(String portraitId, String companyId);
 
  /**
   * Find an existing hiring role with the same name.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return
   *    the hiring group
   */
  HiringRole findByNameIgnoreCase(String companyId, String name);
  
}
