package com.sp.web.dto.badge;

import com.sp.web.dto.BadgeDTO;
import com.sp.web.model.badge.UserBadgeProgress;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class BadgeProgressDTO extends BadgeDTO {
  
  private int totalCount;
  
  private int completedCount;
  
  private int level;
  
  public BadgeProgressDTO() {
  }
  
  public BadgeProgressDTO(UserBadgeProgress badgeProgress) {
    BeanUtils.copyProperties(badgeProgress, this);
    setUpdatedOn(badgeProgress.getAwarededOn());
    if (getUpdatedOn() == null) {
      setUpdatedOn(LocalDateTime.now());
    }
  }
  
  public int getTotalCount() {
    return totalCount;
  }
  
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
  
  public int getCompletedCount() {
    return completedCount;
  }
  
  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
  
  public int getLevel() {
    return level;
  }
  
  public void setLevel(int level) {
    this.level = level;
  }
  
}
