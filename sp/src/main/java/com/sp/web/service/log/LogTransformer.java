package com.sp.web.service.log;

import com.sp.web.Constants;
import com.sp.web.dto.log.NotificationLogMessageDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.log.ActivityLogMessage;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.NotificationLogMessage;
import com.sp.web.model.log.UserNotificationsSummary;
import com.sp.web.repository.log.LogsRepository;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The log transformer.
 */
public class LogTransformer {

  private static final Logger log = Logger.getLogger(LogTransformer.class);
  
  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  private LogsRepository logsRepository;
  
  /**
   * Transformer for the notification type of messages.
   * 
   * @param logRequest
   *          - log request
   * @return
   *      the notification log message
   */
  public NotificationLogMessage transformNotificationLogRequest(LogRequest logRequest) {
    if (log.isDebugEnabled()) {
      log.debug("Notification Transformer called !!!");
    }
    
    final LogActionType logActionType = logRequest.getLogActionType();
    final User byUser = logRequest.getByUser();
    UserMarkerDTO byUserDTO = new UserMarkerDTO(byUser);
    final User forUser = logRequest.getForUser();
    final UserMarkerDTO userDTO = (forUser != null) ? new UserMarkerDTO(
        forUser) : byUserDTO;
    
    NotificationLogMessage logMessage = new NotificationLogMessage();
    logMessage.setCreatedOn(LocalDateTime.now());
    // if log type has been overridden
    logMessage.setLogType(Optional.ofNullable(logRequest.getLogType())
                                  .orElse(logActionType.getLogType()));
    logMessage.setLogActionType(logActionType);
    logMessage.setUser(byUserDTO);
    logMessage.setMemberId(userDTO.getId());
    logMessage.setCompanyId(byUser.getCompanyId());
    logMessage.setUrlParam(logRequest.getParamAsString(Constants.PARAM_NOTIFICATION_URL_PARAM));
    String message = logRequest.getParamAsString(Constants.PARAM_NOTIFICATION_MESSAGE);
    // if log action default then override the message from request
    if (StringUtils.isBlank(message)) {
      message = MessagesHelper.getMessage(logActionType.getMessageKey(),byUser.getLocale(),
          byUserDTO.getName(), byUser.getEmail());
    }
    logMessage.setMessage(message);
    
    if (log.isDebugEnabled()) {
      log.debug("Log message :" + logMessage);
    }
    
    if (forUser != null) {
      // incrementing the notifications count
      UserNotificationsSummary userNotificationsSummary = logsRepository.getUserNotificationsSummary(forUser.getId());
      userNotificationsSummary.incrementCount();
      logsRepository.save(userNotificationsSummary);
      
      // send SSE
      Map<String, Object> payload = new HashMap<String, Object>();
      payload.put(Constants.PARAM_NOTIFICATION_COUNT, 1);
      payload.put(Constants.PARAM_NOTIFICATION_LOG_MESSAGES, new NotificationLogMessageDTO(
          logMessage));
      final boolean logNotificationBox = logActionType.isLogNotificationBox();
      payload.put(Constants.PARAM_NOTIFICATION_SHOW_BOX, logNotificationBox);
      if (logNotificationBox) {
        payload.put(Constants.PARAM_NOTIFICATION_LOG_MESSAGES, new NotificationLogMessageDTO(
            logMessage));
      }
      MessageEventRequest eventRequest = MessageEventRequest.newEvent(ActionType.NotificationLog,
          payload, forUser);
      eventGateway.sendEvent(eventRequest);
    }
    
    return logMessage;    
  }

  /**
   * Get the user from the log request.
   * 
   * @param byUser
   *          - by user
   * @return
   *    the user
   */
  private UserMarkerDTO getUser(User byUser) {
    UserMarkerDTO user = new UserMarkerDTO(byUser);
    if (byUser instanceof FeedbackUser) {
      user.setId(((FeedbackUser)byUser).getMemberId());
    }
    return user;
  }
  
  /**
   * Transform the activity log request.
   * 
   * @param logRequest
   *          - log request
   * @return
   *      the log activity message 
   */
  public ActivityLogMessage transformActivityLogRequest(LogRequest logRequest) {
    if (log.isDebugEnabled()) {
      log.debug("Activity Transformer called !!!");
    }
    final LogActionType logActionType = logRequest.getLogActionType();
    final User byUser = logRequest.getByUser();
    final UserMarkerDTO userDTO = getUser(byUser);
    
    ActivityLogMessage logMessage = new ActivityLogMessage();
    logMessage.setUser(userDTO);
    logMessage.setCompanyId(byUser.getCompanyId());
    logMessage.setCreatedOn(LocalDateTime.now());
    // if log type has been overridden
    logMessage.setLogType(Optional.ofNullable(logRequest.getLogType())
                                  .orElse(logActionType.getLogType()));
    logMessage.setLogActionType(logActionType);
    logMessage.setMemberId(userDTO.getId());
    // if log action default then override the title and message from request
    if (logRequest.isDoMessagesOverride()) {
      logMessage.setMessage((String)logRequest.getParam(Constants.PARAM_MESSAGE_ACTIVITY));
    } else {
      User forUser = Optional.ofNullable(logRequest.getForUser()).orElse(byUser);
      UserMarkerDTO forUserDTO = new UserMarkerDTO(forUser);
      logMessage.setMessage(MessagesHelper.getMessage(logActionType.getActivityKey(),
          byUser.getLocale(), forUserDTO.getName(),
          forUserDTO.getEmail()));
    }
    
    if (log.isDebugEnabled()) {
      log.debug("Log message :" + logMessage);
    }
    return logMessage;
  }

//  /**
//   * The generic message creator method.
//   * 
//   * @param logRequest
//   *          - log request          
//   * @param messageSupplier 
//   *          - log message supplier
//   * @return
//   *      the newly created log message
//   */
//  public LogMessage getMessasge(LogRequest logRequest, Supplier<LogMessage> messageSupplier) {
//
//  }
}
