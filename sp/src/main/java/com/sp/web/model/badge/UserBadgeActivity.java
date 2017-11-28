package com.sp.web.model.badge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * UserBadgeActivity is the model entity which stores the user badge infomation.
 * 
 * @author pradeepruhil
 *
 */
public class UserBadgeActivity implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 1368198595860825230L;
  
  private String id;
  
  /** hold the in progress badge information. */
  private Map<String, UserBadgeProgress> userBadgeProgress;
  
  private String userId;
  
  /** hold the completed badges information. */
  private Map<String, UserBadge> completedBadges;
  
  /** company id to remove the in progress badges in case the ref id is deleted from the company. */
  private String companyId;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * @return blank userbadge progress map.
   */
  public Map<String, UserBadgeProgress> getUserBadgeProgress() {
    if (userBadgeProgress == null) {
      userBadgeProgress = new HashMap<>();
    }
    return userBadgeProgress;
  }
  
  /**
   * Gets the user badge progress for the given id.
   * 
   * @param id
   *          - feature id
   * @return
   *    the user badge progress or null if none exists
   */
  public UserBadgeProgress getUserBadgeProgress(String id) {
    return (userBadgeProgress != null) ? userBadgeProgress.get(id) : null;
  }
  
  public void setUserBadgeProgress(Map<String, UserBadgeProgress> userBadgeProgress) {
    this.userBadgeProgress = userBadgeProgress;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  /**
   * @return
   *      - the user badge progress map.
   */
  public Map<String, UserBadge> getCompletedBadges() {
    if (completedBadges == null) {
      completedBadges = new HashMap<String, UserBadge>();
    }
    return completedBadges;
  }

  /**
   * Gets the completed badge for the given reference id.
   * 
   * @param refId
   *          - ref id
   * @return
   *    the user badge found else null
   */
  public UserBadge getCompletedBadges(String refId) {
    return (completedBadges != null) ? completedBadges.get(refId) : null;
  }
  
  public void setCompletedBadges(Map<String, UserBadge> completedBadges) {
    this.completedBadges = completedBadges;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
}
