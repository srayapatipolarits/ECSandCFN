package com.sp.web.controller.competency;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.form.Operation;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.competency.UserEvaluationRequest;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyEvaluationSummary;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.competency.CompetencyRepository;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The competency factory interface for caching and helper methods.
 */
@Component
public class CompetencyFactory {
  
  private static final Logger log = Logger.getLogger(CompetencyFactory.class);
  
  @Autowired
  private CompetencyEvaluationCache competencyEvaluationCache;
  
  @Autowired
  private CompetencyRepository competencyRepository;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private DevelopmentFeedbackFactory feedbackFactory;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  @Qualifier("noActivityNotificationProcessor")
  NotificationsProcessor noActivityNotificationProcessor;
  
  /**
   * Get all the competency profiles.
   * 
   * @return - all the competency profiles
   */
  public List<CompetencyProfile> getAll() {
    return competencyRepository.getAll();
  }
  
  /**
   * Get the competency profile for the given competency id.
   * 
   * @param competencyId
   *          - competency id
   * @return the competency profile
   */
  @Cacheable("competencyProfile")
  public CompetencyProfileDao getCompetencyProfile(String competencyId) {
    CompetencyProfile competencyProfile = competencyRepository.findById(competencyId);
    Assert.notNull(competencyProfile, "Competency profile not found. " + competencyId);
    return new CompetencyProfileDao(competencyProfile, articlesFactory, goalsFactory);
  }
  
  /**
   * Update the competency profile.
   * 
   * @param competencyProfile
   *          - competency profile to update
   */
  @CacheEvict(value = "competencyProfile", key = "#competencyProfile.id")
  public void updateCompetencyProfile(CompetencyProfileDao competencyProfile) {
    competencyRepository.update(competencyProfile);
  }
  
  /**
   * Update the competency in the database.
   * 
   * @param competency
   *          - competency
   */
  public void updateCompetency(SPGoal competency) {
    goalsRepository.updateGoal(competency);
    
    /*
     * check in case competency is made inactvice, in case it is inactvie delete the development
     * feedback request if exist for the competency
     */
    if (competency.getStatus() == GoalStatus.INACTIVE) {
      feedbackFactory.deleteByDevFeedRefId(competency.getId());
    }
  }
  
  /**
   * Method to update the competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation to update
   */
  public void update(CompetencyEvaluation competencyEvaluation) {
    competencyEvaluationCache.update(competencyEvaluation);
  }
  
  /**
   * Update the given user competency.
   * 
   * @param userCompetency
   *          - user competency
   */
  public void update(UserCompetency userCompetency) {
    competencyRepository.update(userCompetency);
  }
  
  /**
   * Method to create or update the competency evaluation request.
   * 
   * @param competencyEvaluationRequest
   *          - competency evaluation request
   */
  public void update(CompetencyEvaluationRequest competencyEvaluationRequest) {
    competencyRepository.update(competencyEvaluationRequest);
  }
  
  /**
   * Update the given competency evaluation details in DB.
   * 
   * @param evaluationDetails
   *          - evaluation details to update
   */
  public void update(UserCompetencyEvaluationDetails evaluationDetails) {
    competencyRepository.update(evaluationDetails);
  }
  
  /**
   * Update the spectrum competency profile evaluation results.
   * 
   * @param evaluationResult
   *          - spectrum competency evaluation results
   */
  private void update(SpectrumCompetencyProfileEvaluationResults evaluationResult) {
    competencyRepository.update(evaluationResult);
  }
  
  /**
   * Delete the competency profile.
   * 
   * @param competencyProfile
   *          - competency profile to delete
   */
  @CacheEvict(value = "competencyProfile", key = "#competencyProfile.id")
  public void deleteCompetencyProfile(CompetencyProfileDao competencyProfile) {
    deleteCompetencyProfile(competencyProfile, true);
  }
  
  /**
   * Delete the competency profile.
   * 
   * @param competencyProfile
   *          - competency profile to delete
   * 
   * @param doCleanUp
   *          - flag to clean up notes and feedback as well
   */
  @CacheEvict(value = "competencyProfile", key = "#competencyProfile.id")
  public void deleteCompetencyProfile(CompetencyProfileDao competencyProfile, boolean doCleanUp) {
    // removing the profile
    competencyRepository.delete(competencyProfile);
    if (doCleanUp) {
      // removing all the associated competencies
      competencyProfile.getCompetencyList().forEach(this::competencyProfileCleanUp);
    }
    competencyProfile.getCompetencyList().forEach(c -> deleteCompetency(c, doCleanUp));
  }
  
  /**
   * Delete the given competency and the notes and feedback.
   * 
   * @param competency
   *          - competency
   */
  public void deleteCompetency(SPGoal competency) {
    deleteCompetency(competency, true);
  }
  
  /**
   * Delete the given competency along with all it's notes and feedback.
   * 
   * @param competency
   *          - competency
   * @param doCleanUp
   *          - flag to clean up notes and feedback
   */
  public void deleteCompetency(SPGoal competency, boolean doCleanUp) {
    goalsRepository.removeGoal(competency);
    if (doCleanUp) {
      final String competencyId = competency.getId();
      feedbackFactory.deleteByDevFeedRefId(competencyId);
      publicChannelHelper.deletePublicChannel(competencyId);
    }
  }
  
  /**
   * Delete the user competency evaluation details.
   * 
   * @param userCompetencyEvaluationDetails
   *          - user competency evaluation details
   */
  public void delete(UserCompetencyEvaluationDetails userCompetencyEvaluationDetails) {
    competencyRepository.delete(userCompetencyEvaluationDetails);
  }
  
  /**
   * Delete the user competency.
   * 
   * @param userCompetency
   *          - user competency
   */
  public void delete(UserCompetency userCompetency) {
    competencyRepository.delete(userCompetency);
  }
  
  /**
   * Get the list of competency profiles for the company.
   * 
   * @param companyId
   *          - the company id
   * @return the list of competency profiles
   */
  public List<CompetencyProfile> getCompanyCompetencyProfiles(String companyId) {
    return competencyRepository.getCompanyCompetencyProfiles(companyId);
  }
  
  public CompetencyEvaluation getCompetencyEvaluation(String competencyEvaluationId) {
    return competencyEvaluationCache.getCompetencyEvaluation(competencyEvaluationId);
  }
  
  /**
   * Remove all the competency evaluation requests for the given company.
   * 
   * @param companyId
   *          - company id
   */
  public void removeAllEvaluationRequests(String companyId) {
    competencyRepository.removeAllEvaluationRequests(companyId);
  }
  
  /**
   * Get the competency evaluation requests for the given feedback user id.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the feedback evaluation request
   */
  public CompetencyEvaluationRequest getCompetencyEvaluationRequest(String feedbackUserId) {
    return competencyRepository.getEvaluationRequest(feedbackUserId);
  }
  
  /**
   * Create a new competency evaluation with the given score array details.
   * 
   * @param scoreArray
   *          - score array
   * @param userId
   *          - user id
   * @param reviewerId
   *          - reviewer id
   * @param competencyProfileId
   *          - competency profile id
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param type
   *          - evaluation type
   * @return the new competency evaluation details object
   */
  public UserCompetencyEvaluationDetails createCompetencyEvaluationDetails(double[] scoreArray,
      String userId, String reviewerId, String competencyProfileId, String competencyEvaluationId,
      EvaluationType type) {
    UserCompetencyEvaluationDetails competencyEvaluationDetails = new UserCompetencyEvaluationDetails(
        scoreArray, userId, reviewerId, competencyProfileId, competencyEvaluationId, type);
    update(competencyEvaluationDetails);
    return competencyEvaluationDetails;
  }
  
  /**
   * Method to get the competency evaluation details.
   * 
   * @param evaluationDetailsId
   *          - evaluation details id
   * @return the competency evaluation details
   */
  public UserCompetencyEvaluationDetails getCompetencyEvaluationDetails(String evaluationDetailsId) {
    return competencyRepository.getCompetencyEvaluationDetailsById(evaluationDetailsId);
  }
  
  /**
   * Gets the currently running competency evaluations.
   */
  public List<CompetencyEvaluation> getCurrentCompetencyEvaluations() {
    return competencyRepository.getCurrentCompetencyEvaluations();
  }
  
  /**
   * Returns all the completed competency evaluation for the company.
   * 
   * @param companyId
   *          of the company.
   * @return the completed competancy.
   */
  public List<CompetencyEvaluation> getCompletedCompetencyEvaluation(String companyId) {
    return competencyRepository.getAllCompletedCompetancyEvaluations(companyId);
  }
  
  /**
   * Delete the given competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation to delete
   */
  public void deleteCompetencyEvaluation(CompetencyEvaluation competencyEvaluation) {
    competencyEvaluationCache.deleteCompetencyEvaluation(competencyEvaluation);
  }
  
  /**
   * Get the user competency evaluation details for the given user.
   * 
   * @param userId
   *          - user id
   * @return the list of user competency evaluation details
   */
  public List<UserCompetencyEvaluationDetails> getCompetencyEvaluationDetailsForUser(String userId) {
    return competencyRepository.getAllUserCompetencyEvaluationDetails(userId);
  }
  
  /**
   * Get the user competency object if it does not exist create a new one.
   * 
   * @param uid
   *          - user id
   * @return the user competency object
   */
  public UserCompetency getUserCompetency(String uid) {
    UserCompetency userCompetency = competencyRepository.getUserCompetencyEvaluation(uid);
    if (userCompetency == null) {
      User user = userFactory.getUser(uid);
      Assert.notNull(user, "User not found.");
      userCompetency = new UserCompetency();
      userCompetency.setUserId(uid);
      userCompetency.setCompanyId(user.getCompanyId());
      userCompetency.setEvaluationsMap(new HashMap<String, UserCompetencyEvaluation>());
      competencyRepository.save(userCompetency);
    }
    return userCompetency;
  }
  
  /**
   * Adds the given user to the competency evaluation.
   * 
   * @param user
   *          - user
   * @param competencyEvaluation
   *          - competency evaluation
   */
  public void addUserToCompetencyEvaluation(User user, CompetencyEvaluation competencyEvaluation) {
    competencyEvaluation.addUser(user);
    UserCompetency userCompetencyEvaluation = getUserCompetency(user.getId());
    userCompetencyEvaluation.addEvaluation(competencyEvaluation.getId(),
        user.getCompetencyProfileId());
    update(userCompetencyEvaluation);
  }
  
  /**
   * End the given competency evaluation.
   * 
   * @param competencyEvaluation
   *          -competency evaluation
   */
  public void endEvaluation(CompetencyEvaluation competencyEvaluation) {
    competencyEvaluation.setEndedOn(LocalDateTime.now());
    competencyEvaluation.setCompleted(true);
    update(competencyEvaluation);
    final String companyId = competencyEvaluation.getCompanyId();
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setEvaluationInProgress(false);
    company.setCompetencyEvaluationId(null);
    company.setLastCompetencyEvalutationId(competencyEvaluation.getId());
    company.incrementCompetncyEvaluationCount();
    companyFactory.updateCompany(company);
    final List<SpectrumCompetencyProfileEvaluationResults> profileResults = 
        new ArrayList<SpectrumCompetencyProfileEvaluationResults>();
    final HashMap<String, SpectrumCompetencyEvaluationSummary> evaluationResults = 
        new HashMap<String, SpectrumCompetencyEvaluationSummary>();
    final HashMap<String, Set<String>> managerMap = new HashMap<String, Set<String>>();
    final Set<String> peers = new HashSet<String>();
    
    competencyEvaluation
        .getUserIds()
        .stream()
        .map(this::getUserCompetency)
        .forEach(
            uce -> endUserCompetencyEvaluation(uce, evaluationResults, competencyEvaluation,
                profileResults, managerMap, peers));
    
    // remove all the feedback users for the company and evaluation
    userFactory.removeAllFeedbackUsers(FeatureType.Competency, companyId);
    
    // remove all the competency evaluation requests
    removeAllEvaluationRequests(companyId);
    
    // performing the average operation
    evaluationResults.values().forEach(SpectrumCompetencyEvaluationSummary::doAverage);
    // updating all the competency profile results
    profileResults.stream().map(cp -> cp.add(evaluationResults.get(cp.getCompetencyProfileId())))
        .forEach(this::update);
    
    // going to send out the email's to the managers to display the competency results
    processManagerEndEvaluation(managerMap, competencyEvaluation);
    
    // going to check and clean up pending peer todo requests
    processPeerEndEvaluation(peers, competencyEvaluation);
  }
  
  /**
   * Method to remove the todo requests for the peers.
   * 
   * @param peers
   *          - peers
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void processPeerEndEvaluation(Set<String> peers, CompetencyEvaluation competencyEvaluation) {
    final String competencyEvaluationId = competencyEvaluation.getId();
    peers.stream().map(userFactory::getUserByEmail).filter(Objects::nonNull)
        .forEach(p -> todoFactory.removeAllTasksWithParentId(p, competencyEvaluationId));
  }
  
  /**
   * Process the manager end evaluation email's.
   * 
   * @param managerMap
   *          - manager map
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void processManagerEndEvaluation(HashMap<String, Set<String>> managerMap,
      CompetencyEvaluation competencyEvaluation) {
    
    managerMap
        .forEach((key, value) -> processManagerEndEvaluation(key, value, competencyEvaluation));
  }
  
  /**
   * Process the manager end evaluation email.
   * 
   * @param managerEmail
   *          - manager email
   * @param userIds
   *          - user id's
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void processManagerEndEvaluation(String managerEmail, Set<String> userIds,
      CompetencyEvaluation competencyEvaluation) {
    User managerUser = userFactory.getUserByEmail(managerEmail);
    if (managerUser != null) {
      
      final String competencyEvaluationId = competencyEvaluation.getId();
      // clearing any pending todo requests for the manager
      todoFactory.removeAllTasksWithParentId(managerUser, competencyEvaluationId);
      
      Map<String, Object> paramsMap = new HashMap<String, Object>();
      paramsMap.put(Constants.PARAM_COMPETENCY_EVALUATION, competencyEvaluationId);
      paramsMap.put(Constants.PARAM_USER_ID, userIds);
      paramsMap.put(Constants.PARAM_USER, managerUser.getId());
      Token token = tokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
          TokenProcessorType.COMPETENCY_MANAGER_RESULT);
      
      Map<String, Object> emailParams = new HashMap<String, Object>();
      emailParams.put(Constants.PARAM_TOKEN, token);
      emailParams.put(Constants.PARAM_START_DATE,
          MessagesHelper.formatDate(competencyEvaluation.getStartDate(), "competency"));
      emailParams.put(Constants.PARAM_END_DATE,
          MessagesHelper.formatDate(competencyEvaluation.getEndedOn(), "competency"));
      noActivityNotificationProcessor.process(NotificationType.CompetencyManagerResult,
          managerUser, managerUser, emailParams);
    }
  }
  
  /**
   * End the competency evaluation for the given user.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param userCompetencyEvaluation
   *          - user competency evaluation
   * @param evaluationResultsMap
   *          - map to store the evaluation results
   * @param competencyEvaluation
   *          - competency evaluation
   * @param competencyProfileResults
   *          - competency profile results
   * @param managerMap
   *          - the map to store the manager information
   * @param peers
   *          - the list of peers to clean up
   * 
   */
  private void endUserCompetencyEvaluation(UserCompetency userCompetencyEvaluation,
      HashMap<String, SpectrumCompetencyEvaluationSummary> evaluationResultsMap,
      CompetencyEvaluation competencyEvaluation,
      List<SpectrumCompetencyProfileEvaluationResults> competencyProfileResults,
      HashMap<String, Set<String>> managerMap, Set<String> peers) {
    
    String competencyEvaluationId = competencyEvaluation.getId();
    
    // setting the flag for the users last competency evaluation
    userCompetencyEvaluation.setLastCompetencyEvaluationId(competencyEvaluationId);
    // update the show self evaluation flag
    userCompetencyEvaluation.updateShowSelfEvaluation(competencyEvaluation
        .isSupported(EvaluationType.Self));
    // updating the user competency evaluation
    update(userCompetencyEvaluation);
    
    // removing all the pending user tasks if any
    User user = userFactory.getUser(userCompetencyEvaluation.getUserId());
    if (user != null) {
      todoFactory.removeAllTasksWithParentId(user, competencyEvaluationId);
    }
    
    // updating the users competency evaluation summary for spectrum
    UserCompetencyEvaluation evaluation = userCompetencyEvaluation
        .getEvaluation(competencyEvaluationId);
    // getting the evaluation summary object
    SpectrumCompetencyEvaluationSummary evaluationSummary = evaluationResultsMap.computeIfAbsent(
        evaluation.getCompetencyProfileId(),
        k -> getOrCreateSpectrumCompetencyProfileEvaluationResults(k, competencyEvaluation,
            competencyProfileResults));
    
    // if evaluation summary is found then update the same with the users
    // evaluation data
    if (evaluationSummary != null) {
      evaluationSummary.addEvaluation(evaluation, this, user);
    }
    
    UserCompetencyEvaluationScore managerEvaluation = evaluation.getManager();
    if (managerEvaluation != null) {
      final BaseUserDTO manager = managerEvaluation.getReviewer();
      if (manager != null) {
        Set<String> userIds = managerMap.computeIfAbsent(manager.getEmail(),
            k -> new HashSet<String>());
        userIds.add(userCompetencyEvaluation.getUserId());
      }
    }
    
    PeerCompetencyEvaluationScore peerEvaluations = evaluation.getPeers();
    if (peerEvaluations != null) {
      peerEvaluations.getPeers().stream().filter(score -> !score.isCompleted())
          .map(score -> score.getReviewer().getEmail()).forEach(peers::add);
    }
  }
  
  /**
   * Get the competency evaluation summary for the given competency evaluation and profile.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param competencyEvaluation
   *          - competency evaluation
   * @param competencyProfileResults
   *          - competency profile results
   * @return the competency evaluation summary for spectrum
   */
  private SpectrumCompetencyEvaluationSummary getOrCreateSpectrumCompetencyProfileEvaluationResults(
      String competencyProfileId, CompetencyEvaluation competencyEvaluation,
      List<SpectrumCompetencyProfileEvaluationResults> competencyProfileResults) {
    
    // getting the evaluation summary results for the given competency profile id
    SpectrumCompetencyProfileEvaluationResults evaluationResult = 
        getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
    
    // getting the competency profile
    CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
        .getCompetencyProfile(competencyProfileId);
    
    // if evaluation summary results not found then create a new one
    if (evaluationResult == null) {
      // creating new evaluation results
      if (competencyProfile != null) {
        evaluationResult = new SpectrumCompetencyProfileEvaluationResults(competencyProfile,
            competencyEvaluation.getCompanyId());
      } else {
        log.warn("Competency profile not found :" + competencyProfileId);
        return null;
      }
    }
    
    // adding to the list to be updated in DB
    competencyProfileResults.add(evaluationResult);
    
    // get or create the new evaluation summary object for the given competency profile
    SpectrumCompetencyEvaluationSummary evaluationSummary = null;
    if (competencyProfile != null) {
      evaluationSummary = new SpectrumCompetencyEvaluationSummary(competencyEvaluation,
          competencyProfile);
    }
    return evaluationSummary;
  }
  
  /**
   * Remove the given user from the competency evaluation.
   * 
   * @param user
   *          - user to remove
   * @param competencyEvaluation
   *          - competency evaluation
   * @return - returns true if the user was removed else false
   */
  public boolean removeUserFromEvaluation(User user, CompetencyEvaluation competencyEvaluation) {
    final String userId = user.getId();
    if (competencyEvaluation.removeUser(userId)) {
      competencyRepository.update(competencyEvaluation);
      UserCompetency userCompetency = getUserCompetency(userId);
      
      final String competencyEvaluationId = competencyEvaluation.getId();
      todoFactory.remove(user, competencyEvaluationId, competencyEvaluationId);
      
      UserCompetencyEvaluation removedEvaluation = userCompetency
          .removeEvaluation(competencyEvaluationId);
      competencyRepository.save(userCompetency);
      final UserCompetencyEvaluationScore manager = removedEvaluation.getManager();
      // Score check removed as manager request was not removed.
      if (manager != null) {
        removeEvaluationRequest(userId, manager, competencyEvaluationId);
      }
      
      final PeerCompetencyEvaluationScore peerEvaluations = removedEvaluation.getPeers();
      if (peerEvaluations != null) {
        final List<UserCompetencyEvaluationScore> peers = peerEvaluations.getPeers();
        if (!CollectionUtils.isEmpty(peers)) {
          peers.forEach(peer -> removeEvaluationRequest(userId, peer, competencyEvaluationId));
        }
      }
      return true;
    }
    return false;
  }
  
  private void removeEvaluationRequest(final String userId,
      final UserCompetencyEvaluationScore request, String competencyEvaluationId) {
    
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(request.getReviewer().getId());
    CompetencyEvaluationRequest competencyEvaluationRequest = getCompetencyEvaluationRequest(feedbackUser
        .getId());
    final List<UserEvaluationRequest> requestsList = competencyEvaluationRequest.getRequestsList();
    
    Optional<UserEvaluationRequest> evaluationRequest = requestsList.stream()
        .filter(req -> req.getUserId().equals(userId)).findFirst();
    evaluationRequest.ifPresent(req -> requestsList.remove(req));
    update(competencyEvaluationRequest);
    
    if (feedbackUser.getType() == UserType.Member) {
      User user = userFactory.getUserByEmail(feedbackUser.getEmail());
      if (user != null) {
        UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
        ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests
            .getParentTodoTaskRequest(competencyEvaluationId);
        if (parentTodoTaskRequest != null) {
          TodoRequest todoRequest = parentTodoTaskRequest.remove(userId);
          if (todoRequest != null) {
            if (parentTodoTaskRequest.isEmpty()) {
              userTodoRequests.removeByParent(competencyEvaluationId);
            }
            todoFactory.updateUserTodoRequests(userTodoRequests);
            todoFactory.sendSse(user, Operation.DELETE, competencyEvaluationId,
                todoRequest.getRefId(), TodoType.CompetencyEvaluation);
          }
        }
      }
    }
  }
  
  private void competencyProfileCleanUp(CompetencyDao competency) {
    goalsRepository.removeGoal(competency);
    feedbackFactory.deleteByParentRefId(competency.getId());
  }
  
  /**
   * Method to get all the spectrum competency profile evaluation results for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the list of spectrum competency profile evaluation results
   */
  public List<SpectrumCompetencyProfileEvaluationResults> getAllSpectrumCompetencyProfileEvaluationResults(
      String companyId) {
    return competencyRepository.getAllSpectrumCompetencyProfileEvaluationResults(companyId);
  }
  
  /**
   * Get the spectrum competency profile id for the given competency profile id.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @return the spectrum competency profile evaluation results
   */
  public SpectrumCompetencyProfileEvaluationResults getSpectrumCompetencyProfileEvaluationResult(
      String competencyProfileId) {
    return competencyRepository.findSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
  }
  
  /**
   * Delete the given users competency information.
   * 
   * @param user
   *          - user
   */
  public void deleteUser(User user) {
    // cleaning up the user competency
    UserCompetency userCompetencyEvaluation = getUserCompetency(user.getId());
    delete(userCompetencyEvaluation);
    
    // removing the user from the various competency evaluation summary
    userCompetencyEvaluation.getEvaluationsMap().keySet()
        .forEach(evaluationId -> removeUserFromCompetencyEvaluation(user, evaluationId));
    
    // remove all the user competency evaluation details
    competencyRepository.deleteAllUserCompetencyEvaluationDetails(user.getId());
  }

  /**
   * Remove the user from the competency evaluations that the user may have taken part
   * in the past.
   * 
   * @param user
   *          - user
   */
  public void removeUserFromCompetency(User user) {
    // getting the users competency evaluation
    UserCompetency userCompetencyEvaluation = getUserCompetency(user.getId());
    // removing the user from the various competency evaluation summary
    userCompetencyEvaluation.getEvaluationsMap().keySet()
        .forEach(evaluationId -> removeUserFromCompetencyEvaluation(user, evaluationId));
  }
  
  /**
   * Remove the user from the competency evaluation.
   * 
   * @param user
   *          - user to remove
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param evaluation
   *          - evaluation
   */
  private void removeUserFromCompetencyEvaluation(User user, String competencyEvaluationId) {
    CompetencyEvaluation competencyEvaluation = getCompetencyEvaluation(competencyEvaluationId);
    if (competencyEvaluation.removeUser(user.getId())) {
      update(competencyEvaluation);
      cleanupSpectrumSummary(competencyEvaluation);
      if (competencyEvaluation.getUserIds().isEmpty()) {
        // removing the competency evaluation as no more users are
        // remaining in the competency evaluation
        deleteCompetencyEvaluation(competencyEvaluation);
        
        // check if the last competency evaluation is the deleted competency
        // evaluation and if so then clear the same
        final String companyId = competencyEvaluation.getCompanyId();
        CompanyDao company = companyFactory.getCompany(companyId);
        company.decrementCompetencyEvaluationCount();
        if (company.getLastCompetencyEvalutationId().equalsIgnoreCase(competencyEvaluationId)) {
          company.setLastCompetencyEvalutationId(null);
          List<CompetencyEvaluation> competencyEvaluations = getCompletedCompetencyEvaluation(companyId);
          CompetencyEvaluation lastCompetencyEvaluation = null;
          for (CompetencyEvaluation evaluation : competencyEvaluations) {
            if (lastCompetencyEvaluation == null) {
              lastCompetencyEvaluation = evaluation;
            } else {
              if (lastCompetencyEvaluation.getStartDate().isBefore(evaluation.getStartDate())) {
                lastCompetencyEvaluation = evaluation;
              }
            }
          }
          company.setLastCompetencyEvalutationId(Optional.ofNullable(lastCompetencyEvaluation)
              .map(CompetencyEvaluation::getId).orElse(null));
        }
        companyFactory.updateCompany(company);        
      } else {
        createSpectrumSummary(competencyEvaluation);
      }
    }
  }

  /**
   * Remove the competency evaluation summary for the given competency evaluation.
   * 
   * @param competencyEvaluation
   *              - competency evaluation
   */
  private void cleanupSpectrumSummary(CompetencyEvaluation competencyEvaluation) {
    final String competencyId = competencyEvaluation.getId();
    competencyEvaluation.getCompetencyProfileMap().keySet().forEach(cpId -> cleanupSpectrumSummary(cpId, competencyId));
  }

  /**
   * Remove the given competency evaluation from the spectrum summary for the given competency profile.
   *  
   * @param competencyProfileId
   *              - competency profile id
   * @param competencyId
   *              - competency evaluation 
   */
  private void cleanupSpectrumSummary(String competencyProfileId, String competencyId) {
    SpectrumCompetencyProfileEvaluationResults cps = getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
    if (cps != null && cps.removeEvaluation(competencyId)) {
      update(cps);
    }
  }

  /**
   * Reload the competency evaluation summary for spectrum for the given competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   */
  public void createSpectrumSummary(CompetencyEvaluation competencyEvaluation) {
    
    final List<SpectrumCompetencyProfileEvaluationResults> competencyProfileResults = 
        new ArrayList<SpectrumCompetencyProfileEvaluationResults>();
    final HashMap<String, SpectrumCompetencyEvaluationSummary> evaluationResultsMap = 
        new HashMap<String, SpectrumCompetencyEvaluationSummary>();
    final HashMap<String, Set<String>> managerMap = new HashMap<String, Set<String>>();
    final Set<String> peers = new HashSet<String>();
    
    competencyEvaluation
        .getUserIds()
        .stream()
        .map(this::getUserCompetency)
        .forEach(
            uce -> endUserCompetencyEvaluation(uce, evaluationResultsMap, competencyEvaluation,
                competencyProfileResults, managerMap, peers));
    
    // performing the average operation
    evaluationResultsMap.values().forEach(SpectrumCompetencyEvaluationSummary::doAverage);
    // updating all the competency profile results
    competencyProfileResults
        .stream()
        .map(
            cp -> updateEvaluationResult(cp, evaluationResultsMap.get(cp.getCompetencyProfileId())))
        .forEach(this::update);
  }
  
  /**
   * Get all the user competencies for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the list of user competencies
   */
  public List<UserCompetency> getAllUserCompetency(String companyId) {
    return competencyRepository.getAllUserCompetencies(companyId);
  }
  
  /**
   * Update the spectrum competency evaluation summary in the spectrum competency profile results
   * and ensure that there is no duplication.
   * 
   * @param spectrumEvaluationProfileResults
   *          - spectrum evaluation profile results
   * @param spectrumCompetencyEvaluationSummary
   *          - spectrum competency evaluation summary to add
   * @return the updated spectrum evaluation profile results
   */
  private SpectrumCompetencyProfileEvaluationResults updateEvaluationResult(
      SpectrumCompetencyProfileEvaluationResults spectrumEvaluationProfileResults,
      SpectrumCompetencyEvaluationSummary spectrumCompetencyEvaluationSummary) {
    
    final String competencyEvaluationId = spectrumCompetencyEvaluationSummary
        .getCompetencyEvaluationId();
    List<SpectrumCompetencyEvaluationSummary> evaluationResults = spectrumEvaluationProfileResults
        .getEvaluationResults();
    Optional<SpectrumCompetencyEvaluationSummary> findFirst = evaluationResults.stream()
        .filter(er -> er.getCompetencyEvaluationId().equals(competencyEvaluationId)).findFirst();
    
    if (findFirst.isPresent()) {
      evaluationResults.remove(findFirst.get());
    }
    evaluationResults.add(spectrumCompetencyEvaluationSummary);
    return spectrumEvaluationProfileResults;
  }
}
