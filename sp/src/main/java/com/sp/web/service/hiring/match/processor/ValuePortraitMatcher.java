package com.sp.web.service.hiring.match.processor;

import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.model.hiring.match.MatchCriteria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 * 
 *         The portrait matcher for the value (percentage) based comparisons.
 */
public class ValuePortraitMatcher implements PortraitMatcherData {
  
  private BigDecimal matchPercent;
  
  public ValuePortraitMatcher(List<MatchCriteria> matchCriterias) {
    matchPercent = Optional.ofNullable(matchCriterias.get(0)).map(MatchCriteria::getMatchPercent)
        .orElse(BigDecimal.ZERO);
  }
  
  public BigDecimal getMatchPercent() {
    return matchPercent;
  }
}
