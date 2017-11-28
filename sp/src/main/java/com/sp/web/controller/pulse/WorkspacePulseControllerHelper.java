package com.sp.web.controller.pulse;

import com.sp.web.Constants;
import com.sp.web.dto.pulse.PulseQuestionSetListDTO;
import com.sp.web.dto.pulse.PulseResultsDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.PulseAssessmentForm;
import com.sp.web.model.User;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.team.GroupRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The helper class for workspace controller helper.
 */
@Component
public class WorkspacePulseControllerHelper {

  @Autowired
  WorkspacePulseFactory pulseFactory;
  
  @Autowired
  WorkspacePulseRepository pulseRepository;
  
  @Autowired
  GroupRepository groupRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * Helper method to start the pulse.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *    the response to the start request
   */
  public SPResponse startPulse(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the question set id
    String pulseQuestionSetId = (String) params[2];
    
    if (pulseQuestionSetId == null || pulseQuestionSetId.trim().isEmpty()) {
      pulseQuestionSetId = pulseFactory.getDefaultPulseQuestionSet().getId();
    }

    // check if there is an existing pulse for the company
    PulseRequest pulseRequest = pulseRepository.findPulseRequest(pulseQuestionSetId,
        user.getCompanyId());
    
    if (pulseRequest != null) {
      throw new InvalidRequestException("Already executing a pulse for :" + pulseQuestionSetId);
    }
    
    // get start date and end date
    LocalDate startDate = (LocalDate) params[0];
    LocalDate endDate = (LocalDate) params[1];
    
    pulseFactory.startPulse(startDate, endDate, user.getCompanyId(), pulseQuestionSetId);
    // Removed as the SSE for add todo is going to all the users 
    //companyFactory.updateUsersInSession(user.getCompanyId(), ActionType.PulseUpdate);
    
    return resp.isSuccess();
  }

  /**
   * Helper method to cancel an executing pulse.
   *  
   * @param user
   *          - logged in user
   * @param params
   *          - parameters
   * @return
   *     the response to the cancel request
   */
  public SPResponse cancelPulse(User user, Object[] params) {
    String pulseRequestId = (String) params[0];
    boolean saveResults = (boolean) params[1];
    
    pulseFactory.cancelPulse(pulseRequestId, saveResults);
    
//    userFactory.updateUserInSession(user.getId());
//    companyFactory.updateUsersInSession(user.getCompanyId(), ActionType.PulseUpdate);    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to send a reminder to all the users taking the pulse.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params         
   * @return
   *      the response to the send reminder request
   */
  public SPResponse sendReminder(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String pulseRequestId = (String) params[0];
        
    pulseFactory.sendReminder(pulseRequestId);
    return resp.isSuccess();
  }
  
  /**
   * Get the list of pulse question sets for the company.
   * 
   * @param user
   *          - list of users
   * @return
   *      the list of pulse question sets
   */
  public SPResponse getPulseQuestionSets(User user) {
    final SPResponse resp = new SPResponse();
    return resp.add(Constants.PARAM_PULSE_QUESTION,
        pulseFactory.getPulseQuestionSets(user.getCompanyId()));
  }
  
  /**
   * The helper method to get all the pulse requests for the given company.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - parameters
   * @return
   *    the response to the pulse requests
   */
  public SPResponse getPulseRequests(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the question set
    String pulseQuestionSetId = (String) params[0];
    
    List<PulseResults> pulseResults = null;
    
    // get the results for question set if not null and has text
    if (StringUtils.isNotBlank(pulseQuestionSetId)) {
      pulseResults = pulseRepository.getAllPulseResults(pulseQuestionSetId, user.getCompanyId());
    } else {
      pulseResults = pulseRepository.getAllPulseResults(user.getCompanyId());
    }

    List<PulseResultsDTO> pulseResultsDTOList = pulseResults.stream().map(PulseResultsDTO::new)
        .collect(Collectors.toList());
    
    // check if there are any current pulse executing for the company
    PulseRequest pulseRequest = pulseFactory.getPulseRequest(user.getCompanyId());
    if (pulseRequest != null) {
      // add the pulse request to the result list
      PulseResults pulseResult = pulseFactory.getPulseResult(pulseRequest);
      /* check if pulse is given by all the members, then automcatically save the result */
      PulseResultsDTO pulseResultDTO = new PulseResultsDTO(pulseResult);
      pulseResultDTO.setInProgress(true);
      if (pulseResult.getNumberOfMembers() == pulseResult.getNumberOfRespondents()) {
        pulseFactory.finishPulse(pulseRequest, true, pulseResult);
        pulseResultDTO.setInProgress(false);
      }
      pulseResultsDTOList.add(0, pulseResultDTO);
      
    } else {
      // get the first pulse result from the results and check if the results details is present
      // if not then add the same
      if (!pulseResults.isEmpty()) {
        PulseResults firstPulseResult = pulseResults.get(0);
        if (firstPulseResult.getPulseScore() == null) {
          firstPulseResult = pulseRepository.findPulseResultById(firstPulseResult.getId());
          pulseResultsDTOList.set(0, new PulseResultsDTO(firstPulseResult));
        }
      }
    }
    
    // add all the questions set id and their corresponding names
    Set<String> pulseQuestionSetIds = pulseResultsDTOList.stream()
        .map(PulseResultsDTO::getPulseQuestionSetId).collect(Collectors.toSet());
    List<PulseQuestionSetListDTO> collect = pulseQuestionSetIds.stream()
        .map(pulseFactory::getPulseQuestionSet).map(PulseQuestionSetListDTO::new)
        .collect(Collectors.toList());
    resp.add(Constants.PARAM_QUESTION, collect);
    
    // add the result to the response
    return resp.add(Constants.PARAM_PULSE_RESULTS, pulseResultsDTOList);
  }
  
  /**
   * Helper method to get the pulse result details.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *      the response to the get request 
   */
  public SPResponse getPulseResult(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the result id for which result details has to be sent
    final String pulseResultId = (String) params[0];
    // check if the group name provided in request
    final String groupName = (String) params[1];

    // get the pulse result
    PulseResults pulseResult = pulseRepository.findPulseResultById(pulseResultId);
    
    if (pulseResult == null) {
      // check if the request is a pulse request 
      PulseRequest pulseRequest = pulseRepository.findPulseRequestById(pulseResultId);
      if (pulseRequest == null) {
        throw new InvalidRequestException(
            "Invalid request id, no previous result by the given id :" + pulseResultId);
      }
      
      // get the pulse results for the request if the group name is not present
      // if not then the clone the request as  a result for 
      // the factory to process
      if (groupName == null || groupName.trim().isEmpty()) {
        pulseResult = pulseFactory.getPulseResult(pulseRequest);
      } else {
        pulseResult = new PulseResults(pulseRequest);
      }
      
    }
    
    if (groupName != null && !groupName.trim().isEmpty()) {
      // get the group members for the given group
      List<User> groupMembers = groupRepository.getMembers(user.getCompanyId(), groupName);
      // process the 
      pulseResult = pulseFactory.getPulseResult(pulseResult.getPulseRequestId(),
          pulseResult.getPulseQuestionSetId(), groupMembers);
    }
    
    // add the pulse score to the response
    return resp.add(Constants.PARAM_PULSE_RESULTS, new PulseResultsDTO(pulseResult));
  }
  
  /**
   * Helper method to get the pulse question.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *      the response to the get question request
   */
  public SPResponse getPulseQuestions(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the question set id
    String pulseQuestionSetId = (String) params[0];
    
    String pulseRequestId = (String) params[1];
    
    // getting the pulse question set id for the given pulse
    // request if it exists
    if (StringUtils.isNotBlank(pulseRequestId)) {
      PulseRequest pulseRequest = pulseRepository.findPulseRequestById(pulseRequestId);
      if (pulseRequest == null) {
        throw new InvalidRequestException("Pulse request not found for given id.");
      }
      //* check if user has already give the pulse or not */
      PulseAssessment pulseAssessment = pulseRepository.getPulseAssessment(pulseRequestId, user.getId());
      if (pulseAssessment != null) {
        throw new InvalidRequestException("Pulse Aready taken by the user.");
      }
      pulseQuestionSetId = pulseRequest.getPulseQuestionSetId();
    }
    PulseQuestionSet pulseQuestionSet = pulseFactory.getPulseQuestionSet(pulseQuestionSetId);
        
    // add the question set to the response
    return resp.add(Constants.PARAM_PULSE_QUESTION, pulseQuestionSet);
  }

  /**
   * Save the pulse assessment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *      the response to the save request
   */
  @SuppressWarnings({ "unchecked" })
  public SPResponse savePulseAssessment(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the pulse request id for which the assessment was taken
    String pulseRequestId = (String) params[0];
    // get the pulse response
    List<PulseAssessmentForm> pulseAssessment = (List<PulseAssessmentForm>) params[1];
    
    // process the pulse assessment
    pulseFactory.processAssessment(pulseRequestId, pulseAssessment, user);
    
    // send success
    return resp.isSuccess();
  }
  
}
