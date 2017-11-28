package com.sp.web.dto.goal;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.goal.StepType;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the action plan details like edited, created, etc.
 */
public class ActionPlanEditDTO extends ActionPlanDTO {
  
  private StepType stepType;
  private LocalDateTime createdOn;
  private UserMarkerDTO createdBy;
  private LocalDateTime editedOn;
  private UserMarkerDTO editedBy;
  private LocalDateTime publishedOn;
  private UserMarkerDTO publishedBy;
  
  public ActionPlanEditDTO(ActionPlanDao actionPlan) {
    super(actionPlan);
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public UserMarkerDTO getCreatedBy() {
    return createdBy;
  }
  
  public void setCreatedBy(UserMarkerDTO createdBy) {
    this.createdBy = createdBy;
  }
  
  public LocalDateTime getEditedOn() {
    return editedOn;
  }
  
  public void setEditedOn(LocalDateTime editedOn) {
    this.editedOn = editedOn;
  }
  
  public UserMarkerDTO getEditedBy() {
    return editedBy;
  }
  
  public void setEditedBy(UserMarkerDTO editedBy) {
    this.editedBy = editedBy;
  }
  
  public LocalDateTime getPublishedOn() {
    return publishedOn;
  }
  
  public void setPublishedOn(LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
  
  public UserMarkerDTO getPublishedBy() {
    return publishedBy;
  }
  
  public void setPublishedBy(UserMarkerDTO publishedBy) {
    this.publishedBy = publishedBy;
  }

  public StepType getStepType() {
    return stepType;
  }

  public void setStepType(StepType stepType) {
    this.stepType = stepType;
  }
  
}
