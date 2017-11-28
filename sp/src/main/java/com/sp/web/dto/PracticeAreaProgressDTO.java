package com.sp.web.dto;

import com.sp.web.model.goal.SPGoal;

public class PracticeAreaProgressDTO extends PracticeAreaDTO {
  
  private int totalCount;
  
  private int completedCount;
  
  private int level;
  
  private static final long serialVersionUID = 3035517797045382086L;
  
  public PracticeAreaProgressDTO(SPGoal spGoal) {
    super(spGoal);
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
  
  public void setLevel(int level) {
    this.level = level;
  }
  
  public int getLevel() {
    return level;
  }
}
