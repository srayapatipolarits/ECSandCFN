package com.sp.web.repository.competency;

import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The repository interface for competencies.
 */
public interface CompetencyRepository {
  
  /**
   * Get all the competency profiles configured in the system.
   * 
   * @return - the list of competency profiles configured in the system
   */
  List<CompetencyProfile> getAll();
  
  /**
   * Find competency profile by id.
   * 
   * @param competencyId
   *          - competency profile id
   * @return the competency profile
   */
  CompetencyProfile findById(String competencyId);
  
  /**
   * Generic update method.
   * 
   * @param objectToUpdate
   *          - object to update
   */
  <T> void update(T objectToUpdate);
  
  /**
   * Delete the given competency profile.
   * 
   * @param objectToDelete
   *          - object to delete
   */
  <T> void delete(T objectToDelete);
  
  /**
   * The list of competency profile for the company.
   * 
   * @param companyId
   *          - company id
   * @return the list of competency profiles for the company
   */
  List<CompetencyProfile> getCompanyCompetencyProfiles(String companyId);
  
  /**
   * Get the competency evaluation for the given competency evaluation id.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @return the competency evaluation
   */
  CompetencyEvaluation getCompetencyEvaluation(String competencyEvaluationId);
  
  /**
   * Remove all the competency evaluation requests.
   * 
   * @param companyId
   *          - company id
   */
  void removeAllEvaluationRequests(String companyId);
  
  /**
   * Get the competency evaluation request for the given feedback user id.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the competency evaluation request
   */
  CompetencyEvaluationRequest getEvaluationRequest(String feedbackUserId);
  
  /**
   * Method to get the competency evaluation details.
   * 
   * @param evaluationDetailsId
   *          - evaluation details id
   * @return the competency evaluation details
   */
  UserCompetencyEvaluationDetails getCompetencyEvaluationDetailsById(String evaluationDetailsId);
  
  /**
   * Get the currently running competency evaluations.
   * 
   * @return the list of current competency evaluations
   */
  List<CompetencyEvaluation> getCurrentCompetencyEvaluations();
  
  /**
   * Return the competency evaluation of a compay which are completed.
   * 
   * @param companyId of the company. 
   **/
  
  List<CompetencyEvaluation> getAllCompletedCompetancyEvaluations(String companyId);

  /**
   * Get the list of user competency evaluation details.
   * 
   * @param userId
   *          - user id
   * @return
   *    the list of user competency evaluation details
   */
  List<UserCompetencyEvaluationDetails> getAllUserCompetencyEvaluationDetails(String userId);

  /**
   * Get the user's competency evaluation object.
   * 
   * @param userId
   *           -user id
   * @return
   *      the competency evaluation of the user
   */
  UserCompetency getUserCompetencyEvaluation(String userId);

  /**
   * Create a new user competency object.
   * 
   * @param userCompetency
   *          - user competency to create
   */
  void save(UserCompetency userCompetency);

  /**
   * Gets all the spectrum competency profile evaluation results for the given company.
   * 
   * @param companyId
   *          - company id
   * @return
   *    the list of spectrum competency profile evaluation results.
   */
  List<SpectrumCompetencyProfileEvaluationResults> getAllSpectrumCompetencyProfileEvaluationResults(
      String companyId);

  /**
   * Get the spectrum competency profile evaluation results for the given id.
   * 
   * @param competencyProfileId
   *            - the id for the spectrum competency profile evaluation
   * @return
   *    the spectrum competency profile evaluation
   */
  SpectrumCompetencyProfileEvaluationResults findSpectrumCompetencyProfileEvaluationResult(
      String competencyProfileId);

  /**
   * Delete all the user competency evaluation details for the given user id.
   *  
   * @param userId
   *          - user id
   * @return
   *    the number of records updated 
   */
  int deleteAllUserCompetencyEvaluationDetails(String userId);

  /**
   * Get all the user competencies for the given company.
   * 
   * @param companyId
   *          - company id
   * @return
   *    the list of user competencies
   */
  List<UserCompetency> getAllUserCompetencies(String companyId);

}
