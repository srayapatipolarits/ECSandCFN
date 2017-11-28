package com.sp.web.service.lndfeedback;

import com.sp.web.Constants;
import com.sp.web.comments.CommentsFactory;
import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.DevelopmentFeedbackPracticeAreaDTO;
import com.sp.web.dto.blueprint.BlueprintDTO;
import com.sp.web.dto.lndfeedback.BaseDevelopmentFeedbackDTO;
import com.sp.web.dto.lndfeedback.BaseDevelopmentFeedbackResponseDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackListingDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackRequestDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackResponseDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.exception.DashboardRedirectException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.UserNotFoundException;
import com.sp.web.form.lndfeedback.DevelopmentForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.model.lndfeedback.DevelopmentFeedbackResponse;
import com.sp.web.model.lndfeedback.UserDevelopmentFeedbackResponse;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository;
import com.sp.web.service.blueprint.BlueprintFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DevelopmentFeedbackFactory handles operation for development feedback.
 * 
 * @author pradeepruhil
 *
 */
@Component("developmentFeedback")
public class DevelopmentFeedbackFactory implements
    GenericFactory<DevelopmentFeedbackListingDTO, DevelopmentFeedbackDTO, DevelopmentForm> {
  
  /** initializing the logger. */
  private static final Logger LOG = Logger.getLogger(DevelopmentFeedbackFactory.class);
  
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private DevelopmentFeedbackRepository developmentRepository;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private CommentsFactory commentsFactory;
  
  @Autowired
  private BlueprintFactory bluePrintFactory;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private SPGoalFactoryHelper goalsHelper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * @see com.sp.web.controller.generic.GenericFactory#getAll(com.sp.web.model.User)
   */
  @Override
  public List<DevelopmentFeedbackListingDTO> getAll(User user) {
    final List<DevelopmentFeedbackListingDTO> dtos = new ArrayList<>();
    List<DevelopmentFeedback> devFeedbacks = developmentRepository.findByUserId(user.getId());
    Map<String, List<DevelopmentFeedback>> collect = devFeedbacks.stream().collect(
        Collectors.groupingBy(DevelopmentFeedback::getDevFeedRefId, LinkedHashMap::new,
            Collectors.toList()));
    collect.forEach((devFeedRefId, feedbacks) -> addToDTO(devFeedRefId, feedbacks, dtos, user));
    return dtos;
  }
  
  private void addToDTO(String devFeedRefId, List<DevelopmentFeedback> feedbacks,
      List<DevelopmentFeedbackListingDTO> dtos, User user) {
    DevelopmentFeedbackListingDTO dto = new DevelopmentFeedbackListingDTO();
    DevelopmentFeedback developmentFeedback = feedbacks.get(0);
    dto.setSpFeature(developmentFeedback.getSpFeature());
    dto.setCount(feedbacks.size());
    dto.setDevFeedRefId(devFeedRefId);
    
    boolean doAdd = true;
    SPGoal goal = goalsFactory.getGoal(devFeedRefId, user.getUserLocale());
    switch (SPFeature.valueOf(developmentFeedback.getSpFeature())) {
    case OrganizationPlan:
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(developmentFeedback
          .getFeedParentRefId());
      if (actionPlan != null) {
        dto.setName(actionPlan.getName());
      } else {
        doAdd = false;
      }
      break;
    default:
      dto.setName(developmentFeedback.getSpFeature().toString());
      break;
    }
    
    if (goal != null) {
      dto.setTitle(goal.getName());
    } else {
      doAdd = false;
    }
    
    if (doAdd) {
      dto.setUpdatedOn(developmentFeedback.getUpdatedOn());
      dtos.add(dto);
    } else {
      feedbacks.forEach(this::deleteFeedback);
    }
  }
  
  /**
   * @see com.sp.web.controller.generic.GenericFactory#get(com.sp.web.model.User, java.lang.String)
   */
  @Override
  public DevelopmentFeedbackDTO get(User user, DevelopmentForm form) {
    return get(user, form.getId());
  }
  
  /**
   * @see com.sp.web.controller.generic.GenericFactory#get(com.sp.web.model.User, java.lang.String)
   */
  public DevelopmentFeedbackDTO get(User user, String id) {
    DevelopmentFeedback devFeedback = developmentRepository.findById(id);
    
    Assert.notNull(devFeedback, "Invalid request, no development feedback exist");
    
    User member = userFactory.getUser(devFeedback.getUserId());
    DevelopmentFeedbackDTO developmentFeedbackDTO = new DevelopmentFeedbackDTO(devFeedback, member,
        userFactory);
    DevelopmentFeedbackPracticeAreaDTO areaDTO;
    try {
      switch (devFeedback.getSpFeature()) {
      case "Blueprint":
        
        Blueprint blueprint = goalsFactory.getValidBlueprint(member);
        
        final GoalStatus blueprintStatus = blueprint.getStatus();
        if (blueprintStatus != GoalStatus.PUBLISHED) {
          // check if the backup exists
          blueprint = bluePrintFactory.getBlueprintBackupFromBlueprintId(blueprint.getId());
          Assert.notNull(blueprint, "Blueprint not in published state and no backup found.");
        }
        
        developmentFeedbackDTO.getDataMap().put(Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST,
            Boolean.FALSE);
        
        // removing the comments that are not the current users comments
        developmentFeedbackDTO.getDataMap().put(Constants.PARAM_BLUEPRINT,
            new BlueprintDTO(blueprint, commentsFactory).filterComments(user));
        
        // adding the settings
        developmentFeedbackDTO.getDataMap().put(Constants.PARAM_BLUEPRINT_SETTINGS,
            bluePrintFactory.getBlueprintSettings(member.getCompanyId()));
        break;
      case "OrganizationPlan":
        ActionPlanDao actionPlan = actionPlanFactory
            .getActionPlan(devFeedback.getFeedParentRefId());
        areaDTO = populateDevelopmentPracticeDTO(devFeedback);
        areaDTO.setProgramName(actionPlan.getName());
        developmentFeedbackDTO.getDataMap().put("dataMap", areaDTO);
        break;
      case "Erti":
        UserGoalDao userGoal = goalsHelper.getUserGoal(member);
        areaDTO = populateDevelopmentPracticeDTO(devFeedback, userGoal);
        developmentFeedbackDTO.getDataMap().put("dataMap", areaDTO);
        break;
      default:
        areaDTO = populateDevelopmentPracticeDTO(devFeedback);
        developmentFeedbackDTO.getDataMap().put("dataMap", areaDTO);
        
        break;
      }
    } catch (InvalidRequestException ex) {
      /* remove the development feedbak as it is no longer exist */
      developmentRepository.delete(devFeedback);
      throw ex;
    }
    return developmentFeedbackDTO;
  }
  
  private DevelopmentFeedbackPracticeAreaDTO populateDevelopmentPracticeDTO(
      DevelopmentFeedback devFeedback, UserGoalDao userGoal) {
    final String devFeedRefId = devFeedback.getDevFeedRefId();
    SPGoal goal = goalsFactory.getGoal(devFeedRefId);
    Assert.notNull(goal, "Practice area not found.");
    UserGoalProgressDao userGoalProgress = userGoal.getUserGoalProgress(devFeedRefId);
    Assert.notNull(userGoalProgress, "Member not customized goal.");
    return new DevelopmentFeedbackPracticeAreaDTO(goal, userGoalProgress);
  }
  
  private DevelopmentFeedbackPracticeAreaDTO populateDevelopmentPracticeDTO(
      DevelopmentFeedback devFeedback) {
    SPGoal goal = goalsFactory.getGoal(devFeedback.getDevFeedRefId());
    if (goal == null) {
      throw new InvalidRequestException("Development feedback does not exist.");
    }
    DevelopmentFeedbackPracticeAreaDTO areaDTO = new DevelopmentFeedbackPracticeAreaDTO(goal);
    return areaDTO;
  }
  
  /**
   * @see com.sp.web.controller.generic.GenericFactory#create(com.sp.web.model.User,
   *      java.lang.Object)
   */
  @Override
  public DevelopmentFeedbackDTO create(User user, DevelopmentForm form) {
    
    /* validate the request */
    
    // validate the goal id
    // goal and dev strategy are needed in email
    SPGoal devFeedbackGoal = goalsFactory.getGoal(form.getDevFeedRefId());
    if (devFeedbackGoal == null) {
      LOG.warn("Invalid development feed ref id:" + devFeedbackGoal);
      throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    }
    
    /* create the basic development feedback */
    
    /* baseParams map for the email. */
    Map<String, Object> baseParamsMap = new HashMap<String, Object>();
    DevelopmentFeedback devFeedback = form.create(user);
    String key = "developmentFeedback.action." + form.getSpFeature() + ".text";
    /* removed the reduannt null, chekc, this code cannot be null */
    baseParamsMap.put("goal", devFeedbackGoal);
    baseParamsMap.put("feedback", devFeedback);
    final String paramsMessageKey = "log.PracticeFeedback.RequestFeedback.message";
    // adding activities message
    addActivitiesMessage(baseParamsMap, devFeedbackGoal, devFeedback, paramsMessageKey
        + ".activity.",user.getLocale());
    form.getFeedbackUsers()
        .stream()
        .filter(fbEmail -> !user.getEmail().equalsIgnoreCase(fbEmail))
        .forEach(fbEmail -> {
          FeedbackUser feedbackUser = new FeedbackUser();
          feedbackUser.setFeedbackFor(user.getId());
          feedbackUser.setFeatureType(FeatureType.NotesFeedback);
          
          /* populate the feedback user */
          feedbackUser.setEmail(fbEmail);
          // internal & external users. If user found, set feedback user and feedback id in Feedback
          // object.
          // If not found/external, create a new user object and set it in Feedback object
            User memberUser = userFactory.getUserByEmail(fbEmail);
            if (memberUser != null) {
              feedbackUser.updateFrom(memberUser);
            } else {
              feedbackUser.setEmail(fbEmail);
              feedbackUser.setType(UserType.External);
              feedbackUser.addRole(RoleType.FeedbackUser);
              feedbackUser.setCompanyId(user.getCompanyId());
            }
            userFactory.updateUser(feedbackUser);
            devFeedback.setFeedbackUserId(feedbackUser.getId());
            devFeedback.setFeedbackUserEmail(feedbackUser.getEmail());
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.putAll(baseParamsMap);
            generateToken(devFeedback, paramsMap);
            
            // param needed in email template. If variable is blank/empty we need to set it to null
            sendNotification(user, devFeedback, feedbackUser, paramsMap, paramsMessageKey,
                NotificationType.RequestFeedback);
            
            if (memberUser != null) {
              addToDoRequest(user, devFeedbackGoal, devFeedback, key, memberUser);
            }
            // Making it null so that multiple dev feedbacks are created in db.
            devFeedback.setId(null);
          });
    DevelopmentFeedbackDTO developmentFeedbackDTO = new DevelopmentFeedbackDTO(devFeedback, user,
        userFactory);
    return developmentFeedbackDTO;
  }
  
  /**
   * Method to add the activities message to the notification parameters.
   * 
   * @param notificationParams
   *          - notification parameters
   * @param devFeedbackGoal
   *          - development feedback goal
   * @param devFeedback
   *          - development feedback request
   * @param key
   *          - messages key
   * @param locale for which activities are to be found.
   */
  private void addActivitiesMessage(Map<String, Object> notificationParams, SPGoal devFeedbackGoal,
      DevelopmentFeedback devFeedback, String key, Locale locale) {
    final String spFeature = devFeedback.getSpFeature();
    switch (spFeature) {
    case "OrganizationPlan":
      /* Get the action plan name */
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(devFeedback.getFeedParentRefId());
      if (actionPlan != null) {
        notificationParams.put(
            Constants.PARAM_MESSAGE_ACTIVITY,
            MessagesHelper.getMessage(key + spFeature, locale, devFeedbackGoal.getName(),
                actionPlan.getName()));
      } else {
        LOG.error("Organization learning program not found :" + devFeedback.getFeedParentRefId());
      }
      break;
    default:
      notificationParams.put(Constants.PARAM_MESSAGE_ACTIVITY,
          MessagesHelper.getMessage(key + spFeature, devFeedbackGoal.getName()));
      break;
    }
  }
  
  /**
   * Method to add the notification message to the notification parameters.
   * 
   * @param notificationParams
   *          - notification parameters
   * @param devFeedbackGoal
   *          - development feedback goal
   * @param devFeedback
   *          - development feedback request
   * @param key
   *          - messages key
   * @param feedbackUser
   *          - feedback user
   */
  private void addNotificationMessage(Map<String, Object> notificationParams,
      SPGoal devFeedbackGoal, DevelopmentFeedback devFeedback, String key, User feedbackUser) {
    final String spFeature = devFeedback.getSpFeature();
    final UserMarkerDTO feedbackUserDTO = new UserMarkerDTO(feedbackUser);
    notificationParams.put(Constants.PARAM_NOTIFICATION_URL_PARAM, devFeedback.getId());
    switch (spFeature) {
    case "OrganizationPlan":
      /* Get the action plan name */
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(devFeedback.getFeedParentRefId());
      if (actionPlan != null) {
        notificationParams.put(
            Constants.PARAM_NOTIFICATION_MESSAGE,
            MessagesHelper.getMessage(key + spFeature, feedbackUser.getLocale(),devFeedbackGoal.getName(),
                actionPlan.getName(), feedbackUserDTO.getFullNameOrEmail()));
      } else {
        LOG.error("Organization learning program not found :" + devFeedback.getFeedParentRefId());
      }
      break;
    default:
      notificationParams.put(
          Constants.PARAM_NOTIFICATION_MESSAGE,
          MessagesHelper.getMessage(key + spFeature,feedbackUser.getLocale(), devFeedbackGoal.getName(),
              feedbackUserDTO.getFullNameOrEmail()));
      break;
    }
  }
  
  /**
   * <code>addToDoRequest</code> method will add the the todo requet of the feebdack request.
   * 
   * @param user
   *          logged in user.
   * @param devFeedbackGoal
   *          feedback goal
   * @param devFeedback
   *          development feedback
   * @param key
   *          is the key for messages.properties
   * @param memberUser
   *          feedback user.
   */
  private void addToDoRequest(User user, SPGoal devFeedbackGoal, DevelopmentFeedback devFeedback,
      String key, User memberUser) {
    TodoRequest newInstanceFromRefId = TodoRequest.newDevFeedbackUserRequest(user,
        TodoType.Feedback, devFeedback.getId());
    switch (devFeedback.getSpFeature()) {
    case "OrganizationPlan":
      /* Get the action plan name */
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(devFeedback.getFeedParentRefId());
      if (actionPlan != null) {
        newInstanceFromRefId.setText(MessagesHelper.getMessage(key, devFeedbackGoal.getName(),
            actionPlan.getName()));
      } else {
        LOG.error("Not able to set the todo list for organization plan dev feedback for user"
            + user);
      }
      break;
    default:
      newInstanceFromRefId.setText(MessagesHelper.getMessage(key, devFeedbackGoal.getName()));
      break;
    }
    todoFactory.addTodo(memberUser, newInstanceFromRefId);
  }
  
  /**
   * <code>sendNotification </code> method will send the notification to the user.
   * 
   * @param user
   *          logged in user
   * @param devFeedback
   *          development feedback
   * @param feedbackUser
   *          is the feedback user
   * @param baseParamsMap
   *          contains the base params map.
   * @param paramsMessageKey
   *          is the message key used in email template.
   * @param type
   *          notificatio type.
   */
  private void sendNotification(User user, DevelopmentFeedback devFeedback, User feedbackUser,
      Map<String, Object> baseParamsMap, String paramsMessageKey, NotificationType type) {
    /* Create the email params . */
    Map<String, Object> emailParams = new HashMap<String, Object>(baseParamsMap);
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    emailParams.put(Constants.PARAM_COMPANY, company);
    
    if (feedbackUser.getType() == UserType.Member) {
      BaseUserDTO userDto = new BaseUserDTO(feedbackUser);
      
      String paramsMessage = MessagesHelper.getMessage(paramsMessageKey, userDto.getName(),
          userDto.getEmail(), devFeedback.getId());
      emailParams.put(Constants.PARAM_MESSAGE, paramsMessage);
    }
    // adding token id subject. It is used when user replies to feedback thru email
    String spFeature = MessagesHelper.getMessage("pricing.create." + devFeedback.getSpFeature());
    
    String subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + type,
        feedbackUser.getLocale(), user.getFullNameOrEmail(), spFeature);
    
    subject = subject + " [" + devFeedback.getTokenId() + "]";
    emailParams.put(Constants.PARAM_SUBJECT, subject);
    
    notificationsProcessor.process(type, user, feedbackUser, emailParams, false);
  }
  
  /**
   * createToken will create the token for the user and the feedback request and will generate the
   * token url to be used.
   * 
   * @param devFeedback
   *          development feedbackrequest of the user.
   * @param paramsMap
   *          contains the base parameter request.
   */
  private void generateToken(DevelopmentFeedback devFeedback, Map<String, Object> paramsMap) {
    
    /*
     * create the token and get the token url. These param will be used in Token processor and
     * passed to redirect page.
     */
    Token token = spTokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
        TokenProcessorType.REQUEST_FEEDBACK);
    devFeedback.setTokenUrl(token.getTokenUrl());
    devFeedback.setTokenId(token.getTokenId());
    paramsMap.put("tokenUrl", token.getTokenUrl());
    paramsMap.put("tokenId", token.getId());
    // feedbackUser.setTokenUrl(token.getTokenUrl()); //URL needed for email
    // feedbackRepository.addFeedbackUser(feedbackUser);
    // feedback.setFeedbackUserId(feedbackUser.getId());
    developmentRepository.save(devFeedback);
    paramsMap.put("feedbackId", devFeedback.getId());
  }
  
  /**
   * Update method will update the development feedback with the user response and send the update
   * to the user.
   * 
   * @see com.sp.web.controller.generic.GenericFactory#update(com.sp.web.model.User,
   *      java.lang.Object)
   */
  @Override
  public DevelopmentFeedbackDTO update(User feedbackUser, DevelopmentForm form) {
    
    DevelopmentFeedback feedback = developmentRepository.findById(form.getId());
    
    Assert.notNull(feedback, "Invalid Request, feedback no longer exist");
    
    final String devFeedbackGoalId = feedback.getDevFeedRefId();
    
    // Get id of person who requested the feedback.
    User memberUser = userFactory.getUser(feedback.getUserId());
    
    if (memberUser == null) {
      throw new UserNotFoundException("User not found.");
    }
    form.update(feedbackUser, feedback);
    
    SPGoal goal = goalsFactory.getGoal(devFeedbackGoalId);
    if (goal == null) {
      throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    }
    
    developmentRepository.save(feedback);
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("id", feedback.getId());
    params.put("goal", goal);
    params.put("feedback", feedback);
    
    // method is invoked by external and internal both.
    DevelopmentFeedbackDTO feedbackDTO = new DevelopmentFeedbackDTO(feedback, memberUser,
        userFactory);
    final String paramsMessageKey = "log.PracticeFeedback.SubmitRequestFeedback.message";
    if (!form.isDecline()) {
      addActivitiesMessage(params, goal, feedback, paramsMessageKey + ".activity.", feedbackUser.getLocale());
      addNotificationMessage(params, goal, feedback, paramsMessageKey + ".", feedbackUser);
      sendNotification(feedbackUser, feedback, memberUser, params, paramsMessageKey,
          NotificationType.SubmitRequestFeedback);
      populateThankYouMessage(feedbackDTO, feedbackUser, memberUser, goal);
      
      /* send the sse to the user */
      MessageEventRequest eventRequest = MessageEventRequest.newEvent(
          ActionType.DefaultEventProcessor, params, memberUser);
      eventGateway.sendEvent(eventRequest);
    }
    boolean isInternalUser = !(feedbackUser instanceof FeedbackUser)
        || (feedbackUser.getType() != UserType.External);
    if (isInternalUser) {
      
      /* check if internal user is giving feedback without loggin into the system */
      User user = null;
      if (feedbackUser instanceof FeedbackUser) {
        user = userFactory.getUserByEmail(feedbackUser.getEmail());
      } else {
        user = feedbackUser;
      }
      UserDevelopmentFeedbackResponse feedbackResponse = developmentRepository
          .getDevelopmentFeedbackResponse(user.getId());
      feedbackResponse.add(feedback, memberUser);
      developmentRepository.update(feedbackResponse);
      todoFactory.remove(user, feedback.getId());
    }
    
    // else {
    // // Noting as of now for decline flow.
    // }
    /* remove the todo from users list */
    return feedbackDTO;
  }
  
  /**
   * Update the development feedback to database.
   * 
   * @param feedback
   *          to be udpated
   */
  public void update(DevelopmentFeedback feedback) {
    developmentRepository.save(feedback);
  }
  
  /**
   * 
   * @see com.sp.web.controller.generic.GenericFactory#delete(com.sp.web.model.User,
   *      java.lang.String)
   */
  @Override
  public void delete(User user, DevelopmentForm form) {
    
    /* check do we have to delete all the devFeed or all the dev feedback */
    String devFeedRefId = form.getDevFeedRefId();
    if (StringUtils.isNotBlank(devFeedRefId)) {
      /* delete all the dev feedback for the parent ref id. */
      final String spFeature = form.getSpFeature();
      Assert.notNull(spFeature, "Invalid Request, SPFeature missing");
      deleteByDevFeedRefId(devFeedRefId, user.getCompanyId(), spFeature);
    } else {
      DevelopmentFeedback devFeedback = developmentRepository.findById(form.getId());
      Assert.notNull(devFeedback, "Invalid request. No development feedback found.");
      deleteFeedback(devFeedback);
    }
    
  }
  
  /**
   * <code>deleteAllByUser</code> method will delete all the development feedbacks requested by the
   * user.
   * 
   * @param user
   *          is the user who has requested all the feedbacks.
   */
  public void deleteAllByUser(User user) {
    List<DevelopmentFeedback> devFeedsBacks = developmentRepository.findByUserId(user.getId());
    devFeedsBacks.forEach(this::deleteFeedback);
  }
  
  /**
   * delete Feedback will delete the feedback.
   * 
   * @param fb
   *          is the feedback to be deletd.
   */
  private void deleteFeedback(DevelopmentFeedback fb) {
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(fb.getFeedbackUserId());
    userFactory.deleteFeedbackUser(feedbackUser);
    
    /* delete the feedback request */
    developmentRepository.delete(fb);
    
    todoFactory.remove(feedbackUser, fb.getId());
  }
  
  /**
   * Delete all the development feedback for the given parent feed reference id.
   * 
   * @param parentRefId
   *          - parent reference id
   */
  public void deleteByParentRefId(String parentRefId) {
    List<DevelopmentFeedback> feedbacks = developmentRepository.findAllByParentRefId(parentRefId);
    feedbacks.forEach(this::deleteFeedback);
  }
  
  /**
   * <code>deleteByParentRefId</code> method will delete all the feedbackBy parent id.
   * 
   * @param parentRefId
   *          is the reference id for which feedback is created.
   * @param companyId
   *          company id
   */
  public void deleteByParentRefId(String parentRefId, String companyId) {
    List<DevelopmentFeedback> feedbacks = developmentRepository.findAllByParentRefId(parentRefId,
        companyId);
    feedbacks.forEach(this::deleteFeedback);
  }
  
  /**
   * Delete all the development feedback requests.
   * 
   * @param devFeedRefId
   *          - development feedback reference id
   */
  public void deleteByDevFeedRefId(String devFeedRefId) {
    List<DevelopmentFeedback> feedbacks = developmentRepository.findAllByDevFeedRefId(devFeedRefId);
    feedbacks.forEach(this::deleteFeedback);
  }
  
  /**
   * deleteByDevFeedRefId will delete all the feedback associated with the development feed ref id.
   * 
   * @param devFeedRefId
   *          is the development feed ref id.
   * @param companyId
   *          is the company id for which development feed back is to be deleted.
   */
  public void deleteByDevFeedRefId(String devFeedRefId, String companyId, String spFeature) {
    List<DevelopmentFeedback> feedbacks = developmentRepository.findAllByDevFeedRefId(devFeedRefId,
        companyId, spFeature);
    feedbacks.forEach(this::deleteFeedback);
  }
  
  /**
   * Removing the development feedbacks for the given feed ref id.
   * 
   * @param member
   *          - member
   * @param devFeedRefId
   *          - development feedback reference id
   */
  public void deleteByDevFeedRefId(User member, String devFeedRefId) {
    List<DevelopmentFeedback> developmentFeedbacks = developmentRepository.findByUserAndFeedRefId(
        member, devFeedRefId);
    developmentFeedbacks.forEach(this::deleteFeedback);
  }
  
  /**
   * Delete all the feedback requests for the given parent reference id.
   * 
   * @param member
   *          - member
   * @param feedParentRefId
   *          - parent feedback reference id
   */
  public void deleteByFeedParentRefId(User member, String feedParentRefId) {
    List<DevelopmentFeedback> developmentFeedbacks = developmentRepository
        .findByUserAndFeedParentRefId(member, feedParentRefId);
    developmentFeedbacks.forEach(this::deleteFeedback);
  }
  
  /**
   * populate thank you message method will populate the response with the data to be sent to front
   * end to populate the thank you view.
   * 
   * @param feedbackDTO
   *          is the feedback dto to be sent to front end.
   * @param feedbackUser
   *          is the feedback user
   * @param memberUser
   *          is the memberuser
   * @param goal
   *          is the practice feedback goal.
   */
  private void populateThankYouMessage(DevelopmentFeedbackDTO feedbackDTO, User feedbackUser,
      User memberUser, SPGoal goal) {
    String name = feedbackUser.getFullNameOrEmail();
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
    feedbackDTO.getDataMap().put("heading1", heading1);
    feedbackDTO.getDataMap().put("heading2", heading2);
  }
  
  /**
   * getAllByDevFeedRefId will return all the feedback request for the user.
   * 
   * @param user
   *          is the logged in user.
   * @param devFeedRefId
   *          is the feed refid.
   * @param spFeature
   *          from where user has made the request.
   * @return the base developmentFeedbackDTO.
   */
  public List<BaseDevelopmentFeedbackDTO> getAllByDevFeedRefId(User user, String devFeedRefId,
      SPFeature spFeature) {
    
    List<DevelopmentFeedback> findAllByDevFeedRefId = developmentRepository
        .findAllByDevFeedRefIdUserId(devFeedRefId, user.getId(), spFeature.toString());
    List<BaseDevelopmentFeedbackDTO> collect = findAllByDevFeedRefId.stream()
        .map(db -> new BaseDevelopmentFeedbackDTO(db, user, userFactory))
        .collect(Collectors.toList());
    return collect;
  }
  
  /**
   * DevelopmentFeedback returns the development feedback by token id.
   * 
   * @param tokenId
   *          of the user.
   * @return the development feedback.
   */
  public DevelopmentFeedback findDevFeedbackbyTokenId(String tokenId) {
    return developmentRepository.findDevFeedbackbyTokenId(tokenId);
  }
  
  /**
   * Email feedbacks will send the feedback to the user.
   * 
   * @param user
   *          logged in user
   * @param form
   *          is the development form.
   */
  public void emailFeedbacks(User user, DevelopmentForm form) {
    
    List<DevelopmentFeedback> devFeedbacks = new ArrayList<>();
    SPGoal devFeedbackGoal;
    if (StringUtils.isNotBlank(form.getDevFeedRefId())) {
      devFeedbacks.addAll(developmentRepository.findAllByDevFeedRefId(form.getDevFeedRefId(),
          user.getCompanyId(), form.getSpFeature()));
      devFeedbackGoal = goalsFactory.getGoal(form.getDevFeedRefId(), user.getUserLocale());
    } else {
      DevelopmentFeedback findById = developmentRepository.findById(form.getId());
      Assert.notNull(findById, "Invalid feedback Id");
      devFeedbackGoal = goalsFactory.getGoal(findById.getDevFeedRefId(), user.getUserLocale());
      devFeedbacks.add(findById);
    }
    
    String name = devFeedbackGoal.getName();
    String organgeBar;
    if (devFeedbackGoal.getCategory() == GoalCategory.Blueprint) {
      name = MessagesHelper.getMessage("pricing.create.Blueprint");
      /* doing the dirty stuff, as the localized word is availble in Upper case */
      organgeBar = StringUtils.capitalize(MessagesHelper.getMessage("group.feedback.title")
          .toLowerCase(LocaleHelper.locale()))
          + " "
          + MessagesHelper.getMessage("generic.for",
              user.getUserLocale().toLowerCase(LocaleHelper.locale())) + " " + name;
    } else {
      organgeBar = devFeedbackGoal.getName()
          + " "
          + StringUtils.capitalize(MessagesHelper.getMessage("group.feedback.title",
              user.getUserLocale()).toLowerCase(LocaleHelper.locale()));
    }
    String heading = MessagesHelper.getMessage("pricing.create." + form.getSpFeature());
    Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put("devFeedbacks", devFeedbacks);
    paramsMap.put("goal", devFeedbackGoal);
    paramsMap.put("heading", heading);
    paramsMap.put("organgeBar", organgeBar);
    paramsMap.put(Constants.PARAM_SUBJECT,
        MessagesHelper.getMessage(Constants.PARAM_EMAIL_FEEDBACK_SUB, user.getLocale(), name));
    notificationsProcessor.process(NotificationType.EmailFeedbacks, user, user, paramsMap, false);
  }
  
  /**
   * getAllFeedbackRequest will return all the pending feedback request for the user.
   * 
   * @param feedbackUser
   *          is the feedback user.
   */
  public List<DevelopmentFeedbackRequestDTO> getAllFeedbackRequest(User feedbackUser) {
    
    List<DevelopmentFeedback> developmentFeedbacks = developmentRepository
        .getAllFeedbackUserRequest(feedbackUser.getEmail(), RequestStatus.NOT_INITIATED);
    Comparator<DevelopmentFeedback> updatedOnComp = (db1, db2) -> db2.getUpdatedOn().compareTo(
        db1.getUpdatedOn());
    return developmentFeedbacks
        .stream()
        .sorted(updatedOnComp)
        .map(
            devFeed -> {
              String userId = devFeed.getUserId();
              User user = userFactory.getUser(userId);
              DevelopmentFeedbackRequestDTO developmentFeedbackRequestDTO = new DevelopmentFeedbackRequestDTO(
                  user, devFeed.getId());
              developmentFeedbackRequestDTO.setSpFeature(devFeed.getSpFeature());
              return developmentFeedbackRequestDTO;
            }).collect(Collectors.toList());
  }
  
  /**
   * Method to get the listing of all the users development feedback response.
   * 
   * @param user
   *          - user
   * @return the list of development feedback response by the user
   */
  public List<DevelopmentFeedbackResponseDTO> getAllUserFeedbackResponses(User user) {
    UserDevelopmentFeedbackResponse developmentFeedbackResponse = developmentRepository
        .getDevelopmentFeedbackResponse(user.getId());
    final List<DevelopmentFeedbackResponseDTO> resp = new ArrayList<DevelopmentFeedbackResponseDTO>();
    final String userLocale = user.getUserLocale();
    developmentFeedbackResponse.getKeyOrder().forEach(
        k -> process(developmentFeedbackResponse.getResponse(k), resp, userLocale));
    return resp;
  }
  
  private void process(List<DevelopmentFeedbackResponse> feedbackResponseList,
      List<DevelopmentFeedbackResponseDTO> resp, String locale) {
    DevelopmentFeedbackResponse developmentFeedbackResponse = feedbackResponseList.get(0);
    DevelopmentFeedbackResponseDTO feedbackResponse = getDevelopmentFeedbackResponse(
        developmentFeedbackResponse, locale);
    if (feedbackResponse != null) {
      List<BaseDevelopmentFeedbackResponseDTO> baseFeedbackList = feedbackResponse
          .getFeedbackResponseList();
      Comparator<BaseDevelopmentFeedbackResponseDTO> updatedOnComp = (db1, db2) -> db2.getRepliedOn().compareTo(
          db1.getRepliedOn());
      feedbackResponseList.stream().map(BaseDevelopmentFeedbackResponseDTO::new)
          .sorted(updatedOnComp)   .forEach(baseFeedbackList::add);
      resp.add(feedbackResponse);
    }
  }
  
  private DevelopmentFeedbackResponseDTO getDevelopmentFeedbackResponse(
      DevelopmentFeedbackResponse developmentFeedbackResponse, String locale) {
    DevelopmentFeedbackResponseDTO responseDTO = null;
    switch (developmentFeedbackResponse.getSpFeature()) {
    case Blueprint:
      responseDTO = new DevelopmentFeedbackResponseDTO(developmentFeedbackResponse);
      break;
    case OrganizationPlan:
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(developmentFeedbackResponse
          .getFeedParentRefId());
      if (actionPlan != null) {
        String feedRefId = developmentFeedbackResponse.getDevFeedRefId();
        SPGoal goal = goalsFactory.getGoal(feedRefId);
        if (goal != null) {
          responseDTO = new DevelopmentFeedbackResponseDTO(developmentFeedbackResponse);
          responseDTO.setTitle(goal.getName());
          responseDTO.setParentTitle(actionPlan.getName());
        }
      }
      break;
    default:
      String feedRefId = developmentFeedbackResponse.getDevFeedRefId();
      SPGoal goal = goalsFactory.getGoal(feedRefId, locale);
      if (goal != null) {
        responseDTO = new DevelopmentFeedbackResponseDTO(developmentFeedbackResponse);
        responseDTO.setTitle(goal.getName());
      }
      break;
    }
    return responseDTO;
  }
}
