package com.sp.web.service.teamdynamics;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryPairs;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dto.teamdynamics.CategoryPairTeamDynamicsResult;
import com.sp.web.dto.teamdynamics.TeamDynamicsResultDTO;
import com.sp.web.model.User;
import com.sp.web.model.teamdynamics.CategoryPairData;
import com.sp.web.model.teamdynamics.TeamDynamicsLoadData;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryPairsTeamDynamcisProcessor will load the team dynamics for the category pairs data.
 * 
 * @author pradeepruhil
 *
 */
@Component("categoryPairTeamDynamicsProcessor")
public class CategoryPairsTeamDynamcisProcessor implements TeamDynamicsProcessor {
  
  /**
   * @see TeamDynamicsProcessor#loadData(User, TeamDynamicsLoadData, CategoryType).
   */
  @Override
  public void loadData(User user, TeamDynamicsLoadData dynamicsLoadData, CategoryType categoryType) {
    AnalysisBean analysisBean = user.getAnalysis();
    Map<TraitType, BigDecimal> processing = null;
    switch (categoryType) {
    case Motivation:
      processing = analysisBean.getMotivationHow();
      processing.putAll(analysisBean.getMotivationWhat());
      ;
      processing.putAll(analysisBean.getMotivationWhy());
      break;
    case Processing:
      processing = analysisBean.getProcessing();
      break;
    case DecisionMaking:
      processing = analysisBean.getDecisionMaking();
      
    default:
      break;
    }
    if (processing == null) {
      return;
    }
    processing.forEach((traitKey, bigDec) -> {
      
      if (bigDec.intValue() >= 40 && bigDec.intValue() <= 60) {
        CategoryPairs categoryPairForTrait = CategoryPairs.getCategoryPairForTrait(traitKey);
        dynamicsLoadData.addCategoryPairData(user, categoryPairForTrait, categoryType, true,
            traitKey);
      } else if (bigDec.intValue() > 60) {
        CategoryPairs categoryPairForTrait = CategoryPairs.getCategoryPairForTrait(traitKey);
        dynamicsLoadData.addCategoryPairData(user, categoryPairForTrait, categoryType, false,
            traitKey);
      }
    });
    
  }
  
  /**
   * processResult will process the result for the category pair personality module.
   */
  @Override
  public void processResult(TeamDynamicsLoadData dynamicsLoadData,
      TeamDynamicsResultDTO dynamicsResultDTO, CategoryType categoryType) {
    
    dynamicsResultDTO.getResults().computeIfAbsent(categoryType,
        k -> new HashMap<CategoryPairs, CategoryPairData>());
    
    final Map<CategoryPairs, CategoryPairData> resultCategoryPairMap = (Map<CategoryPairs, CategoryPairData>) dynamicsResultDTO
        .getResults().get(categoryType);
    Map<CategoryPairs, CategoryPairData> loadMap = dynamicsLoadData.getCategoryPairData().get(
        categoryType);
    
    /*
     * In CategoryPair,there are pairing of traits. So each category pair contains equal traits and
     * individual traits users.
     */
    loadMap
        .forEach((pair, pairData) -> {
          
          final CategoryPairTeamDynamicsResult teamDynamicsResult = (CategoryPairTeamDynamicsResult) resultCategoryPairMap
              .computeIfAbsent(pair, k -> new CategoryPairTeamDynamicsResult());
          
          teamDynamicsResult.setTraitsMap(pairData.getTraitsMap());
          teamDynamicsResult.setEqualTraitsUserIds(pairData.getEqualTraitsUserIds());
          
          Map<TraitType, List<String>> traitsMap = pairData.getTraitsMap();
          List<String> trait1List = traitsMap.get(pair.getTraitType1());
          int traitOneUsers = trait1List != null ? trait1List.size() : 0;
          
          List<String> trait2List = traitsMap.get(pair.getTraitType2());
          int traitTwoUsers = trait2List != null ? trait2List.size() : 0;
          int totalUsers = traitOneUsers + traitTwoUsers;
          if (totalUsers == 0) {
            teamDynamicsResult.setShowLeaningStrengths(true);
          } else {
            BigDecimal traitOnePercent = (new BigDecimal(traitOneUsers).divide(new BigDecimal(
                totalUsers), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)).setScale(2,
                RoundingMode.UP);
            
            BigDecimal traitTwoPercent = (new BigDecimal(traitTwoUsers).divide(new BigDecimal(
                totalUsers), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)).setScale(2,
                RoundingMode.UP);
            
            if (traitOnePercent.compareTo(traitTwoPercent) == 0) {
              teamDynamicsResult.setShowInBalanceStrengths(true);
            } else if ((traitOnePercent.floatValue() > 50 && traitOnePercent.floatValue() < 60)
                || (traitTwoPercent.floatValue() > 50 && traitTwoPercent.floatValue() < 60)) {
              teamDynamicsResult.setShowLeaningStrengths(true);
            } else if ((traitOnePercent.floatValue() >= 60 && traitOnePercent.floatValue() < 75)
                || (traitTwoPercent.floatValue() >= 60 && traitTwoPercent.floatValue() < 75)) {
              teamDynamicsResult.setShowLibalitesAndAdvice(true);
              teamDynamicsResult.setShowLeaningStrengths(true);
              teamDynamicsResult.setColor("yellow");
            } else if ((traitOnePercent.floatValue() >= 75 && traitOnePercent.floatValue() < 90)
                || (traitTwoPercent.floatValue() >= 75 && traitTwoPercent.floatValue() < 90)) {
              teamDynamicsResult.setShowLibalitesAndAdvice(true);
              teamDynamicsResult.setShowLeaningStrengths(true);
              teamDynamicsResult.setColor("orange");
            } else if ((traitOnePercent.floatValue() >= 90 && traitOnePercent.floatValue() <= 100)
                || (traitTwoPercent.floatValue() >= 90 && traitTwoPercent.floatValue() <= 100)) {
              teamDynamicsResult.setShowLibalitesAndAdvice(true);
              teamDynamicsResult.setShowLeaningStrengths(true);
              teamDynamicsResult.setColor("red");
            }
          }
          
        });
    
  }
}
