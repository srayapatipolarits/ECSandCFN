package com.sp.web.model.spectrum;

import java.io.Serializable;

/**
 * @author pradeepruhil
 *
 */
public class SP360LearnerStatusTracker implements Serializable {
  
  /**
   * Default serial vesrion id.
   */
  private static final long serialVersionUID = -2117529871302863872L;
  
  private String id;
  
  private int requestSent;
  
  private int assessmentCompleted;
  
  private int assessmentPending;
  
  private int assessmentDeactivated;
  
  public int getRequestSent() {
    return requestSent;
  }
  
  public void setRequestSent(int requestSent) {
    this.requestSent = requestSent;
  }
  
  public int getAssessmentCompleted() {
    return assessmentCompleted;
  }
  
  public void setAssessmentCompleted(int assessmentCompleted) {
    this.assessmentCompleted = assessmentCompleted;
  }
  
  public int getAssessmentPending() {
    return assessmentPending;
  }
  
  public void setAssessmentPending(int assessmentPending) {
    this.assessmentPending = assessmentPending;
  }
  
  public int getAssessmentDeactivated() {
    return assessmentDeactivated;
  }
  
  public void setAssessmentDeactivated(int assessmentDeactivated) {
    this.assessmentDeactivated = assessmentDeactivated;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
}
