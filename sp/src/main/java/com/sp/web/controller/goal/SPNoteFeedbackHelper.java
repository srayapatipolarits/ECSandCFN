package com.sp.web.controller.goal;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.PracticeFeedbackRequestDTO;
import com.sp.web.dto.SPNoteDTO;
import com.sp.web.dto.SPNoteFeedbackPracticeAreaDTO;
import com.sp.web.dto.blueprint.BlueprintDTO;
import com.sp.web.dto.goal.NotesAndFeedbackListingDTO;
import com.sp.web.exception.DashboardRedirectException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.UserNotFoundException;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Gender;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.RoleType;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.SPNote;
import com.sp.web.model.goal.SPNoteFeedbackType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.goal.MongoGoalsRepository;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author SPNoteFeedbackHelper
 * 
 *         The helper class for spnotefeedback controller.
 */
@Component
public class SPNoteFeedbackHelper {
  
  /** initializing the logger. */
  private static final Logger LOG = Logger.getLogger(SPNoteFeedbackHelper.class);
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private MongoGoalsRepository goalRepository;
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  private SPNoteFeedbackRepository spNoteFeedbackRepository;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  TokenRepository tokenRepository;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  @Autowired
  @Qualifier("notificationLog")
  private LogGateway notificationGateway;
  
  @Autowired
  private ThemeCacheableFactory themeCacheableFactory;
  
  /**
   * Helper method to add a Note for the goal/dev strategy.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the add comment
   */
  public SPResponse addNote(User user, Object[] param) {
    
    String content = (String) param[0];
    String goalId = (String) param[1];
    String devStrategyId = (String) param[2];
    
    SPGoal goal = goalRepository.findById(goalId);
    if (goal == null) {
      throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    }
    
    SPNote spnote = new SPNote();
    
    spnote.setContent(content);
    spnote.setGoalId(goalId);
    spnote.setCompanyId(user.getCompanyId());
    spnote.setDevStrategyId(devStrategyId);
    spnote.setCreatedOn(LocalDateTime.now());
    spnote.setUserId(user.getId());
    spnote.setType(SPNoteFeedbackType.NOTE);
    
    spNoteFeedbackRepository.addNote(spnote);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to remove the comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse deleteNote(User user, Object[] params) {
    
    String noteId = (String) params[0];
    
    spNoteFeedbackRepository.deleteNote(noteId);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to delete all the notes or feedbacks for the given practice area.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the delete all request
   */
  public SPResponse deleteAll(User user, Object[] params) {
    
    String goalId = (String) params[0];
    boolean deleteNote = (boolean) params[1];
    boolean deleteFeedback = (boolean) params[2];
    
    // check if at least one flag is turned on
    Assert.isTrue(deleteNote || deleteFeedback,
        "No action taken as both delete note and delete feedback flags were false.");
    
    // check if the goal is valid
    SPGoal goal = goalsFactory.getGoal(goalId);
    Assert.notNull(goal, "Gaol not found.");
    
    if (deleteNote) {
      spNoteFeedbackRepository.deleteAllNote(goalId);
    }
    
    if (deleteFeedback) {
      spNoteFeedbackRepository.deleteAllFeedback(goalId);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to remove the comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse getNoteDetail(User user, Object[] params) {
    
    String noteId = (String) params[0];
    
    SPResponse response = new SPResponse();
    
    SPNote spnote = spNoteFeedbackRepository.findNoteById(noteId);
    SPNoteDTO spNoteDTO = new SPNoteDTO(spnote);
    
    LOG.debug("Returning all notes for the user" + user.getEmail() + ", Notes " + spnote);
    
    response.add("spNoteDetail", spNoteDTO);
    
    return response;
  }
  
  /**
   * Helper method to get the feedback details.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse getFeedbackDetail(User user, Object[] params) {
    
    String feedbackId = (String) params[0];
    
    SPResponse response = new SPResponse();
    
    PracticeFeedback feedback = spNoteFeedbackRepository.findFeedbackById(feedbackId);
    
    PracticeFeedbackRequestDTO spNoteDTO = new PracticeFeedbackRequestDTO(feedback);
    
    LOG.debug("Returning feedback detail for the user" + user.getEmail() + ", Feedback "
        + feedback.getContent());
    
    response.add("spFeedbackDetail", spNoteDTO);
    
    return response;
  }
  
  /**
   * The helper method to update the user comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the update request
   */
  public SPResponse updateNote(User user, Object[] params) {
    
    String noteId = (String) params[0];
    String desc = (String) params[1];
    
    spNoteFeedbackRepository.updateNote(noteId, desc);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to add a Note for the goal/dev strategy.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the add comment
   */
  @SuppressWarnings("unchecked")
  public SPResponse createRequestFeedback(User user, Object[] param) {
    
    String comment = (String) param[0];
    String goalId = (String) param[1];
    String devStrategyId = (String) param[2];
    final List<String> feedbackUserEmailList = (List<String>) param[3];
    
    // creating the base information to copy from
    PracticeFeedback baseFeedback = new PracticeFeedback();
    if (!StringUtils.isEmpty(comment)) {
      baseFeedback.setComment(comment);
    }
    baseFeedback.setDevStrategyId(devStrategyId);
    baseFeedback.setGoalId(goalId);
    baseFeedback.setCreatedOn(LocalDateTime.now());
    baseFeedback.setUserId(user.getId());
    baseFeedback.setCompanyId(user.getCompanyId());
    baseFeedback.setFeedbackStatus(RequestStatus.NOT_INITIATED);
    baseFeedback.setType(SPNoteFeedbackType.FEEDBACK);
    
    // parameters for the email
    Map<String, Object> baseParamsMap = new HashMap<String, Object>();
    
    // validate the goal id
    // goal and dev strategy are needed in email
    DevelopmentStrategy devStrategy = null;
    SPGoal goal = goalsFactory.getGoal(goalId);
    if (goal == null) {
      LOG.warn("Goal not found for goal id :" + goalId);
      throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    }
    
    if (!StringUtils.isBlank(devStrategyId)) {
      try {
        devStrategy = goalsFactory.getDevelopmentStrategyById(goalId, devStrategyId,
            user.getUserLocale());
        if (devStrategy == null) {
          throw new InvalidRequestException("Development strategy not found.");
        }
      } catch (Exception e) {
        LOG.warn("Error getting the development strategy.", e);
        throw new InvalidRequestException("Development strategy not found.");
      }
    }
    
    /* removed the reduannt null, chekc, this code cannot be null */
    baseParamsMap.put("goalName", goal.getName());
    baseParamsMap.put("goalDesc", goal.getDescription());
    baseParamsMap.put("goal", goal);
    
    if (devStrategy != null) {
      baseParamsMap.put("devStrategyDesc", devStrategy.getDsDescription());
      baseParamsMap.put("devStrategy", devStrategy);
      if (devStrategy.getDsDescription().isEmpty()) {
        devStrategy.setDsDescription(null);
      }
    }
    addGender(user, baseParamsMap);
    
    // add feedback request for each email id
    for (String feedbackUserEmail : feedbackUserEmailList) {
      
      /*
       * skip the current user, in case user ernter him self as the addres for providing the
       * feebdakc.
       */
      if (user.getEmail().equalsIgnoreCase(feedbackUserEmail)) {
        continue;
      }
      PracticeFeedback feedback = new PracticeFeedback(baseFeedback);
      feedback.setFeedbackUserEmail(feedbackUserEmail);
      
      // copying the base
      Map<String, Object> paramsMap = new HashMap<String, Object>();
      baseParamsMap.forEach(paramsMap::put);
      
      paramsMap.put("feedbackUserEmail", feedbackUserEmail);
      paramsMap.put("feedbackComment", feedback.getComment());
      
      // create the token and get the token url. These param will be used in Token processor and
      // passed to redirect page.
      Token token = spTokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
          TokenProcessorType.REQUEST_FEEDBACK);
      feedback.setTokenUrl(token.getTokenUrl());
      feedback.setTokenId(token.getTokenId());
      
      // internal & external users. If user found, set feedback user and feedback id in Feedback
      // object.
      // If not found/external, create a new user object and set it in Feedback object
      FeedbackUser feedbackUser = new FeedbackUser();
      feedbackUser.addRole(RoleType.FeedbackUser);
      feedbackUser.setFeedbackFor(user.getId());
      feedbackUser.setFeatureType(FeatureType.NotesFeedback);
      User memberUser = userRepository.findByEmail(feedbackUserEmail);
      if (memberUser != null) {
        feedbackUser.updateFrom(memberUser);
      } else {
        feedbackUser.setEmail(feedbackUserEmail);
        feedbackUser.setType(UserType.External);
      }
      
      // feedbackUser.setTokenUrl(token.getTokenUrl()); //URL needed for email
      feedbackRepository.addFeedbackUser(feedbackUser);
      feedback.setFeedbackUserId(feedbackUser.getId());
      spNoteFeedbackRepository.createRequestFeedback(feedback);
      
      paramsMap.put("feedbackId", feedback.getId());
      tokenRepository.updateToken(token);
      
      // param needed in email template. If variable is blank/empty we need to set it to null
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("feedback", feedback);
      CompanyDao company = themeCacheableFactory.getCompanyByIdForTheme(user.getCompanyId());
      params.put(Constants.PARAM_COMPANY, company);
      baseParamsMap.forEach(params::put);
      
      if (feedbackUser.getType() == UserType.Member && memberUser != null) {
        BaseUserDTO userDto = new BaseUserDTO(user);
        String paramsMessage = MessagesHelper.getMessage(
            "log.PracticeFeedback.RequestFeedback.message", userDto.getName(), userDto.getEmail(),
            feedback.getId());
        params.put(Constants.PARAM_MESSAGE, paramsMessage);
        Locale locale = feedbackUser.getLocale();
        // adding token id subject. It is used when user replies to feedback thru email
        String subject;
        if (goal.isBluePrint()) {
          subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + "Blueprint."
              + NotificationType.RequestFeedback, locale, user.getFirstName(), user.getLastName());
        } else {
          subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
              + NotificationType.RequestFeedback, locale, user.getFirstName(), user.getLastName());
        }
        
        subject = subject + " [" + token.getTokenId() + "]";
        params.put(Constants.PARAM_SUBJECT, subject);
        
        notificationsProcessor.process(NotificationType.RequestFeedback, user, memberUser, params,
            false);
      } else {
        String subject;
        if (goal.isBluePrint()) {
          subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + "Blueprint."
              + NotificationType.RequestFeedbackExternal, user.getFirstName(), user.getLastName());
        } else {
          subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
              + NotificationType.RequestFeedbackExternal, user.getFirstName(), user.getLastName());
        }
        subject = subject + " [" + token.getTokenId() + "]";
        params.put(Constants.PARAM_SUBJECT, subject);
        
        notificationsProcessor.process(NotificationType.RequestFeedbackExternal, user,
            feedbackUser, params, false);
      }
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * The helper method to update the user comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the update request
   */
  public SPResponse updateRequestFeedbackStatus(User user, Object[] params) {
    
    String feedbackId = (String) params[0];
    RequestStatus feedbackStatus = (RequestStatus) params[1];
    String feedbackResponse = (String) params[2];
    PracticeFeedback feedback = spNoteFeedbackRepository.findFeedbackById(feedbackId);
    if (!StringUtils.isBlank(feedbackResponse)) {
      feedback.setFeedbackResponse(feedbackResponse);
    }
    if (feedback == null) {
      throw new InvalidRequestException("Feedback not found.");
    }
    
    // updating the status with the one provided
    feedback.setFeedbackStatus(feedbackStatus);
    spNoteFeedbackRepository.updateRequestFeedback(feedback);
    
    if (feedbackStatus == RequestStatus.DECLINED) {
      String feedbackForUserId = feedback.getUserId();
      User feedbackFor = userRepository.findUserById(feedbackForUserId);
      UserType userTye = UserType.Member;
      if (user instanceof FeedbackUser) {
        userTye = ((FeedbackUser) user).getType();
      }
      BaseUserDTO userDto = new BaseUserDTO(user);
      String message;
      if (userTye == UserType.External) {
        message = MessagesHelper.getMessage("log.PracticeFeedback.FeedbackDeclined.message",
            userDto.getEmail(), userDto.getEmail(), feedback.getId());
      } else {
        message = MessagesHelper.getMessage("log.PracticeFeedback.FeedbackDeclined.message",
            userDto.getName(), userDto.getEmail(), feedback.getId());
      }
      
      LogRequest logRequest = new LogRequest(LogActionType.FeedbackDeclined, feedbackFor, user);
      logRequest.setDoMessagesOverride(true);
      logRequest.addParam(Constants.PARAM_MESSAGE, message);
      notificationGateway.logNotification(logRequest);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * The helper method to update the user comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the update request
   */
  public SPResponse deleteRequestFeedback(User user, Object[] params) {
    
    String feedbackId = (String) params[0];
    
    spNoteFeedbackRepository.deleteRequestFeedback(feedbackId);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method gets invoked when internal or external user give feedback response.
   * 
   * @param feedbackUser
   *          - user
   * @param param
   *          - params
   * @return the response to the add comment
   */
  public SPResponse giveRequestFeedback(User feedbackUser, Object[] param) {
    
    String feedbackId = (String) param[0];
    String feedbackResponse = (String) param[1];
    
    PracticeFeedback feedback = spNoteFeedbackRepository.giveRequestFeedback(feedbackId,
        feedbackResponse);
    
    // invalidate token once feedback response is given
    // goal and dev strategy are needed in email and notifications
    
    final String goalId = feedback.getGoalId();
    DevelopmentStrategy devStrategy = null;
    String devStrategyId = feedback.getDevStrategyId();
    if (!StringUtils.isBlank(devStrategyId)) {
      devStrategy = goalsFactory.getDevelopmentStrategyById(goalId, devStrategyId,
          feedbackUser.getUserLocale());
    }
    
    // Get id of person who requested the feedback.
    User memberUser = userRepository.findUserById(feedback.getUserId());
    
    if (memberUser == null) {
      throw new UserNotFoundException("User not found.");
    }
    
    SPGoal goal = goalsFactory.getGoal(goalId);
    if (goal == null) {
      throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    }
    
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("id", feedback.getId());
    params.put("goal", goal);
    params.put("devStrategy", devStrategy);
    params.put("feedback", feedback);
    
    addGender(feedbackUser, params);
    
    UserType userType = UserType.Member;
    if (feedbackUser instanceof FeedbackUser) {
      userType = ((FeedbackUser) feedbackUser).getType();
    }
    
    // checking whether it is external or internal. We cannot use feedback user type because this
    // method is invoked by external and internal both.
    
    BaseUserDTO feedbackUserDTO = new BaseUserDTO(feedbackUser);
    
    if (userType == UserType.Member) {
      String paramsMessage = MessagesHelper.getMessage(
          "log.PracticeFeedback.SubmitRequestFeedback.message", feedbackUserDTO.getName(),
          memberUser.getEmail(), feedback.getId());
      params.put(Constants.PARAM_MESSAGE, paramsMessage);
      
      String subject;
      if (goal.isBluePrint()) {
        subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + "Blueprint."
            + NotificationType.SubmitRequestFeedback, memberUser.getLocale());
      } else {
        subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
            + NotificationType.SubmitRequestFeedback, memberUser.getLocale(),
            feedbackUser.getFirstName(), feedbackUser.getLastName());
      }
      
      params.put(Constants.PARAM_SUBJECT, subject);
      
      notificationsProcessor.process(NotificationType.SubmitRequestFeedback, feedbackUser,
          memberUser, params, false);
    } else {
      /* using emailin case user is an external user */
      String paramsMessage = MessagesHelper.getMessage(
          "log.PracticeFeedback.SubmitRequestFeedback.message", feedbackUserDTO.getEmail(),
          memberUser.getEmail(), feedback.getId());
      params.put(Constants.PARAM_MESSAGE, paramsMessage);
      // this will get executed for external user and if successful, it will display feedback
      // thank you jsp
      
      String subject;
      if (goal.isBluePrint()) {
        subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + "Blueprint."
            + NotificationType.SubmitRequestFeedbackExternal);
      } else {
        subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
            + NotificationType.SubmitRequestFeedbackExternal, feedbackUserDTO.getEmail());
      }
      
      params.put(Constants.PARAM_SUBJECT, subject);
      
      notificationsProcessor.process(NotificationType.SubmitRequestFeedbackExternal, feedbackUser,
          memberUser, params, false);
    }
    
    String name = StringUtils.isBlank(feedbackUser.getFirstName()) ? "" : feedbackUser
        .getFirstName() + " " + feedbackUser.getLastName();
    StringBuilder heading1Builder = new StringBuilder(name);
    String key;
    String heading2 = "";
    String heading1 = null;
    switch (goal.getCategory()) {
    case Blueprint:
      key = "blueprint.feedback.thankyou.heading1";
      heading1 = MessagesHelper.genderNormalizeFromKey(key, memberUser);
      break;
    default:
      key = StringUtils.isBlank(feedbackUser.getFirstName()) ? "notesAndFeedback.feedback.thankyou.ext.heading1"
          : "notesAndFeedback.feedback.thankyou.heading1";
      heading1 = heading1Builder.append(MessagesHelper.genderNormalizeFromKey(key, memberUser))
          .append(goal.getName()).append(".").toString();
      heading2 = MessagesHelper.genderNormalizeFromKey(
          "notesAndFeedback.feedback.thankyou.heading2", memberUser).concat(
          " " + goal.getName() + ".");
      break;
    }
    
    // PracticeFeedbackRequestDTO feedbackRequestDTO = new PracticeFeedbackRequestDTO(feedback,
    // user, goal, devStrategy);
    //
    //
    // feedbackRequestDTO.setUser(new BaseUserDTO(feedbackUser));
    return new SPResponse().add("heading1", heading1).add("heading2", heading2);
    
  }
  
  /**
   * Get the blueprint for the given feedback users requester.
   * 
   * @param user
   *          - feedback user
   * @return the blueprint if exists
   */
  public SPResponse getBlueprintDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String feedbackId = (String) params[0];
    Assert.hasText(feedbackId, "Feedback id is required.");
    
    PracticeFeedback feedback = spNoteFeedbackRepository.findFeedbackById(feedbackId);
    User userFor = userRepository.findUserById(feedback.getUserId());
    Assert.notNull(userFor, "User not found.");
    Blueprint blueprint = goalsFactory.getBlueprint(userFor);
    resp.add(Constants.PARAM_BLUEPRINT, new BlueprintDTO(blueprint));
    return resp;
  }
  
  /**
   * Add the gender for the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params to update
   */
  private void addGender(User user, Map<String, Object> params) {
    // adding all gender text
    params.putAll(MessagesHelper.getGenderText(user));
    
    Gender gender = user.getGender();
    
    // needed in email template. if gender is "M", display "his". If gender is "F", display "her"
    if (gender != null) {
      if (gender.toString().equalsIgnoreCase("M")) {
        params.put("genderParam1", "him");
        params.put("genderParam2", "his");
        
      } else if (gender.toString().equalsIgnoreCase("F")) {
        params.put("genderParam1", "her");
        params.put("genderParam2", "her");
      } else {
        
        params.put("genderParam1", "them");
        params.put("genderParam2", "their");
      }
    }
  }
  
  /**
   * Method to create the notes and feedback DTO.
   * 
   * @param notesFeedbackList
   *          - list of notes and feedback
   * @param isProvideFeedback
   *          - is this a provide feedback request
   * @return the DTO list for notes and feedback
   */
  private List<PracticeFeedbackRequestDTO> createNotesFeedbackDTO(
      List<? extends SPNote> notesFeedbackList, boolean isProvideFeedback, String locale) {
    List<PracticeFeedbackRequestDTO> allNotesFeedbackDTOList = new ArrayList<PracticeFeedbackRequestDTO>();
    User memberUser = null;
    SPGoal goal = null;
    DevelopmentStrategy devStrategy = null;
    HashMap<String, User> userMap = new HashMap<String, User>();
    
    for (SPNote spNoteFeedback : notesFeedbackList) {
      memberUser = null;
      goal = null;
      devStrategy = null;
      
      // check if feedback type then add the user
      if (spNoteFeedback.getType() == SPNoteFeedbackType.FEEDBACK && !isProvideFeedback) {
        PracticeFeedback practiceFeedback = (PracticeFeedback) spNoteFeedback;
        final String feedbackUserId = practiceFeedback.getFeedbackUserId();
        memberUser = userMap.get(feedbackUserId);
        if (memberUser == null) {
          memberUser = userRepository.findFeedbackUser(feedbackUserId);
          userMap.put(feedbackUserId, memberUser);
        }
      }
      
      // getting the goal
      goal = goalsFactory.getGoal(spNoteFeedback.getGoalId());
      if (goal == null) {
        LOG.warn("Goal not found for goal id :" + spNoteFeedback.getGoalId());
        throw new InvalidRequestException("Goal not found.");
      }
      
      // getting the development strategy
      String devStrategyId = spNoteFeedback.getDevStrategyId();
      if (!StringUtils.isBlank(devStrategyId)) {
        devStrategy = goalsFactory.getDevelopmentStrategyById(spNoteFeedback.getGoalId(),
            devStrategyId, locale);
      }
      
      PracticeFeedbackRequestDTO feedbackRequestDTO = new PracticeFeedbackRequestDTO(
          spNoteFeedback, memberUser, goal, devStrategy);
      
      if (isProvideFeedback) {
        final String userId = spNoteFeedback.getUserId();
        User user = userMap.get(userId);
        if (user == null) {
          user = userRepository.findUserById(userId);
          if (user == null) {
            // user is not found in the DB
            // deleting the request
            spNoteFeedbackRepository.deleteRequest(spNoteFeedback);
            // continuing the loop as this request should not be added
            continue;
          }
          userMap.put(userId, user);
        }
        feedbackRequestDTO.setUser(new BaseUserDTO(user));
        
        // adding the feedback request introduction
        String introMessage = null;
        switch (goal.getCategory()) {
        case ActionPlan:
          introMessage = MessagesHelper.getMessage(
              "notesAndFeedback.feedback.response.orgPlan.intro", goal.getName());
          break;
        case Blueprint:
          introMessage = MessagesHelper
              .getMessage("notesAndFeedback.feedback.response.blueprint.intro");
          break;
        default:
          introMessage = MessagesHelper.getMessage(
              "notesAndFeedback.feedback.response.individual.intro", goal.getName());
        }
        
        introMessage = MessagesHelper.genderNormalize(introMessage, user);
        
        feedbackRequestDTO.setFeedbackRequestIntroduction(introMessage);
      }
      allNotesFeedbackDTOList.add(feedbackRequestDTO);
    }
    return allNotesFeedbackDTOList;
  }
  
  /**
   * Helper method to add a Note for the goal/dev strategy.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the add comment
   */
  
  public SPResponse getNotesFeedbackForGoal(User user, Object[] param) {
    SPResponse response = new SPResponse();
    
    String goalId = (String) param[0];
    
    String memberEmail = (String) param[1];
    if (StringUtils.isNotBlank(memberEmail)) {
      user = userRepository.findByEmail(memberEmail);
    }
    
    List<SPNote> allNotesFeedbackList = null;
    if (StringUtils.isBlank(goalId)) {
      allNotesFeedbackList = spNoteFeedbackRepository.findAllNotesFeedback(user.getId());
      List<PracticeFeedbackRequestDTO> createNotesFeedbackDTO = createNotesFeedbackDTO(
          allNotesFeedbackList, false, user.getUserLocale());
      List<NotesAndFeedbackListingDTO> notesFeedbackListing = new ArrayList<NotesAndFeedbackListingDTO>();
      Map<String, List<PracticeFeedbackRequestDTO>> practiceAreaNotesMap = new HashMap<String, List<PracticeFeedbackRequestDTO>>();
      Map<String, List<PracticeFeedbackRequestDTO>> practiceAreaFeedbackMap = new HashMap<String, List<PracticeFeedbackRequestDTO>>();
      
      for (PracticeFeedbackRequestDTO dto : createNotesFeedbackDTO) {
        if (dto.getType() == SPNoteFeedbackType.NOTE) {
          createOrAddToListingDTO(notesFeedbackListing, practiceAreaNotesMap, dto);
        } else if (dto.getType() == SPNoteFeedbackType.FEEDBACK) {
          createOrAddToListingDTO(notesFeedbackListing, practiceAreaFeedbackMap, dto);
        }
      }
      response.add("notesAndFeedbackList", notesFeedbackListing);
    } else {
      allNotesFeedbackList = spNoteFeedbackRepository.findAllNotesFeedbackForGoal(user.getId(),
          goalId);
      response.add("notesAndFeedbackList",
          createNotesFeedbackDTO(allNotesFeedbackList, false, user.getUserLocale()));
    }
    return response;
  }
  
  /**
   * Method to add to the existing practice area listing or create a new one.
   * 
   * @param notesFeedbackListing
   *          - notes and feedback listing
   * @param practiceAreaMap
   *          - practice area map to add to
   * @param dto
   *          - notes and feedback DTO
   */
  private void createOrAddToListingDTO(List<NotesAndFeedbackListingDTO> notesFeedbackListing,
      Map<String, List<PracticeFeedbackRequestDTO>> practiceAreaMap, PracticeFeedbackRequestDTO dto) {
    SPNoteFeedbackPracticeAreaDTO practiceAreaDTO = dto.getPracticeAreaDTO();
    List<PracticeFeedbackRequestDTO> listToAddTo;
    listToAddTo = practiceAreaMap.get(practiceAreaDTO.getId());
    if (listToAddTo == null) {
      NotesAndFeedbackListingDTO listingDto = new NotesAndFeedbackListingDTO(practiceAreaDTO, dto);
      listToAddTo = listingDto.getNotesFeedbackList();
      practiceAreaMap.put(practiceAreaDTO.getId(), listToAddTo);
      notesFeedbackListing.add(listingDto);
    } else {
      listToAddTo.add(dto);
    }
  }
  
  /**
   * Helper method to add a Note for the goal/dev strategy.
   * 
   * @param user
   *          - user
   * @return the response to the add comment
   */
  
  public SPResponse getRequestFeedbackReceived(User user) {
    
    SPResponse response = new SPResponse();
    String email = user.getEmail();
    
    List<PracticeFeedback> allFeedback = spNoteFeedbackRepository
        .findRequestFeedbackReceived(email);
    
    LOG.debug("Returning all feedback for the user" + email + ", Feedback " + allFeedback);
    response.add("feedbackRequestList",
        createNotesFeedbackDTO(allFeedback, true, user.getUserLocale()));
    
    return response;
  }
  
}
