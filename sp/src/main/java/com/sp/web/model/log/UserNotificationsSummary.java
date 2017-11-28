package com.sp.web.model.log;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 * 
 *         The entity to store the users notifications summary.
 */
public class UserNotificationsSummary implements Serializable {
  
  private static final long serialVersionUID = -7208936425100939958L;
  private String id;
  private String userId;
  private int count;
  
  /*
   * Default constructor.
   */
  public UserNotificationsSummary() { }
  
  /**
   * Constructor.
   * 
   * @param userId
   *          - user id
   */
  public UserNotificationsSummary(String userId) { 
    this.userId = userId;
    this.count = 0;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }

  public void incrementCount() {
    count++;
  }
  
}
