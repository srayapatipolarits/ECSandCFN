package com.sp.web.controller.dashboard;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.controller.profile.ProfileControllerHelper;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.dto.PracticeFeedbackRequestDTO;
import com.sp.web.dto.UserTaskDTO;
import com.sp.web.dto.badge.BadgeProgressDTO;
import com.sp.web.dto.feed.DashboardMessageCommentDTO;
import com.sp.web.dto.feed.DashboardMessageCommentsDTO;
import com.sp.web.dto.feed.DashboardMessageDTO;
import com.sp.web.dto.feed.DashboardMessageNewsFeedDTO;
import com.sp.web.dto.library.ArticleDetailsDto;
import com.sp.web.dto.lndfeedback.BaseDevelopmentFeedbackDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackListingDTO;
import com.sp.web.dto.user.UserDetailsDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.CommentForm;
import com.sp.web.form.Operation;
import com.sp.web.form.feed.DashboardMessageForm;
import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.model.badge.UserBadgeProgress;
import com.sp.web.model.bm.ta.ToneRequestType;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.DashboardMessageType;
import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.SPNote;
import com.sp.web.model.goal.SPNoteFeedbackType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.poll.SPMiniPoll;
import com.sp.web.model.poll.SPMiniPollResult;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.task.UserTask;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.bm.ToneAnalyserFactory;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;
import com.sp.web.utils.RandomGenerator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The dashboard controller helper.
 */
@Component
public class DashboardControllerHelper {
  
  /*
   * Commented for lack of clarity on email notification for comments. private static final String
   * MSG_OWNER = "msgOwner"; private static final String OWNER = "owner";
   */
  private static final Logger log = Logger.getLogger(DashboardControllerHelper.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  WorkspacePulseRepository pulseRepository;
  
  @Autowired
  SPNoteFeedbackRepository feedbackRepository;
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  NewsFeedFactory newsFeedFactory;
  
  @Autowired
  GroupRepository groupRepository;
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationProcessor;
  
  @Autowired
  EventGateway eventGateway;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  DevelopmentFeedbackFactory developmentFeedbackFactory;
  
  @Autowired
  ToneAnalyserFactory toneAnalyzerFactory;
  
  @Autowired
  ProfileControllerHelper profileControllerHelper;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  /*
   * The comparator for the dashboard message.
   */
  private static final Comparator<DashboardMessageNewsFeedDTO> comparator = (m1, m2) -> m1
      .getUpdatedOn().compareTo(m2.getUpdatedOn());
  
  /**
   * Helper method to add the tasks for the given company.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get task list for the user
   */
  public SPResponse getTasks(User user) {
    final SPResponse resp = new SPResponse();
    final List<UserTask> taskList = user.getTaskList();
    if (!taskList.isEmpty()) {
      List<UserTaskDTO> userTaskDTOs = new ArrayList<UserTaskDTO>();
      List<UserTask> userTasksToDelete = new ArrayList<UserTask>();
      Iterator<UserTask> taskIterator = taskList.iterator();
      while (taskIterator.hasNext()) {
        UserTask task = taskIterator.next();
        switch (task.getType()) {
        case WorkspacePulse:
          LocalDate endDate = LocalDate.parse((String) task.getParam(Constants.PARAM_END_DATE));
          String pulsRequestId = (String) task.getParam(Constants.PARAM_PULSE_REQUEST_ID);
          PulseRequest pulseRequest = pulseRepository.findPulseRequestById(pulsRequestId);
          // check if the pulse is ended
          if (endDate.isBefore(LocalDate.now()) || pulseRequest == null) {
            userTasksToDelete.add(task);
          } else {
            final UserTaskDTO taskDTO = new UserTaskDTO(task);
            taskDTO.getParams().put(Constants.PARAM_END_DATE, MessagesHelper.formatDate(endDate));
            userTaskDTOs.add(taskDTO);
          }
          break;
        default:
          LocalDateTime taskEndDate = task.getEndDate();
          if (taskEndDate.isBefore(LocalDateTime.now())) {
            userTasksToDelete.add(task);
          } else {
            userTaskDTOs.add(new UserTaskDTO(task));
          }
        }
      }
      
      if (!userTasksToDelete.isEmpty()) {
        // removing the pulse request from the company
        taskList.removeAll(userTasksToDelete);
        userFactory.updateUserAndSession(user);
      }
      
      resp.add(Constants.PARAM_TASK_LIST, userTaskDTOs);
    }
    resp.isSuccess();
    return resp;
  }
  
  /**
   * Get the top notes and feedback for the users.
   * 
   * @param user
   *          - logged in user
   * @return the response for the get notes and feedback request.
   */
  public SPResponse getNotesAndFeedback(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the list of top notes and feedback for dashboard
    List<SPNote> findTopNotesFeedback = feedbackRepository.findTopNotesFeedback(user.getId());
    
    // array to store the DTO list
    List<PracticeFeedbackRequestDTO> allNotesFeedbackDTOList = new ArrayList<PracticeFeedbackRequestDTO>();
    
    // loop over all the notes and feedback and create the dto objects
    for (SPNote spNoteFeedback : findTopNotesFeedback) {
      
      FeedbackUser memberUser = null;
      
      // check if feedback type then add the user
      if (spNoteFeedback.getType() == SPNoteFeedbackType.FEEDBACK) {
        PracticeFeedback practiceFeedback = (PracticeFeedback) spNoteFeedback;
        memberUser = userFactory.getFeedbackUser(practiceFeedback.getFeedbackUserId());
        
      }
      
      // get the goal to set in DTO
      SPGoal goal = goalsFactory.getGoal(spNoteFeedback.getGoalId());
      
      // create the DTO
      PracticeFeedbackRequestDTO feedbackRequestDTO = new PracticeFeedbackRequestDTO(
          spNoteFeedback, memberUser, goal);
      allNotesFeedbackDTOList.add(feedbackRequestDTO);
    }
    
    // adding to the response to send back
    resp.add("allNotesFeedback", allNotesFeedbackDTOList);
    
    // sending back the response
    return resp;
  }
  
  /**
   * Helper method to get the users news feed.
   * 
   * @param user
   *          - user
   * @return the news feed for the user
   */
  public SPResponse getNewsFeed(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    int startIdx = (int) params[0];
    
    // get the user news feed
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    
    // get the news feed list to iterate
    final List<NewsFeed> newsFeeds = (startIdx == 0) ? newsFeedHelper.getNewsFeeds()
        : newsFeedHelper.getNewsFeeds(startIdx);
    
    List<Object> feedArray = new ArrayList<Object>(Constants.NEWS_FEED_DASHBOARD_LIMIT);
    
    // process the news feed messages for details
    if (!CollectionUtils.isEmpty(newsFeeds)) {
      // create a new feed array to store the response in
      // iterate over all the feeds till the dashboard limit is reached
      int index = startIdx;
      int counter = 0;
      for (NewsFeed newsFeed : newsFeeds) {
        index++;
        // process the news feed for its details
        final Object newsFeedResponse = processResponse(user, newsFeed);
        // add to response array if news feed present
        if (newsFeedResponse != null) {
          feedArray.add(newsFeedResponse);
          counter++;
          // check if the feed has reached the limit
          if (counter == Constants.NEWS_FEED_DASHBOARD_LIMIT) {
            resp.add(Constants.PARAM_PAGINATION, true);
            resp.add(Constants.PARAM_START_IDX, index);
            break;
          }
        }
      }
    }
    
    // adding the feed to the response
    // sending back the response
    return resp.add(Constants.PARAM_NEWS_FEED, feedArray);
  }
  
  /**
   * Helper method to get the users news feed.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get user news feed request
   */
  public SPResponse getUserNewsFeed(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    int startIdx = (int) params[0];
    
    String userEmail = (String) params[1];
    
    User userForFeed = user;
    if (!StringUtils.isBlank(userEmail)) {
      User userByEmail = userFactory.getUserByEmail(userEmail);
      Assert.notNull(userByEmail, "User not found.");
      Assert.isTrue(user.isSameCompany(userByEmail), "Unauthorised reqeust.");
      userForFeed = userByEmail;
    }
    
    // get the user news feed
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    
    // get the news feed list to iterate
    final List<NewsFeed> newsFeeds = (startIdx == 0) ? newsFeedHelper.getNewsFeeds()
        : newsFeedHelper.getNewsFeeds(startIdx);
    
    List<Object> feedArray = new ArrayList<Object>(Constants.NEWS_FEED_DASHBOARD_LIMIT);
    
    // process the news feed messages for details
    if (!CollectionUtils.isEmpty(newsFeeds)) {
      // create a new feed array to store the response in
      // iterate over all the feeds till the dashboard limit is reached
      int index = startIdx;
      int counter = 0;
      for (NewsFeed newsFeed : newsFeeds) {
        index++;
        // process the news feed for its details
        final Object newsFeedResponse = processResponse(userForFeed, newsFeed, true);
        // add to response array if news feed present
        if (newsFeedResponse != null) {
          feedArray.add(newsFeedResponse);
          counter++;
          // check if the feed has reached the limit
          if (counter == Constants.NEWS_FEED_DASHBOARD_LIMIT) {
            resp.add(Constants.PARAM_PAGINATION, true);
            resp.add(Constants.PARAM_START_IDX, index);
            break;
          }
        }
      }
    }
    
    // adding the feed to the response
    // sending back the response
    return resp.add(Constants.PARAM_NEWS_FEED, feedArray);
  }
  
  /**
   * Helper method to get the user profile feed.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get user profile feed
   */
  public SPResponse getUserProfileFeed(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final int startIdx = (int) params[0];
    String userId = (String) params[1];
    
    User userForFeed = userFactory.getUser(userId);
    Assert.notNull(userForFeed, "User not found.");
    Assert.isTrue(user.isSameCompany(userForFeed), "Unauthorised reqeust.");
    
    // get the user news feed
    List<DashboardMessage> dashboardMessages = newsFeedFactory.getDashbaordMessageByOwner(userId);
    Comparator<DashboardMessage> updatedOnComp = (db1, db2) -> db1.getUpdatedOn().compareTo(
        db2.getUpdatedOn());
    dashboardMessages = dashboardMessages.stream().sorted(updatedOnComp.reversed())
        .collect(Collectors.toList());
    if (startIdx > 0) {
      if (startIdx < dashboardMessages.size()) {
        dashboardMessages = dashboardMessages.subList(startIdx, dashboardMessages.size());
      } else {
        dashboardMessages = null;
      }
    }
    
    // process the news feed messages for details
    if (!CollectionUtils.isEmpty(dashboardMessages)) {
      List<DashboardMessageNewsFeedDTO> feedArray = new ArrayList<DashboardMessageNewsFeedDTO>(
          Constants.NEWS_FEED_DASHBOARD_LIMIT);
      
      // create a new feed array to store the response in
      // iterate over all the feeds till the dashboard limit is reached
      final String memberId = user.getId();
      int index = startIdx;
      int counter = 0;
      for (DashboardMessage dashboardMessage : dashboardMessages) {
        
        log.info("checking the newsfeed" + dashboardMessage.canShow(memberId) + ", is default "
            + dashboardMessage.getType() + ", dashboaredMeesage" + dashboardMessage.getId());
        // only dashboard messages are processed
        if (dashboardMessage.getType() != DashboardMessageType.Default
            || !dashboardMessage.canShow(memberId)) {
          continue;
        }
        
        index++;
        feedArray.add(new DashboardMessageNewsFeedDTO(user, dashboardMessage));
        counter++;
        if (counter == Constants.NEWS_FEED_DASHBOARD_LIMIT) {
          resp.add(Constants.PARAM_PAGINATION, true);
          resp.add(Constants.PARAM_START_IDX, index);
          break;
        }
      }
      
      // sort the dashboard messages
      feedArray.sort(comparator.reversed());
      
      // adding the feed to the response
      // sending back the response
      resp.add(Constants.PARAM_NEWS_FEED, feedArray);
    }
    
    // add the initialization data as well
    if (startIdx == 0) {
      UserDetailsDTO userDetailsDTO = new UserDetailsDTO(userForFeed);
      userDetailsDTO.updateBadge(userForFeed, badgeFactory);
      resp.add(Constants.PARAM_MEMBER, userDetailsDTO);
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      boolean showRelationshipAdvisor = false;
      if (company.hasFeature(SPFeature.RelationShipAdvisor)) {
        if (company.isRestrictRelationShipAdvisor()) {
          if (userForFeed.hasGroupAssociation(user.getGroupAssociationList())) {
            showRelationshipAdvisor = true;
          }
        } else {
          showRelationshipAdvisor = true;
        }
        
        if (user.getRoles().contains(RoleType.AccountAdministrator)) {
          showRelationshipAdvisor = true;
        }
      }
      resp.add(Constants.PARAM_RELATIONSHIP_ADVISOR_SHOW, showRelationshipAdvisor);
      profileControllerHelper.addPersonalityAnalysis(userForFeed, resp);
    }
    
    return resp;
  }
  
  /**
   * Get all the announcements for the given company.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get announcement request
   */
  public SPResponse getAnnouncements(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    int startIdx = (int) params[0];
    
    // get the user news feed
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    
    // get the news feed list to iterate
    final List<NewsFeed> newsFeeds = (startIdx == 0) ? newsFeedHelper.getNewsFeeds()
        : newsFeedHelper.getNewsFeeds(startIdx);
    
    List<Object> feedArray = new ArrayList<Object>(Constants.NEWS_FEED_DASHBOARD_LIMIT);
    
    // process the news feed messages for details
    if (!CollectionUtils.isEmpty(newsFeeds)) {
      // create a new feed array to store the response in
      // iterate over all the feeds till the dashboard limit is reached
      int index = startIdx;
      int counter = 0;
      for (NewsFeed newsFeed : newsFeeds) {
        index++;
        if (newsFeed.getType() == NewsFeedType.DashboardMessage) {
          DashboardMessage message = newsFeedFactory.getDashboardMessage(newsFeed.getFeedRefId());
          if (message != null) {
            if (message.getType() == DashboardMessageType.Announcement) {
              final Object newsFeedResponse = processResponse(user, newsFeed, false);
              // add to response array if news feed present
              if (newsFeedResponse != null) {
                feedArray.add(newsFeedResponse);
                counter++;
                // check if the feed has reached the limit
                if (counter == Constants.NEWS_FEED_DASHBOARD_LIMIT) {
                  resp.add(Constants.PARAM_PAGINATION, true);
                  resp.add(Constants.PARAM_START_IDX, index);
                  break;
                }
              }
            }
          }
        }
      }
    }
    
    // adding the feed to the response
    // sending back the response
    return resp.add(Constants.PARAM_NEWS_FEED, feedArray);
  }
  
  /**
   * Helper method to get the dashboard message details.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get message details request
   */
  public SPResponse getDashboardMessageDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the form to process
    String messageId = (String) params[0];
    Assert.hasText(messageId, "Message id required.");
    
    boolean commentsOnly = (boolean) params[1];
    boolean msgDetailsPage = (boolean) params[2];
    
    // get the dashboard message
    DashboardMessage dashboardMessage = newsFeedFactory.getDashboardMessage(messageId);
    Assert.notNull(dashboardMessage, MessagesHelper.getMessage("dashboard.post.notFound"));
    
    // Authorize the user
    dashboardMessage.authoriseUser(user);
    
    // send the response back
    final String userId = user.getId();
    
    if (msgDetailsPage) {
      return resp.add(Constants.PARAM_DASHBOARD_MESSAGE,
          new DashboardMessageNewsFeedDTO(user.getId(), dashboardMessage, msgDetailsPage));
    } else {
      return resp.add(Constants.PARAM_DASHBOARD_MESSAGE,
          (commentsOnly) ? new DashboardMessageCommentsDTO(dashboardMessage, userId)
              : new DashboardMessageDTO(dashboardMessage, userId, msgDetailsPage));
    }
  }
  
  /**
   * Controller method to create a new dashboard message.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add message
   */
  public SPResponse newDashboardMessage(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the form to process
    DashboardMessageForm form = (DashboardMessageForm) params[0];
    form.validate();
    
    final boolean allMembers = form.isAllMembers();
    
    // get the dashboard message
    DashboardMessage message = form.getMessage(user, companyFactory);
    
    // if not for all members then get the list of members
    // the message is applicable for
    if (!allMembers) {
      // get the members that are applicable for this message
      final List<String> memberIds = getMemberList(user, form);
      message.setMemberIds(memberIds);
    }
    
    // adding the tagged members
    final List<User> taggedMembers = getTaggedMembers(message, form.getTaggedMemberIds());
    
    createDashboardMessage(user, message);
    
    // sending the notifications
    // creating the notification parameters
    final Comment dbMessage = message.getMessage();
    if (!CollectionUtils.isEmpty(taggedMembers)) {
      final Map<String, Object> notificationParams = new HashMap<String, Object>();
      notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE, dbMessage);
      final String messageId = message.getId();
      notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, messageId);
      notificationParams.put(Constants.PARAM_NOTIFICATION_URL_PARAM, messageId);
      notificationParams.put(Constants.PARAM_USER, Optional.ofNullable(dbMessage.getOnBehalfUser())
          .orElse(dbMessage.getUser()));
      ContentReference contentReference = dbMessage.getContentReference();
      notificationParams.put("hasReference",
          contentReference != null && contentReference.isValid());
      
      taggedMembers.forEach(u -> {
          notificationProcessor.process(NotificationType.DashboardMessageTagged, user, u,
              notificationParams);
        });
    }
    
    if (form.isSendEmail()
        && user.hasAnyRole(RoleType.AccountAdministrator, RoleType.GroupLead,
            RoleType.SuperAdministrator)) {
      final Map<String, Object> notificationParams = new HashMap<String, Object>();
      notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE, dbMessage);
      final String messageId = message.getId();
      notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, messageId);
      notificationParams.put(Constants.PARAM_NOTIFICATION_URL_PARAM, messageId);
      notificationParams.put(Constants.PARAM_USER, Optional.ofNullable(dbMessage.getOnBehalfUser())
          .orElse(dbMessage.getUser()));
      ContentReference contentReference = dbMessage.getContentReference();
      notificationParams.put("hasReference",
          contentReference != null && contentReference.isValid());
      List<User> notificationMembers;
      if (form.isAllMembers()) {
        notificationMembers = userFactory.getAllMembersForCompany(user.getCompanyId());
      } else {
        ArrayList<String> notificationMemberIds = new ArrayList<String>(message.getMemberIds());
        notificationMemberIds.removeAll(form.getTaggedMemberIds());
        notificationMembers = userFactory.getUsers(notificationMemberIds);
      }
      notificationMembers.remove(user);
      final NotificationType notificationType = form.isAnnouncement() ? NotificationType.DashboardMessageAnnouncement
          : NotificationType.DashboardMessageCommunication;
      notificationMembers.forEach(u -> {
          notificationProcessor.process(notificationType, user, u, notificationParams);
        });
    }
    
    /* send the minipoll notification email in case send notification is checked */
    sendMinipollNotification(user, allMembers, message, dbMessage);
    
    // send the notification in case notification flag is on for SPMInig poll.
    
    final String messageText = dbMessage.getText();
    if (!StringUtils.isBlank(messageText)) {
      // analyzing the tone of the comment
      toneAnalyzerFactory.process(ToneRequestType.DashboardMessageComment, messageText, user);
    }
    
    // sending back the response
    return resp.add(Constants.PARAM_DASHBOARD_MESSAGE, new DashboardMessageNewsFeedDTO(user,
        message));
  }
  
  /**
   * createDashboardMessage is to be called to create a new DashboardMesage. This method is called
   * from the BadgeFactory as a convieneance method.
   * 
   * @param user
   *          who is created the dashboard message.
   * @param message
   *          dashboardMessage.
   */
  public void createDashboardMessage(User user, DashboardMessage message) {
    // creating the dashboard message in db
    newsFeedFactory.createDashbaordMessage(message);
    
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);
    
    // sending Sse event
    Map<String, Object> messagePayLoad = new HashMap<String, Object>();
    messagePayLoad.put(Constants.PARAM_OPERATION, Operation.ADD);
    
    sendSseEvent(user, message, messagePayLoad);
  }
  
  @Async
  private void sendMinipollNotification(User user, final boolean allMembers,
      DashboardMessage message, final Comment dbMessage) {
    if (dbMessage.getMiniPoll() != null && dbMessage.getMiniPoll().isSendNotificationEmail()) {
      final Map<String, Object> notificationParams = new HashMap<String, Object>();
      notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE, dbMessage);
      notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getId());
      notificationParams.put(Constants.PARAM_USER, Optional.ofNullable(dbMessage.getOnBehalfUser())
          .orElse(dbMessage.getUser()));
      List<User> allUsers;
      if (!allMembers) {
        allUsers = new ArrayList<User>();
        List<String> memberIds = message.getMemberIds();
        memberIds.forEach(mid -> {
          if (!mid.equalsIgnoreCase(user.getId())) {
            allUsers.add(userFactory.getUser(mid));
          }
          
        });
      } else {
        allUsers = userFactory.getAllMembersForCompany(user.getCompanyId());
      }
      
      allUsers.forEach(u -> {
        notificationProcessor
            .process(NotificationType.MiniPollCreated, user, u, notificationParams);
      });
      
    }
  }
  
  /**
   * Helper method to update the dashboard message.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse updateDashboardMessage(User user, Object[] params) {
    // sending back the response
    return new SPResponse().add(process(user, (DashboardMessageForm) params[0], Operation.UPDATE));
  }
  
  /**
   * Helper method to add a comment to the dashboard message.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add comment request
   */
  public SPResponse addCommentDashboardMessage(User user, Object[] params) {
    // sending back the response
    return new SPResponse().add(process(user, (DashboardMessageForm) params[0],
        Operation.ADD_COMMENT));
  }
  
  /**
   * Helper method to like a dashboard message.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the like comment request
   */
  public SPResponse likeDashboardMessage(User user, Object[] params) {
    // sending back the response
    return new SPResponse().add(process(user, (DashboardMessageForm) params[0],
        Operation.LIKE_COMMENT));
  }
  
  /**
   * Helper method to get the list of users who have liked the message.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the liked users
   */
  public SPResponse dashboardMessageLikeUserInfo(User user, Object[] params) {
    
    String messageId = (String) params[0];
    Assert.hasText(messageId, "Message id is required.");
    
    int cid = (int) params[1];
    int childCid = (int) params[2];
    
    DashboardMessage dashboardMessage = getDashboardMessage(messageId);
    
    Map<String, UserMarkerDTO> likedByMembers = null;
    if (cid >= 0) {
      Comment comment = dashboardMessage.getComment(cid);
      if (childCid >= 0) {
        Comment childComment = comment.getComment(childCid);
        likedByMembers = childComment.getLikedByMembers();
      } else {
        likedByMembers = comment.getLikedByMembers();
      }
    } else {
      likedByMembers = dashboardMessage.getLikedByMembers();
    }
    
    // sending back the response
    return new SPResponse().add(Constants.PARAM_DASHBOARD_MESSAGE_LIKE_USERS,
        (likedByMembers != null) ? likedByMembers.values() : null);
  }
  
  /**
   * Helper method to delete a dashboard message.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the message delete request
   */
  public SPResponse deleteDashboardMessage(User user, Object[] params) {
    // sending back the response
    return new SPResponse().add(process(user, (DashboardMessageForm) params[0], Operation.DELETE));
  }
  
  /**
   * Helper method to delete the dashboard message comment.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the message comment delete request
   */
  public SPResponse deleteComment(User user, Object[] params) {
    
    String messageId = (String) params[0];
    Assert.hasText(messageId, "Message id required.");
    
    int cid = (int) params[1];
    int childCid = (int) params[2];
    
    DashboardMessage message = getDashboardMessage(messageId);
    
    Assert.isTrue(cid >= 0, "Comment id required.");
    Comment comment = message.getComment(cid);
    
    if (childCid >= 0) {
      Comment childComment = comment.getComment(childCid);
      isOwnerOrCommentOwner(message, childComment, user);
      comment.deleteComment(childComment);
    } else {
      isOwnerOrCommentOwner(message, comment, user);
      message.deleteComment(comment);
    }
    
    newsFeedFactory.updateDashboardMessage(message);
    
    // sending back the response
    final Map<String, Object> messagePayLoad = new HashMap<String, Object>();
    messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getRefId());
    messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_COMMENT_ID, cid);
    messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_CHILD_COMMENT_ID, childCid);
    messagePayLoad.put(Constants.PARAM_USER_ID, user.getId());
    messagePayLoad.put(Constants.PARAM_OPERATION, Operation.DELETE_COMMENT);
    
    // sending the sse event
    sendSseEvent(user, message, messagePayLoad);
    
    return new SPResponse().add(messagePayLoad);
  }
  
  private void isOwnerOrCommentOwner(DashboardMessage message, Comment childComment, User user) {
    if (!message.isOwner(user.getId())) {
      Assert.isTrue(childComment.isOwner(user.getId()), "Unauthorized request.");
    }
  }
  
  /**
   * Helper method to follow unfollow dashboard messages.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the message delete request
   */
  public SPResponse followUnfollowDashboardMessage(User user, Object[] params) {
    
    String messageId = (String) params[0];
    
    boolean follow = (boolean) params[1];
    
    DashboardMessage message = getDashboardMessage(messageId);
    
    // set the follow unfollow flag
    message.follow(user.getId(), follow);
    
    // updating the dashboard message
    newsFeedFactory.updateDashboardMessage(message);
    
    // sending back the response
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to block unblock commenting for a message post.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return response to the block unblock request
   */
  public SPResponse blockCommenting(User user, Object[] params) {
    
    String messageId = (String) params[0];
    boolean blockCommenting = (boolean) params[1];
    
    DashboardMessage message = getDashboardMessage(messageId);
    
    Assert.isTrue(message.isOwner(user.getId()), "Unauthorized request.");
    
    if (message.isBlockCommenting() != blockCommenting) {
      message.setBlockCommenting(blockCommenting);
      // updating the dashboard message
      newsFeedFactory.updateDashboardMessage(message);
      Map<String, Object> messagePayLoad = new HashMap<String, Object>();
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getRefId());
      messagePayLoad.put(Constants.PARAM_USER_ID, user.getId());
      messagePayLoad.put(Constants.PARAM_OPERATION, Operation.COMMENT_BLOCKED);
      sendSseEvent(user, message, messagePayLoad);
    }
    
    // sending back the response
    return new SPResponse().isSuccess();
  }
  
  private Map<String, Object> process(User user, DashboardMessageForm form, Operation op) {
    
    // validate the form
    form.validate(op);
    
    DashboardMessage message = getDashboardMessage(form.getMessageId());
    
    // authorize the request
    message.authoriseUser(user);
    
    // get the news feed object
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    
    // send out the SSE event
    final Map<String, Object> messagePayLoad = new HashMap<String, Object>();
    
    final String userId = user.getId();
    final boolean messageOwner = message.isOwner(userId);
    switch (op) {
    
    case UPDATE:
      Assert.isTrue(messageOwner, "Unauthorized request.");
      
      // delete the tagged members
      List<String> deletedTaggedMemberIds = form.getDeletedTaggedMemberIds();
      if (!CollectionUtils.isEmpty(deletedTaggedMemberIds)) {
        deletedTaggedMemberIds.forEach(message::removeTaggedMember);
      }
      
      // updating the message content
      form.updateMessage(message);
      
      // adding any new tagged members
      final List<User> taggedMembers = getTaggedMembers(message, form.getTaggedMemberIds());
      
      if (!CollectionUtils.isEmpty(taggedMembers)) {
        if (!message.isAllCompany()) {
          taggedMembers.forEach(message::addTaggedMember);
        }
      }
      
      // updating the message
      newsFeedFactory.updateDashboardMessage(message);
      
      final DashboardMessageDTO messageDTO = new DashboardMessageDTO(message, userId);
      if (!CollectionUtils.isEmpty(taggedMembers)) {
        // sending the notifications
        // creating the notification parameters
        final Map<String, Object> notificationParams = new HashMap<String, Object>();
        final Comment dbMessage = message.getMessage();
        notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE, messageDTO.getMessage());
        notificationParams.put(Constants.PARAM_USER,
            Optional.ofNullable(dbMessage.getOnBehalfUser()).orElse(dbMessage.getUser()));
        final String messageId = message.getId();
        notificationParams.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, messageId);
        notificationParams.put(Constants.PARAM_NOTIFICATION_URL_PARAM, messageId);
        ContentReference contentReference = dbMessage.getContentReference();
        notificationParams.put("hasReference",
            contentReference != null && contentReference.isValid());
        
        taggedMembers.forEach(u -> {
          notificationProcessor.process(NotificationType.DashboardMessageTagged, user, u,
              notificationParams);
        });
      }
      
      // send out the SSE event
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE, messageDTO);
      
      newsFeedHelper.updateNewsFeed(message);
      
      break;
    
    case DELETE:
      Assert.isTrue(messageOwner, "Unauthorized request.");
      
      // deleting from the company news
      newsFeedHelper.deleteNewsFeed(message);
      
      // delete the message
      newsFeedFactory.deleteDashboardMessage(message);
      
      // add the message id
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getRefId());
      break;
    
    case LIKE_COMMENT:
      final int likeCid = form.getCid();
      final int likeChildCid = form.getChildCid();
      MutableBoolean isLike = new MutableBoolean(true);
      
      // add the liked count
      final int likeCount = message.updatedLike(user, likeCid, likeChildCid, isLike);
      
      // updating the dashboard message
      newsFeedFactory.updateDashboardMessage(message);
      
      // updating the news feed
      newsFeedHelper.updateNewsFeed(message);
      
      // adding to the message payload
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getRefId());
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_COMMENT_ID, likeCid);
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_CHILD_COMMENT_ID, likeChildCid);
      messagePayLoad.put(Constants.PARAM_USER_ID, userId);
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_DO_LIKE, form.isDoLike());
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_LIKE_COUNT, likeCount);
      if (isLike.booleanValue()) {
        if (!messageOwner) {
          addNotificationLog(user, message, message.getOwnerId(), LogActionType.DashboardMessageLike);
        }
        addNotificationLog(user, message, message.getTaggedMemberIds(),
            LogActionType.DashboardMessageTaggedLike);
      }
      break;
    
    case ADD_COMMENT:
      
      Assert.isTrue(!message.isBlockCommenting(),
          "Message owner has turned off commenting on this message thread.");
      final CommentForm commentForm = form.getComment();
      // add the new comment
      final Comment newComment = commentForm.newComment(user);
      final int cid = form.getCid();
      Set<String> notificationEmails = null;
      if (cid >= 0) {
        // getting the comment with the given comment id
        Comment existingComment = message.getComment(cid);
        existingComment.addComment(newComment);
        final Set<String> sourceNotificationEmails = existingComment.getNotificationEmails();
        if (sourceNotificationEmails != null) {
          notificationEmails = new HashSet<String>(sourceNotificationEmails);
        } else {
          notificationEmails = new HashSet<String>();
        }
        
        if (!messageOwner) {
          existingComment.addToNotifications(user);
        }
        notificationEmails.add(existingComment.getUser().getEmail());
        
        // send out the SSE event
        messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE, new DashboardMessageCommentDTO(
            message, newComment, cid));
      } else {
        message.addComment(newComment);
        final Set<String> sourceNotificationEmails = message.getNotificationEmails();
        if (sourceNotificationEmails != null) {
          notificationEmails = new HashSet<String>(sourceNotificationEmails);
        } else {
          notificationEmails = new HashSet<String>();
        }
        
        if (!messageOwner) {
          message.addToNotifications(user);
        }
        
        // send out the SSE event
        messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE, new DashboardMessageCommentDTO(
            message, newComment));
      }
      
      messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getId());
      final String messageText = newComment.getText();
      if (!StringUtils.isBlank(messageText)) {
        // analyzing the tone of the comment
        toneAnalyzerFactory.process(ToneRequestType.DashboardMessageComment, messageText, user);
      }
      
      // updating the dashboard message
      newsFeedFactory.updateDashboardMessage(message);
      
      // updating the news feed
      newsFeedHelper.updateNewsFeed(message);
      
      // removing self
      notificationEmails.remove(user.getEmail());
      
      if (!messageOwner) {
        addNotificationLog(user, message, message.getOwnerId(),
            LogActionType.DashboardMessageComment);
      }
      
      addNotificationLog(user, message, message.getTaggedMemberIds(),
          LogActionType.DashboardMessageTaggedComment);
      
      List<String> taggedMemberIds = form.getTaggedMemberIds();
      if (!CollectionUtils.isEmpty(taggedMemberIds)) {
        messagePayLoad.put(Constants.PARAM_NOTIFICATION_URL_PARAM, message.getId());
        sendEmail(user, taggedMemberIds, messagePayLoad, NotificationType.DashboardMessageCommentTag);
      }
      /*
       * Commented owing to complexities of email's that have not been sorted out by the product
       * marketing team.
       * 
       * final UserMarkerDTO msgOwner = message.getMessage().getUser(); if
       * (!CollectionUtils.isEmpty(notificationEmails)) { messagePayLoad.put(MSG_OWNER, false);
       * messagePayLoad.put(OWNER, msgOwner); if (messageOwner) { messagePayLoad.put(
       * Constants.PARAM_SUBJECT,
       * MessagesHelper.getMessage("notification.subject.DashboardMessageOwnerComment",
       * user.getFirstName(), msgOwner.getFirstName())); messagePayLoad.put(
       * Constants.PARAM_EMAIL_ORANGE_BAND,
       * MessagesHelper.getMessage("dashboardPost.email.header.ownerComment", user.getFirstName(),
       * msgOwner.getFirstName())); } else { messagePayLoad.put( Constants.PARAM_SUBJECT,
       * MessagesHelper.getMessage("notification.subject.DashboardMessageComment",
       * user.getFirstName(), msgOwner.getFirstName())); messagePayLoad.put(
       * Constants.PARAM_EMAIL_ORANGE_BAND,
       * MessagesHelper.getMessage("dashboardPost.email.header.comment", user.getFirstName(),
       * msgOwner.getFirstName())); }
       * 
       * // send email notificationEmails .stream() .map(userFactory::getUserByEmail) .filter(u -> u
       * != null && !message.unfollowed(u)) .forEach( u ->
       * notificationProcessor.process(NotificationType.DashboardMessageComment, user, u,
       * messagePayLoad)); }
       * 
       * // send an email to all the people who were mentioned if any List<String> taggedMemberIds =
       * form.getTaggedMemberIds(); if (!CollectionUtils.isEmpty(taggedMemberIds)) {
       * messagePayLoad.remove(Constants.PARAM_SUBJECT); taggedMemberIds .stream()
       * .map(userFactory::getUser) .filter(u -> u != null) .forEach( u ->
       * notificationProcessor.process(NotificationType.DashboardMessageCommentTag, user, u,
       * messagePayLoad)); }
       * 
       * if (!messageOwner) { // send an email to the owner messagePayLoad.put(MSG_OWNER, true);
       * messagePayLoad.put( Constants.PARAM_SUBJECT,
       * MessagesHelper.getMessage("notification.subject.DashboardMessageCommentOwner",
       * user.getFirstName())); messagePayLoad.put( Constants.PARAM_EMAIL_ORANGE_BAND,
       * MessagesHelper.getMessage("dashboardPost.email.header.commentForOwner",
       * user.getFirstName()));
       * 
       * notificationProcessor.process(NotificationType.DashboardMessageComment, user, user,
       * messagePayLoad, false); }
       * 
       * // cleaning up message payload messagePayLoad.remove(OWNER);
       * messagePayLoad.remove(MSG_OWNER); messagePayLoad.remove(Constants.PARAM_SUBJECT);
       * messagePayLoad.remove(Constants.PARAM_EMAIL_ORANGE_BAND);
       */
      break;
    
    default:
      break;
    }
    
    // adding operation to the payload
    messagePayLoad.put(Constants.PARAM_OPERATION, op);
    
    // sending the sse event
    sendSseEvent(user, message, messagePayLoad);
    
    return messagePayLoad;
  }
  
  private void sendEmail(User user, List<String> taggedMemberIds,
      Map<String, Object> messagePayLoad, NotificationType type) {
    for (String userId : taggedMemberIds) {
      User usrFor = userFactory.getUser(userId);
      if (usrFor != null) {
        notificationProcessor.process(type, user, usrFor, messagePayLoad);
      }
    }
  }
  
  /**
   * Add a notification log for the given user.
   * 
   * @param user
   *          - user
   * @param message
   *          - message
   * @param userIdList
   *          - user for id list
   * @param type
   *          - log action type
   */
  private void addNotificationLog(User user, DashboardMessage message, List<String> userIdList,
      LogActionType type) {
    if (!CollectionUtils.isEmpty(userIdList)) {
      userIdList.forEach(u -> addNotificationLog(user, message, u, type));
    }
  }
  
  /**
   * Add the notification log for the given user.
   * 
   * @param user
   *          - user
   * @param message
   *          - message
   * @param userForId
   *          - user for id
   * @param type
   *          - notification type
   */
  private void addNotificationLog(User user, DashboardMessage message, String userForId,
      LogActionType type) {
    if (!user.getId().equalsIgnoreCase(userForId)) {
      User userFor = userFactory.getUser(userForId);
      if (userFor != null) {
        LogRequest logRequest = new LogRequest(type, user, userFor);
        logRequest.addParam(Constants.PARAM_NOTIFICATION_URL_PARAM, message.getId());
        logGateway.logNotification(logRequest);
      }
    }
  }
  
  private DashboardMessage getDashboardMessage(String messageId) {
    // get the message to update
    DashboardMessage message = newsFeedFactory.getDashboardMessage(messageId);
    Assert.notNull(message, MessagesHelper.getMessage("dashboard.post.notFound"));
    return message;
  }
  
  /**
   * Update the tagged members in the message.
   * 
   * @param message
   *          - dashboard message
   * @param taggedMemberIds
   *          - tagged member ids
   * @return the list of users
   */
  private List<User> getTaggedMembers(DashboardMessage message, final List<String> taggedMemberIds) {
    
    // removing any existing tagged members
    List<String> existingTaggedMemberIds = message.getTaggedMemberIds();
    if (!CollectionUtils.isEmpty(existingTaggedMemberIds)) {
      taggedMemberIds.removeAll(existingTaggedMemberIds);
    }
    
    // getting the list of newly tagged members
    List<User> taggedMembers = getTaggedMembers(taggedMemberIds);
    if (!CollectionUtils.isEmpty(taggedMembers)) {
      message.addTaggedMembers(taggedMembers.stream().map(UserMarkerDTO::new)
          .collect(Collectors.toList()));
    }
    return taggedMembers;
  }
  
  /**
   * Get the tagged members list.
   * 
   * @param taggedMemberIds
   *          - tagged member id's
   * @return the list of users
   */
  private List<User> getTaggedMembers(List<String> taggedMemberIds) {
    ArrayList<User> taggedMembers = new ArrayList<User>(taggedMemberIds.size());
    ArrayList<String> toRemoveIds = new ArrayList<String>();
    for (String memberId : taggedMemberIds) {
      User user = userFactory.getUser(memberId);
      if (user == null) {
        toRemoveIds.add(memberId);
      } else {
        if (!taggedMembers.contains(user)) {
          taggedMembers.add(user);
        } else {
          toRemoveIds.add(memberId);
        }
      }
    }
    taggedMemberIds.removeAll(toRemoveIds);
    return taggedMembers;
  }
  
  /**
   * Send out the SSE event.
   * 
   * @param companyId
   *          - company id
   * @param isAllCompany
   *          - is for all company
   * @param sseMemberIds
   *          - sseMemberIds
   * @param messagePayLoad
   *          - messagePayLoad
   */
  private void sendSseEvent(User user, DashboardMessage message, Map<String, Object> messagePayLoad) {
    try {
      final String companyId = user.getCompanyId();
      MessageEventRequest eventRequest = null;
      if (message.isAllCompany()) {
        eventRequest = MessageEventRequest.newEvent(ActionType.DashboardMessage, companyId,
            messagePayLoad, message.getUnfollowMemberIds());
      } else {
        List<String> sseMemberIds = new ArrayList<String>(message.getMemberIds());
        sseMemberIds.removeAll(message.getUnfollowMemberIds());
        eventRequest = MessageEventRequest.newEvent(ActionType.DashboardMessage, sseMemberIds,
            messagePayLoad, companyId);
      }
      eventRequest.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
      eventGateway.sendEvent(eventRequest);
    } catch (Exception e) {
      log.warn("Could not send SSE event.", e);
    }
  }
  
  /**
   * Get members from the given form.
   * 
   * @param user
   *          - logged in user
   * @param form
   *          - form
   * @return the members list
   */
  private List<String> getMemberList(User user, DashboardMessageForm form) {
    final List<String> memberIds = new ArrayList<String>();
    
    // adding the tagged member ids
    memberIds.addAll(form.getTaggedMemberIds());
    
    // adding the current user
    final String uid = user.getId();
    if (!memberIds.contains(uid)) {
      memberIds.add(uid);
    }
    
    // adding all the group users
    final List<String> groupIds = form.getGroupIds();
    if (!CollectionUtils.isEmpty(groupIds)) {
      groupIds.forEach(gId -> addGroupMembers(gId, memberIds, user));
    }
    // adding all the competency users
    final List<String> competencyIds = form.getCompetencyIds();
    if (!CollectionUtils.isEmpty(competencyIds)) {
      competencyIds.forEach(cId -> addCompetencyMembers(cId, memberIds, user));
    }
    
    // adding all the action plan members
    final List<String> actionPlanIds = form.getActionPlanIds();
    if (!CollectionUtils.isEmpty(actionPlanIds)) {
      actionPlanIds.forEach(apId -> addActionPlanMembers(apId, memberIds, user));
    }
    
    return memberIds;
  }
  
  /**
   * Add the member ids from the given action plan.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param memberIds
   *          - member ids to add
   * @param user
   *          - user
   */
  private void addActionPlanMembers(String actionPlanId, List<String> memberIds, User user) {
    ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
    if (actionPlan != null) {
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, user.getCompanyId());
      if (capSettings != null) {
        final List<String> capMemberIds = capSettings.getMemberIds();
        if (!CollectionUtils.isEmpty(capMemberIds)) {
          memberIds.addAll(capMemberIds);
        }
      }
    }
  }
  
  /**
   * Add all the members for the given competency profile id.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param memberIds
   *          - member id list
   * @param user
   *          - logged in user
   */
  private void addCompetencyMembers(String competencyProfileId, List<String> memberIds, User user) {
    Assert.isTrue(user.hasRole(RoleType.AccountAdministrator), "Unauthroised request.");
    List<User> allMembersWithCompetencyProfile = userFactory
        .getAllMembersWithCompetencyProfile(competencyProfileId);
    allMembersWithCompetencyProfile.stream().forEach(u -> {
      final String uid = u.getId();
      if (!memberIds.contains(uid)) {
        memberIds.add(uid);
      }
    });
  }
  
  /**
   * Add all the members from the group to the members list.
   * 
   * @param groupId
   *          - group id
   * @param memberIds
   *          - members list
   * @param user
   *          - user
   */
  private void addGroupMembers(String groupId, List<String> memberIds, User user) {
    // find the group
    UserGroup userGroup = groupRepository.findById(groupId);
    Assert.notNull(userGroup, "User group not found.");
    
    // validate if the user is the group lead
    final String groupLead = userGroup.getGroupLead();
    if (!user.hasRole(RoleType.AccountAdministrator)) {
      Assert
          .isTrue(groupLead != null && groupLead.equals(user.getEmail()), "Unauthorized request.");
    }
    
    // adding the group lead user
    if (!StringUtils.isBlank(groupLead)) {
      User groupLeadUser = userFactory.getUserByEmail(groupLead);
      if (groupLeadUser != null) {
        final String groupLeadUserId = groupLeadUser.getId();
        if (!memberIds.contains(groupLeadUserId)) {
          memberIds.add(groupLeadUserId);
        }
      }
    }
    
    // get all the members for the group
    userGroup.getMemberList().stream().map(userFactory::getUserByEmail).forEach(u -> {
      if (u != null && !memberIds.contains(u.getId())) {
        memberIds.add(u.getId());
      }
    });
  }
  
  /**
   * Process the given news feed.
   * 
   * @param user
   *          - user
   * @param newsFeed
   *          - news feed to process
   * @return the processed news feed
   */
  private Object processResponse(User user, NewsFeed newsFeed) {
    return processResponse(user, newsFeed, false);
  }
  
  /**
   * Process the given news feed.
   * 
   * @param user
   *          - user
   * @param newsFeed
   *          - news feed to process
   * @param filterByUser
   *          - filter by user
   * @return the processed news feed
   */
  private Object processResponse(User user, NewsFeed newsFeed, boolean filterByUser) {
    return newsFeedFactory.getProcessor(newsFeed.getType()).process(user, newsFeed, filterByUser);
  }
  
  /**
   * Get the development feedback for the user for dashbaord
   * 
   * @param user
   *          - logged in user
   * @return the response for the get notes and feedback request.
   */
  public SPResponse getDevelopmentFeedbacks(User user) {
    final SPResponse resp = new SPResponse();
    
    List<DevelopmentFeedbackListingDTO> feedbackListing = developmentFeedbackFactory.getAll(user);
    
    /* get the detail of the first feedback */
    if (!CollectionUtils.isEmpty(feedbackListing)) {
      
      DevelopmentFeedbackListingDTO developmentFeedbackListingDTO = feedbackListing.get(0);
      String devFeedRefId = developmentFeedbackListingDTO.getDevFeedRefId();
      List<BaseDevelopmentFeedbackDTO> allByDevFeedRefId = developmentFeedbackFactory
          .getAllByDevFeedRefId(user, devFeedRefId,
              SPFeature.valueOf(developmentFeedbackListingDTO.getSpFeature()));
      resp.add("feedbackDetail", allByDevFeedRefId);
    }
    // adding to the response to send back
    resp.add("developmentFeedbacks", feedbackListing);
    
    // sending back the response
    return resp;
  }
  
  /**
   * <code>answerMiniPoll</code> method capture the answer of the minipoll from the user.
   * 
   * @param user
   *          who is giving the mini poll
   * @param params
   *          contains the message id and answer selections.
   * @return the spresponse.
   */
  public SPResponse answerMiniPoll(User user, Object[] params) {
    String messageId = (String) params[0];
    
    // get the news feed object
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    
    DashboardMessage dashboardMessage = getDashboardMessage(messageId);
    
    /*
     * get the miniPoll *.
     */
    SPMiniPoll miniPoll = dashboardMessage.getMessage().getMiniPoll();
    Assert.notNull(miniPoll, "Invaild mesasgeId");
    
    SPMiniPollResult result = miniPoll.getResult();
    
    if (result == null) {
      /* first user who is doing the poll */
      result = new SPMiniPollResult(miniPoll.getOptions().size());
      miniPoll.setResult(result);
    }
    
    /* get the options */
    List<Integer> options = (List<Integer>) params[1];
    Assert.notEmpty(options, "No answer is present");
    result.updateResult(options, user.getId());
    
    // updating the message
    newsFeedFactory.updateDashboardMessage(dashboardMessage);
    
    final Map<String, Object> messagePayLoad = new HashMap<String, Object>();
    messagePayLoad.put(Constants.PARAM_OPERATION, Operation.VOTE_MINIPOLL);
    messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, dashboardMessage.getId());
    messagePayLoad.put(Constants.PARAM_MINIPOLL_RESULT, result);
    sendSseEvent(user, dashboardMessage, messagePayLoad);
    newsFeedHelper.updateNewsFeed(dashboardMessage);
    
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_MINIPOLL_RESULT, result);
    return response;
    
  }
  
  /**
   * <code>updateMiniPoll</code> method will update the mini poll and send the results back
   * 
   * @param user
   *          who is giving the mini poll
   * @param params
   *          contains the message id
   * @return the spresponse.
   */
  public SPResponse updateMiniPoll(User user, Object[] params) {
    String messageId = (String) params[0];
    
    String updateType = (String) params[1];
    
    DashboardMessage dashboardMessage = getDashboardMessage(messageId);
    
    /*
     * get the miniPoll *.
     */
    SPMiniPoll miniPoll = dashboardMessage.getMessage().getMiniPoll();
    Assert.notNull(miniPoll, "Invaild mesasgeId");
    
    SPMiniPollResult result = miniPoll.getResult();
    
    if (result == null) {
      /* first user who is doing the poll */
      result = new SPMiniPollResult(miniPoll.getOptions().size());
      miniPoll.setResult(result);
    }
    
    final Map<String, Object> messagePayLoad = new HashMap<String, Object>();
    
    switch (updateType) {
    case "end":
      miniPoll.setEndDate(LocalDateTime.now());
      messagePayLoad.put(Constants.PARAM_END_DATE, miniPoll.getEndDate());
      break;
    case "share":
      miniPoll.setHideResults(false);
      messagePayLoad.put(Constants.PARAM_MINIPOLL_SHARE, miniPoll.isHideResults());
      break;
    default:
      break;
    }
    
    // updating the message
    newsFeedFactory.updateDashboardMessage(dashboardMessage);
    
    messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, dashboardMessage.getId());
    messagePayLoad.put(Constants.PARAM_MINIPOLL_RESULT, result);
    messagePayLoad.put(Constants.PARAM_OPERATION, Operation.VOTE_MINIPOLL);
    
    sendSseEvent(user, dashboardMessage, messagePayLoad);
    // get the news feed object
    newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId()).updateNewsFeed(dashboardMessage);
    
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_MINIPOLL_RESULT, result);
    return response;
  }
  
  /**
   * Get the article for the user from the users prism portrait.
   * 
   * @param user
   *          - user
   * @return the article from users prism
   */
  public SPResponse getPrismArticle(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the goals for the users primary personality
    AnalysisBean analysis = user.getAnalysis();
    PersonalityType personalityType = null;
    if (analysis != null) {
      personalityType = analysis.getPersonality(RangeType.Primary);
      final String userLocale = user.getUserLocale();
      PersonalityPracticeArea personalityPracticeArea = goalsFactory
          .getPersonalityPracticeArea(personalityType);
      if (personalityPracticeArea != null) {
        List<String> goalIds = new ArrayList<>(personalityPracticeArea.getGoalIds());
        UserGoalDao userGoal = goalsFactory.getUserGoal(user.getUserGoalId(), userLocale);

        Map<String, UserArticleProgressDao> articleProgressMap = userGoal.getArticleProgressMap();
        while (!goalIds.isEmpty()) {
          String goalId = goalIds.get(RandomGenerator.getNextInt(goalIds.size()));
          List<ArticleDao> articles = articlesFactory.getArtilces(Constants.THEME, goalId,
              userLocale);
          List<ArticleDao> articlesList = articles.stream()
              .filter(a -> !articleProgressMap.containsKey(a.getId())).collect(Collectors.toList());
          if (!articlesList.isEmpty()) {
            ArticleDao articleDao = articlesList
                .get(RandomGenerator.getNextInt(articlesList.size()));
            resp.add(Constants.PARAM_ARTICLE, new ArticleDetailsDto(articleDao));
            SPGoal goal = goalsFactory.getGoal(goalId, userLocale);
            if (goal != null) {
              resp.add(Constants.PARAM_GOAL, new BaseGoalDto(goal));
              break;
            }
          } else {
            goalIds.remove(goalId);
          }
        }
      }
    }    
    return resp.isSuccess();
  }
  
  /**
   * getGoalsInProgress method will return the goals in progress for the user.
   * 
   * @param user
   *          is the logged in profile of the user.
   * @return the SPResponse.
   */
  public SPResponse getGoalsInProgress(User user) {
    
    UserBadgeActivity userBadge = badgeFactory.getUserBadge(user);
    
    List<BadgeProgressDTO> badgeProgressList = new ArrayList<BadgeProgressDTO>();
    /* iterate through all the badges and a */
    
    Map<String, UserBadgeProgress> userBadgeProgress = userBadge.getUserBadgeProgress();
    for (BadgeType badgeType : BadgeType.values()) {
      
      /* check if the feature is available to the use ror not */
      if (user.getRoles().contains(badgeType.getSpFeature().getRoles()[0])) {
        
        for (Entry<String, UserBadgeProgress> entry : userBadgeProgress.entrySet()) {
          if (entry.getValue().getBadgeType() == badgeType) {
            UserBadgeProgress badgeProgress = entry.getValue();
            if (!badgeProgress.isActiveProgress()) {
              continue;
            }
            BadgeProgressDTO badgeProgressDTO = new BadgeProgressDTO(badgeProgress);
            badgeProgressList.add(badgeProgressDTO);
          }
          
        }
        
      }
    }
    Comparator<BadgeProgressDTO> updatedOnComp = (db1, db2) -> db2.getUpdatedOn().compareTo(
        db1.getUpdatedOn());
    List<BadgeProgressDTO> sortedList = badgeProgressList.stream().sorted(updatedOnComp)
        .collect(Collectors.toList());
    SPResponse response = new SPResponse();
    response.add("goalsInProgress", sortedList);
    return response;
  }
}
