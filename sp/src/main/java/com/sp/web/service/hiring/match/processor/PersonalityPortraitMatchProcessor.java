package com.sp.web.service.hiring.match.processor;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.dao.hiring.match.PersonalityMatcherData;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The hiring portrait match processor for Personality.
 */
@Component("personalityPortraitMatchProcessor")
public class PersonalityPortraitMatchProcessor extends AbstractPortraitMatchProcessor {
  
  @Override
  protected PortraitMatcherData loadData(PortraitDataMatch matchData) {
    return new PersonalityMatcherData(matchData.getMatchCriterias());
  }
  
  @Override
  public void process(AnalysisBean analysis, Map<String, PortraitMatcherData> portraitMatchData,
      Map<String, MatchDetailsDTO> matchResult) {
    HashMap<RangeType, PersonalityBeanResponse> personality = analysis.getPersonality();
    for (String key : portraitMatchData.keySet()) {
      PersonalityMatcherData portraitMatcher = (PersonalityMatcherData) portraitMatchData.get(key);
      final PersonalityType personalityType = personality.get(RangeType.valueOf(key))
          .getPersonalityType();
      MatchDetailsDTO matchDetails = new MatchDetailsDTO(personalityType,
          portraitMatcher.matchPersonality(personalityType));
      matchResult.put(key, matchDetails);
    }
  }
  
}
