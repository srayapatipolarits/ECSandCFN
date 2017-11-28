package com.sp.web.repository.log;

import com.sp.web.model.log.ActivityLogMessage;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.NotificationLogMessage;
import com.sp.web.model.log.UserNotificationsSummary;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The repository interface for the log messages.
 */
public interface LogsRepository {
  
  /**
   * Get the notification log messages.
   * 
   * @param memberId
   *          - member id
   * @param limitCount
   *          - the count to limit if -1 no limit will be applied
   * @return the list of notification logs
   */
  List<NotificationLogMessage> getNotificationLogs(String memberId, int limitCount);
  
  /**
   * Get the activity log messages.
   * 
   * @param memberId
   *          - member id
   * @param limitCount
   *          - the count to limit if -1 no limit will be applied
   * @return the list of activity logs
   */
  List<ActivityLogMessage> getActivityLogs(String memberId, int limitCount);
  
  /**
   * removeActivityLogs method will remove all the activity associated with the user.
   * 
   * @param memberId
   *          whose activity needs to be cleared.
   */
  void removeActivityLogs(String memberId);
  
  /**
   * removeNotificationLogs will remvoe the all notification for the user.
   * 
   * @param memberId
   *          whose notification are to be removed.
   */
  void removeNotificationLogs(String memberId);

  /**
   * Remove the notification logs for the given user and log action type.
   * 
   * @param userId
   *          - user id
   * @param logActionType
   *          - log action type
   */
  void removeNotificationLogs(String userId, LogActionType logActionType);
  
  /**
   * Gets the activity logs for the given company.
   *  
   * @param companyId
   *          - company id
   * @param limitCount
   *          - limit to apply
   * @return
   *    the list of activity log messages for the company
   *      
   */
  List<ActivityLogMessage> getCompanyActivityLogs(String companyId, int limitCount);

  /**
   * Get the user notifications summary.
   * 
   * @param userId
   *          - user id
   * @return
   *      the user notifications summary
   */
  UserNotificationsSummary getUserNotificationsSummary(String userId);

  /**
   * Update the user notifications summary and the DB.
   * 
   * @param userNotificationsSummary
   *            - user notifications summary
   */
  void save(UserNotificationsSummary userNotificationsSummary);

  /**
   * Update the notification log message in DB.
   * 
   * @param notificationLog
   *            - notification log message
   */
  void save(NotificationLogMessage notificationLog);
  
  /**
   * Get the notification log by id.
   * 
   * @param notificationId
   *            - notification id
   * @return
   *    the notification log message
   */
  NotificationLogMessage notificationsLogFindById(String notificationId);

}
