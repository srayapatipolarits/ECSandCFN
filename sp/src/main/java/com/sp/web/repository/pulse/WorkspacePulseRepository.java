package com.sp.web.repository.pulse;

import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The workspace pulse repository class.
 */
public interface WorkspacePulseRepository {

  /**
   * Repository method to find the pulse question set for the given name.
   * 
   * @param pulseQuestionSetName
   *            - pulse question set name
   * @return
   *      the pulse question set for the given name
   */
  PulseQuestionSet findPulseQuestionSetByName(String pulseQuestionSetName);

  /**
   * Get the pulse question set by id.
   * 
   * @param pulseQuestionSetId
   *          - question set id
   * @return
   *    the pulse question set
   */
  PulseQuestionSet findPulseQuestionSetById(String pulseQuestionSetId);
  
  /**
   * Find the pulse request for the given question set and company.
   * 
   * @param pulseQuestionSetId
   *            - pulse question id
   * @param companyId
   *            - company id
   * @return
   *      the pulse request
   */
  PulseRequest findPulseRequest(String pulseQuestionSetId, String companyId);
  
  /**
   * Get all the pulse requests for the given company.
   * 
   * @param companyId
   *          - company Id
   * @return
   *    the pulse request for the current company.
   */
  PulseRequest findPulseRequest(String companyId);

  /**
   * Creating a new pulse request.
   * 
   * @param pulseRequest
   *          - the pulse request
   * @return
   *      the newly created pulse request
   */
  PulseRequest createPulseRequest(PulseRequest pulseRequest);

  /**
   * Gets the list of all the pulse results for the given pulse.
   * 
   * @param pulseQuestionSetId
   *            - pulse question set
   * @param companyId 
   *            - company id
   * @return
   *      the list of pulse results
   */
  List<PulseResults> getAllPulseResults(String pulseQuestionSetId, String companyId);

  /**
   * Gets all the pulse results for the given company.
   * 
   * @param companyId
   *          - company 
   * @return
   *      the pulse results
   */
  List<PulseResults> getAllPulseResults(String companyId);

  /**
   * Gets the pulse result for the given pulse id.
   * 
   * @param pulseResultId
   *            - pulse result id
   * @return
   *      the pulse result
   */
  PulseResults findPulseResultById(String pulseResultId);

  /**
   * Get all the assessments for the given members, for the pulse request.
   * 
   * @param pulseRequestId
   *            - pulse request
   * @param memberList
   *            - member list
   * @return
   *      the member assessments
   */
  List<PulseAssessment> getAllPulseAssessments(String pulseRequestId, List<String> memberList);

  /**
   * Get all the pulse assessments for the pulse request.
   * 
   * @param pulseRequestId
   *            - the pulse requests
   * @return
   *      the pulse assessments
   */
  List<PulseAssessment> getAllPulseAssessments(String pulseRequestId);
  
  /**
   * The pulse question set to create or update.
   * 
   * @param pulseQuestionSet
   *            - pulse question set
   */
  void savePulseQuestionSet(PulseQuestionSet pulseQuestionSet);

  /**
   * Find the pulse request for the given request id.
   * 
   * @param pulseRequestId
   *            - request id
   * @return
   *        the pulse request
   */
  PulseRequest findPulseRequestById(String pulseRequestId);

  /**
   * Store the pulse assessment.
   * 
   * @param pulseAssessment
   *            - pulse assessment
   */
  void savePulseAssessment(PulseAssessment pulseAssessment);

  /**
   * The pulse requests that are older than the given date.
   * 
   * @param endDate
   *          - end date
   * @return
   *      the list of pulse requests
   */
  List<PulseRequest> findPulseRequestsByEndDate(LocalDate endDate);

  /**
   * Save the pulse result.
   * 
   * @param pulseResult
   *            - the pulse result 
   */
  void savePulseResult(PulseResults pulseResult);

  /**
   * Remove the given pulse request.
   * 
   * @param pulseRequest
   *            - pulse request to remove
   */
  void removePulseRequest(PulseRequest pulseRequest);

  /**
   * Get the pulse assessment for the given request and user id.
   * 
   * @param pulseRequestId
   *            - pulse request id
   * @param memberId
   *            - member id
   * @return
   *      the pulse assessment
   */
  PulseAssessment getPulseAssessment(String pulseRequestId, String memberId);

  /**
   * Get the list of pulse questions for the given company.
   * 
   * @param companyId 
   *          - company id
   * @return
   *      the list of pulse question set
   */
  List<PulseQuestionSet> findPulseQuestionSetsFor(String companyId);

  /**
   * Get the list of pulse questions.
   * 
   * @return
   *      the list of pulse questions
   */
  List<PulseQuestionSet> getAllPulseQuestionSets();

  /**
   * Get all the pulse requests in the system.
   * 
   * @return
   *      list of all pulse requests
   */
  List<PulseRequest> findAllPulseRequests();

  /**
   * Delete the given pulse result.
   * 
   * @param pulseResult
   *            - the pulse result
   */
  void delete(PulseResults pulseResult);

  /**
   * Delete the pulse assessment.
   * 
   * @param pulseAssessment
   *            - pulse assessment to delete
   */
  void delete(PulseAssessment pulseAssessment);
  
  /**
   * Get all the pulse assessments for the given user id.
   * 
   * @param userId
   *            - user id
   * @return
   *    the pulse assessment list
   */
  List<PulseAssessment> getAllPulseAssessmentsForUser(String userId);
}
