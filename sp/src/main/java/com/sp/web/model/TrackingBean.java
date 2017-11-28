package com.sp.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author pradeep
 *
 *         The tracking bean entity.
 */
public class TrackingBean implements Serializable {

  private static final long serialVersionUID = 4189755996572275834L;

  private LocalDateTime accessTime;

  private String userId;
  
  private int accessCount;

  /**
   * Default Constructor.
   */
  public TrackingBean() {
  }
  
  /**
   * Constructor from user.
   * 
   * @param userId
   *          - user id
   * @param updateAccessTime
   */
  public TrackingBean(String userId, boolean updateAccessTime) {
    this.userId = userId;
    if (updateAccessTime) {
      updateAccessTime();
    } else {
      this.accessTime = LocalDateTime.now();
    }
    
  }

  public LocalDateTime getAccessTime() {
    return accessTime;
  }

  public void setAccessTime(LocalDateTime accessTime) {
    this.accessTime = accessTime;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Updates the access time for the tracking bean.
   */
  public void updateAccessTime() {
    accessTime = LocalDateTime.now();
    accessCount++;
  }

  public int getAccessCount() {
    return accessCount;
  }

  public void setAccessCount(int accessCount) {
    this.accessCount = accessCount;
  }
  
  public void incrementAccessCount() {
    accessCount++;
  }
  
}
