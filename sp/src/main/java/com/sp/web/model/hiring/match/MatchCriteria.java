package com.sp.web.model.hiring.match;

import com.sp.web.assessment.personality.PersonalityTypeMapper;

import java.math.BigDecimal;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity to store the match criteria.
 */
public class MatchCriteria {
  
  private String key;
  private BigDecimal matchPercent;
  
  public String getKey() {
    return key;
  }
  
  public void setKey(String key) {
    this.key = key;
  }
  
  public BigDecimal getMatchPercent() {
    return matchPercent;
  }
  
  public void setMatchPercent(BigDecimal matchPercent) {
    this.matchPercent = matchPercent;
  }
  
  public void updatePersonalityMapping() {
    key = PersonalityTypeMapper.getPersonalityTypeMapper(key) + "";
  }
}
