package com.sp.web.model.discussion;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the groups discussion that the user is part of.
 */
public class UserGroupDiscussion implements Serializable{
  
  private static final long serialVersionUID = -185633789690010666L;
  private String id;
  private String uid;
  private LinkedList<String> groupDiscussionIds;
  private Map<String, Integer> unreadCountMap;
  
  /**
   * Default Constructor.
   */
  public UserGroupDiscussion() { /*Default*/ }
  
  /**
   * Constructor from user id.
   * 
   * @param userId
   *          - user id
   */
  public UserGroupDiscussion(String userId) {
    this.uid = userId;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public LinkedList<String> getGroupDiscussionIds() {
    return groupDiscussionIds;
  }
  
  public void setGroupDiscussionIds(LinkedList<String> groupDiscussionIds) {
    this.groupDiscussionIds = groupDiscussionIds;
  }
  
  public Map<String, Integer> getUnreadCountMap() {
    return unreadCountMap;
  }
  
  public void setUnreadCountMap(Map<String, Integer> unreadCountMap) {
    this.unreadCountMap = unreadCountMap;
  }
  
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
  
  /**
   * Gets the unread count map if it does not exists then returns a blank one.
   * 
   * @return the unread count map
   */
  public Map<String, Integer> getUnreadCountMapOrCreate() {
    if (unreadCountMap == null) {
      unreadCountMap = new HashMap<String, Integer>();
    }
    return unreadCountMap;
  }
  
  /**
   * Add the given group discussion id to the user.
   * 
   * @param groupDiscussionId
   *          - group discussion id
   */
  public void addGroupDiscussion(String groupDiscussionId) {
    createIfNotPresent();
    groupDiscussionIds.add(0, groupDiscussionId);
  }

  private synchronized void createIfNotPresent() {
    if (groupDiscussionIds == null) {
      groupDiscussionIds = new LinkedList<String>();
    }
  }

  /**
   * Check if the given group discussion id is part of users group discussions.
   * 
   * @param gdId
   *          - group discussion id
   * @return
   *    true if present else false
   */
  public boolean partOfGroupDiscussion(String gdId) {
    if (CollectionUtils.isEmpty(groupDiscussionIds)) {
      return false;
    }
    return groupDiscussionIds.contains(gdId);
  }

  /**
   * Reset the value of the unread count for the given group discussion.
   *  
   * @param gdId
   *          - group discussion id
   */
  public void resetUnreadCount(String gdId) {
    if (!CollectionUtils.isEmpty(unreadCountMap)) {
      unreadCountMap.remove(gdId);
    }
  }

  /**
   * Update the unread count for the given group discussion.
   * 
   * @param gdId
   *          - group discussion id
   */
  public void updateUnreadCount(String gdId) {
    updateUnreadCount(gdId, 1);
  }

  /**
   * Method to update the unread count.
   * 
   * @param gdId
   *          - group discussion id
   * @param countToAdd
   *          - the unread count to update by
   */
  public void updateUnreadCount(String gdId, int countToAdd) {
    Map<String, Integer> unreadCountMapOrCreate = getUnreadCountMapOrCreate();
    Integer unreadCount = unreadCountMapOrCreate.get(gdId);
    unreadCountMapOrCreate.put(gdId, (unreadCount != null) ? unreadCount + countToAdd : countToAdd);
  }

  /**
   * Remove the group discussion for the given user.
   * 
   * @param gdId
   *          - group discussion id
   */
  public void removeGroupDiscussion(String gdId) {
    // removing the group discussion from the list
    if (!CollectionUtils.isEmpty(groupDiscussionIds)) {
      groupDiscussionIds.remove(gdId);
    }
    // removing the unread count from the map
    if (!CollectionUtils.isEmpty(unreadCountMap)) {
      unreadCountMap.remove(gdId);
    }
  }
  
}
