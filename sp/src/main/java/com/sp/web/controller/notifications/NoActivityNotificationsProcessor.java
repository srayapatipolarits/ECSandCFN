package com.sp.web.controller.notifications;

import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The default marker implementation for the notifications processor.
 * 
 */
@Component("noActivityNotificationProcessor")
public class NoActivityNotificationsProcessor extends NotificationsProcessor {

  /**
   * Doesn't log activity.
   */
  protected boolean doLogActivity() {
    return false;
  }
  
}
