package com.sp.web.repository.bm.ta;

import com.sp.web.model.bm.ta.ToneAnalysisRecord;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The repository interface for tone analysis records.
 */
public interface ToneAnalysisRepository extends GenericMongoRepository<ToneAnalysisRecord> {

  /**
   * Get all the tone analysis records for the given user.
   * 
   * @param userId
   *          - user id
   * @return
   *    the list of tone analysis records for the user
   */
  List<ToneAnalysisRecord> findAllByUserId(String userId);
  
}
