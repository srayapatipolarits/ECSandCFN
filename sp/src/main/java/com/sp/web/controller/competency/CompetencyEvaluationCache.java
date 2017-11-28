package com.sp.web.controller.competency;

import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.repository.competency.CompetencyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Dax Abraham
 * 
 *         The cache class for the competency evaluation.
 */
@Component
public class CompetencyEvaluationCache {
 
  @Autowired
  private CompetencyRepository competencyRepository;

  /**
   * Get the competency evaluation for the given competency evaluation id.
   * 
   * @param competencyEvaluationId
   *            - competency evaluation id
   * @return
   *    the competency evaluation 
   */
  @Cacheable("competencyEvaluation")
  public CompetencyEvaluation getCompetencyEvaluation(String competencyEvaluationId) {
    return competencyRepository.getCompetencyEvaluation(competencyEvaluationId);
  }

  /**
   * Method to update the competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation to update
   */
  @CacheEvict(value = "competencyEvaluation", key = "#competencyEvaluation.id")
  public void update(CompetencyEvaluation competencyEvaluation) {
    competencyRepository.update(competencyEvaluation);
  }
  
  /**
   * Delete the given competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation to delete
   */
  @CacheEvict(value = "competencyEvaluation", key = "#competencyEvaluation.id")
  public void deleteCompetencyEvaluation(CompetencyEvaluation competencyEvaluation) {
    competencyRepository.delete(competencyEvaluation);
  }
  
}
