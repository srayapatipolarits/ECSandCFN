package com.sp.web.service.hiring.match.processor;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The portrait match processor for percentage based comparisons.
 */
@Component("percentagePortraitMatchProcessor")
@Scope("prototype")
public class PercentagePortraitMatchProcessor extends AbstractPortraitMatchProcessor {
  
  @Override
  protected PortraitMatcherData loadData(PortraitDataMatch value) {
    return new ValuePortraitMatcher(value.getMatchCriterias());
  }
  
  @Override
  public void process(AnalysisBean analysis, Map<String, PortraitMatcherData> portraitMatchData,
      Map<String, MatchDetailsDTO> matchResult) {
    Map<TraitType, BigDecimal> portraitData = null;
    switch (categoryType) {
    case Processing:
      portraitData = analysis.getProcessing();
      break;
    case LearningStyle:
      portraitData = analysis.getLearningStyle();
      break;
    case DecisionMaking:
      portraitData = analysis.getDecisionMaking();
      break;
    case Motivation:
      portraitData = new HashMap<TraitType, BigDecimal>();
      portraitData.putAll(analysis.getMotivationHow());
      portraitData.putAll(analysis.getMotivationWhat());
      portraitData.putAll(analysis.getMotivationWhy());
      break;
    default:
      throw new IllegalArgumentException("Category not configured " + categoryType);
    }
    
    // processing the match
    loadMatchResult(portraitData, portraitMatchData, matchResult);
  }
  
  /**
   * Load the match result for the given analysis category.
   * 
   * @param portraitMap
   *          - the portrait map with users analysis data
   * @param portraitMatchData
   *          - the matcher data
   * @param matchResult
   *          - the match result
   */
  private void loadMatchResult(Map<TraitType, BigDecimal> portraitMap,
      Map<String, PortraitMatcherData> portraitMatchData, Map<String, MatchDetailsDTO> matchResult) {
    try {
      portraitMatchData.forEach((key, value) -> matchResult.put(key,
          getMatch((ValuePortraitMatcher) value, portraitMap.get(TraitType.valueOf(key)))));
    } catch (Exception e) {
      log.warn(
          "Could not process for " + portraitMatchData.keySet() + ": Data : "
              + portraitMap.keySet(), e);
      throw new IllegalArgumentException("Could not process for " + portraitMatchData.keySet());
    }
  }
  
  /**
   * Get the portrait match.
   *
   * @param value
   *          - value to match against
   * @param portraitScore
   *          - the users score
   * @return the match details
   */
  private MatchDetailsDTO getMatch(ValuePortraitMatcher value, BigDecimal portraitScore) {
    MatchDetailsDTO matchDetails = new MatchDetailsDTO(portraitScore);
    int intValue = portraitScore.subtract(value.getMatchPercent()).abs().intValue();
    if (intValue <= 10) {
      matchDetails.setMatchPercent(100);
    } else if (intValue <= 15) {
      matchDetails.setMatchPercent(90);
    } else if (intValue <= 20) {
      matchDetails.setMatchPercent(80);
    } else {
      matchDetails.setMatchPercent(0);
    }
    return matchDetails;
  }
  
}
