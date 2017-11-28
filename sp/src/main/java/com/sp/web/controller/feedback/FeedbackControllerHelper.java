package com.sp.web.controller.feedback;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.controller.notifications.FeedbackNotificationProcessor;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.AccountDashboardFeedbackUserDTO;
import com.sp.web.dto.FeedbackMemberDto;
import com.sp.web.dto.FeedbackRequestArchivedDTO;
import com.sp.web.dto.FeedbackRequestDTO;
import com.sp.web.dto.FeedbackUserArchiveDTO;
import com.sp.web.dto.FeedbackUserDTO;
import com.sp.web.dto.UserGoalProgressSummaryDto;
import com.sp.web.exception.InvalidParameterException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.NoFeedbackRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.FeedbackForm;
import com.sp.web.form.ReferencesForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.RequestType;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.TrackingType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SwotType;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.tracking.SP360RequestTracking;
import com.sp.web.mvc.SPResponse;
import com.sp.web.relationship.RelationshipReportManager;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactoryCache;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.DateTimeUtil;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <code>FeedbackCOntrollerHelper</code> is the controller heplper for the feedback tool.
 * 
 * @author pradeep
 */
@Component
public class FeedbackControllerHelper {
  
  /** initializing the logger. */
  private static final Logger LOG = Logger.getLogger(FeedbackControllerHelper.class);
  
  /** Feedback repositry. */
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  /** User Repository. */
  @Autowired
  private UserRepository userRepository;
  
  /** Sp Token factor. */
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private SPGoalFactoryHelper spGoalFactory;
  
  @Autowired
  private SPGoalFactoryCache factoryCache;
  
  @Autowired
  private RelationshipReportManager relationShipReportManager;
  
  @Autowired
  @Qualifier("feedbackNotificationsProcessor")
  private FeedbackNotificationProcessor feedbackNotificationProcessor;
  
  /**
   * Communication gateway to send the email.
   */
  @Autowired
  private ThemeCacheableFactory cacheableFactory;
  
  private String workEnvContKey = "profile.personality.{0}.WorkEnvironmentContribution";
  private String notableCharKey = "profile.personality.{0}.NotableCharacteristics";
  
  @Autowired
  private TrackingRepository trackingRepository;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * <code>getFeedbackTeam</code> method will return the feedback team for the user.
   * 
   * @param user
   *          for which feedback team will be terurned.
   * @return the feedback team.
   */
  public SPResponse getFeedbackTeam(User user, Object[] param) {
    
    /* Get the user email for which growth team is to be returned */
    final SPResponse fbTeamResponse = new SPResponse();
    
    String memberEmail = (String) param[0];
    boolean accountDashboardRequest = (boolean) param[1];
    
    /**
     * check if memberEmail is coming, then return the feedbackTeam for the member EMial.
     */
    user = getGroupMemberUser(memberEmail, user);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Get the feebback team for the user: " + user.getEmail());
    }
    
    List<FeedbackUser> feedbackUserList = userRepository.findFeedbackUsers(user.getId(),
        FeatureType.PrismLens);
    
    if (!feedbackUserList.isEmpty()) {
      if (accountDashboardRequest) {
        fbTeamResponse.add(
            "feedbackTeam",
            feedbackUserList.stream().map(AccountDashboardFeedbackUserDTO::new)
                .collect(Collectors.toList()));
      } else {
        fbTeamResponse.add("feedbackTeam", feedbackUserList.stream().map(fbUsr -> {
          FeedbackUserDTO fbDto = new FeedbackUserDTO(fbUsr);
          if (fbUsr.getUserStatus() == UserStatus.VALID) {
            fbTeamResponse.add("themesPresent", true);
          }
          return fbDto;
        }).collect(Collectors.toList()));
      }
    }
    fbTeamResponse.isSuccess();
    return fbTeamResponse;
  }
  
  /**
   * <code>getAllMembers</code> method will return the all the members present for the user in the
   * same company. In case user is of individual, direct form will be displayed to invite members
   * 
   * @param user
   *          for which members will be returned
   * @return the SPResponse
   */
  public SPResponse getAllForInviteMembers(User user, Object[] param) {
    SPResponse response = new SPResponse();
    
    /*
     * check if user type is individual or business user, checking if customer is belong to a
     * company
     */
    // whether user is
    // individual or business.
    if (Optional.ofNullable(user.getCompanyId()).isPresent()) {
      /**
       * check if memberEmail is coming, then return the feedbackTeam for the member EMial.
       */
      String memberEmail = (String) param[0];
      user = getGroupMemberUser(memberEmail, user);
      
      /* fetch all the members belonging to the company */
      List<FeedbackMemberDto> filteredFeedbackMembers = getAllFeedbackMembers(user);
      
      response.add("feedbackUsers", filteredFeedbackMembers);
      response.add("totalMembers", filteredFeedbackMembers.size());
    } else {
      /*
       * send userType in the response to let the view know, we need to directly open the non member
       * invite form, without heading and search.
       */
      response.add("userType", "individual");
      response.isSuccess();
    }
    return response;
  }
  
  /**
   * Get the feedback status for the given user and provided member id.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the feedback status request
   */
  public SPResponse getFeedbackStatus(User user, Object[] param) {
    final SPResponse response = new SPResponse();
    
    String userId = (String) param[0];
    
    FeedbackUser feedbackUser = feedbackRepository.findFeedbackUser(userId, user.getId(),
        FeatureType.PrismLens);
    
    boolean addRequest = true;
    
    if (feedbackUser != null) {
      FeedbackRequest feedbackRequest = feedbackRepository
          .findFeedbackRequestByFeedbackUserId(feedbackUser.getId());
      if (feedbackRequest == null) {
        feedbackRepository.remove(feedbackUser);
      } else {
        response.add(Constants.PARAM_FEEDBACK_USER, new FeedbackUserDTO(feedbackUser));
        if (feedbackUser.getUserStatus() == UserStatus.VALID) {
          response.add(Constants.PARAM_MESSAGE, MessagesHelper.getMessage("fb.profile.complete",
              feedbackUser.getFirstName(),
              MessagesHelper.formatDate(feedbackUser.getAnalysis().getCreatedOn().toLocalDate())));
        } else {
          response.add(
              Constants.PARAM_MESSAGE,
              MessagesHelper.getMessage("fb.profile.requestPending",
                  MessagesHelper.formatDate(feedbackUser.getCreatedOn())));
        }
        addRequest = false;
      }
    }
    
    if (addRequest) {
      User fbUser = userFactory.getUser(userId);
      if (fbUser != null) {
        response.add(Constants.PARAM_MESSAGE,
            MessagesHelper.getMessage("fb.profile.request", fbUser.getFirstName()));
      }
    }
    return response;
  }
  
  /**
   * sendFeedbackRequest method will send the feedback request to the user.
   * 
   * @param user
   *          logged in user who wants to send the feedback request to the other user.
   * @param params
   *          contains the detail fo the other user for which he wants to send the feedback request.
   * @return SPResposne.
   */
  public SPResponse sendFeedbackRequest(User user, Object[] params) {
    
    LOG.info("Enter send FeedbackRequest method " + (params == null ? null : params.length));
    
    if (params == null || params.length == 0) {
      LOG.error("Params is null or empty, required parameters missing");
      throw new InvalidRequestException(
          MessagesHelper.getMessage(Constants.FB_ERROR_INVALID_PARAMA));
    }
    
    FeedbackForm feedbackForm = (FeedbackForm) params[0];
    final ReferencesForm referencesForm = (ReferencesForm) params[1];
    
    String groupMemberEmail = (String) params[2];
    user = getGroupMemberUser(groupMemberEmail, user);
    Optional.ofNullable(feedbackForm).orElseThrow(
        () -> new InvalidRequestException(MessagesHelper
            .getMessage(Constants.FB_ERROR_INVALID_PARAMA)));
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Feedback Form is " + feedbackForm);
    }
    
    User memberUser = userRepository.findByEmail(referencesForm.getEmail(), user.getCompanyId());
    if (feedbackForm.getRequestType() == RequestType.EXTERNAL) {
      /*
       * check if the user email passed belongs to same company and is present in the system
       */
      
      if (user.getCompanyId() != null) {
        if (memberUser != null) {
          throw new InvalidParameterException(
              MessagesHelper.getMessage("fb.error.same.company.user"));
        }
        
      } else {
        /**
         * for invidual user, check if user is not inviting himself for feedback request.
         */
        if (user.getEmail().equalsIgnoreCase(referencesForm.getEmail())) {
          throw new InvalidParameterException(
              MessagesHelper.getMessage("fb.error.same.user.memberfound"));
        }
      }
    }
    
    /* check the request type */
    FeedbackRequest feedbackRequest = processFeedbackRequest(feedbackForm, referencesForm, user,
        memberUser);
    FeedbackUser fbUser = feedbackRepository.findByIdValidated(feedbackRequest.getFeedbackUserId());
    FeedbackMemberDto feedbackMemberDto = new FeedbackMemberDto(feedbackRequest);
    feedbackMemberDto.load(fbUser);
    feedbackMemberDto.setSelectedMember(true);
    
    SPResponse inviteResponse = new SPResponse();
    inviteResponse.add("inviteCreated", feedbackMemberDto);
    inviteResponse.isSuccess();
    
    SP360RequestTracking requestTracking = new SP360RequestTracking(user.getId(), false,
        TrackingType.REQUESTS);
    requestTracking.setCompanyId(user.getCompanyId());
    trackingRepository.storeTrackingInfomation(requestTracking);
    LOG.info("Feedback user invited succesffully");
    return inviteResponse;
  }
  
  /**
   * <code>processFeedbackRequest</code> method will createa new feedback request for user
   * 
   * @param feedbackForm
   *          containing the information of the user to which feedback is to be given
   * @param user
   *          logged inyser.
   * @param memberUser
   *          the internal member user
   */
  private FeedbackRequest processFeedbackRequest(FeedbackForm feedbackForm,
      ReferencesForm referencesForm, User user, User memberUser) {
    
    RequestType requestType = feedbackForm.getRequestType();
    
    /*
     * Retrevie the user from the repository as user is presnet in the same company.
     */
    /* Save the feedback User in the repository */
    /* Create a feedback request */
    FeedbackRequest feedbackRequest = new FeedbackRequest();
    FeedbackUser feedbackUser;
    if (requestType == RequestType.INTERNAL) {
      feedbackUser = new FeedbackUser();
      feedbackUser.updateFrom(memberUser);
      feedbackUser.setUserStatus(UserStatus.ASSESSMENT_PENDING);
    } else {
      feedbackUser = FeedbackUser.newExternalUser(referencesForm);
      feedbackUser.setCompanyId(user.getCompanyId());
    }
    feedbackRequest.setRequestStatus(RequestStatus.NOT_INITIATED);
    feedbackRequest.setRequestType(requestType);
    /* Create a new feedback user */
    feedbackUser.setFeedbackFor(user.getId());
    try {
      feedbackRepository.addFeedbackUser(feedbackUser);
    } catch (Exception e) {
      LOG.warn("Error creating the feedback user.", e);
      throw new InvalidRequestException(
          MessagesHelper.getMessage("fb.error.external.duplicate.request"), e);
    }
    
    LOG.debug("Feedback user creatd is" + feedbackUser);
    
    feedbackRequest.setEndDate(DateTimeUtil.getLocalDate(feedbackForm.getEndDate()));
    feedbackRequest.setStartDate(LocalDate.now());
    feedbackRequest.setRequestedById(user.getId());
    
    feedbackRequest.setFeedbackUserId(feedbackUser.getId());
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("feedback request created is " + feedbackRequest);
    }
    
    String tokenUrl = null;
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_FEEDBACK_USERID, feedbackUser.getId());
    paramsMap.put(Constants.PARAM_LOCALE,
        requestType == RequestType.INTERNAL ? memberUser.getUserLocale() : user.getUserLocale());
    Token token = tokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
        TokenProcessorType.FEEDBACK_INVITE);
    
    tokenUrl = token.getTokenUrl();
    feedbackRequest.setTokenUrl(tokenUrl);
    
    /*
     * store the fb request
     */
    feedbackRepository.createFeedbackRequest(feedbackRequest);
    
    // send the invite message
    Map<String, Object> emailParamsMap = new HashMap<String, Object>();
    emailParamsMap.put(Constants.PARAM_TOKEN, tokenUrl);
    if (StringUtils.isNotEmpty(feedbackForm.getComment())) {
      emailParamsMap.put(Constants.PARAM_COMMENT, feedbackForm.getComment());
    }
    
    
    CompanyDao company = cacheableFactory.getCompanyByIdForTheme(user.getCompanyId());
    
    emailParamsMap.put(Constants.PARAM_COMPANY, company);
    if (requestType == RequestType.INTERNAL) {
      emailParamsMap.putAll(MessagesHelper.getGenderText(memberUser));
      emailParamsMap.put(Constants.PARAM_END_DATE,
          MessagesHelper.formatDate(feedbackRequest.getEndDate(),memberUser.getLocale()));
      emailParamsMap.put(Constants.PARAM_NOTIFICATION_TYPE, NotificationType.FeedbackInviteMember);
      emailParamsMap.put(
          Constants.PARAM_SUBJECT,
          MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
              + NotificationType.FeedbackInviteMember, memberUser.getLocale(),
              user.getFirstNameOrEmail(), user.getLastName()));
      feedbackNotificationProcessor.process(NotificationType.FeedbackInviteMember, user,
          feedbackUser, emailParamsMap);
      todoFactory.addTodo(memberUser,
          TodoRequest.newPrismLensRequest(user, feedbackRequest, feedbackUser));
    } else {
      emailParamsMap.putAll(MessagesHelper.getGenderText(user));
      emailParamsMap.put(Constants.PARAM_END_DATE,
          MessagesHelper.formatDate(feedbackRequest.getEndDate(),user.getLocale()));
      emailParamsMap
          .put(Constants.PARAM_NOTIFICATION_TYPE, NotificationType.FeedbackInviteExternal);
      emailParamsMap.put(
          Constants.PARAM_SUBJECT,
          MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
              + NotificationType.FeedbackInviteExternal, user.getLocale(),
              user.getFirstNameOrEmail(), user.getLastName()));
      feedbackNotificationProcessor.process(NotificationType.FeedbackInviteExternal, user,
          feedbackUser, emailParamsMap, false);
    }
    
    return feedbackRequest;
  }
  
  /**
   * <code>getFeedbackDetail</code> method will return the feedback detal.
   * 
   * @param user
   *          logged in user.
   * @param param
   *          logged in user growth requested it.
   * @return the feedback detail list.
   */
  @SuppressWarnings("unchecked")
  public SPResponse getFeedbackDetail(User user, Object[] param) {
    SPResponse response = new SPResponse();
    /* check if feedback request is available or not */
    
    String feedbackUserId = (String) param[0];
    Assert.hasText(feedbackUserId, MessagesHelper.getMessage(Constants.FB_ERROR_INVALID_PARAMA));
    String groupMemberEmail = (String) param[1];
    user = getGroupMemberUser(groupMemberEmail, user);
    /* fetch the feedback request for the user */
    
    FeedbackRequest feedbackRequest = feedbackRepository
        .findFeedbackRequestByFeedbackUserId(feedbackUserId);
    
    if (feedbackRequest == null) {
      FeedbackUser feedbackUser = feedbackRepository.findByIdValidated(feedbackUserId);
      if (feedbackUser != null) {
        feedbackRepository.remove(feedbackUser);
      }
      throw new NoFeedbackRequestException(
          MessagesHelper.getMessage(Constants.FB_ERROR_NO_FR_PRESENT));
    }
    
    FeedbackUser feedbackUser = feedbackRepository.findByIdValidated(feedbackUserId);
    /*
     * check the status of the feedback user, whether he has given the feedback or not
     */
    if (feedbackUser.getUserStatus() == UserStatus.VALID) {
      /*
       * add the feedback goals to the user goals as a fallback if user has no goals present
       * 
       * Map<GoalCategory, List<UserGoalProgressSummaryDto>> mapUserGoalProgress = spGoalFactory
       * .mapUserGoalProgressToCategories(populateFeedbackGoals(feedbackUser, user));
       * mapUserGoalProgress.remove(GoalCategory.Business);
       * mapUserGoalProgress.remove(GoalCategory.Individual);
       * response.add("feedbackGoals",mapUserGoalProgress);
       */
      
      // Goals are no longer will be shown, now SWOT analysis will eb shown to
      // the user */
      response.isSuccess();
      Map<String, Object> responseMap = response.getSuccess();
      addFeedbackUserSwotData(user, feedbackUser, responseMap, user.getLocale());
      addFeedbackUserProfileData(user, feedbackUser, responseMap, user.getLocale());
    } else {
      response.add("feedbackNotGiven", MessagesHelper.getMessage(Constants.FB_FEEDBACK_NOT_GIVEN));
      response.add(
          "feedbackLastDayMessage",
          MessagesHelper.getMessage(Constants.FB_FEEDBACK_LAST_DAY,
              MessagesHelper.formatDate(feedbackRequest.getEndDate())));
    }
    response.add(Constants.PARAM_FEEDBACK_USER, new FeedbackUserDTO(feedbackUser));
    return response;
  }
  
  /**
   * Add the SWOT analysis data for the given user to the response.
   * 
   * @param user
   *          - user for whom feedback has been provided
   * @param feedbackUser
   *          - feedback user
   * @param responseMap
   *          - the response object
   */
  private void addFeedbackUserSwotData(User user, FeedbackUser feedbackUser,
      Map<String, Object> responseMap, Locale locale) {
    PersonalityPracticeArea personalityPracticeArea = factoryCache.getPersonalityPracticeArea(
        feedbackUser.getAnalysis().getPersonality().get(RangeType.Primary).getPersonalityType(),
        locale.toString());
    Map<SwotType, List<String>> swotProfileMap = personalityPracticeArea.getSwotProfileMap();
    responseMap.put("swotanalysis", swotProfileMap);
  }
  
  /**
   * Add the feedback profile data to the response.
   * 
   * @param user
   *          - user for whom feedback has been provided
   * @param feedbackUser
   *          - feedback user
   * @param responseMap
   *          - the response object
   * @param locale
   */
  private void addFeedbackUserProfileData(User user, User feedbackUser,
      Map<String, Object> responseMap, Locale locale) {
    
    HashMap<RangeType, PersonalityBeanResponse> personality = feedbackUser.getAnalysis()
        .getPersonality();
    
    PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
    String primaryPersonalityType = personalityBeanResponse.getPersonalityType().toString();
    String profileStatement = MessagesHelper.genderNormalizeFromKey("feedback."
        + primaryPersonalityType + ".title", user, locale);
    responseMap.put("profileStatment", profileStatement);
    
    responseMap.put("notablechar.text", MessagesHelper.genderNormalizeFromKey(
        "profile.personality.NotableCharacteristics", user, locale));
    String messageNotableKey = MessageFormat.format(notableCharKey, primaryPersonalityType);
    responseMap.put("notablechar.desc",
        MessagesHelper.genderNormalizeFromKey(messageNotableKey, user, locale));
    responseMap.put("workEnviornment.text", MessagesHelper.genderNormalizeFromKey(
        "profile.personality.WorkEnvironmentContribution", user, locale));
    
    String workEnvKey = MessageFormat.format(workEnvContKey, primaryPersonalityType);
    
    responseMap.put("workEnviornment.desc",
        MessagesHelper.genderNormalizeFromKey(workEnvKey, user, locale));
  }
  
  /**
   * <code>archiveFeedback</code> user will archive the user.
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the feedback request id to be archived.
   * @return the response to the archive request
   */
  public SPResponse archiveFeedback(User user, Object[] param) {
    
    final SPResponse response = new SPResponse();
    String feedbackUserId = (String) param[0];
    
    /* Archiving the feedbackRequest */
    LOG.info("Archiving the feedback " + feedbackUserId);
    FeedbackRequest feedbackRequest = feedbackRepository
        .findFeedbackRequestByFeedbackUserId(feedbackUserId);
    Optional.ofNullable(feedbackRequest).orElseThrow(
        () -> new SPException("No Feedback Reuqest present for the user"));
    FeedbackArchiveRequest archiveFeedbackRequest = feedbackRepository.archiveFeedbackRequest(
        feedbackRequest, user);
    /** Track the request for the sp360. */
    if (StringUtils.isNotEmpty(user.getCompanyId())) {
      if (archiveFeedbackRequest.getRequestStatus() == RequestStatus.DEACTIVE) {
        SP360RequestTracking requestTracking = new SP360RequestTracking(user.getId(), false,
            TrackingType.DEACTIVATED);
        requestTracking.setCompanyId(user.getCompanyId());
        trackingRepository.storeTrackingInfomation(requestTracking);
      }
      
      if (feedbackRequest.getRequestType() == RequestType.INTERNAL) {
        String feedbackUserMemberId = archiveFeedbackRequest.getFeedbackUserMemberId();
        if (feedbackUserMemberId != null) {
          User member = userFactory.getUser(feedbackUserMemberId);
          if (member != null) {
            todoFactory.remove(member, feedbackUserId);
          }
        }
      }
    }
    
    response.isSuccess();
    return response;
  }
  
  /**
   * <code>getAllArchivedFeedbacks</code> method will return all the archived list.
   * 
   * @param user
   *          logged in user
   * @return the all archived feedback list.
   */
  public SPResponse getAllArchivedFeedbacks(User user) {
    LOG.info("Feedback rqeuest for user" + user.getEmail());
    
    final SPResponse response = new SPResponse();
    
    String requestedById = user.getId();
    
    /*
     * fetch all teh feedback request where feedback request contains the user id
     */
    
    @SuppressWarnings("unchecked")
    List<FeedbackArchiveRequest> allArchivedFeedbackRequests = (List<FeedbackArchiveRequest>) feedbackRepository
        .getAllArchivedFeedbackRequests(requestedById);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("All feedback request recived by user " + allArchivedFeedbackRequests);
    }
    
    Set<String> filterStatus = new HashSet<String>();
    List<FeedbackRequestDTO> feedbackRequestsDto = allArchivedFeedbackRequests
        .stream()
        .map(
            fbArchvedRequest -> {
              FeedbackUserArchive fbForUser = userRepository
                  .findArchivedFeedbackUser(fbArchvedRequest.getFeedbackUserId());
              if (fbForUser == null) {
                LOG.error("INvlaid feedback request, as no feedback archived present for the feedback archived request"
                    + fbArchvedRequest.getId()
                    + ", Feedback Archived User: "
                    + fbArchvedRequest.getFeedbackUserId());
                return null;
              }
              FeedbackRequestArchivedDTO feedbackRequestDto = new FeedbackRequestArchivedDTO(
                  fbArchvedRequest);
              if (fbForUser.getUserStatus() == UserStatus.VALID) {
                response.add("themesPresent", true);
              }
              filterStatus.add(feedbackRequestDto.getRequestStatus());
              feedbackRequestDto.setFeedbackUser(new FeedbackUserDTO(fbForUser));
              return feedbackRequestDto;
            }).filter(frDto -> frDto != null).collect(Collectors.toList());
    
    response.add("feedbackRequest", feedbackRequestsDto);
    response.add("feedbackRequestFilter", filterStatus);
    
    LOG.debug("Feedback Archive Request are " + feedbackRequestsDto);
    return response;
  }
  
  /**
   * Helper method to get the details for the feedback request.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the resposne to the get archive request
   */
  @SuppressWarnings("unchecked")
  public SPResponse getArchiveRequestDetails(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    
    // get the feedback request id
    String feedbackUserArchiveId = (String) params[0];
    
    // get the feedback user
    FeedbackUserArchive archiveUser = userRepository
        .findArchivedFeedbackUser(feedbackUserArchiveId);
    
    // check if archive user exists
    Assert.notNull(archiveUser, "Archive user:" + feedbackUserArchiveId + ": not found !!!");
    
    if (archiveUser.getUserStatus() == UserStatus.VALID) {
      // create the archive user
      resp.add(Constants.PARAM_FEEDBACK_USER, new FeedbackUserArchiveDTO(archiveUser));
      // List<UserGoalProgressSummaryDto> requestProgressSummary =
      // populateFeedbackGoals(archiveUser, user);
      // List<UserGoalProgressSummaryDto> finalGoalSumary =
      // requestProgressSummary
      // .stream()
      // .filter(
      // ugp -> ugp.getCategory() != GoalCategory.Business
      // && ugp.getCategory() !=
      // GoalCategory.Individual).collect(Collectors.toList());
      // resp.add("feedbackGoals", finalGoalSumary);
      // addFeedbackUserProfileData(user, archiveUser, resp);
      //
      // Goals are no longer will be shown, now SWOT analysis will eb shown to
      // the user */
      Map<String, Object> responseMap = resp.getSuccess();
      addFeedbackUserSwotData(user, archiveUser, responseMap, user.getLocale());
      addFeedbackUserProfileData(user, archiveUser, responseMap, user.getLocale());
      resp.isSuccess();
      
    } else {
      throw new InvalidRequestException("User assessment incomplete !!!");
    }
    return resp;
  }
  
  /**
   * <code>getAllFeedbackMembers</code> method will return all the feeback users that can be present
   * for the user in the company.
   * 
   * @param companyName
   *          name of the company
   * @return the users
   */
  private List<FeedbackMemberDto> getAllFeedbackMembers(User user) {
    // get all the users from the company
    List<User> findUsers = userRepository.findUsers("companyId", user.getCompanyId());
    // removing the self
    findUsers.remove(user);
    // get all the users for whom feedback request has been sent
    List<FeedbackUser> feedbackUserList = userRepository.findFeedbackUsers(user.getId(),
        FeatureType.PrismLens);
    // create the final DTO list and send back
    return findUsers.stream()
        .filter(u -> !feedbackUserList.contains(u) && u.getUserStatus() == UserStatus.VALID)
        .map(FeedbackMemberDto::new).collect(Collectors.toList());
  }
  
  private User getGroupMemberUser(String memberEmail, User user) {
    User finalUser = user;
    if (StringUtils.isNotBlank(memberEmail)) {
      finalUser = userRepository.findByEmail(memberEmail, user.getCompanyId());
    }
    return Optional.ofNullable(finalUser).orElseThrow(
        () -> new NoFeedbackRequestException("Invalid Request for finding the user "));
  }
  
  /**
   * deleteFeedbackRequest will delete the feebdack request.
   * 
   * @param user
   *          logged in user
   * @param params
   *          contains the request id.
   * @return the response.
   */
  public SPResponse deleteFeedbackRequest(User user, Object[] params) {
    
    String feedbackUserId = (String) params[0];
    
    Assert.hasText(feedbackUserId, "No feedback user present");
    
    FeedbackRequest feedbackRequest = feedbackRepository
        .findFeedbackRequestByFeedbackUserId(feedbackUserId);
    
    Assert.notNull(feedbackRequest, "Invalid request id");
    
    /*
     * check if request is for internal or extenral, in case of internal we have to remove the
     * request from todo.
     */
    FeedbackUser feedbackUser = feedbackRepository.findByIdValidated(feedbackRequest
        .getFeedbackUserId());
    if (feedbackRequest.getRequestType() == RequestType.INTERNAL) {
      /* get the user */
      if (feedbackUser.getUserStatus() != UserStatus.VALID) {
        User feedbackMember = userRepository.findByEmail(feedbackUser.getEmail());
        todoFactory.remove(feedbackMember, feedbackRequest.getFeedbackUserId());
      }
    }
    feedbackRepository.remove(feedbackUser);
    feedbackRepository.removeFeedbackRequest(feedbackRequest);
    
    return new SPResponse().isSuccess();
    
  }
  
  /**
   * Get the lens response for the given user and feedback user.
   * 
   * @param user
   *          - user
   * @param feedbackUser
   *          - feedback user
   * @param locale
   * @return the map containing the lens response
   */
  public Map<String, Object> getLensResponse(User user, FeedbackUser feedbackUser, Locale locale) {
    Map<String, Object> lensResponse = new HashMap<String, Object>();
    addFeedbackUserSwotData(user, feedbackUser, lensResponse, locale);
    addFeedbackUserProfileData(user, feedbackUser, lensResponse, locale);
    return lensResponse;
  }
}
