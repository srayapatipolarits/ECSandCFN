package com.sp.web.dto.bm.ta;

import com.sp.web.model.bm.ta.ToneAnalysisRecord;
import com.sp.web.model.bm.ta.ToneAnalysisResult;
import com.sp.web.model.bm.ta.ToneRequestType;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the tone analysis records.
 */
public class ToneAnalysisRecordDTO {
  
  private ToneRequestType type;
  private String text;
  private LocalDateTime createdOn;
  private ToneAnalysisResult analysis;
  
  /**
   * Constructor from tone analysis.
   * 
   * @param toneAnalysis
   *          - tone analysis
   */
  public ToneAnalysisRecordDTO(ToneAnalysisRecord toneAnalysis) {
    BeanUtils.copyProperties(toneAnalysis, this);
  }
  
  public ToneRequestType getType() {
    return type;
  }
  
  public void setType(ToneRequestType type) {
    this.type = type;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public ToneAnalysisResult getAnalysis() {
    return analysis;
  }
  
  public void setAnalysis(ToneAnalysisResult analysis) {
    this.analysis = analysis;
  }
  
}
