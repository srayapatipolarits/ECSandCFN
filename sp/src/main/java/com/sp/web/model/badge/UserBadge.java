package com.sp.web.model.badge;

import com.sp.web.model.ContentReference;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <code>UserBadge</code> class holds the badge information for the user.
 * 
 * @author pradeepruhil
 *
 */
public class UserBadge implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -6630682820656973690L;
  
  private String refId;
  
  private BadgeType badgeType;
  
  private ContentReference contentReference;
  
  private LocalDateTime awarededOn;
  
  /* level of the badge in case of level. */
  private int level;
  
  public String getRefId() {
    return refId;
  }
  
  public void setRefId(String refId) {
    this.refId = refId;
  }
  
  public BadgeType getBadgeType() {
    return badgeType;
  }
  
  public void setBadgeType(BadgeType badgeType) {
    this.badgeType = badgeType;
  }
  
  public int getLevel() {
    return level;
  }
  
  public void setLevel(int level) {
    this.level = level;
  }
  
  public void setContentReference(ContentReference contentReference) {
    this.contentReference = contentReference;
  }
  
  public ContentReference getContentReference() {
    return contentReference;
  }
  
  public void setAwarededOn(LocalDateTime awarededOn) {
    this.awarededOn = awarededOn;
  }
  
  public LocalDateTime getAwarededOn() {
    return awarededOn;
  }
}
