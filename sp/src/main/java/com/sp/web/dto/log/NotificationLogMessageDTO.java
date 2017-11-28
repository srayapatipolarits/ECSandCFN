package com.sp.web.dto.log;

import com.sp.web.model.log.NotificationLogMessage;

/**
 * @author Dax Abraham
 *
 *         The notification log message DTO.
 */
public class NotificationLogMessageDTO extends BaseLogMessageDTO {

  private static final long serialVersionUID = -4950552428772124335L;
  private String urlParam;
  private boolean read;
  
  /**
   * Constructor.
   * 
   * @param logMessage
   *          - log message
   */
  public NotificationLogMessageDTO(NotificationLogMessage logMessage) {
    super(logMessage);
  }

  public String getUrlParam() {
    return urlParam;
  }

  public void setUrlParam(String urlParam) {
    this.urlParam = urlParam;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }
}
