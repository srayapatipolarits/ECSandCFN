package com.sp.web.service.log;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.LogType;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The log request for logging activity and notification messages.
 */
public class LogRequest {

  private LogType logType; 
  private LogActionType logActionType;
  private User byUser;
  private User forUser;
  private boolean doMessagesOverride;
  private Map<String, Object> params;

  /**
   * Constructor to create the request with the given parameters.
   * 
   * @param logActionType
   *          - the log type
   * @param byUser
   *          - logged by user
   */
  public LogRequest(LogActionType logActionType, User byUser) {
    Assert.notNull(logActionType, "Log Type required !!!");
    this.logActionType = logActionType;
    Assert.notNull(byUser, "By user required !!!");
    this.byUser = byUser;
    params = new HashMap<String, Object>();
  }

  /**
   * Constructor.
   * 
   * @param logActionType
   *          - log type
   * @param byUser
   *          - logged by user
   * @param forUser
   *          - for user
   */
  public LogRequest(LogActionType logActionType, User byUser, User forUser) {
    this(logActionType, byUser);
    Assert.notNull(forUser, "For user required !!!");
    this.forUser = forUser;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public LogActionType getLogActionType() {
    return logActionType;
  }

  public User getByUser() {
    return byUser;
  }

  public User getForUser() {
    return forUser;
  }

  public LogType getLogType() {
    return logType;
  }

  public void setLogType(LogType logType) {
    this.logType = logType;
  }

  public boolean isDoMessagesOverride() {
    return doMessagesOverride;
  }

  public void setDoMessagesOverride(boolean doMessagesOverride) {
    this.doMessagesOverride = doMessagesOverride;
  }

  public Object getParam(String key) {
    return (params != null) ? params.get(key) : null;
  }

  /**
   * Add the parameter to params map.
   * 
   * @param key
   *          - key
   * @param value
   *          - value
   */
  public void addParam(String key, Object value) {
    if (params == null) {
      params = new HashMap<String, Object>();
    }
    params.put(key, value);
  }

  public void addMessage(String message) {
    addParam(Constants.PARAM_MESSAGE, message);
  }

  /**
   * Method to override the activity message.
   * 
   * @param message
   *          - message
   */
  public void addActivityMessage(String message) {
    doMessagesOverride = true;
    addParam(Constants.PARAM_MESSAGE_ACTIVITY, message);
  }

  /**
   * Casts the return value as string for the given key.
   * 
   * @param key
   *          - key
   * @return
   *    the value for the key
   */
  public String getParamAsString(String key) {
    return (String)getParam(key);
  }

}
