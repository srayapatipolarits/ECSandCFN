package com.sp.web.model.assessment;

/**
 * @author Dax Abraham
 * 
 *         This is the bean that stores the assessment progress.
 * 
 *         As well as the user responses.
 */
public class AssessmentProgressTracker {
  
  private String id;
  private String userId;
  private boolean assessmentInProgress;
  private String currentAssessment;
  
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
  
  public boolean isAssessmentInProgress() {
    return assessmentInProgress;
  }
  
  public void setAssessmentInProgress(boolean assessmentInProgress) {
    this.assessmentInProgress = assessmentInProgress;
  }
  
  public String getCurrentAssessment() {
    return currentAssessment;
  }
  
  public void setCurrentAssessment(String currentAssessment) {
    this.currentAssessment = currentAssessment;
  }

  /**
   * Marks the current assessment as completed.
   * 
   * @return
   *    the current tracker instance
   */
  public AssessmentProgressTracker assessmentCompleted() {
    this.assessmentInProgress = false;
    this.currentAssessment = null;
    return this;
  }
  
}