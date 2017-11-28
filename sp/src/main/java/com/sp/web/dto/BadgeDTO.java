package com.sp.web.dto;

import com.sp.web.model.ContentReference;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.badge.UserBadge;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class BadgeDTO {
  
  private BadgeType badgeType;
  
  private ContentReference contentReference;
  
  private String refId;
  
  private int level;
  
  private LocalDateTime updatedOn;
  
  public BadgeDTO() {
  }
  
  public BadgeDTO(UserBadge userBadge) {
    BeanUtils.copyProperties(userBadge, this);
    this.updatedOn = userBadge.getAwarededOn();
    if (this.updatedOn == null) {
      this.updatedOn = LocalDateTime.now();
    }
  }
  
  public BadgeDTO(String key, BadgeType badgeType, int level) {
    this.refId = key;
    this.badgeType = badgeType;
  }
  
  public BadgeType getBadgeType() {
    return badgeType;
  }
  
  public void setBadgeType(BadgeType badgeType) {
    this.badgeType = badgeType;
  }
  
  /**
   * Return the content reference.
   * 
   * @return the content reference.
   */
  public ContentReference getContentReference() {
    if (contentReference == null) {
      contentReference = new ContentReference();
    }
    return contentReference;
  }
  
  public void setContentReference(ContentReference contentReference) {
    this.contentReference = contentReference;
  }
  
  public String getRefId() {
    return refId;
  }
  
  public void setRefId(String refId) {
    this.refId = refId;
  }
  
  public void setLevel(int level) {
    this.level = level;
  }
  
  public int getLevel() {
    return level;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
}
