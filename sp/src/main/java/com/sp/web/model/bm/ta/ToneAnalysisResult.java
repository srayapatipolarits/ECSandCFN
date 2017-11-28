package com.sp.web.model.bm.ta;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ElementTone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 * 
 *         The model to store the tone analysis results.
 */
public class ToneAnalysisResult {
  
  private Map<ToneType, Double> emotion;
  private Map<ToneType, Double> languageStyle;
  private Map<ToneType, Double> socialTendencies;
  
  /**
   * Default Constructor.
   */
  public ToneAnalysisResult() {
  }
  
  /**
   * Constructor from tone analysis.
   * 
   * @param analysis
   *          - tone analysis
   */
  public ToneAnalysisResult(ToneAnalysis analysis) {
    ElementTone documentTone = analysis.getDocumentTone();
    List<ToneCategory> toneCategories = documentTone.getTones();
    for (ToneCategory toneCategory : toneCategories) {
      switch (toneCategory.getId()) {
      case "emotion_tone":
        emotion = new HashMap<ToneType, Double>();
        populateScores(toneCategory, emotion, 0.25d);
        break;
      
      case "language_tone":
        languageStyle = new HashMap<ToneType, Double>();
        populateScores(toneCategory, languageStyle, 0.25d);
        break;

      case "social_tone":
        socialTendencies = new HashMap<ToneType, Double>();
        populateScores(toneCategory, socialTendencies, 0d);
        break;
        
      default:
        break;
      }
    }
  }
  
  /**
   * Populate the scores.
   * 
   * @param toneCategory
   *          - tone category
   * @param scoreMap
   *          - score map
   * @param minScore
   *          - minimum score to set 
   */
  private void populateScores(ToneCategory toneCategory, Map<ToneType, Double> scoreMap, double minScore) {
    for (ToneScore score : toneCategory.getTones()) {
      final Double scoreVal = score.getScore();
      if (scoreVal >= minScore) {
        scoreMap.put(ToneType.valueOf(StringUtils.deleteWhitespace(score.getName())),
            scoreVal);
      }
    }
  }
  
  public Map<ToneType, Double> getEmotion() {
    return emotion;
  }
  
  public void setEmotion(Map<ToneType, Double> emotion) {
    this.emotion = emotion;
  }
  
  public Map<ToneType, Double> getLanguageStyle() {
    return languageStyle;
  }
  
  public void setLanguageStyle(Map<ToneType, Double> languageStyle) {
    this.languageStyle = languageStyle;
  }
  
  public Map<ToneType, Double> getSocialTendencies() {
    return socialTendencies;
  }
  
  public void setSocialTendencies(Map<ToneType, Double> socialTendencies) {
    this.socialTendencies = socialTendencies;
  }
  
}
