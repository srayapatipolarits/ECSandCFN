package com.sp.web.service.sse;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MessageEventRequest is the request for events to be created. All the information is dumped in
 * this to be proessed by the event processor.
 * 
 * @author pradeepruhil
 *
 */
public class MessageEventRequest implements Serializable {
  
  private static final long serialVersionUID = -626342346906721463L;
  
  private ActionType eventActionType;
  private LocalDateTime createdOn;
  private Map<String, Object> messagePayLoad;
  private List<String> userIds;
  private List<String> filterUserIds;
  private boolean allMembers;
  private String companyId;
  private String sessionId;
  
  /**
   * Constructor from event action type as this is mandatory.
   * 
   * @param eventActionType
   *          - event action type
   */
  public MessageEventRequest(ActionType eventActionType) {
    this.setEventActionType(eventActionType);
    this.createdOn = LocalDateTime.now();
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public Map<String, Object> getMessagePayLoad() {
    return messagePayLoad;
  }
  
  public void setMessagePayLoad(Map<String, Object> messagePayLoad) {
    this.messagePayLoad = messagePayLoad;
  }
  
  public List<String> getUserIds() {
    return userIds;
  }
  
  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }
  
  public boolean isAllMembers() {
    return allMembers;
  }
  
  public void setAllMembers(boolean allMembers) {
    this.allMembers = allMembers;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public ActionType getEventActionType() {
    return eventActionType;
  }
  
  public void setEventActionType(ActionType eventActionType) {
    this.eventActionType = eventActionType;
  }
  
  /**
   * Creates a new event request with the data provided.
   * 
   * @param eventActionType
   *          - event action type
   * @param userIdList
   *          - user id list
   * @param payload
   *          - payload
   * @return the message event request
   */
  public static MessageEventRequest newEvent(ActionType eventActionType, List<String> userIdList,
      Map<String, Object> payload, String companyId) {
    return newEvent(eventActionType, userIdList, payload, null, companyId);
  }
  
  /**
   * Creates a new event request with the data provided.
   * 
   * @param eventActionType
   *          - event action type
   * @param userIdList
   *          - user id list
   * @param payload
   *          - payload
   * @param filterUserIds
   *          - filter member ids
   * @return the message event request
   */
  public static MessageEventRequest newEvent(ActionType eventActionType, List<String> userIdList,
      Map<String, Object> payload, List<String> filterUserIds, String companyId) {
    MessageEventRequest eventRequest = new MessageEventRequest(eventActionType);
    eventRequest.setCreatedOn(LocalDateTime.now());
    eventRequest.setUserIds(userIdList);
    eventRequest.setMessagePayLoad(payload);
    eventRequest.setCompanyId(companyId);
    if (payload != null) {
      payload.put(Constants.PARAM_ACTION_TYPE, eventActionType);
    }
    eventRequest.setFilterUserIds(filterUserIds);
    return eventRequest;
  }
  
  /**
   * Creates a new event request with the data provided.
   * 
   * @param eventActionType
   *          - event action type
   * @param companyId
   *          - company id
   * @param payload
   *          - payload
   * @return the message event request
   */
  public static MessageEventRequest newEvent(ActionType eventActionType, String companyId,
      Map<String, Object> payload) {
    return newEvent(eventActionType, companyId, payload, null);
  }
  
  /**
   * Creates a new event request with the data provided.
   * 
   * @param eventActionType
   *          - event action type
   * @param companyId
   *          - company id
   * @param payload
   *          - payload
   * @param filterUserIds
   *          - filter user id list
   * @return the message event request
   */
  public static MessageEventRequest newEvent(ActionType eventActionType, String companyId,
      Map<String, Object> payload, List<String> filterUserIds) {
    MessageEventRequest eventRequest = new MessageEventRequest(eventActionType);
    eventRequest.setCreatedOn(LocalDateTime.now());
    eventRequest.setAllMembers(true);
    eventRequest.setCompanyId(companyId);
    eventRequest.setMessagePayLoad(payload);
    if (payload != null) {
      payload.put(Constants.PARAM_ACTION_TYPE, eventActionType);
    }
    eventRequest.setFilterUserIds(filterUserIds);
    return eventRequest;
  }
  
  /**
   * Creates a new event request with the data provided.
   * 
   * @param eventActionType
   *          - event action type
   * @param user
   *          - user
   * @param payload
   *          - payload
   * @return the message event request
   */
  public static MessageEventRequest newEvent(ActionType eventActionType,
      Map<String, Object> payload, User user) {
    
    List<String> userIds = new ArrayList<String>();
    if (user instanceof FeedbackUser) {
      userIds.add(((FeedbackUser)user).getMemberId());
    } else {
      userIds.add(user.getId());
    }
    MessageEventRequest eventRequest = new MessageEventRequest(eventActionType);
    eventRequest.setCreatedOn(LocalDateTime.now());
    eventRequest.setUserIds(userIds);
    eventRequest.setMessagePayLoad(payload);
    eventRequest.setCompanyId(user.getCompanyId());
    if (payload != null) {
      payload.put(Constants.PARAM_ACTION_TYPE, eventActionType);
    }
    return eventRequest;
  }
  
  public List<String> getFilterUserIds() {
    return filterUserIds;
  }
  
  public void setFilterUserIds(List<String> filterUserIds) {
    this.filterUserIds = filterUserIds;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  
}
