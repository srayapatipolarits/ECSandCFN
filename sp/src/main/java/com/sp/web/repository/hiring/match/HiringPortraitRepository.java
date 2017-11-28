package com.sp.web.repository.hiring.match;

import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.repository.generic.GenericMongoRepository;

/**
 * 
 * @author Dax Abraham
 *
 *         The repository interface for Hiring Portraits.
 */
public interface HiringPortraitRepository extends GenericMongoRepository<HiringPortrait> {

  /**
   * Get the hiring portrait for the given name.
   * 
   * @param name
   *          - portrait name
   * @return
   *    hiring portrait found.
   */
  HiringPortrait findByNameIgnoreCase(String name);
  
}
