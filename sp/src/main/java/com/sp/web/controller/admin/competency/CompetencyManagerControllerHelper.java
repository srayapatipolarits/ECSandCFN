package com.sp.web.controller.admin.competency;

import com.sp.web.Constants;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.UserGroupDTO;
import com.sp.web.dto.competency.BaseCompetencyEvaluationDTO;
import com.sp.web.dto.competency.BaseCompetencyEvaluationUserDTO;
import com.sp.web.dto.competency.BaseCompetencyProfileDTO;
import com.sp.web.dto.competency.CompetencyEvaluationDTO;
import com.sp.web.dto.competency.CompetencyEvaluationUserDTO;
import com.sp.web.dto.competency.CompetencyEvaluationViewByUserDTO;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.dto.competency.UserCompetencyEvaluationDTO;
import com.sp.web.dto.spectrum.competency.SpectrumCompetencyProfileEvaluationResultsListingDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.competency.CompetencyEvaluationForm;
import com.sp.web.model.Company;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyEvaluationSummary;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;
import com.sp.web.model.task.TaskType;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The helper class for the competency manager controller.
 */
@Component
public class CompetencyManagerControllerHelper {
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  CompetencyFactory competencyFactory;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  @Qualifier("noActivityNotificationProcessor")
  NotificationsProcessor noActivityNotificationProcessor;
  
  @Autowired
  SPTokenFactory tokenFactory;
  
  /**
   * Get the competency details for the competency manager.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * 
   * @return the competency evaluation manager details
   */
  public SPResponse get(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String companyId = user.getCompanyId();
    Company company = companyFactory.getCompany(companyId);
    
    if (company.isEvaluationInProgress()) {
      
      // get the current competency evaluation to send
      final String competencyEvaluationId = company.getCompetencyEvaluationId();
      Assert.hasText(competencyEvaluationId, "Competency evaluation not found.");
      resp.add(Constants.PARAM_COMPETENCY_EVALUATION,
          getCompetencyEvaluation(competencyEvaluationId));
    } else {
      
      String competencyEvaluationId = (String) params[0];
      if (StringUtils.isBlank(competencyEvaluationId)) {
        // check if a previous evaluation existed
        competencyEvaluationId = company.getLastCompetencyEvalutationId();
      }
      if (!StringUtils.isBlank(competencyEvaluationId)) {
        resp.add(Constants.PARAM_COMPETENCY_EVALUATION,
            getCompetencyEvaluation(competencyEvaluationId));
        resp.add(Constants.PARAM_COMPETENCY_EVALUATION_COUNT,
            company.getCompetencyEvaluationCount());
      } else {
        
        // no previous competency evaluations
        // adding the count of users to indicate that there exists users with competency profiles
        resp.add(Constants.PARAM_COMPETENCY_USER_COUNT,
            userFactory.getCompetencyUserCount(companyId));
      }
    }
    
    return resp;
  }
  
  /**
   * Helper method to activate the evaluation.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for request
   * @return the response to the activate evaluation request
   */
  public SPResponse activateEvaluation(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // check if there is an evaluation in progress
    CompanyDao company = checkIfEvaluationInProgress(user);
    
    // get the end date for the evaluation
    final CompetencyEvaluationForm form = (CompetencyEvaluationForm) params[0];
    form.validate();
    
    // creating a new competency evaluation
    final CompetencyEvaluation competencyEvaluation = form.createCompetencyEvaluation(company);
    competencyFactory.update(competencyEvaluation);
    
    // adding the user to the competency evaluation
    final List<User> userList = new ArrayList<User>();
    form.getUserIds().stream().map(userFactory::getUser)
        .filter(u -> u.getCompetencyProfileId() != null)
        .forEach(u -> addUserToCompetency(u, competencyEvaluation, userList));
    
    // check if any user was added
    if (userList.isEmpty()) {
      competencyFactory.deleteCompetencyEvaluation(competencyEvaluation);
      throw new InvalidRequestException("No valid user found to initiate competency evaluation.");
    }
    
    // saving the competency profiles and users added
    competencyFactory.update(competencyEvaluation);
    
    // update the companies competency evaluation object
    // set the flag for the start of evaluation in the company
    company.setEvaluationInProgress(true);
    company.setCompetencyEvaluationId(competencyEvaluation.getId());
    companyFactory.updateCompanyDao(company);
    
    notifyStartEvaluation(company, competencyEvaluation, userList);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to add a new set of users to the evaluation.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for request
   * @return the response to the activate evaluation request
   */
  @SuppressWarnings("unchecked")
  public SPResponse addUserToEvaluation(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    CompanyDao company = validateEvaluationInProgress(user);
    
    List<String> userIdsToAdd = (List<String>) params[0];
    Assert.notEmpty(userIdsToAdd, "User id's requried.");
    
    // get the competency evaluation
    CompetencyEvaluation competencyEvaluation = competencyFactory.getCompetencyEvaluation(company
        .getCompetencyEvaluationId());
    
    // adding the user to the competency evaluation
    final List<User> userList = new ArrayList<User>();
    userIdsToAdd.stream().filter(uid -> !competencyEvaluation.hasUser(uid))
        .map(userFactory::getUser).filter(u -> u != null && u.getCompetencyProfileId() != null)
        .forEach(u -> addUserToCompetency(u, competencyEvaluation, userList));
    
    // check if any user was added
    Assert.notEmpty(userList, "No valid user found to add to competency evaluation.");
    
    // saving the competency profiles and users added
    competencyFactory.update(competencyEvaluation);
    
    // update the user's about evaluation
    // and send the notification
    notifyStartEvaluation(company, competencyEvaluation, userList);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to remove a user from the evaluation.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for request
   * @return the response to the activate evaluation request
   */
  public SPResponse removeUserFromEvaluation(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // check if there is an evaluation in progress
    CompanyDao company = validateEvaluationInProgress(user);
    
    // get the user to remove
    String userId = (String) params[0];
    Assert.hasText(userId, "User id requried.");
    
    // get the competency evaluation
    CompetencyEvaluation competencyEvaluation = competencyFactory.getCompetencyEvaluation(company
        .getCompetencyEvaluationId());
    
    // removing the user from the competency evaluation also validate if the user is the last
    // user in the evaluation
    Assert.isTrue(competencyEvaluation.getUserIds().size() > 1,
        "Cannot remove user at least one user required in evaluation.");
    
    User userToRemove = userFactory.getUser(userId);
    if (userToRemove != null) {
      competencyFactory.removeUserFromEvaluation(userToRemove, competencyEvaluation);
      userFactory.updateUserAndSession(userToRemove);
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to end the current evaluation.
   * 
   * @param user
   *          - logged in user
   * @return the response to the end evaluation request
   */
  public SPResponse endEvaluation(User user) {
    final SPResponse resp = new SPResponse();
    
    // check if the evaluation is in progress
    CompanyDao company = validateEvaluationInProgress(user);
    
    // get the competency evaluation
    CompetencyEvaluation competencyEvaluation = competencyFactory.getCompetencyEvaluation(company
        .getCompetencyEvaluationId());
    Assert.notNull(competencyEvaluation, "Competency evaluation not found.");
    
    competencyFactory.endEvaluation(competencyEvaluation);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to the get the users with competency evaluation.
   * 
   * @param user
   *          - logged in user
   * @return the list of valid users along with competency profile and group information
   */
  public SPResponse getUsers(User user) {
    final SPResponse resp = new SPResponse();
    
    // check if the evaluation is in progress
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    
    // get the competency evaluation
    List<User> users = userFactory.getAllUsersWithCompetencyProfile(company.getId());
    
    if (!users.isEmpty()) {
      final Map<String, BaseCompetencyProfileDTO> profileMap = new HashMap<String, BaseCompetencyProfileDTO>();
      final Map<String, UserGroupDTO> userGroupMap = new HashMap<String, UserGroupDTO>();
      resp.add(
          Constants.PARAM_USER,
          users.stream().map(u -> getCompetencyUserDTO(u, profileMap, userGroupMap))
              .filter(Objects::nonNull).collect(Collectors.toList()));
      resp.add(Constants.PARAM_COMPETENCY_PROFILE, profileMap.values());
      resp.add(Constants.PARAM_GROUPS, userGroupMap.values());
    } else {
      resp.add(Constants.PARAM_USER, users);
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get the list of previous evaluations.
   * 
   * @param user
   *          - logged in user
   * @return the list of previous evaluations
   */
  public SPResponse getEvaluationList(User user) {
    final SPResponse resp = new SPResponse();
    
    List<CompetencyEvaluation> competencyEvaluations = competencyFactory
        .getCompletedCompetencyEvaluation(user.getCompanyId());
    
    return resp.add(
        Constants.PARAM_COMPETENCY_EVALUATION,
        competencyEvaluations.stream().map(BaseCompetencyEvaluationDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get the list of competency profiles to display in view by filter.
   * 
   * @param user
   *          - logged in user
   * @return the list of competency profiles
   */
  public SPResponse getViewByCompetencyProfiles(User user) {
    final SPResponse resp = new SPResponse();
    
    List<SpectrumCompetencyProfileEvaluationResults> allCompetencyProfileEvaluationResults = competencyFactory
        .getAllSpectrumCompetencyProfileEvaluationResults(user.getCompanyId());
    
    return resp.add(
        Constants.PARAM_COMPETENCY_EVALUATION,
        allCompetencyProfileEvaluationResults.stream()
            .filter(cper -> cper.hasEvaluations())
            .map(SpectrumCompetencyProfileEvaluationResultsListingDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get the details for the given competency profile.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get details request
   */
  public SPResponse getCompetencyProfileDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String competencyProfileId = (String) params[0];
    
    // get the competency profile evaluation results for the given
    // competency profile id
    SpectrumCompetencyProfileEvaluationResults evaluationResult = getCompetencyProfileEvaluationRestuls(
        user, competencyProfileId);
    
    return resp.add(Constants.PARAM_COMPETENCY_EVALUATION, evaluationResult);
  }
  
  /**
   * Helper method to get the user details for the given competency profile and competency
   * evaluation.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get user details request
   */
  public SPResponse getCompetencyProfileEvaluationUsers(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String competencyProfileId = (String) params[0];
    String competencyEvaluationId = (String) params[1];
    
    // get the competency profile evaluation results for the given
    // competency profile id
    SpectrumCompetencyProfileEvaluationResults evaluationResults = getCompetencyProfileEvaluationRestuls(
        user, competencyProfileId);
    
    // get the evaluation results object
    Optional<SpectrumCompetencyEvaluationSummary> evaluationResult = evaluationResults
        .getEvaluationResult(competencyEvaluationId);
    Assert.isTrue(evaluationResult.isPresent(), "Competency evaluation not found.");
    
    return resp.add(
        Constants.PARAM_COMPETENCY_EVALUATION,
        getCompetencyEvaluationUsers(evaluationResult.get().getUserIds(), competencyEvaluationId,
            null));
  }
  
  /**
   * Helper method to get the list of members for the view by members filter.
   * 
   * @param user
   *          - logged in user
   * 
   * @return the list of valid members
   */
  public SPResponse getViewByMembers(User user) {
    final SPResponse resp = new SPResponse();
    
    // get all the user competencies
    List<UserCompetency> userCompetencies = competencyFactory.getAllUserCompetency(user
        .getCompanyId());
    
    // creating map to store the competency profile information of the users
    final Map<String, BaseCompetencyProfileDTO> competencyProfileMap = new HashMap<String, BaseCompetencyProfileDTO>();
    // creating the map to store the user group information
    final Map<String, UserGroupDTO> userGroupMap = new HashMap<String, UserGroupDTO>();
    
    // iterate over all the users and add to response as well as add competency profile
    // and group information
    resp.add(
        Constants.PARAM_USER,
        userCompetencies.stream().map(UserCompetency::getUserId).map(userFactory::getUser)
            .filter(Objects::nonNull)
            .map(u -> getCompetencyUserDTO(u, competencyProfileMap, userGroupMap))
            .filter(Objects::nonNull).collect(Collectors.toList()));
    resp.add(Constants.PARAM_COMPETENCY_PROFILE, competencyProfileMap.values());
    resp.add(Constants.PARAM_GROUPS, userGroupMap.values());
    return resp;
  }
  
  /**
   * Helper method to get the last competency evaluation details for the user id's passed.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the list of user competency evaluations
   */
  @SuppressWarnings("unchecked")
  public SPResponse getUserCompetencies(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    List<String> userIds = (List<String>) params[0];
    Assert.notEmpty(userIds, "User id required.");
    
    final Map<String, BaseCompetencyEvaluationDTO> evaluationMap = new HashMap<String, BaseCompetencyEvaluationDTO>();
    final Map<String, CompetencyProfileSummaryDTO> profileMap = new HashMap<String, CompetencyProfileSummaryDTO>();
    resp.add(
        Constants.PARAM_COMPETENCY_EVALUATION_USERS,
        userIds.stream().map(userFactory::getUser).filter(Objects::nonNull)
            .map(u -> getLastCompetencyEvalutionUser(u, profileMap, evaluationMap))
            .filter(Objects::nonNull).collect(Collectors.toList()));
    
    resp.add(Constants.PARAM_COMPETENCY_EVALUATION, evaluationMap.values());
    return resp.add(Constants.PARAM_COMPETENCY_PROFILE, profileMap);
  }
  
  /**
   * Helper method to get all the past competency evaluations for a user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getUserCompetencyEvaluations(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userId = (String) params[0];
    Assert.hasText(userId, "User id required.");
    
    User userToGetCompetencyEvaluationsFor = userFactory.getUser(userId);
    Assert.notNull(userToGetCompetencyEvaluationsFor, "User not found.");
    user.isSameCompany(userToGetCompetencyEvaluationsFor);
    
    UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
    
    final List<UserCompetencyEvaluationDTO> competencyEvaluations = new ArrayList<UserCompetencyEvaluationDTO>();
    userCompetency.getEvaluationsMap().forEach(
        (competencyEvaluationId, userCompetencyEvaluation) -> addUserCompetencyEvaluationDTO(
            competencyEvaluationId, userCompetencyEvaluation, competencyEvaluations));
    return resp.add(Constants.PARAM_COMPETENCY_EVALUATION, competencyEvaluations);
  }
  
  /**
   * Helper method to get all the peer requests for the given user along with the evaluations if
   * completed.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  @SuppressWarnings("unchecked")
  public SPResponse getCompetencyEvaluationPeerRequests(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String competencyEvaluationId = (String) params[0];
    Assert.hasText(competencyEvaluationId, "Competency evaluation id required.");
    
    String userId = (String) params[1];
    Assert.hasText(userId, "User id required.");
    
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(competencyEvaluationId);
    Assert.notNull(competencyEvaluation, "Competency evaluation not found.");
    Assert.isTrue(competencyEvaluation.isUserPartOfEvaluation(userId),
        "User not part of evaluation.");
    
    User competencyUser = userFactory.getUser(userId);
    Assert.notNull(competencyUser, "User not found.");
    user.isSameCompany(competencyUser);
    
    UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
    UserCompetencyEvaluation userEvaluation = userCompetency.getEvaluation(competencyEvaluationId);
    Assert.notNull(userEvaluation, "User evaluation not found.");
    
    CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
        .getCompetencyProfile(userEvaluation.getCompetencyProfileId());
    Assert.notNull(competencyProfile, "Competency profile not found in evaluation.");
    
    resp.add(Constants.PARAM_COMPETENCY_PROFILE, competencyProfile);
    return resp.add(Constants.PARAM_COMPETENCY_EVALUATION,
        Optional.ofNullable(userEvaluation.getPeers()).map(PeerCompetencyEvaluationScore::getPeers)
            .orElse(Collections.EMPTY_LIST));
  }
  
  /**
   * Get the list of competency users for the manager.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  @SuppressWarnings("unchecked")
  public SPResponse getManagerResult(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String tokenId = (String) params[0];
    
    Token token = tokenFactory.findTokenById(tokenId);
    Assert.notNull(token, "Cannot process request.");
    
    String userId = token.getParamAsString(Constants.PARAM_USER);
    Assert.isTrue(user.getId().equals(userId), "Unauthorized request.");
    
    String competencyEvaluationId = token.getParamAsString(Constants.PARAM_COMPETENCY_EVALUATION);
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(competencyEvaluationId);
    Assert.notNull(competencyEvaluation, "Competency evaluation not found.");
    
    Set<String> uids = (Set<String>) new HashSet<String>(
        (List<String>) token.getParam(Constants.PARAM_USER_ID));
    
    resp.add(Constants.PARAM_COMPETENCY_EVALUATION, new BaseCompetencyEvaluationDTO(
        competencyEvaluation));
    resp.add(Constants.PARAM_COMPETENCY_PROFILE, competencyEvaluation.getCompetencyProfileMap());
    return resp.add(Constants.PARAM_COMPETENCY_EVALUATION_USERS,
        getCompetencyEvaluationUsers(uids, competencyEvaluationId, null));
  }
  
  /**
   * Add a competency evaluation DTO for the given competency evaluation and user results.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param userCompetencyEvaluation
   *          - user competency evaluation
   * @param competencyEvaluations
   *          - the evaluations list to update
   */
  private void addUserCompetencyEvaluationDTO(String competencyEvaluationId,
      UserCompetencyEvaluation userCompetencyEvaluation,
      List<UserCompetencyEvaluationDTO> competencyEvaluations) {
    
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(competencyEvaluationId);
    
    if (competencyEvaluation != null && competencyEvaluation.isCompleted()) {
      CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
          .getCompetencyProfile(userCompetencyEvaluation.getCompetencyProfileId());
      if (competencyProfile != null) {
        competencyEvaluations.add(new UserCompetencyEvaluationDTO(competencyEvaluation,
            competencyProfile, userCompetencyEvaluation));
      }
    }
  }
  
  /**
   * Get the competency evaluation user DTO object along with the competency profile.
   * 
   * @param user
   *          - user
   * @param competencyProfileMap
   *          - competency profile map
   * @param competencyEvaluationMap
   *          - competency evaluation map
   * @return the competency profile
   */
  private CompetencyEvaluationViewByUserDTO getLastCompetencyEvalutionUser(User user,
      Map<String, CompetencyProfileSummaryDTO> competencyProfileMap,
      Map<String, BaseCompetencyEvaluationDTO> competencyEvaluationMap) {
    
    CompetencyEvaluationViewByUserDTO userDTO = null;
    
    // get the user competency and then the last competency evaluation for the user
    UserCompetency userCompetency = competencyFactory.getUserCompetency(user.getId());
    String competencyEvaluationId = userCompetency.getLastCompetencyEvaluationId();
    UserCompetencyEvaluation userEvaluation = userCompetency.getEvaluation(competencyEvaluationId);
    
    if (userEvaluation != null) {
      // getting the evaluation
      CompetencyEvaluation competencyEvaluation = competencyFactory
          .getCompetencyEvaluation(competencyEvaluationId);
      if (competencyEvaluation != null && competencyEvaluation.isCompleted()) {
        // get the competency profile
        CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
            .getCompetencyProfile(userEvaluation.getCompetencyProfileId());
        // Creating the user DTO object
        userDTO = new CompetencyEvaluationViewByUserDTO(user, userEvaluation,
            competencyEvaluationId);
        competencyProfileMap.computeIfAbsent(competencyEvaluationId + competencyProfile.getId(),
            k -> competencyProfile);
        competencyEvaluationMap.computeIfAbsent(competencyEvaluationId,
            k -> new BaseCompetencyEvaluationDTO(competencyEvaluation));
      }
    }
    return userDTO;
  }
  
  /**
   * Get the spectrum competency profile evaluation results for the given competency profile id.
   * 
   * @param user
   *          - user
   * @param competencyProfileId
   *          - competency profile id
   * @return the spectrum competency profile evaluation results
   */
  private SpectrumCompetencyProfileEvaluationResults getCompetencyProfileEvaluationRestuls(
      User user, String competencyProfileId) {
    SpectrumCompetencyProfileEvaluationResults evaluationResult = competencyFactory
        .getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
    Assert.notNull(evaluationResult, "Competency profile not found.");
    Assert.isTrue(user.getCompanyId().equals(evaluationResult.getCompanyId()),
        "Unauthorised request.");
    return evaluationResult;
  }
  
  /**
   * Validate if an evaluation is currently in progress for the given users company.
   * 
   * @param user
   *          - user
   * @return the company object
   */
  private CompanyDao validateEvaluationInProgress(User user) {
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    Assert.isTrue(company.isEvaluationInProgress(), "Evalaution not in progress.");
    return company;
  }
  
  /**
   * Notify the users.
   * 
   * @param company
   *          - company
   * @param competencyEvaluation
   *          - competency evaluation
   * @param userList
   *          - user list
   */
  protected void notifyStartEvaluation(CompanyDao company,
      final CompetencyEvaluation competencyEvaluation, final List<User> userList) {
    // update the user's about evaluation
    // and send the notification
    final Map<String, Object> paramsMap = new HashMap<String, Object>();
    final String endDate = MessagesHelper.formatDate(competencyEvaluation.getEndDate(),
        "competency");
    paramsMap.put(Constants.PARAM_END_DATE, endDate);
    paramsMap.put(Constants.PARAM_COMPETENCY_EVALUATION_MANAGER,
        competencyEvaluation.isSupported(EvaluationType.Manager));
    final TodoRequest todoRequest = getTodoRequest(competencyEvaluation);
    final String companyName = company.getName();
    userList.forEach(u -> notifyStartEvaluation(paramsMap, u, todoRequest, companyName));
  }
  
  /**
   * Notify the user about the start of the evaluation.
   * 
   * @param paramsMap
   *          - params map
   * @param user
   *          - user
   * @param todoRequest
   *          - todo request
   * @param companyName
   *          - company name
   */
  private void notifyStartEvaluation(Map<String, Object> paramsMap, User user,
      TodoRequest todoRequest, String companyName) {
    // add the task to the user
    todoFactory.addTodo(user, todoRequest);
    
    HashMap<String, Object> clone = new HashMap<String, Object>(paramsMap);
    clone.put(
        Constants.PARAM_SUBJECT,
        MessagesHelper.getMessage("notification.subject.CompetencyEvalautionActivated",
            user.getLocale(), companyName));
    userFactory.updateUserAndSession(user);
    // sending the notification
    noActivityNotificationProcessor.process(NotificationType.CompetencyEvalautionActivated, user,
        user, paramsMap, false);
  }
  
  /**
   * Get the user task for the given competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   * @return the user task
   */
  private TodoRequest getTodoRequest(CompetencyEvaluation competencyEvaluation) {
    List<EvaluationType> requiredEvaluationList = competencyEvaluation.getRequiredEvaluationList();
    TodoRequest request = null;
    // checking for if only self evaluation
    if (requiredEvaluationList.size() == 1 && requiredEvaluationList.contains(EvaluationType.Self)) {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationSelf);
    } else if (requiredEvaluationList.size() == 1
        && requiredEvaluationList.contains(EvaluationType.Peer)) {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationInitiatePeer);
    } else if (requiredEvaluationList.size() == 1
        && requiredEvaluationList.contains(EvaluationType.Manager)) {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationInitiateManager);
    } else if (requiredEvaluationList.size() == 2
        && requiredEvaluationList.contains(EvaluationType.Self)
        && requiredEvaluationList.contains(EvaluationType.Peer)) {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationInitiateSelfPeer);
    } else if (requiredEvaluationList.size() == 2
        && requiredEvaluationList.contains(EvaluationType.Peer)
        && requiredEvaluationList.contains(EvaluationType.Manager)) {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationInitiateManagerPeer);
    } else if (requiredEvaluationList.size() == 2
        && requiredEvaluationList.contains(EvaluationType.Manager)
        && requiredEvaluationList.contains(EvaluationType.Self)) {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationInitiateManagerSelf);
    } else {
      request = TodoRequest.newCompetencyEvaluationRequest(competencyEvaluation,
          TaskType.CompetencyEvaluationInitiate);
    }
    return request;
  }
  
  /**
   * Add the given user to the competency evaluation and also add successful users to the user list.
   * 
   * @param user
   *          - user
   * @param competencyEvaluation
   *          - competency evaluation
   * @param userList
   *          - user list to update
   */
  private void addUserToCompetency(User user, CompetencyEvaluation competencyEvaluation,
      List<User> userList) {
    if (addUserToCompetency(user, competencyEvaluation)) {
      userList.add(user);
    }
  }
  
  /**
   * Add the given user to the competency evaluation send flag true if added else false.
   * 
   * @param user
   *          - user
   * @param competencyEvaluation
   *          - competency evaluation
   * @return flag true if added else false
   */
  private boolean addUserToCompetency(User user, CompetencyEvaluation competencyEvaluation) {
    
    // removing all the users who have not completed
    // evaluation
    if (user.getUserStatus() != UserStatus.VALID) {
      return false;
    }
    
    // get the competency profile if it exists then add it to the evaluation as
    // well as add it for the user
    CompetencyProfileDao competencyProfile = competencyFactory.getCompetencyProfile(user
        .getCompetencyProfileId());
    if (competencyProfile != null) {
      competencyEvaluation.addCompetencyProfile(competencyProfile);
      competencyFactory.addUserToCompetencyEvaluation(user, competencyEvaluation);
      return true;
    }
    return false;
  }
  
  /**
   * Check if there is an evaluation in progress.
   * 
   * @param user
   *          - user for company id
   * @return the reference to the company object
   */
  private CompanyDao checkIfEvaluationInProgress(User user) {
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    Assert.isTrue(company.isEvaluationInProgress() == false,
        MessagesHelper.getMessage("competency.admin.dashboard.evaluationInProgress"));
    return company;
  }
  
  /**
   * Create a DTO for the given competency evaluation id.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @return the competency evaluation DTO
   */
  private CompetencyEvaluationDTO getCompetencyEvaluation(String competencyEvaluationId) {
    
    // getting the competency evaluation
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(competencyEvaluationId);
    Assert.notNull(competencyEvaluation, "Competency evaluation not found.");
    
    // creating the base DTO
    final CompetencyEvaluationDTO evaluationDTO = new CompetencyEvaluationDTO(competencyEvaluation);
    
    // adding the user information
    evaluationDTO.setUsers(getCompetencyEvaluationUsers(competencyEvaluation.getUserIds(),
        competencyEvaluationId, evaluationDTO.getUserGroupMap()));
    
    return evaluationDTO;
  }
  
  private List<CompetencyEvaluationUserDTO> getCompetencyEvaluationUsers(Set<String> userIds,
      String competencyEvaluationId, Map<String, UserGroupDTO> userGroupMap) {
    return userIds.stream()
        .map(uid -> getCompetencyUserDTO(uid, competencyEvaluationId, userGroupMap))
        .filter(Objects::nonNull).collect(Collectors.toList());
  }
  
  /**
   * Method to get the competency evaluation for a given user.
   * 
   * @param uid
   *          - user id
   * @param evaluationDTO
   *          - evaluation DTO
   * @return the competency evaluation user DTO
   */
  private CompetencyEvaluationUserDTO getCompetencyUserDTO(String uid,
      String competencyEvaluationId, Map<String, UserGroupDTO> userGroupMap) {
    
    // get the user if not found return null
    User user = userFactory.getUser(uid);
    if (user == null) {
      return null;
    }
    
    // get the user's competency for the given evaluation if not found return null
    UserCompetency userCompetency = competencyFactory.getUserCompetency(uid);
    UserCompetencyEvaluation userEvaluation = userCompetency.getEvaluation(competencyEvaluationId);
    if (userEvaluation == null) {
      return null;
    }
    
    // creating a new user competency evaluation DTO
    CompetencyEvaluationUserDTO userEvaluationDTO = new CompetencyEvaluationUserDTO(user,
        userEvaluation);
    
    // adding the users group associations
    if (userGroupMap != null) {
      Optional.ofNullable(user.getGroupAssociationList()).ifPresent(
          gaList -> gaList.forEach(ga -> addGroupAssociation(ga, userEvaluationDTO, userGroupMap)));
    }
    
    return userEvaluationDTO;
  }
  
  /**
   * Create a new base competency user.
   * 
   * @param user
   *          - user
   * @param competencyProfileMap
   *          - competency profile map
   * @param userGroupMap
   *          - user group map
   * @return the base user DTO
   */
  private BaseCompetencyEvaluationUserDTO getCompetencyUserDTO(User user,
      Map<String, BaseCompetencyProfileDTO> competencyProfileMap,
      Map<String, UserGroupDTO> userGroupMap) {
    if (user.getUserStatus() == UserStatus.VALID && user.getCompetencyProfileId() != null) {
      BaseCompetencyEvaluationUserDTO userDTO = new BaseCompetencyEvaluationUserDTO(user);
      final String competencyProfileId = userDTO.getCompetencyProfileId();
      if (!competencyProfileMap.containsKey(competencyProfileId)) {
        CompetencyProfileDao competencyProfile = competencyFactory
            .getCompetencyProfile(competencyProfileId);
        if (competencyProfile != null) {
          competencyProfileMap.put(competencyProfileId, new BaseCompetencyProfileDTO(
              competencyProfile));
        } else {
          return null;
        }
      }
      
      Optional.ofNullable(user.getGroupAssociationList()).ifPresent(
          gaList -> gaList.forEach(ga -> addGroupAssociation(ga, userDTO, userGroupMap)));
      return userDTO;
    }
    return null;
  }
  
  /**
   * Add the group association to the user.
   * 
   * @param ga
   *          - group association
   * @param userDTO
   *          - user dto
   * @param userGroupMap
   *          - user group map
   */
  private void addGroupAssociation(GroupAssociation ga, BaseCompetencyEvaluationUserDTO userDTO,
      Map<String, UserGroupDTO> userGroupMap) {
    userGroupMap.computeIfAbsent(ga.getGroupId(), k -> new UserGroupDTO(ga));
    userDTO.addGroup(ga);
  }
  
}
