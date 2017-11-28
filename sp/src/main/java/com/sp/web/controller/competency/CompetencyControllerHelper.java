package com.sp.web.controller.competency;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.competency.CompetencyProfileDTO;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.dto.competency.CompetencyScoreDTO;
import com.sp.web.dto.competency.CompetencySummaryDTO;
import com.sp.web.dto.competency.UserCompetencySelfEvaluationDTO;
import com.sp.web.dto.competency.UserEvaluationRequestDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.FeedbackUserForm;
import com.sp.web.form.Operation;
import com.sp.web.form.competency.CompetencyRequestForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.competency.UserEvaluationRequest;
import com.sp.web.model.task.TaskType;
import com.sp.web.model.todo.CompetencyTodoRequest;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.token.TokenRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class for the competency controller.
 */
@Component
public class CompetencyControllerHelper {
  
  private static final Logger log = Logger.getLogger(CompetencyControllerHelper.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  @Qualifier("noActivityNotificationProcessor")
  private NotificationsProcessor noActivityNotificationProcessor;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private TodoFactory todoFactory;
  
  /**
   * Helper method to initiate the evaluation for a user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the initiate request
   */
  public SPResponse initiateEvaluation(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // check if the evaluation is in progress
    CompanyDao company = checkIfEvaluationNotInProgress(user);
    
    // get the initiate request form
    CompetencyRequestForm form = (CompetencyRequestForm) params[0];
    
    boolean isManagerRequest = false;
    
    User userFor = user;
    
    // get the user to update
    final String userForEmail = form.getUserForEmail();
    if (!StringUtils.isBlank(userForEmail)) {
      userFor = userFactory.getUserByEmail(userForEmail);
      isManagerRequest = !user.hasRole(RoleType.CompetencyAdmin);
    }
    
    // check if the users competency profile has been set
    final String competencyProfileId = userFor.getCompetencyProfileId();
    Assert.notNull(competencyProfileId, "Competency profile not set for user.");
    
    // check if the user is part of the competency evaluation
    final CompetencyEvaluation competencyEvaluation = getCompetencyEvaluation(company);
    final String competencyEvaluationId = competencyEvaluation.getId();
    final String userForId = userFor.getId();
    Assert.isTrue(competencyEvaluation.hasUser(userForId),
        "User not part of competency evaluation.");
    
    // get the user's competency evaluation request
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(userForId);
    UserCompetencyEvaluation evaluation = userCompetencyEvaluation
        .getEvaluation(competencyEvaluationId);
    
    if (isManagerRequest) {
      try {
        Assert.isTrue(evaluation.getManager().checkIfSameUser(user), "Must be manager.");
      } catch (Exception e) {
        log.warn("Error checking manager.", e);
        throw new InvalidRequestException("Must be manager.");
      }
    }
    
    // adding the manager
    final Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.putAll(MessagesHelper.getGenderText(userFor));
    final FeedbackUserForm managerRequest = form.getManager();
    if (!isManagerRequest && managerRequest != null) {
      FeedbackUser managerUser = getFeedbackUser(user.getCompanyId(), managerRequest);
      
      // checking if manager is added as a peer request
      // and removing the request
      final User usrFor = userFor;
      Optional.ofNullable(evaluation.getPeers()).ifPresent(
          p -> removePeerRequest(p, managerUser, userCompetencyEvaluation, competencyEvaluation,
              usrFor));

      // removing previous manager request if one is present
      UserCompetencyEvaluationScore managerEvaluation = evaluation.getManager();
      if (managerEvaluation != null) {
        removeEvaluationRequestAndResult(managerEvaluation, userFor, competencyEvaluationId);
      }
      // set the evaluation
      evaluation.setManager(new UserCompetencyEvaluationScore(managerUser));
      paramsMap.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
          "notification.subject.CompetencyEvaluationRequestManager", managerUser.getLocale(),
          userFor.getFullNameOrEmail(), company.getName()));
      addToEvaluationRequest(userFor, competencyProfileId, paramsMap, managerUser,
          EvaluationType.Manager, NotificationType.CompetencyEvaluationRequestManager,
          competencyEvaluation);
    }
    
    // adding the peer evaluations
    final List<FeedbackUserForm> peerRequestList = form.getPeerList();
    if (!CollectionUtils.isEmpty(peerRequestList)) {
      for (FeedbackUserForm peerRequest : peerRequestList) {
        addPeerRequest(peerRequest, userFor, competencyProfileId, paramsMap, evaluation,
            competencyEvaluation);
      }
    }
    // Updating the competency evaluation in DB after manager and peer requests
    competencyFactory.update(userCompetencyEvaluation);
    
    // update the user task list
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(userFor);
    ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests
        .getParentTodoTaskRequest(competencyEvaluationId);
    if (parentTodoTaskRequest != null) {
      TodoRequest todoRequest = parentTodoTaskRequest.getRequest(competencyEvaluationId);
      if (todoRequest != null && todoRequest instanceof CompetencyTodoRequest) {
        CompetencyTodoRequest competencyTodoRequest = (CompetencyTodoRequest) todoRequest;
        final TaskType taskType = competencyTodoRequest.getTaskType();
        if (taskType == TaskType.CompetencyEvaluationInitiate
            || taskType == TaskType.CompetencyEvaluationInitiatePeer
            || taskType == TaskType.CompetencyEvaluationInitiateManagerSelf
            || taskType == TaskType.CompetencyEvaluationInitiateManagerPeer
            || taskType == TaskType.CompetencyEvaluationInitiateManager
            || taskType == TaskType.CompetencyEvaluationInitiateSelfPeer) {
          
          // check if manager evaluation is required and if the manager is set for the user
          boolean isManagerSet = true;
          if (competencyEvaluation.isSupported(EvaluationType.Manager)) {
            isManagerSet = evaluation.getManager() != null;
          }
          
          if (isManagerSet) {
            if (competencyEvaluation.isSupported(EvaluationType.Self)) {
              // add the self evaluation task list
              competencyTodoRequest.setTaskType(TaskType.CompetencyEvaluationSelf);
              todoFactory.sendSse(userFor, Operation.UPDATE, todoFactory.processWithParent(userFor,
                  parentTodoTaskRequest, new MutableBoolean()), false);
            } else {
              userTodoRequests.deleteRequest(competencyEvaluationId, competencyEvaluationId);
              if (parentTodoTaskRequest.getTodoTasks().isEmpty()) {
                userTodoRequests.removeByParent(competencyEvaluationId);
                todoFactory.sendSse(userFor, Operation.DELETE, competencyEvaluationId, null,
                    TodoType.CompetencyEvaluation);
              } else {
                MutableBoolean doUpdate = new MutableBoolean(false);
                todoFactory.sendSse(userFor, Operation.UPDATE,
                    todoFactory.processWithParent(userFor, parentTodoTaskRequest, doUpdate), false);
              }
            }
            todoFactory.updateUserTodoRequests(userTodoRequests);
          }
        }
      }
    }
    return resp.isSuccess();
  }
  
  /**
   * Remove the given peer request from the users evaluation requests. 
   * 
   * @param peers
   *          - peer requests
   * @param managerUser
   *          - manager user
   * @param userCompetencyEvaluation
   *          - user competency evaluation
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void removePeerRequest(PeerCompetencyEvaluationScore peers, FeedbackUser managerUser,
      UserCompetency userCompetencyEvaluation, CompetencyEvaluation competencyEvaluation,
      final User userFor) {
    
    UserCompetencyEvaluationScore peerRequest = peers.getPeerRequest(managerUser.getId());
    if (peerRequest != null) {
      if (peers.removePeer(peerRequest)) {
        if (peerRequest.isCompleted()) {
          peers.updateAverageScore();
        }
        competencyFactory.update(userCompetencyEvaluation);

        // clean up the peer request
        final String competencyEvaluationId = competencyEvaluation.getId();
        removeEvaluationRequestAndResult(peerRequest, userFor, competencyEvaluationId);
      }
    }
  }

  /**
   * Add or create a new user competency evaluation request and add the following request.
   * 
   * @param userFor
   *          - user for
   * @param competencyProfileId
   *          - competency profile id
   * @param paramsMap
   *          - params map
   * @param reviewer
   *          - reviewer user
   * @param evaluationType
   *          - evaluation type
   * @param notificationType
   *          - notification type
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void addToEvaluationRequest(User userFor, final String competencyProfileId,
      final Map<String, Object> paramsMap, FeedbackUser reviewer, EvaluationType evaluationType,
      NotificationType notificationType, CompetencyEvaluation competencyEvaluation) {
    // set the manager request
    CompetencyEvaluationRequest competencyEvaluationRequest = competencyFactory
        .getCompetencyEvaluationRequest(reviewer.getId());
    if (competencyEvaluationRequest == null) {
      competencyEvaluationRequest = new CompetencyEvaluationRequest(reviewer);
      competencyFactory.update(competencyEvaluationRequest);
    }
    competencyEvaluationRequest.add(competencyProfileId, userFor.getId(), evaluationType);
    competencyFactory.update(competencyEvaluationRequest);
    // adding the todo for the manager
    if (reviewer.getType() == UserType.Member) {
      todoFactory.addTodo(reviewer,
          TodoRequest.newCompetencyEvaluationRequest(reviewer, competencyEvaluation, userFor));
    }
    // send the notification
    noActivityNotificationProcessor.process(notificationType, userFor, (User) reviewer, paramsMap);
  }
  
  /**
   * Get or create a feedback user.
   * 
   * @param companyId
   *          - company id
   * @param feedbackUserForm
   *          - feedback user form
   * @return the feedback user
   */
  private FeedbackUser getFeedbackUser(String companyId, final FeedbackUserForm feedbackUserForm) {
    // get the user
    FeedbackUser feedbackUser = userFactory.getOrCreateFeedbackUser(feedbackUserForm,
        FeatureType.Competency, companyId);
    
    // adding a token for the manager user
    if (StringUtils.isEmpty(feedbackUser.getTokenId())) {
      TokenRequest tokenRequest = new TokenRequest(TokenType.PERPETUAL);
      tokenRequest.addParam(Constants.PARAM_FEEDBACK_USERID, feedbackUser.getId());
      Token token = tokenFactory.getToken(tokenRequest, TokenProcessorType.COMPETENCY_EVALUATE);
      feedbackUser.saveTokenInformation(token);
      userFactory.updateUser(feedbackUser);
    }
    return feedbackUser;
  }
  
  /**
   * Get the feedback user.
   * 
   * @param user
   *          - existing user
   * @return feedback user
   */
  private FeedbackUser getFeedbackUser(User user) {
    if (user instanceof FeedbackUser) {
      return (FeedbackUser) user;
    }
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(user.getEmail(),
        FeatureType.Competency, user.getCompanyId());
    Assert.notNull(feedbackUser, "User not found.");
    return feedbackUser;
  }
  
  /**
   * Remove the previous evaluation request.
   * 
   * @param userEvaluation
   *          - user evaluation
   * @param userFor
   *          - user
   */
  private void removeEvaluationRequestAndResult(UserCompetencyEvaluationScore userEvaluation,
      User userFor, String competencyEvaluationId) {
    BaseUserDTO reviewer = userEvaluation.getReviewer();
    CompetencyEvaluationRequest competencyEvaluationRequest = competencyFactory
        .getCompetencyEvaluationRequest(reviewer.getId());
    final List<UserEvaluationRequest> requestsList = competencyEvaluationRequest.getRequestsList();
    final String userForId = userFor.getId();
    Optional<UserEvaluationRequest> findFirst = requestsList.stream()
        .filter(r -> r.getUserId().equals(userForId)).findFirst();
    // removing the request
    if (findFirst.isPresent()) {
      UserEvaluationRequest userEvaluationRequest = findFirst.get();
      requestsList.remove(userEvaluationRequest);
      competencyFactory.update(competencyEvaluationRequest);
      FeedbackUser fbUser = userFactory.getFeedbackUser(reviewer.getId());
      if (fbUser != null) {
        updateTodo(userEvaluationRequest.getType(), userEvaluationRequest.getUserId(), fbUser,
            competencyEvaluationId);
      }
    } else {
      log.warn("Request not found to remove." + reviewer.getEmail());
    }
  }
  
  /**
   * Add a peer request.
   * 
   * @param peerRequest
   *          - peer request form
   * @param userFor
   *          - user for
   * @param competencyProfileId
   *          - competency profile id
   * @param paramsMap
   *          - params map
   * @param evaluation
   *          - user competency result
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void addPeerRequest(FeedbackUserForm peerRequest, User userFor,
      String competencyProfileId, Map<String, Object> paramsMap,
      UserCompetencyEvaluation evaluation, CompetencyEvaluation competencyEvaluation) {
    
    // getting the peer user
    FeedbackUser peerUser = getFeedbackUser(userFor.getCompanyId(), peerRequest);
    
    // adding the peer user
    if (evaluation.addPeer(new UserCompetencyEvaluationScore(peerUser))) {
      // adding the evaluation request
      addToEvaluationRequest(userFor, competencyProfileId, paramsMap, peerUser,
          EvaluationType.Peer, NotificationType.CompetencyEvaluationRequestPeer,
          competencyEvaluation);
    }
  }
  
  /**
   * Check if there is an evaluation in progress.
   * 
   * @param user
   *          - user for company id
   * @return the reference to the company object
   */
  private CompanyDao checkIfEvaluationNotInProgress(User user) {
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    if (!company.isEvaluationInProgress()) {
      log.error("Evaluation not in progress");
      throw new InvalidRequestException(MessagesHelper.getMessage("service.growl.message5"));
    }
    return company;
  }
  
  /**
   * Helper method to get the competency details for a user.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse get(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // check if member email was passed.
    String memberEmail = (String) params[0];
    
    boolean addEvaluationsFlag = true;
    if (!StringUtils.isBlank(memberEmail)) {
      // Check if the member is part of the company
      user = userRepository.findByEmailValidated(memberEmail, user.getCompanyId());
      addEvaluationsFlag = false;
    }
    
    final String competencyProfileId = user.getCompetencyProfileId();
    if (!StringUtils.isBlank(competencyProfileId)) {
      // get the competency profile for the user
      CompetencyProfileDao competencyProfile = competencyFactory
          .getCompetencyProfile(competencyProfileId);
      // get the users article progress
      resp.add(Constants.PARAM_COMPETENCY_PROFILE, new CompetencyProfileDTO(competencyProfile,
          user, getUserGoalDao(user), articlesFactory));
      if (addEvaluationsFlag) {
        UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
        resp.add(Constants.PARAM_COMPETENCY_EVALUATION,
            userCompetencyEvaluation.isShowSelfEvaluations());
      }
    }
    return resp.isSuccess();
  }
  
  /**
   * Get the the user goal dao.
   * 
   * @param user
   *          - user
   * @return the UserGoalDao
   */
  private UserGoalDao getUserGoalDao(User user) {
    final String userGoalId = user.getUserGoalId();
    UserGoalDao userGoal = null;
    if (!StringUtils.isBlank(userGoalId)) {
      userGoal = goalsFactory.getUserGoal(userGoalId, user.getUserLocale());
    }
    if (userGoal == null) {
      userGoal = goalsFactory.addGoalsForUser(user);
    }
    return userGoal;
  }
  
  /**
   * Helper method to start an evaluation either self or user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the evaluation request
   */
  public SPResponse evaluationStart(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // check if evaluation has ended
    CompanyDao company = checkIfEvaluationNotInProgress(user);
    
    // get the member for which the evaluation is required
    String memberId = (String) params[0];
    
    // if blank it is assumed that it is self evaluation
    CompetencyProfileSummaryDTO competencyProfileSummary = null;
    CompetencyEvaluation competencyEvaluation = getCompetencyEvaluation(company);
    if (StringUtils.isBlank(memberId)) {
      competencyProfileSummary = getCompetencyEvaluationByProfile(competencyEvaluation,
          user.getCompetencyProfileId(), EvaluationType.Self);
    } else {
      // get the competency evaluation request for the given feedback user
      CompetencyEvaluationRequest competencyEvaluationRequest = getCompetencyEvaluationRequest(user);
      // get the feedback request for the given member
      UserEvaluationRequest userEvaluationRequest = getUserEvaluationRequest(memberId,
          competencyEvaluationRequest);
      // get the competency profile summary
      competencyProfileSummary = getCompetencyEvaluationByProfile(competencyEvaluation,
          userEvaluationRequest.getCompetencyProfileId(), userEvaluationRequest.getType());
      // adding the user information to the response
      resp.add(Constants.PARAM_USER, new BaseUserDTO(userFactory.getUser(memberId)));
    }
    
    Assert.notNull(competencyProfileSummary, "Competency profile not set.");
    return resp.add(Constants.PARAM_COMPETENCY_PROFILE, competencyProfileSummary);
  }
  
  /**
   * Get the competency evaluation by profile for the given competency profile id. Also validate if
   * the given evaluation type is supported for the current evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   * @param competencyProfileId
   *          - competency profile
   * @param evaluationType
   *          - evaluation type
   * @return the competency evaluation profile summary
   */
  private CompetencyProfileSummaryDTO getCompetencyEvaluationByProfile(
      CompetencyEvaluation competencyEvaluation, final String competencyProfileId,
      EvaluationType evaluationType) {
    Assert
        .isTrue(competencyEvaluation.isSupported(evaluationType), "Evalution type not supported.");
    return competencyEvaluation.getCompetencyProfile(competencyProfileId);
  }
  
  /**
   * Method to get the competency evaluation for the company.
   * 
   * @param company
   *          - company
   * @return the competency evaluation
   */
  private CompetencyEvaluation getCompetencyEvaluation(CompanyDao company) {
    CompetencyEvaluation competencyEvaluation = competencyFactory.getCompetencyEvaluation(company
        .getCompetencyEvaluationId());
    Assert.notNull(competencyEvaluation, "Competency evaluation not found.");
    return competencyEvaluation;
  }
  
  /**
   * Helper method to get the details to start an evaluation.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the save evaluation request
   */
  public SPResponse evaluationSave(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // check if evaluation has ended
    CompanyDao company = checkIfEvaluationNotInProgress(user);
    
    // get the member for which the evaluation is required
    String memberId = (String) params[0];
    double[] scoreArray = (double[]) params[1];
    
    CompetencyEvaluation competencyEvaluation = competencyFactory.getCompetencyEvaluation(company
        .getCompetencyEvaluationId());
    
    // if blank it is assumed that it is self evaluation
    if (StringUtils.isBlank(memberId)) {
      // update the evaluation result
      updateEvaluationResult(competencyEvaluation, scoreArray, EvaluationType.Self, user.getId(),
          user.getCompetencyProfileId(), user);
      sendEvaluationSaveNotification(
          competencyEvaluation.getCompetencyProfile(user.getCompetencyProfileId()), user, user,
          NotificationType.CompetencyEvaluationSelf, scoreArray, company);
    } else {
      
      FeedbackUser userFor = getFeedbackUser(user);
      // get the feedback request for the given member
      CompetencyEvaluationRequest competencyEvaluationRequest = getCompetencyEvaluationRequest(userFor);
      // get the user evaluation request
      UserEvaluationRequest userEvaluationRequest = getUserEvaluationRequest(memberId,
          competencyEvaluationRequest);
      // update the evaluation result
      final EvaluationType evaluationType = userEvaluationRequest.getType();
      final String userForId = userEvaluationRequest.getUserId();
      final String competencyProfileId = userEvaluationRequest.getCompetencyProfileId();
      updateEvaluationResult(competencyEvaluation, scoreArray, evaluationType, userForId,
          competencyProfileId, userFor);
      // remove the evaluation request
      competencyEvaluationRequest.removeRequest(userEvaluationRequest);
      // update the db
      competencyFactory.update(competencyEvaluationRequest);
      if (evaluationType == EvaluationType.Manager || evaluationType == EvaluationType.Peer) {
        sendEvaluationSaveNotification(
            competencyEvaluation.getCompetencyProfile(competencyProfileId),
            userFactory.getUser(userForId), userFor, NotificationType.CompetencyEvaluationManager,
            scoreArray, company);
      }
    }
    return resp.isSuccess();
  }
  
  private void sendEvaluationSaveNotification(
      CompetencyProfileSummaryDTO competencyProfileSummaryDTO, User user, User userFor,
      NotificationType notificationType, double[] scoreArray, CompanyDao company) {
    
    OptionalDouble average = Arrays.stream(scoreArray).average();
    double totalScore = BigDecimal.valueOf(average.orElse(0d))
        .setScale(Constants.PERCENT_PRECISION, Constants.ROUNDING_MODE).doubleValue();
    List<CompetencyScoreDTO> competencyScoreList = createCompetencyScoreList(
        competencyProfileSummaryDTO, scoreArray);
    HashMap<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_COMPETENCY_SCORE_TOTAL, totalScore);
    paramsMap.put(Constants.PARAM_COMPETENCY_SCORE_LIST, competencyScoreList);
    paramsMap.putAll(MessagesHelper.getGenderText(userFor));
    if (notificationType == NotificationType.CompetencyEvaluationManager) {
      paramsMap.put(
          Constants.PARAM_SUBJECT,
          MessagesHelper.getMessage("notification.subject.CompetencyEvaluationManager",
              userFor.getLocale(), user.getFirstName(), user.getLastName(), company.getName()));
    }
    notificationProcessor.process(notificationType, user, userFor, paramsMap);
  }
  
  /**
   * Get the list of competency and scores.
   * 
   * @param competencyProfile
   *          - competency profile
   * @param scoreArray
   *          - score array
   * @return the list of competency score dto's
   */
  private List<CompetencyScoreDTO> createCompetencyScoreList(
      CompetencyProfileSummaryDTO competencyProfile, double[] scoreArray) {
    ArrayList<CompetencyScoreDTO> competencyScoreDTOs = new ArrayList<CompetencyScoreDTO>(
        scoreArray.length);
    int index = 0;
    for (CompetencySummaryDTO competency : competencyProfile.getCompetencyList()) {
      competencyScoreDTOs.add(new CompetencyScoreDTO(competency, scoreArray[index++]));
    }
    return competencyScoreDTOs;
  }
  
  /**
   * Update the competency evaluation result.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   * @param scoreArray
   *          - score array
   * @param type
   *          - evaluation type
   * @param userId
   *          - user id
   * @param competencyProfileId
   *          - competency profile id
   * @param reviewer
   *          - reviewer
   * @return the updated competency evaluation profile
   */
  private void updateEvaluationResult(CompetencyEvaluation competencyEvaluation,
      double[] scoreArray, EvaluationType type, String userId, String competencyProfileId,
      User reviewer) {
    Assert.isTrue(competencyEvaluation.hasUser(userId), "User not part of competency evaluation.");
    Assert.isTrue(competencyEvaluation.isSupported(type),
        "Competency evaluation type not supported.");
    CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
        .getCompetencyProfile(competencyProfileId);
    Assert.notNull(competencyProfile, "Competency profile not found in evaluation.");
    Assert.isTrue(competencyProfile.getCompetencyList().size() == scoreArray.length,
        "Score mismatch from competency profile.");
    final String competencyEvaluationId = competencyEvaluation.getId();
    UserCompetencyEvaluationDetails evaluationDetails = competencyFactory
        .createCompetencyEvaluationDetails(scoreArray, userId, reviewer.getId(),
            competencyProfileId, competencyEvaluationId, type);
    UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
    UserCompetencyEvaluation evaluation = userCompetency.getEvaluation(competencyEvaluationId);
    Assert.notNull(evaluation, "User not part of competency evaluation.");
    if (evaluation.updateScore(type, reviewer, evaluationDetails)) {
      competencyFactory.update(userCompetency);
    }
    updateTodo(type, userId, reviewer, competencyEvaluationId);
  }
  
  private void updateTodo(EvaluationType type, String userId, User reviewer,
      final String competencyEvaluationId) {
    
    if (reviewer.getType() == UserType.External) {
      return;
    }
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(reviewer);
    ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests
        .getParentTodoTaskRequest(competencyEvaluationId);
    if (parentTodoTaskRequest != null) {
      if (type == EvaluationType.Self) {
        parentTodoTaskRequest.remove(competencyEvaluationId);
      } else {
        parentTodoTaskRequest.remove(userId);
      }
      if (parentTodoTaskRequest.isEmpty()) {
        userTodoRequests.removeByParent(competencyEvaluationId);
        todoFactory.sendSse(reviewer, Operation.DELETE, competencyEvaluationId, null,
            TodoType.CompetencyEvaluation);
      } else {
        todoFactory.sendSse(reviewer, Operation.UPDATE,
            todoFactory.processWithParent(reviewer, parentTodoTaskRequest, new MutableBoolean()),
            false);
      }
      todoFactory.updateUserTodoRequests(userTodoRequests);
    }
  }
  
  /**
   * Get the user evaluation request for the given member id.
   * 
   * @param memberId
   *          - member id
   * @param competencyEvaluationRequest
   *          - competency evaluation request
   * @return the user evaluation request
   */
  private UserEvaluationRequest getUserEvaluationRequest(String memberId,
      CompetencyEvaluationRequest competencyEvaluationRequest) {
    Assert.notNull(competencyEvaluationRequest, "No competency request for user.");
    final List<UserEvaluationRequest> requestsList = competencyEvaluationRequest.getRequestsList();
    Assert.notEmpty(requestsList, "No requests found for user.");
    Optional<UserEvaluationRequest> findFirst = requestsList.stream()
        .filter(r -> r.getUserId().equals(memberId)).findFirst();
    Assert.isTrue(findFirst.isPresent(), "No requests found for user.");
    return findFirst.get();
  }
  
  /**
   * Helper method to get the evaluation details for the given user evaluation details id.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the user competency evaluation details
   */
  public SPResponse getEvaluationDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String scoreDetailsId = (String) params[0];
    
    // get the instance of the competency evaluation details
    UserCompetencyEvaluationDetails evaluationDetails = competencyFactory
        .getCompetencyEvaluationDetails(scoreDetailsId);
    Assert.notNull(evaluationDetails, "Competency evaluation details not found.");
    
    // get the competency evaluation
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(evaluationDetails.getEvaluationId());
    Assert.notNull(competencyEvaluation, "Competency evalaution not found.");
    
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(evaluationDetails
        .getUserId());
    
    // get the competency profile
    CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
        .getCompetencyProfile(evaluationDetails.getCompetencyId());
    
    UserCompetencyEvaluation evalaution = userCompetencyEvaluation
        .getEvaluation(competencyEvaluation.getId());
    Assert.notNull(evalaution, "User competency evaluation not found.");
    
    // validate if the user is the manager user
    if (!user.hasRole(RoleType.CompetencyAdmin)) {
      final UserCompetencyEvaluationScore manager = evalaution.getManager();
      Assert.notNull(manager, "Unauthorized request.");
      manager.checkIfSameUser(getFeedbackUser(user));
    }
    
    BaseUserDTO reviewer = evalaution.getReviewer(evaluationDetails.getEvaluationType(),
        evaluationDetails.getReviewerId());
    
    // get the competency profile and score
    List<CompetencyScoreDTO> competencyScoreList = createCompetencyScoreList(competencyProfile,
        evaluationDetails.getScoreArray());
    
    // adding to the response
    resp.add(Constants.PARAM_COMPETENCY_SCORE_TOTAL, evaluationDetails.getTotalScore());
    resp.add(Constants.PARAM_COMPETENCY_SCORE_LIST, competencyScoreList);
    resp.add(Constants.PARAM_COMPETENCY_REVIEWER, reviewer);
    
    return resp;
  }
  
  /**
   * Get all the evaluation requests for the user.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get request
   */
  public SPResponse getEvaluationRequests(User user) {
    final SPResponse resp = new SPResponse();
    
    CompanyDao company = checkIfEvaluationNotInProgress(user);
    
    CompetencyEvaluationRequest evaluationRequests = getCompetencyEvaluationRequest(user);
    Assert.notNull(evaluationRequests, "User requests not found.");
    
    List<UserEvaluationRequestDTO> evaluationResultList = null;
    final List<UserEvaluationRequest> requestsList = evaluationRequests.getRequestsList();
    if (!CollectionUtils.isEmpty(requestsList)) {
      final CompetencyEvaluation competencyEvaluation = getCompetencyEvaluation(company);
      
      // adding the flag if the peer evaluation
      resp.add(Constants.PARAM_COMPETENCY_EVALUATION_PEER,
          competencyEvaluation.isSupported(EvaluationType.Peer));

      // adding the competency evaluation id
      resp.add(Constants.PARAM_COMPETENCY_EVALUATION_ID, competencyEvaluation.getId());
      
      final Map<String, CompetencyProfileSummaryDTO> competencyProfileMap = 
          new HashMap<String, CompetencyProfileSummaryDTO>();
      
      // adding the requests
      evaluationResultList = requestsList.stream()
          .map(request -> getUserResult(request, competencyEvaluation, competencyProfileMap))
          .collect(Collectors.toList());
      
      // adding the competency profiles to use
      resp.add(Constants.PARAM_COMPETENCY_PROFILE, competencyProfileMap);
    }
    
    // adding the user details
    resp.add(Constants.PARAM_MEMBER, new BaseUserDTO(user));
    
    // adding the user requests
    return resp.add(Constants.PARAM_COMPETENCY_EVALUATION, Optional
        .ofNullable(evaluationResultList).orElse(Collections.emptyList()));
  }
  
  /**
   * Helper method to get the users self evaluations.
   * 
   * @param user
   *          - user
   * @return the response to the get self evaluation
   */
  public SPResponse getPreviousEvaluations(User user) {
    final SPResponse resp = new SPResponse();
    
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
    
    final List<UserCompetencySelfEvaluationDTO> userSelfEvaluations = new ArrayList<UserCompetencySelfEvaluationDTO>();
    userCompetencyEvaluation.getEvaluationsMap().forEach(
        (key, value) -> addSelfEvaluationDTO(key, value, userSelfEvaluations));
    
    return resp.add(Constants.PARAM_COMPETENCY_EVALUATION, userSelfEvaluations);
  }
  
  /**
   * Add the given user competency evaluation to the self evaluations list.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @param userCompetencyEvaluation
   *          - user competency evaluation
   * @param userSelfEvaluations
   *          - the self evaluation list to update
   */
  private void addSelfEvaluationDTO(String competencyEvaluationId,
      UserCompetencyEvaluation userCompetencyEvaluation,
      List<UserCompetencySelfEvaluationDTO> userSelfEvaluations) {
    
    // get the competency evaluation
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(competencyEvaluationId);
    if (competencyEvaluation != null && competencyEvaluation.isCompleted()) {
      // check if the competency evaluation has self evaluation
      if (competencyEvaluation.isSupported(EvaluationType.Self)) {
        // get the competency profile
        CompetencyProfileSummaryDTO competencyProfile = competencyEvaluation
            .getCompetencyProfile(userCompetencyEvaluation.getCompetencyProfileId());
        if (competencyProfile != null) {
          // create a new competency evaluation DTO to store the data
          UserCompetencySelfEvaluationDTO competencyEvaluationDTO = new UserCompetencySelfEvaluationDTO(
              competencyEvaluation, competencyProfile);
          // retrieve the evaluation details and update the evaluation DTO
          Optional
              .ofNullable(userCompetencyEvaluation.getSelf())
              .map(
                  evaluation -> competencyFactory.getCompetencyEvaluationDetails(evaluation
                      .getCompetencyEvaluationScoreDetailsId()))
              .ifPresent(evaluation -> competencyEvaluationDTO.add(evaluation, competencyProfile));
          // adding the evaluation to the list of self evaluations
          userSelfEvaluations.add(competencyEvaluationDTO);
        }
      }
    }
  }
  
  /**
   * Get the user evaluation requests for the given user. If the user is the feedback user then it
   * is the id else if it is the currently logged in user then retrieve the feedback user and then
   * get evaluation request.
   * 
   * @param user
   *          - user
   * @return the evaluation request
   */
  private CompetencyEvaluationRequest getCompetencyEvaluationRequest(User user) {
    return competencyFactory.getCompetencyEvaluationRequest(getFeedbackUserId(user));
  }
  
  private String getFeedbackUserId(User user) {
    return getFeedbackUser(user).getId();
    // String userId = user.getId();
    // if (!(user instanceof FeedbackUser)) {
    // FeedbackUser feedbackUser = userFactory.getFeedbackUser(user.getEmail(),
    // FeatureType.Competency, user.get);
    // Assert.notNull(feedbackUser, "Evaluation requests not found.");
    // userId = feedbackUser.getId();
    // }
    // return userId;
  }
  
  /**
   * Get the user evaluation request DTO for the given evaluation request.
   * 
   * @param evaluationRequest
   *          - evaluation request
   * @param competencyEvaluation
   *          - competency evaluation
   * @param competencyProfileMap
   *          - map to store the competency profile  
   * @return the user evaluation request DTO
   */
  private UserEvaluationRequestDTO getUserResult(UserEvaluationRequest evaluationRequest,
      CompetencyEvaluation competencyEvaluation,
      Map<String, CompetencyProfileSummaryDTO> competencyProfileMap) {
    
    // creating a new evaluation request to send back
    UserEvaluationRequestDTO evaluationRequestDTO = null;
    
    // Retrieving the user
    final String userId = evaluationRequest.getUserId();
    User user = userFactory.getUser(userId);
    
    // getting the user competency evaluation for the given user and competency evaluation 
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(userId);
    UserCompetencyEvaluation evaluation = userCompetencyEvaluation
        .getEvaluation(competencyEvaluation.getId());
    Assert.notNull(evaluation, "User evaluation result not found.");
    
    // adding the data depending on the type of request
    switch (evaluationRequest.getType()) {
    case Manager:
      evaluationRequestDTO = new UserEvaluationRequestDTO(user, evaluation);
      // setting the competency profile id in the request
      final String competencyProfileId = evaluation.getCompetencyProfileId();
      evaluationRequestDTO.setCompetencyProfileId(competencyProfileId);
      
      // adding the competency profile to send back
      competencyProfileMap.computeIfAbsent(competencyProfileId,
          k -> competencyEvaluation.getCompetencyProfile(k));
      break;
    
    case Peer:
      evaluationRequestDTO = new UserEvaluationRequestDTO(user);
      break;
    
    default:
      break;
    }
    
    return evaluationRequestDTO;
  }
  
}
