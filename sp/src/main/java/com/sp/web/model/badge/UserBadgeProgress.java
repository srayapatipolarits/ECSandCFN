package com.sp.web.model.badge;

/**
 * UserBadgeProgres captures the badge progress.
 * 
 * @author pradeepruhil
 *
 */
public class UserBadgeProgress extends UserBadge {
  
  /**
   * default serial version id.
   */
  private static final long serialVersionUID = -2873983702025420977L;
  
  private int completedCount;
  
  private int totalCount;
  
  private boolean activeProgress;
  
  public int getCompletedCount() {
    return completedCount;
  }
  
  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
  
  public int getTotalCount() {
    return totalCount;
  }
  
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
  
  public void increaseCompleteCount() {
    this.completedCount += 1;
    
  }
  
  public boolean isCompleted() {
    return this.totalCount == this.completedCount;
  }
  
  public void setActiveProgress(boolean activeProgress) {
    this.activeProgress = activeProgress;
  }
  
  public boolean isActiveProgress() {
    return activeProgress;
  }
}
