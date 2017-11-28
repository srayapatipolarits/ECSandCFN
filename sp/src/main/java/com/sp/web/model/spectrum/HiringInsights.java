package com.sp.web.model.spectrum;

import java.io.Serializable;

/**
 * HiringInsights.
 * 
 * @author pradeepruhil
 *
 */
public class HiringInsights implements Serializable {
  
  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 794282159483277339L;
  
  private String id;
  
  private int totalCandidates;
  
  private int assesmentIncomplete;
  
  private int assessmentCompleted;
  
  private int achived;
  
  private int hired;
  
  public int getTotalCandidates() {
    return totalCandidates;
  }
  
  public void setTotalCandidates(int totalCandidates) {
    this.totalCandidates = totalCandidates;
  }
  
  public int getAssesmentIncomplete() {
    return assesmentIncomplete;
  }
  
  public void setAssesmentIncomplete(int assesmentIncomplete) {
    this.assesmentIncomplete = assesmentIncomplete;
  }
  
  public int getAssessmentCompleted() {
    return assessmentCompleted;
  }
  
  public void setAssessmentCompleted(int assessmentCompleted) {
    this.assessmentCompleted = assessmentCompleted;
  }
  
  public int getAchived() {
    return achived;
  }
  
  public void setAchived(int achived) {
    this.achived = achived;
  }
  
  public int getHired() {
    return hired;
  }
  
  public void setHired(int hired) {
    this.hired = hired;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}
