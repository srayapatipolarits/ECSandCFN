package com.sp.web.repository.hiring.group;

import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

public interface HiringGroupRepository extends GenericMongoRepository<HiringGroup> {

  /**
   * Get the groups for the given user id.
   * 
   * @param companyId
   *          - company id
   * @param userId
   *          - user id
   * @return
   *    the list of groups
   */
  List<HiringGroup> findGroupsForUser(String companyId, String userId);

  /**
   * Find an existing hiring group with the same name.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return
   *    the hiring group
   */
  HiringGroup findByNameIgnoreCase(String companyId, String name);
  
}
