package com.sp.web.service.teamdynamics;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.processing.ConflictManagementComparator;
import com.sp.web.assessment.questions.CategoryPairs;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dto.teamdynamics.TeamDynamicsResultDTO;
import com.sp.web.model.User;
import com.sp.web.model.teamdynamics.CategoryPairData;
import com.sp.web.model.teamdynamics.TeamDynamicsLoadData;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Conflict Manager team dynamcis processor will load the user primary and seconday processor.
 * 
 * @author pradeepruhil
 *
 */
@Component("conflictTeamDynamicsProcessor")
public class ConflictManageTeamDynamicsProcessor implements TeamDynamicsProcessor {
  
  @Override
  public void loadData(User user, TeamDynamicsLoadData dynamicsLoadData, CategoryType categoryType) {
    
    AnalysisBean analysisBean = user.getAnalysis();
    Map<TraitType, BigDecimal> conflictManagement = analysisBean.getConflictManagement();
    
    /* sort the data */
    List<Entry<TraitType, BigDecimal>> sortedScores = conflictManagement.entrySet().stream()
        .sorted(new ConflictManagementComparator().reversed()).collect(Collectors.toList());
    
    TraitType primaryTrait = sortedScores.get(0).getKey();
    
    TraitType secondaryTrait = sortedScores.get(1).getKey();
    
    CategoryPairs primaryCategory = CategoryPairs.getCategoryPairForTrait(primaryTrait);
    dynamicsLoadData.addCategoryPairData(user, primaryCategory, categoryType, false, primaryTrait);
    
    CategoryPairs secondaryCategory = CategoryPairs.getCategoryPairForTrait(secondaryTrait);
    dynamicsLoadData.addCategoryPairData(user, secondaryCategory, categoryType, false,
        secondaryTrait);
    
  }
  
  @Override
  public void processResult(TeamDynamicsLoadData dynamicsLoadData,
      TeamDynamicsResultDTO dynamicsResultDTO, CategoryType categoryType) {
    
    List<CategoryPairData> listResults = new ArrayList<CategoryPairData>();
    dynamicsResultDTO.getResults().computeIfAbsent(categoryType, k -> listResults);
    
    Map<CategoryPairs, CategoryPairData> loadMap = dynamicsLoadData.getCategoryPairData().get(
        categoryType);
    
    /* find the top 2 category pair whose has the most votes */
    Set<Entry<CategoryPairs, CategoryPairData>> entrySet = loadMap.entrySet();
    
    Comparator<Entry<CategoryPairs, CategoryPairData>> entryCompartor = (e1, e2) -> {
      int compareScore = ((Integer) e1.getValue().getTraitsMap().get(e1.getKey().getTraitType1())
          .size()).compareTo((Integer) e2.getValue().getTraitsMap()
          .get(e2.getKey().getTraitType1()).size());
      if (compareScore == 0) {
        return (e1.getKey().getTraitType1().getPriority()).compareTo(e2.getKey().getTraitType1()
            .getPriority());
      }
      return compareScore;
    };
    List<Entry<CategoryPairs, CategoryPairData>> collect = entrySet.stream()
        .sorted(entryCompartor.reversed()).collect(Collectors.toList());
    
    collect.stream().forEach(entry -> {
      listResults.add(entry.getValue());
    });
    
  }
}
