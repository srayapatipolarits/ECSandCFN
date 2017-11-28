package com.sp.web.service.hiring.match.processor;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.processing.ConflictManagementComparator;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.dto.hiring.user.MatchDetailsWithReasonDTO;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The portrait match processor for percentage based comparisons.
 */
@Component("conflictManagementPortraitMatchProcessor")
public class ConflictManagementPortraitMatchProcessor extends AbstractPortraitMatchProcessor {
  
  private static final ConflictManagementComparator comparator = new ConflictManagementComparator();
  private static final String[][] scoreOverride = { { "CollaborateAccommodate", "Compromise" },
      { "CollaborateCompromise", "Accommodate" },
      { "CollaborateAvoid", "Accommodate", "Compromise" },
      { "AccommodateCollaborate", "Compromise" }, { "AccommodateCompromise", "Collaborate" },
      { "AccommodateAvoid", "Collaborate", "Compromise" }, { "CompeteCollaborate", "Accommodate" },
      { "CompeteAccommodate", "Collaborate" },
      { "CompeteCompromise", "Collaborate", "Accommodate" } };
  
  private Map<String, Set<TraitType>> scoreOverrideMap = new HashMap<String, Set<TraitType>>();
  
  /**
   * Constructor.
   */
  public ConflictManagementPortraitMatchProcessor() {
    for (int i = 0; i < scoreOverride.length; i++) {
      Set<TraitType> overrideTraits = new HashSet<TraitType>();
      for (int j = 0; j < scoreOverride[i].length; j++) {
        if (j == 0) {
          scoreOverrideMap.put(scoreOverride[i][j], overrideTraits);
          continue;
        }
        overrideTraits.add(TraitType.valueOf(scoreOverride[i][j]));
      }
    }
  }
  
  @Override
  protected PortraitMatcherData loadData(PortraitDataMatch value) {
    return new TraitPortraitMatcher(value.getMatchCriterias());
  }
  
  @Override
  public void process(AnalysisBean analysis, Map<String, PortraitMatcherData> portraitMatchData,
      Map<String, MatchDetailsDTO> matchResult) {
    
    Map<TraitType, BigDecimal> conflictManagement = analysis.getConflictManagement();
    
    TraitPortraitMatcher primaryMatcher = (TraitPortraitMatcher) portraitMatchData
        .get(Constants.PRIMARY);
    
    // sorting the data
    List<Entry<TraitType, BigDecimal>> sortedScores = conflictManagement.entrySet().stream()
        .sorted(comparator.reversed()).collect(Collectors.toList());
    
    Entry<TraitType, BigDecimal> userPrimaryScore = sortedScores.get(0);
    if (userPrimaryScore.getKey() == TraitType.Avoid) {
      matchedZero(matchResult, sortedScores, "AVOID_PRIMARY", portraitMatchData.size());
      return;
    }
    
    int scoreReducer = (conflictManagement.get(TraitType.Compete).compareTo(BigDecimal.ZERO) == 0) ? 20
        : 0;
    if (portraitMatchData.size() == 2) {
      processMatchPrimaryAndSecondary(portraitMatchData, sortedScores, matchResult, scoreReducer);
    } else {
      processMatchPrimary(primaryMatcher, sortedScores, matchResult, conflictManagement,
          scoreReducer);
    }
  }
  
  /**
   * Processing the primary and secondary traits match for conflict management.
   * 
   * @param portraitMatchData
   *          - match data
   * @param sortedScores
   *          - sorted scores
   * @param matchResult
   *          - match results
   * @param scoreReducer
   *          - score reducer
   */
  private void processMatchPrimaryAndSecondary(Map<String, PortraitMatcherData> portraitMatchData,
      List<Entry<TraitType, BigDecimal>> sortedScores, Map<String, MatchDetailsDTO> matchResult,
      int scoreReducer) {
    
    String reason = scoreReducer == 0 ? "MATCH" : "COMPETE_ZERO";
    TraitType primaryTraitToMatch = getTraitToMatch(portraitMatchData, Constants.PRIMARY);
    TraitType secondaryTraitToMatch = getTraitToMatch(portraitMatchData, Constants.SECONDARY);
    
    Entry<TraitType, BigDecimal> userPrimaryScore = sortedScores.get(0);
    Entry<TraitType, BigDecimal> userSecondaryScore = sortedScores.get(1);
    
    final TraitType userPrimaryTrait = userPrimaryScore.getKey();
    final TraitType userSecondaryTrait = userSecondaryScore.getKey();
    
    boolean matched = false;
    if (primaryTraitToMatch == userPrimaryTrait) { // primary matched
      matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(userPrimaryScore,
          100 - scoreReducer, reason));
      if (secondaryTraitToMatch == userSecondaryTrait) {
        matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(userSecondaryScore,
            100 - scoreReducer, reason));
      } else { // override match
        Integer scoreOverride = getScoreOverride(primaryTraitToMatch, secondaryTraitToMatch,
            userSecondaryTrait);
        matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(userSecondaryScore,
            scoreOverride - scoreReducer, "OVERRIDE_MATCH"));
      }
      matched = true;
    } else if (secondaryTraitToMatch == userPrimaryTrait) { // reverse matched
      if (primaryTraitToMatch == userSecondaryTrait) {
        reason = "REVERSE_MATCH";
        matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(userPrimaryScore,
            80 - scoreReducer, reason));
        matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(userSecondaryScore,
            80 - scoreReducer, reason));
        matched = true;
      }
    } else if (secondaryTraitToMatch == userSecondaryTrait) { // secondary match
      matchResult.put(Constants.PRIMARY, new MatchDetailsWithReasonDTO(userPrimaryScore,
          0 - scoreReducer, reason));
      matchResult.put(Constants.SECONDARY, new MatchDetailsWithReasonDTO(userSecondaryScore,
          40 - scoreReducer, reason));
      matched = true;
    }
    
    if (!matched) {
      matchedZero(matchResult, sortedScores, "NO_MATCH", 2);
    }
  }
  
  /**
   * Get the override score for the given primary secondary traits match.
   * 
   * @param primaryTraitToMatch
   *          - primary trait to match
   * @param secondaryTraitToMatch
   *          - secondary trait to match
   * @param userSecondaryTrait
   *          - user secondary trait
   * @return the override score
   */
  private Integer getScoreOverride(TraitType primaryTraitToMatch, TraitType secondaryTraitToMatch,
      TraitType userSecondaryTrait) {
    return Optional
        .ofNullable(scoreOverrideMap.get(primaryTraitToMatch + "" + secondaryTraitToMatch))
        .map(set -> set.contains(userSecondaryTrait) ? 60 : 0).orElse(0);
  }
  
  /**
   * Get the traits to match from the portrait matcher.
   * 
   * @param portraitMatchData
   *          - portrait match data
   * @param key
   *          - key
   * @return the trait to match
   */
  private TraitType getTraitToMatch(Map<String, PortraitMatcherData> portraitMatchData, String key) {
    return ((TraitPortraitMatcher) portraitMatchData.get(key)).getTraitToMatch();
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
   * @param scoreReducer
   *          - in case there is a score reducer to apply
   */
  private void processMatchPrimary(TraitPortraitMatcher portraitMatcherData,
      List<Entry<TraitType, BigDecimal>> sortedScores, Map<String, MatchDetailsDTO> matchResult,
      Map<TraitType, BigDecimal> fundamentalNeeds, int scoreReducer) {
    
    final Entry<TraitType, BigDecimal> userPrimaryScore = sortedScores.get(0);
    TraitType primaryTrait = userPrimaryScore.getKey();
    
    if (portraitMatcherData.getTraitToMatch() == primaryTrait) {
      matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 100 - scoreReducer));
    } else {
      matchResult.put(Constants.PRIMARY, new MatchDetailsDTO(userPrimaryScore, 0));
    }
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
}
