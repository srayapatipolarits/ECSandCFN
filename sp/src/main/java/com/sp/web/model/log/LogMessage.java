package com.sp.web.model.log;

import com.sp.web.dto.user.UserMarkerDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 *
 *         The base log message class for both notifications and activities.
 */
public class LogMessage implements Serializable {

  private static final long serialVersionUID = -7729434452485645179L;
  private String id;
  private LocalDateTime createdOn;
  private LogActionType logActionType;
  private LogType logType;
  private String title;
  private String message;
  private String memberId;
  private UserMarkerDTO user;
  private String companyId;

  public LogMessage() { }

  /**
   * Constructor.
   * 
   * @param logActionType
   *          - log type
   * @param memberId
   *          - member id
   */
  public LogMessage(LogActionType logActionType, String memberId) {
    this.createdOn = LocalDateTime.now();
    this.logType = logActionType.getLogType();
    this.logActionType = logActionType;
    this.memberId = memberId;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public LogActionType getLogActionType() {
    return logActionType;
  }

  public void setLogActionType(LogActionType logActionType) {
    this.logActionType = logActionType;
  }

  public LogType getLogType() {
    return logType;
  }

  public void setLogType(LogType logType) {
    this.logType = logType;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMemberId() {
    return memberId;
  }

  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public UserMarkerDTO getUser() {
    return user;
  }

  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
