package com.sp.web.service.hiring.match.processor;

import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.model.hiring.match.MatchCriteria;

import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 * 
 *         The portrait match holder for key based portrait matching.
 */
public class TraitPortraitMatcher implements PortraitMatcherData {
  
  private TraitType traitToMatch = null;
  private Map<TraitType, Integer> traitsPriority;
  
  /**
   * Constructor.
   * 
   * @param matchCriterias
   *          - match criteria to process
   */
  public TraitPortraitMatcher(List<MatchCriteria> matchCriterias) {
    Optional.ofNullable(matchCriterias.get(0)).map(MatchCriteria::getKey)
        .ifPresent(key -> traitToMatch = TraitType.valueOf(key));
    Assert.notNull(traitToMatch, "Could not process " + matchCriterias);
  }
  
  public TraitType getTraitToMatch() {
    return traitToMatch;
  }

  public Map<TraitType, Integer> getTraitsPriority() {
    return traitsPriority;
  }

  public void setTraitsPriority(Map<TraitType, Integer> traitsPriority) {
    this.traitsPriority = traitsPriority;
  }
}
