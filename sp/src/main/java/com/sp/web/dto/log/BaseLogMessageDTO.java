package com.sp.web.dto.log;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.LogMessage;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The base message DTO to log messages.
 */
public class BaseLogMessageDTO implements Serializable {
  
  private static final long serialVersionUID = 1285549674302035345L;
  private String id;
  private LocalDateTime createdOn;
  private LogActionType logActionType;
  private String message;
  private UserMarkerDTO user;


  /**
   * Constructor.
   * 
   * @param logMessage
   *          - log message
   */
  public BaseLogMessageDTO(LogMessage logMessage) {
    BeanUtils.copyProperties(logMessage, this);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LogActionType getLogActionType() {
    return logActionType;
  }

  public void setLogActionType(LogActionType logActionType) {
    this.logActionType = logActionType;
  }

  public UserMarkerDTO getUser() {
    return user;
  }

  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
