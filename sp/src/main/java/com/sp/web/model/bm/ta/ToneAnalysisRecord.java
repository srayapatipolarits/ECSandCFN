package com.sp.web.model.bm.ta;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.sp.web.model.User;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The entity class to store the tone analysis.
 */
public class ToneAnalysisRecord {
  
  private String id;
  private String text;
  private String userId;
  private String companyId;
  private LocalDateTime createdOn;
  private ToneRequestType type;
  private ToneAnalysisResult analysis;
  
  /**
   * Default Constructor.
   */
  public ToneAnalysisRecord() { }

  /**
   * Constructor from tone analysis.
   * @param user 
   *          - user
   * @param analysis
   *          - tone analysis
   * @param type
   *          - tone request type
   * @param text 
   *          - text
   */
  public ToneAnalysisRecord(User user, ToneAnalysis analysis, ToneRequestType type, String text) {
    this.userId = user.getId();
    this.companyId = user.getCompanyId();
    this.createdOn = LocalDateTime.now();
    this.type = type;
    this.analysis = new ToneAnalysisResult(analysis);
    this.text = text;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public ToneRequestType getType() {
    return type;
  }
  
  public void setType(ToneRequestType type) {
    this.type = type;
  }

  public ToneAnalysisResult getAnalysis() {
    return analysis;
  }

  public void setAnalysis(ToneAnalysisResult analysis) {
    this.analysis = analysis;
  }
  
}
