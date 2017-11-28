package com.sp.web.dao.hiring.match;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.hiring.match.MatchCriteria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The matcher data for the personality matching.
 */
public class PersonalityMatcherData implements PortraitMatcherData {
  
  private Map<PersonalityType, BigDecimal> personalityMatcher;
  
  /**
   * Constructor.
   * 
   * @param matchCriterias
   *          - match criteria to load
   */
  public PersonalityMatcherData(List<MatchCriteria> matchCriterias) {
    personalityMatcher = matchCriterias.stream()
        .collect(
            Collectors.toMap(mc -> PersonalityType.valueOf(mc.getKey()),
                MatchCriteria::getMatchPercent));
  }
  
  /**
   * Match the given personality.
   * 
   * @param type
   *          - personality type
   * @return the match percentage
   */
  public int matchPersonality(PersonalityType type) {
    return Optional.ofNullable(personalityMatcher.get(type)).orElse(BigDecimal.ZERO).intValue();
  }
}
