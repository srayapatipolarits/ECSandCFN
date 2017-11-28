package com.sp.web.assessment.questions;

import java.util.List;
import java.util.Map;

/**
 * @author daxabraham
 * 
 *         The traits bean class.
 */
public class TraitsBean {
  
  private Map<String, List<TraitsTransform>> traitsTransformMap;
  private QuestionType type;
  private RatingBean[] factor;
  
  public QuestionType getType() {
    return type;
  }
  
  public void setType(QuestionType type) {
    this.type = type;
  }
  
  public RatingBean[] getFactor() {
    return factor;
  }
  
  public void setFactor(RatingBean[] factor) {
    this.factor = factor;
  }
  
  public Map<String, List<TraitsTransform>> getTraitsTransformMap() {
    return traitsTransformMap;
  }

  public void setTraitsTransformMap(Map<String, List<TraitsTransform>> traitsTransformMap) {
    this.traitsTransformMap = traitsTransformMap;
  }

  public List<TraitsTransform> getTraitsTransform(String selection) {
    return traitsTransformMap.get(selection);
  }
}
