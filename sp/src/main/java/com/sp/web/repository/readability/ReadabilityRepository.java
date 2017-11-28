package com.sp.web.repository.readability;

import com.sp.web.model.readability.ReadabilityScore;

/**
 * @author Prasanna Venkatesh
 *
 *         
 */
public interface ReadabilityRepository {

  /**
   * Repository method to find the Readability Score by Id.
   * 
   * @param String Id
   * @return
   *      the Readability score
   */
  ReadabilityScore getScoreById(String id);

  /**
   * Save Readability Score.
   * 
   * @param ReadabilityScore 
   *            - score
   */
  void saveReadabilityScore(ReadabilityScore score);

  
}
