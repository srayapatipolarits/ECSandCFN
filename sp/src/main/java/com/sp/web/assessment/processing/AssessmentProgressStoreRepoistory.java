package com.sp.web.assessment.processing;

import com.sp.web.model.assessment.AssessmentProgressTracker;
import com.sp.web.model.assessment.PrismAssessment;

/**
 * @author Dax Abraham
 * 
 *         The repository class to store and retrieve the assessment progress.
 */
public interface AssessmentProgressStoreRepoistory {

  /**
   * Get the progress.
   * 
   * @param userId
   *          - the user id
   * @return the assessment progress if in database
   */
  AssessmentProgressTracker getAssessmentTracker(String userId);

  /**
   * Add the progress.
   * 
   * @param store
   *          - the progress store to add to the database
   * @return - the reference to the repository
   */
  AssessmentProgressTracker add(AssessmentProgressTracker store);

  /**
   * Update the progress.
   * 
   * @param store
   *          - store to update in DB
   * @return the updated store
   */
  AssessmentProgressTracker update(AssessmentProgressTracker store);

  /**
   * Add/Update the prism assessment.
   * 
   * @param assessment
   *            - prism assessment
   */
  void update(PrismAssessment assessment);
  
  /**
   * Remove the assessment progress.
   * 
   * @param assessmentFromStore
   *          - remove the assessment progress
   */
  void remove(AssessmentProgressTracker assessmentFromStore);

  /**
   * Get the prism assessment for the given assessment id.
   * 
   * @param assessmentId
   *            - assessment id
   * @return
   *      the prism assessment
   */
  PrismAssessment getPrismAssessment(String assessmentId);


}
