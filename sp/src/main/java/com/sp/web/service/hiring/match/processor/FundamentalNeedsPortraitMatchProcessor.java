package com.sp.web.service.hiring.match.processor;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.dto.hiring.user.MatchDetailsWithReasonDTO;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The portrait match processor for percentage based comparisons.
 */
@Component("fundamentalNeedsPortraitMatchProcessor")
public class FundamentalNeedsPortraitMatchProcessor extends AbstractPortraitMatchProcessor {
  
  private static final long SCORE_THRESHOLD = 32;
  
  /**
   * Update the matcher map and also update the traits priority.
   * 
   * @param key
   *          - key
   * @param matchData
   *          - match data
   * @param matcherData
   *          - matcher data map
   * @param counter
   *          - counter
   * @param traitsPriority
   *          - traits priority map
   */
  private void updateMatcherData(String key, TraitPortraitMatcher matchData,
      Map<String, PortraitMatcherData> matcherData, AtomicInteger counter,
      Map<TraitType, Integer> traitsPriority) {
    matcherData.put(key, matchData);
    traitsPriority.put(matchData.getTraitToMatch(), counter.decrementAndGet());
    if (key.equals(Constants.PRIMARY)) {
      matchData.setTraitsPriority(traitsPriority);
    }
  }
  
  @Override
  public void loadData(CategoryType categoryType, Map<String, PortraitDataMatch> matchData,
      Map<String, List<MatchCriteria>> reportData, Map<String, PortraitMatcherData> matcherData) {
    final AtomicInteger counter = new AtomicInteger(matchData.size());
    final Map<TraitType, Integer> traitsPriority = new HashMap<TraitType, Integer>();
    matchData
        .entrySet()
        .stream()
        .filter(map -> map.getValue().isEnabled())
        .map(map -> loadReport(map, reportData))
        .forEach(
            map -> updateMatcherData(map.getKey(), (TraitPortraitMatcher) loadData(map.getValue()),
                matcherData, counter, traitsPriority));
  }
  
  @Override
  protected PortraitMatcherData loadData(PortraitDataMatch value) {
    return new TraitPortraitMatcher(value.getMatchCriterias());
  }
  
  @Override
  public void process(AnalysisBean analysis, Map<String, PortraitMatcherData> portraitMatchData,
      Map<String, MatchDetailsDTO> matchResult) {
    
    Map<TraitType, BigDecimal> fundamentalNeeds = analysis.getFundamentalNeeds();
    
    final AtomicInteger scoreThresholdCount = new AtomicInteger(0);
    
    TraitPortraitMatcher primaryMatcher = (TraitPortraitMatcher) portraitMatchData
        .get(Constants.PRIMARY);
    
    // sorting the data
    FundamentalNeedsPortraitMatchComparator comparator = new FundamentalNeedsPortraitMatchComparator(
        primaryMatcher.getTraitsPriority());
    List<Entry<TraitType, BigDecimal>> sortedScores = fundamentalNeeds.entrySet().stream()
        .map(entry -> countThresholdViolation(entry, scoreThresholdCount))
        .sorted(comparator.reversed()).collect(Collectors.toList());
    
    final int matchSize = portraitMatchData.size();
    if (scoreThresholdCount.intValue() > 2) {
      matchedZero(matchResult, sortedScores, "BALANCED", matchSize);
      return;
    }
    
    Entry<TraitType, BigDecimal> userPrimaryScore = sortedScores.get(0);
    if (userPrimaryScore.getKey() == TraitType.Security) {
      matchedZero(matchResult, sortedScores, "SECURITY_PRIMARY", matchSize);
      return;
    }
    
    if (matchSize == 2) {
      processMatchPrimaryAndSecondary(portraitMatchData, sortedScores, matchResult);
    } else {
      processMatchPrimary(primaryMatcher, sortedScores, matchResult, fundamentalNeeds);
    }
  }
  
  private void processMatchPrimaryAndSecondary(Map<String, PortraitMatcherData> portraitMatchData,
      List<Entry<TraitType, BigDecimal>> sortedScores, Map<String, MatchDetailsDTO> matchResult) {
    
    TraitPortraitMatcher primaryMatcher = (TraitPortraitMatcher) portraitMatchData
        .get(Constants.PRIMARY);
    
    TraitPortraitMatcher secondaryMatcher = null;
    Entry<TraitType, BigDecimal> userSecondaryScore = null;
    
    Entry<TraitType, BigDecimal> userPrimaryScore = sortedScores.get(0);
    switch (primaryMatcher.getTraitToMatch()) {
    case Control:
      if (userPrimaryScore.getKey() == TraitType.Control) {
        matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 100));
        
        secondaryMatcher = (TraitPortraitMatcher) portraitMatchData.get(Constants.SECONDARY);
        userSecondaryScore = sortedScores.get(1);
        if (userSecondaryScore.getKey() == secondaryMatcher.getTraitToMatch()) {
          matchResult.put(Constants.SECONDARY, new MatchDetailsDTO(userSecondaryScore, 100));
        } else {
          matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(userSecondaryScore, 0,
              "SECONDARY_NOT_MATCH"));
        }
      } else {
        matchedZero(matchResult, sortedScores, "PRIMARY_NOT_MATCH", 2);
      }
      break;
    
    case Significance:
      if (userPrimaryScore.getKey() == TraitType.Significance) {
        matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 100));
        
        secondaryMatcher = (TraitPortraitMatcher) portraitMatchData.get(Constants.SECONDARY);
        userSecondaryScore = sortedScores.get(1);
        if (userSecondaryScore.getKey() == secondaryMatcher.getTraitToMatch()) {
          Entry<TraitType, BigDecimal> userTertiaryScore = sortedScores.get(2);
          if (userSecondaryScore.getValue().compareTo(userTertiaryScore.getValue()) == 0) {
            matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(userSecondaryScore,
                60, "CONTROL_SECURITY_EQUAL"));
          } else {
            matchResult.put(Constants.SECONDARY, new MatchDetailsDTO(userSecondaryScore, 100));
          }
        } else {
          matchedZero(matchResult, sortedScores, "SIGNIFICANCE_SECONDARY_NOT_MATCH", 2);
        }
      } else {
        matchedZero(matchResult, sortedScores, "PRIMARY_NOT_MATCH", 2);
      }
      break;
    
    default:
      matchedZero(matchResult, sortedScores, "INVALID", 2);
      break;
    }
  }
  
  /**
   * Match the primary scale for the fundamental needs.
   * 
   * @param portraitMatcherData
   *          - data to match
   * @param sortedScores
   *          - sorted scores
   * @param matchResult
   *          - match results
   * @param fundamentalNeeds
   *          - fundamental needs
   */
  private void processMatchPrimary(TraitPortraitMatcher portraitMatcherData,
      List<Entry<TraitType, BigDecimal>> sortedScores, Map<String, MatchDetailsDTO> matchResult,
      Map<TraitType, BigDecimal> fundamentalNeeds) {
    
    final Entry<TraitType, BigDecimal> userPrimaryScore = sortedScores.get(0);
    TraitType primaryTrait = userPrimaryScore.getKey();
    
    switch (portraitMatcherData.getTraitToMatch()) {
    case Control:
      if (primaryTrait == TraitType.Control) {
        if (compareTraits(userPrimaryScore, TraitType.Security, fundamentalNeeds)) {
          matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(userPrimaryScore, 0,
              "CONTROL_EQUAL_SECURITY"));
        } else {
          matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 100));
        }
      } else {
        matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 0));
      }
      break;
    case Significance:
      if (primaryTrait == TraitType.Significance) {
        Entry<TraitType, BigDecimal> userSecondary = sortedScores.get(1);
        if (userSecondary.getValue().compareTo(userPrimaryScore.getValue()) == 0) {
          matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(userPrimaryScore, 50,
              "PRIMARY_SCONDARY_EQUAL"));
        } else {
          matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 100));
        }
      } else {
        matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 0));
      }
      break;
    default:
      matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(userPrimaryScore, 0,
          "UNKNOWN"));
      break;
    }
  }
  
  private boolean compareTraits(final Entry<TraitType, BigDecimal> userPrimaryScore,
      TraitType trait, Map<TraitType, BigDecimal> fundamentalNeeds) {
    return userPrimaryScore.getValue().compareTo(fundamentalNeeds.get(trait)) == 0;
  }
  
  /**
   * Adding Zero as the match percent for both.
   * 
   * @param portraitMatchData
   *          - portrait match data
   * @param matchResult
   *          - match result
   * @param sortedScores
   *          - sorted scores
   * @param reason
   *          - reason
   * @param matchSize
   *          - count to indicate if both primary and secondary present
   */
  private void matchedZero(Map<String, MatchDetailsDTO> matchResult,
      List<Entry<TraitType, BigDecimal>> sortedScores, String reason, int matchSize) {
    final Iterator<Entry<TraitType, BigDecimal>> scoreIterator = sortedScores.iterator();
    matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(scoreIterator.next(), 0,
        reason));
    if (matchSize > 1) {
      matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(scoreIterator.next(), 0,
          reason));
    }
  }
  
  private Entry<TraitType, BigDecimal> countThresholdViolation(Entry<TraitType, BigDecimal> entry,
      AtomicInteger scoreThresholdCount) {
    
    if (entry.getValue().longValue() > SCORE_THRESHOLD) {
      scoreThresholdCount.incrementAndGet();
    }
    return entry;
  }
}
