package com.sp.web.service.log;

import org.springframework.integration.annotation.Gateway;

/**
 * @author Dax Abraham
 * 
 *         The gateway to log the messages.
 */
public interface LogGateway {

  /**
   * The gateway method to log the activity request.
   * 
   * @param logRequest
   *          - activity request
   */
  @Gateway(requestChannel = "inputActivityLogChannel")
  public void logActivity(LogRequest logRequest);

  /**
   * The gateway method to log the notification requests.
   * 
   * @param logRequest
   *            - notification request
   */
  @Gateway(requestChannel = "inputNotificationLogChannel")
  public void logNotification(LogRequest logRequest);

}
