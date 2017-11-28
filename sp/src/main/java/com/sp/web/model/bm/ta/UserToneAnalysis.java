package com.sp.web.model.bm.ta;

import com.sp.web.model.User;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The entity class to store the users tone analysis aggregate scores.
 */
public class UserToneAnalysis {
  
  private String id;
  private String userId;
  private String companyId;
  private ToneAnalysisResult aggregateToneAnalysis;
  private LocalDateTime updatedOn;
  
  /**
   * Default Constructor.
   */
  public UserToneAnalysis() {
  }
  
  /**
   * Constructor from user.
   * 
   * @param user
   *          - user
   */
  public UserToneAnalysis(User user) {
    this.userId = user.getId();
    this.companyId = user.getCompanyId();
    this.aggregateToneAnalysis = new ToneAnalysisResult();
  }
  
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
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public ToneAnalysisResult getAggregateToneAnalysis() {
    return aggregateToneAnalysis;
  }
  
  public void setAggregateToneAnalysis(ToneAnalysisResult aggregateToneAnalysis) {
    this.aggregateToneAnalysis = aggregateToneAnalysis;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  /**
   * Add the given tone analysis record to the aggregate.
   * 
   * @param toneAnalysisRecord
   *          - tone analysis record
   */
  public void add(ToneAnalysisRecord toneAnalysisRecord) {
    
    ToneAnalysisResult analysis = toneAnalysisRecord.getAnalysis();
    
    // check if this is the first time the add is called
    if (aggregateToneAnalysis == null) {
      aggregateToneAnalysis = analysis;
      return;
    }
    
    Map<ToneType, Double> aggregateScoreMap = aggregateToneAnalysis.getEmotion();
    if (aggregateScoreMap == null) {
      aggregateToneAnalysis.setEmotion(analysis.getEmotion());
    } else {
      aggregateScores(aggregateScoreMap, analysis.getEmotion());
    }
    
    aggregateScoreMap = aggregateToneAnalysis.getLanguageStyle();
    if (aggregateScoreMap == null) {
      aggregateToneAnalysis.setLanguageStyle(analysis.getLanguageStyle());
    } else {
      aggregateScores(aggregateScoreMap, analysis.getLanguageStyle());
    }

    aggregateScoreMap = aggregateToneAnalysis.getSocialTendencies();
    if (aggregateScoreMap == null) {
      aggregateToneAnalysis.setSocialTendencies(analysis.getSocialTendencies());
    } else {
      aggregateScores(aggregateScoreMap, analysis.getSocialTendencies());
    }
    
  }
  
  /**
   * Update the aggregate scores.
   * 
   * @param aggregateScoreMap
   *          - aggregate score map
   * @param scoreMap
   *          - score map
   */
  private void aggregateScores(Map<ToneType, Double> aggregateScoreMap,
      Map<ToneType, Double> scoreMap) {
    scoreMap.forEach((toneType, score) -> updateAggregate(aggregateScoreMap, toneType, score));
  }
  
  /**
   * Update the aggregate score map.
   * 
   * @param aggregateScoreMap
   *            - aggregate score map
   * @param toneType
   *            - tone type
   * @param score
   *            - score
   */
  private void updateAggregate(Map<ToneType, Double> aggregateScoreMap, ToneType toneType,
      Double score) {
    if (score != 0) {
      Double aggScore = aggregateScoreMap.get(toneType);
      if (aggScore == null) {
        aggregateScoreMap.put(toneType, score);
      } else {
        
        aggregateScoreMap.put(toneType, (aggScore + score) / 2);
      }
    }
  }
  
}
