package com.sp.web.controller.log;

import com.sp.web.Constants;
import com.sp.web.dto.log.ActivityLogMessageDTO;
import com.sp.web.dto.log.NotificationLogMessageDTO;
import com.sp.web.model.User;
import com.sp.web.model.log.NotificationLogMessage;
import com.sp.web.model.log.UserNotificationsSummary;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.log.LogsRepository;
import com.sp.web.user.UserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The logs controller helper.
 */
@Component
public class LogsControllerHelper {
  
  private static final int PROFILE_ACTIVITY_COUNT = 5;
  
  @Autowired
  private LogsRepository logsRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * The controller helper to get the logs to show in the dashboard.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get logs request
   */
  public SPResponse getDashboardNotificationLogs(User user) {
    UserNotificationsSummary userNotificationsSummary = logsRepository
        .getUserNotificationsSummary(user.getId());
    return new SPResponse().add(Constants.PARAM_NOTIFICATION_COUNT,
        userNotificationsSummary.getCount());
  }

  /**
   * Helper method to reset the users notifications summary count.
   * 
   * @param user
   *          - user
   * @return
   *    the response to the user notifications summary reset request
   */
  public SPResponse resetNotificationLogsCount(User user) {
    resetNotificationsCount(user);
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to get all the notification logs for the user.
   * 
   * @param user
   *          - logged in user
   * @return response to the logs request
   */
  public SPResponse getAllNotificationLogs(User user) {
    return getNotificationLogs(user, -1);
  }
  
  /**
   * Get the notification logs.
   * 
   * @param user
   *          - user
   * @param count
   *          - count
   * @return the notification logs
   */
  private SPResponse getNotificationLogs(User user, int count) {
    final SPResponse resp = new SPResponse();
    /* filtering the log request for Growth after removing */
    resp.add(
        Constants.PARAM_NOTIFICATION_LOG_MESSAGES,
        logsRepository.getNotificationLogs(user.getId(), count).stream()
            .map(NotificationLogMessageDTO::new).collect(Collectors.toList()));
    resetNotificationsCount(user);
    return resp;
  }

  private void resetNotificationsCount(User user) {
    UserNotificationsSummary userNotificationsSummary = logsRepository
        .getUserNotificationsSummary(user.getId());
    userNotificationsSummary.setCount(0);
    logsRepository.save(userNotificationsSummary);
  }
  
  /**
   * The helper method to mark as read the notifications.
   *  
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the mark as read request
   */
  public SPResponse markAsRead(User user, Object[] params) {
    String notificationId = (String) params[0];
    Assert.hasText(notificationId, "Notification id required.");
    
    NotificationLogMessage notificationLog = logsRepository
        .notificationsLogFindById(notificationId);
    Assert.notNull(notificationLog, "Log not found.");
    
    notificationLog.setRead(true);
    logsRepository.save(notificationLog);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * The controller helper to get the logs to show in the dashboard.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get logs request
   */
  public SPResponse getDashboardActivityLogs(User user) {
    return new SPResponse().add(
        Constants.PARAM_ACTIVITY_LOG_MESSAGES,
        logsRepository.getCompanyActivityLogs(user.getCompanyId(), Constants.DASHBOARD_LOG_MESSAGE_LIMIT)
            .stream()
            .map(ActivityLogMessageDTO::new).collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get all the notification logs for the user.
   * 
   * @param user
   *          - logged in user
   * @return response to the logs request
   */
  public SPResponse getAllActivityLogs(User user) {
    return new SPResponse().add(Constants.PARAM_ACTIVITY_LOG_MESSAGES,
        logsRepository.getActivityLogs(user.getId(), -1).stream().map(ActivityLogMessageDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get the users activity logs.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the users activity log response
   */
  public SPResponse getUserActivityLogs(User user, Object[] params) {
    
    // get the user and validate access and permissions
    String userId = (String) params[0];
    User userForLogs = userFactory.getUser(userId);
    Assert.notNull(userForLogs, "User not found.");
    user.isSameCompany(userForLogs);
    
    return new SPResponse().add(
        Constants.PARAM_ACTIVITY_LOG_MESSAGES,
        logsRepository.getActivityLogs(userForLogs.getId(), PROFILE_ACTIVITY_COUNT).stream()
            .map(ActivityLogMessageDTO::new).collect(Collectors.toList()));
  }
  
}
