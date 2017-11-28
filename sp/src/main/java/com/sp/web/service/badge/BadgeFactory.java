package com.sp.web.service.badge;

import com.sp.web.Constants;
import com.sp.web.controller.dashboard.DashboardControllerHelper;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.exception.SPException;
import com.sp.web.form.Operation;
import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.badge.UserBadge;
import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.model.badge.UserBadgeProgress;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.service.tutorial.SPTutorialFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BadgeFactory class is the factory class for handling the User Badges. It provides services to
 * fetch/ add and update badges to users. It also sends SSE and do post processing for the badges
 * for the user.
 * 
 * @author pradeepruhil
 *
 */
@Component
@Lazy
public class BadgeFactory {
  
  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(BadgeFactory.class);
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private SPTutorialFactory spTutorialFactory;
  
  @Autowired
  private BadgeFactoryCache factoryCache;
  
  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  private DashboardControllerHelper dashboardControllerHelper;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  @Qualifier("notificationLog")
  private LogGateway notificationGateway;
  
  /**
   * getUserBadge method will return the user badge associated with user.
   * 
   * @param user
   *          for which badges are to be returned.
   * @return the UserBadgeActivity.
   */
  public UserBadgeActivity getUserBadge(User user) {
    UserBadgeActivity userBadgeActivity = getUserBadge(user.getId());
    if (userBadgeActivity == null) {
      userBadgeActivity = new UserBadgeActivity();
      userBadgeActivity.setCompanyId(user.getCompanyId());
      userBadgeActivity.setUserId(user.getId());
    }
    return userBadgeActivity;
  }
  
  public UserBadgeActivity getUserBadge(String userId) {
    return factoryCache.getUserBadge(userId);
  }
  
  /**
   * updateUserBadgeActivity method will update the userbadge activity in the repository and cache.
   * 
   * @param userBadgeActivity
   *          user badge activity.
   */
  public void updateUserBadgeActivity(UserBadgeActivity userBadgeActivity) {
    factoryCache.updateUserBadgeActivity(userBadgeActivity);
  }
  
  public SPGoalFactory getGoalFactory() {
    return goalFactory;
  }
  
  public void setGoalFactory(SPGoalFactory goalFactory) {
    this.goalFactory = goalFactory;
  }
  
  public ActionPlanFactory getActionPlanFactory() {
    return actionPlanFactory;
  }
  
  public void setActionPlanFactory(ActionPlanFactory actionPlanFactory) {
    this.actionPlanFactory = actionPlanFactory;
  }
  
  public SPTutorialFactory getSpTutorialFactory() {
    return spTutorialFactory;
  }
  
  public void setSpTutorialFactory(SPTutorialFactory spTutorialFactory) {
    this.spTutorialFactory = spTutorialFactory;
  }
  
  /**
   * updateBadgeProgress method will update the badge progress for the user. It will create the
   * {@link UserBadgeActivity} in case badge progress is tracked first time.
   * 
   * @param user
   *          logged in user.
   * @param refId
   *          is the reference id for which badge is to be updated.
   * @param badgeType
   *          is the badgeType.
   */
  public void addToBadgeProgress(User user, String refId, BadgeType badgeType) {
    
    if (log.isDebugEnabled()) {
      log.debug("Adding badge progress for referenceId " + refId + ", Badge Type :" + badgeType);
    }
    
    UserBadgeActivity userBadgeActivity = getUserBadge(user);
    
    addToBadgeProgress(user, refId, badgeType, userBadgeActivity, null);
    
    if (log.isDebugEnabled()) {
      log.debug("Add badge progress for referenceId is done");
    }
  }
  
  /**
   * updateBadgeProgress method will update the badge progress for the user. It will create the
   * {@link UserBadgeActivity} in case badge progress is tracked first time.
   * 
   * @param user
   *          logged in user.
   * @param refId
   *          is the reference id for which badge is to be updated.
   * @param badgeType
   *          is the badgeType.
   */
  public void addToBadgeProgress(User user, String refId, BadgeType badgeType, Object refObject) {
    
    if (log.isDebugEnabled()) {
      log.debug("Adding badge progress for referenceId " + refId + ", Badge Type :" + badgeType);
    }
    
    UserBadgeActivity userBadgeActivity = getUserBadge(user);
    
    addToBadgeProgress(user, refId, badgeType, userBadgeActivity, refObject);
    
    if (log.isDebugEnabled()) {
      log.debug("Add badge progress for referenceId is done");
    }
  }
  
  /**
   * Adds to the user's badge progress the given id.
   * 
   * @param user
   *          - user
   * @param refId
   *          - reference id
   * @param badgeType
   *          - badge type
   * @param userBadgeActivity
   *          - user badge activity
   */
  public UserBadgeProgress addToBadgeProgress(User user, String refId, BadgeType badgeType,
      UserBadgeActivity userBadgeActivity, Object refObject) {
    
    UserBadgeProgress userBadgeProgress = userBadgeActivity.getUserBadgeProgress(refId);
    if (userBadgeProgress == null) {
      /* no progress exist for the goal, create a new badge progress for the reference */
      userBadgeProgress = new UserBadgeProgress();
      userBadgeProgress.setBadgeType(badgeType);
      userBadgeProgress.setRefId(refId);
      ContentReference contentRefernce = new ContentReference();
      updateContentReference(user, refId, badgeType, contentRefernce, refObject);
      userBadgeProgress.setContentReference(contentRefernce);
      userBadgeProgress.setActiveProgress(true);
      userBadgeActivity.getUserBadgeProgress().put(refId, userBadgeProgress);
    }
    UserBadge userBadge = userBadgeActivity.getCompletedBadges(refId);
    userBadgeProgress.setActiveProgress(true);
    switch (badgeType) {
    case Erti:
      
      /*
       * check if there is any badge progress alrady exist for the user or not for the passed
       * reference id
       */
      
      if (userBadge != null) {
        /* check if highest level is granted to user or not */
        if (userBadge.getLevel() == 5) {
          /* highest level badge awared to user, remove add progress. */
          userBadgeActivity.getUserBadgeProgress().remove(refId);
          break;
        }
        
        userBadgeProgress.setLevel(userBadge.getLevel() + 1);
        int level = userBadgeProgress.getLevel();
        int totalCount = level == 1 || level == 2 ? 5 : level == 3 ? 10 : 15;
        userBadgeProgress.setTotalCount(totalCount);
        
      } else {
        userBadgeProgress.setLevel(1);
        userBadgeProgress.setTotalCount(5);
      }
      
      break;
    
    case OrgPlan:
      
      /* in case a badge is awared in the org plan, no need to add it again */
      if (userBadge == null) {
        userBadgeProgress.setLevel(1);
        ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(refId);
        UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
        if (actionPlan != null) {
          userBadgeProgress.setTotalCount(actionPlan.getActionCount());
          ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(refId);
          if (actionPlanProgress != null) {
            userBadgeProgress.setCompletedCount(actionPlanProgress.getCompletedCount());
          }
        } else {
          /* in case the ref id is no longer exist, then no progresse xsit for the user. */
          userBadgeActivity.getUserBadgeProgress().remove(refId);
        }
        
      } else {
        /*
         * user has already completed the badge, seems new steps is added/deleted. Adding it to the
         * progress, but badge will not be awareded
         */
        userBadgeProgress.setLevel(1);
        ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(refId);
        UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
        userBadgeProgress.setTotalCount(actionPlan.getActionCount());
        ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgressMap()
            .get(refId);
        if (actionPlanProgress != null) {
          /*
           * if step is deleted then no need to add user badge progress becuase it is already
           * completed
           */
          if (actionPlan.getActionCount() <= actionPlanProgress.getCompletedCount()) {
            // remove the user badge progress
            userBadgeActivity.getUserBadgeProgress().remove(refId);
          } else {
            userBadgeProgress.setCompletedCount(actionPlanProgress.getCompletedCount());
          }
          
        }
      }
      break;
    case Tutorial:
      if (userBadge == null) {
        userBadgeProgress.setLevel(1);
        SPTutorialDao tutorial = spTutorialFactory.getTutorail(refId, user.getUserLocale());
        userBadgeProgress.setTotalCount(tutorial.getActionCount());
      }
      break;
    default:
      log.warn("Invalid badgeType, Badge Type " + badgeType + " not supported.");
      throw new SPException("Invalid Badge Type");
    }
    /* update the user badge activity in the database. */
    updateUserBadgeActivity(userBadgeActivity);
    return userBadgeProgress;
  }
  
  public void updateBadgeProgress(String userId, String refId, BadgeType badgeType, Object refObj) {
    User user = userRepository.findUserById(userId);
    updateBadgeProgress(user, refId, badgeType, refObj);
  }
  
  public void updateBadgeProgress(User user, String refId, BadgeType badgeType) {
    updateBadgeProgress(user, refId, badgeType, null);
  }
  
  /**
   * updateBadgeProgres method will update the badge progress for the user.
   * 
   * @param user
   *          for which badge progress is to be updated.
   * @param refId
   *          reference id for the updating the progress for.
   * @param badgeType
   *          type of the badge.
   */
  @Async
  public void updateBadgeProgress(User user, String refId, BadgeType badgeType, Object refObj) {
    
    if (log.isDebugEnabled()) {
      log.debug("Updating badge progress for referenceId " + refId + ", Badge Type :" + badgeType);
    }
    
    UserBadgeActivity userBadgeActivity = getUserBadge(user);
    
    /* get the progress for the badge for the passed ref id */
    UserBadgeProgress userBadgeProgress = userBadgeActivity.getUserBadgeProgress().get(refId);
    if (userBadgeProgress == null) {
      if (log.isDebugEnabled()) {
        log.debug("No badge progress present for the completed action, Starting the badge progress now ");
      }
      addToBadgeProgress(user, refId, badgeType, userBadgeActivity, refObj);
      userBadgeProgress = userBadgeActivity.getUserBadgeProgress().get(refId);
      if (userBadgeProgress == null) {
        /* user has completed all the badge for the badge type */
        return;
      }
    }
    
    switch (badgeType) {
    case Erti:
      /* check if last level is completed */
      if (!userBadgeProgress.isCompleted() && userBadgeProgress.getLevel() <= 5) {
        
        userBadgeProgress.increaseCompleteCount();
        if (userBadgeProgress.isCompleted()) {
          /* add the badge to completd badge */
          addToCompletedBadge(userBadgeProgress, userBadgeActivity, user, refObj);
          /* add the next level to the badge in progress */
          
          /* check if final level is achieved */
          UserBadge userBadge = userBadgeActivity.getCompletedBadges().get(refId);
          if (userBadge != null && userBadge.getLevel() < 5) {
            
            /* add the new badge to in progress mode */
            UserBadgeProgress newUserBadgeProgress = new UserBadgeProgress();
            newUserBadgeProgress.setBadgeType(BadgeType.Erti);
            newUserBadgeProgress.setRefId(refId);
            newUserBadgeProgress.setActiveProgress(true);
            newUserBadgeProgress.setLevel(userBadge.getLevel() + 1);
            newUserBadgeProgress.setContentReference(userBadgeProgress.getContentReference());
            int level = newUserBadgeProgress.getLevel();
            int totalCount = level == 1 || level == 2 ? 5 : level == 3 ? 10 : 15;
            newUserBadgeProgress.setTotalCount(totalCount);
            userBadgeActivity.getUserBadgeProgress().put(refId, newUserBadgeProgress);
          }
        }
      }
      break;
    case OrgPlan:
      ActionPlanDao actionPlan = (ActionPlanDao) refObj;
      if (actionPlan == null) {
        actionPlan = actionPlanFactory.getActionPlan(refId);
      }
      userBadgeProgress.setTotalCount(actionPlan.getActionCount());
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgressMap().get(refId);
      
      if (actionPlanProgress != null) {
        if (actionPlan.getActionCount() < actionPlanProgress.getCompletedCount()) {
          log.error("Invalid learnign program, data is not intialized properly" + refId + ", user"
              + user.getId());
          return;
        }
        /*
         * if step is deleted then no need to add user badge progress becuase it is already
         * completed
         */
        userBadgeProgress.setCompletedCount(actionPlanProgress.getCompletedCount());
      }
      if (userBadgeProgress.isCompleted()) {
        /* check if already badge is added or not */
        if (userBadgeActivity.getCompletedBadges().get(refId) == null) {
          addToCompletedBadge(userBadgeProgress, userBadgeActivity, user, refObj);
        } else {
          userBadgeActivity.getUserBadgeProgress().remove(userBadgeProgress.getRefId());
        }
      }
      break;
    case Tutorial:
      
      UserTutorialActivity userTutorialActivity = spTutorialFactory.getUserTutorialActivity(user);
      if (userTutorialActivity != null) {
        userBadgeProgress.setCompletedCount(userTutorialActivity.getUserActivityData(refId)
            .getCount());
      }
      SPTutorialDao tutorial = (SPTutorialDao) refObj;
      if (tutorial == null) {
        tutorial = spTutorialFactory.getTutorail(refId, user.getUserLocale());
      }
      
      userBadgeProgress.setTotalCount(tutorial.getActionCount());
      
      if (userBadgeProgress.isCompleted()) {
        if (userBadgeActivity.getCompletedBadges().get(refId) == null) {
          addToCompletedBadge(userBadgeProgress, userBadgeActivity, user, refObj);
        } else {
          userBadgeActivity.getUserBadgeProgress().remove(userBadgeProgress.getRefId());
        }
        
      }
      break;
    default:
      break;
    }
    
    /* use the awared on local date time for updated on to sort the badge in progress */
    userBadgeProgress.setAwarededOn(LocalDateTime.now());
    updateUserBadgeActivity(userBadgeActivity);
  }
  
  private void addToCompletedBadge(UserBadgeProgress userBadgeProgress,
      UserBadgeActivity userBadgeActivity, User user, Object refObj) {
    UserBadge userBadge = new UserBadge();
    userBadge.setBadgeType(userBadgeProgress.getBadgeType());
    userBadge.setLevel(userBadgeProgress.getLevel());
    userBadge.setRefId(userBadgeProgress.getRefId());
    userBadge.setContentReference(userBadgeProgress.getContentReference());
    userBadge.setAwarededOn(LocalDateTime.now());
    userBadgeActivity.getCompletedBadges().put(userBadgeProgress.getRefId(), userBadge);
    
    userBadgeActivity.getUserBadgeProgress().remove(userBadgeProgress.getRefId());
    /* send the sse to clear the locale storage and send the new profile badges for the user */
    sendSseForBadge(user);
    
    /* log Activity */
    logActivity(user, userBadge, refObj);
    
    logNotification(user, userBadge);
  }
  
  private void sendSseForBadge(User user) {
    
    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.PARAM_OPERATION, Operation.BADGE_COMPLETED);
    payload.put(Constants.PARAM_ACTION_PLAN, user.getId());
    MessageEventRequest messageEventRequest = MessageEventRequest.newEvent(
        ActionType.BadgeCompletion, payload, user);
    eventGateway.sendEvent(messageEventRequest);
  }
  
  /**
   * sendDashbaordMessage method will create a dashboard message for all users of the company.
   * 
   * @param user
   *          who has completd the badge.
   * @param userBadge
   *          user badge.
   */
  private void logActivity(User user, UserBadge userBadge, Object refObj) {
    
    String message = String.valueOf(userBadge.getLevel());
    Comment comment = Comment.newCommment(user, message);
    ContentReference contentReference = new ContentReference();
    updateContentReference(user, userBadge.getRefId(), userBadge.getBadgeType(), contentReference,
        refObj);
    comment.setContentReference(contentReference);
    String activityMessage = null;
    String title = contentReference.getTitle();
    String heading = MessagesHelper.getMessage("badge.dashboard.message", user.getLocale());
    switch (userBadge.getBadgeType()) {
    case Erti:
      
      String badgeIcon = "icon-badge_ertiLevel" + message;
      
      String description = MessagesHelper.getMessage("manageAccountContent.erti", user.getLocale())
          + " - " + MessagesHelper.getMessage("badge.erti.level", user.getLocale()) + " "
          + comment.getText();
      
      activityMessage = MessagesHelper.getMessage(LogActionType.BadgeCompleted.getActivityKey(),
          user.getLocale(), badgeIcon, title, description, heading);
      break;
    
    case OrgPlan:
      String orgBadgeIcon = "icon-badge_learningprogram";
      
      String orgDescription = MessagesHelper.getMessage("manageAccountContent.organization",
          user.getLocale());
      
      activityMessage = MessagesHelper.getMessage(LogActionType.BadgeCompleted.getActivityKey(),
          user.getLocale(), orgBadgeIcon, title, orgDescription, heading);
      break;
    
    case Tutorial:
      
      String tutBadgeIcon = "icon-badge_tutorial";
      
      String tutDescription = MessagesHelper.getMessage("badge.dashboard.tutorial",
          user.getLocale());
      
      activityMessage = MessagesHelper.getMessage(LogActionType.BadgeCompleted.getActivityKey(),
          user.getLocale(), tutBadgeIcon, title, tutDescription, heading);
      break;
    
    default:
      break;
    }
    
    LogActionType actionType = LogActionType.BadgeCompleted;
    LogRequest logRequest = new LogRequest(actionType, user, user);
    logRequest.setDoMessagesOverride(true);
    
    logRequest.addParam(Constants.PARAM_MESSAGE_ACTIVITY, activityMessage);
    notificationGateway.logActivity(logRequest);
    
    // DashboardMessage dashboardMessage = DashboardMessage.newMessage(comment, user.getCompanyId(),
    // true);
    // dashboardMessage.setType(DashboardMessageType.UserBadge);
    // dashboardMessage.setSrcId(userBadge.getRefId());
    // dashboardControllerHelper.createDashboardMessage(user, dashboardMessage);
  }
  
  // /**
  // * updateContentRefernece will update the localized content reference for the badges.
  // *
  // * @param user
  // * for which content is to be upated.
  // * @param refId
  // * reference id.
  // * @param badgeType
  // * badge type
  // * @param contentRefernce
  // * content reference
  // */
  // public void updateContentReference(User user, String refId, BadgeType badgeType,
  // ContentReference contentRefernce) {
  // updateContentReference(user, refId, badgeType, contentRefernce, "1");
  // }
  
  /**
   * updateContentRefernece will update the localized content reference for the badges.
   * 
   * @param user
   *          for which content is to be upated.
   * @param refId
   *          reference id.
   * @param badgeType
   *          badge type
   * @param contentRefernce
   *          content reference
   */
  public void updateContentReference(User user, String refId, BadgeType badgeType,
      ContentReference contentRefernce, Object refObject) {
    switch (badgeType) {
    case Erti:
      SPGoal goal = (SPGoal) refObject;
      if (goal == null) {
        goal = goalFactory.getGoal(refId, user.getUserLocale());
      }
      
      if (goal != null) {
        contentRefernce.setTitle(goal.getName());
        contentRefernce.setDescription(goal.getDescription());
        contentRefernce.setUrl(MessagesHelper.getMessage("og.Erti.url"));
        contentRefernce.setSpFeature(SPFeature.Erti);
      }
      
      break;
    case OrgPlan:
      ActionPlanDao actionPlan = (ActionPlanDao) refObject;
      if (actionPlan == null) {
        actionPlan = actionPlanFactory.getActionPlan(refId);
      }
      
      if (actionPlan != null) {
        contentRefernce.setTitle(actionPlan.getName());
        contentRefernce.setDescription(actionPlan.getDescription());
        contentRefernce.setUrl(MessagesHelper.getMessage("og.OrganizationPlan.url"));
        contentRefernce.setSpFeature(SPFeature.OrganizationPlan);
        
      }
      
      break;
    case Tutorial:
      SPTutorialDao tutorail = (SPTutorialDao) refObject;
      if (tutorail == null) {
        tutorail = spTutorialFactory.getTutorail(refId, user.getUserLocale());
      }
      contentRefernce.setTitle(tutorail.getName());
      contentRefernce.setDescription(tutorail.getDescription());
      contentRefernce.setSpFeature(SPFeature.SPTutorial);
      break;
    
    default:
      break;
    }
  }
  
  /**
   * <code>deleteBadgeProgress</code> will delete the badge progress from all the user of the
   * company when the learning program or tutorial or spGoal is made inactive for the system.
   * 
   * @param refId
   *          is the reference id.
   */
  @Async
  public void deleteBadgeProgress(String refId) {
    
    /* Get all the users in the system where the refId is to be deleted */
    List<User> allMembers = userRepository.findAllMembers(true);
    
    deleteBadgeProgress(refId, allMembers);
  }
  
  /**
   * <code>deleteBadgeProgress</code> will delete the badge progress from all the user of the
   * company when the learning program or tutorial or spGoal is made inactive for the system.
   * 
   * @param refId
   *          is the reference id.
   */
  @Async
  public void deleteBadgeProgressForCompany(String refId, String companyId) {
    
    /* Get all the users in the system where the refId is to be deleted */
    List<User> allMembers = userRepository.findUsers(Constants.ENTITY_COMPANY_ID, companyId);
    
    deleteBadgeProgress(refId, allMembers);
  }
  
  public void deleteBadgeProgress(String refId, List<User> allMembers) {
    allMembers.parallelStream().forEach(u -> {
      deleteBadgeProgress(refId, u);
      
    });
  }
  
  public void deleteBadgeProgress(String refId, User u) {
    UserBadgeActivity userBadgeNoCache = factoryCache.getUserBadgeNoCache(u);
    UserBadgeProgress userBadgeProgress = userBadgeNoCache.getUserBadgeProgress().get(refId);
    if (userBadgeProgress != null) {
      switch (userBadgeProgress.getBadgeType()) {
      case Erti:
        if (userBadgeProgress.getCompletedCount() == 0 && userBadgeProgress.getLevel() == 1) {
          userBadgeNoCache.getUserBadgeProgress().remove(refId);
        }
        break;
      default:
        userBadgeNoCache.getUserBadgeProgress().remove(refId);
        break;
      }
      factoryCache.updateUserBadgeActivity(userBadgeNoCache);
    }
  }
  
  public void suspendBadgeProgress(String refId, User u) {
    UserBadgeActivity userBadgeCache = factoryCache.getUserBadge(u.getId());
    if (userBadgeCache != null) {
      UserBadgeProgress userBadgeProgress = userBadgeCache.getUserBadgeProgress().get(refId);
      if (userBadgeProgress != null) {
        userBadgeProgress.setActiveProgress(false);
        factoryCache.updateUserBadgeActivity(userBadgeCache);
      }
      
    }
  }
  
  public void activateBadgeProgress(String refId, User u) {
    UserBadgeActivity userBadgeCache = factoryCache.getUserBadge(u.getId());
    if (userBadgeCache != null) {
      UserBadgeProgress userBadgeProgress = userBadgeCache.getUserBadgeProgress().get(refId);
      if (userBadgeProgress != null) {
        userBadgeProgress.setActiveProgress(true);
        factoryCache.updateUserBadgeActivity(userBadgeCache);
      }
      
    }
  }
  
  public void logNotification(User user, UserBadge userBadge) {
    LogActionType actionType = userBadge.getBadgeType() == BadgeType.Erti ? LogActionType.BadgeCompletedErti
        : LogActionType.BadgeCompleted;
    LogRequest logRequest = new LogRequest(actionType, user, user);
    logRequest.setDoMessagesOverride(true);
    String message = MessagesHelper.getMessage(actionType.getMessageKey(), user.getLocale(),
        userBadge.getLevel(), userBadge.getContentReference().getTitle());
    logRequest.addParam(Constants.PARAM_NOTIFICATION_MESSAGE, message);
    notificationGateway.logNotification(logRequest);
  }
  
  public void resetBadge(User usr) {
    UserBadgeActivity userBadge = factoryCache.getUserBadgeNoCache(usr);
    if (userBadge != null) {
      factoryCache.resetUserBadge(userBadge);
    }
  }
  
}
