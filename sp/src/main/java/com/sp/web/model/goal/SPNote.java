package com.sp.web.model.goal;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <code>SPNote</code> class holds the notes associated with the goal / dev strategy.
 * 
 * @author vikram
 */
@Document(collection = "sPNote")
public class SPNote implements Serializable {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 2616848098366361962L;

  private String id;
  
  private String goalId;
  
  private String content;
  
  private String devStrategyId;
  
  private String userId;
  
  private String companyId;
  
  private LocalDateTime createdOn;
  
  private SPNoteFeedbackType type ;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getGoalId() {
    return goalId;
  }

  public void setGoalId(String goalId) {
    this.goalId = goalId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public SPNoteFeedbackType getType() {
    return type;
  }

  public void setType(SPNoteFeedbackType type) {
    this.type = type;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDevStrategyId() {
    return devStrategyId;
  }

  public void setDevStrategyId(String devStrategyId) {
    this.devStrategyId = devStrategyId;
  }
}
