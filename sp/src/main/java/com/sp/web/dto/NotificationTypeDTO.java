package com.sp.web.dto;

import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;

/**
 * @author Dax Abraham
 * 
 *         The notification type DTO.
 */
public class NotificationTypeDTO {
  
  private static final Logger log = Logger.getLogger(NotificationTypeDTO.class);
  
  private NotificationType type;
  private String name;
  
  /**
   * Constructor from notification type.
   * 
   * @param type
   *          - notification type
   */
  public NotificationTypeDTO(NotificationType type) {
    this.type = type;
    try {
      this.name = MessagesHelper.getMessage("notificationType." + type);
    } catch (Exception e) {
      log.warn("Message not found.", e);
      this.name = type.toString();
    }
  }

  public NotificationType getType() {
    return type;
  }
  
  public void setType(NotificationType type) {
    this.type = type;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
