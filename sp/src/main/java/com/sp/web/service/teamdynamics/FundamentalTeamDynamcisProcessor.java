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
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Component("fundamentalTeamDynamicsProcessor")
public class FundamentalTeamDynamcisProcessor implements TeamDynamicsProcessor {
  
  @Override
  public void loadData(User user, TeamDynamicsLoadData dynamicsLoadData, CategoryType categoryType) {
    
    AnalysisBean analysisBean = user.getAnalysis();
    
    Map<TraitType, BigDecimal> fundamentalNeeds = analysisBean.getFundamentalNeeds();
    
    List<Entry<TraitType, BigDecimal>> sortedScores = fundamentalNeeds.entrySet().stream()
        .sorted(new ConflictManagementComparator().reversed()).collect(Collectors.toList());
    
    TraitType primaryTrait = sortedScores.get(0).getKey();
    dynamicsLoadData.addCategoryPairData(user, CategoryPairs.getCategoryPairForTrait(primaryTrait),
        categoryType, false, primaryTrait);
    
  }
  
  @Override
  public void processResult(TeamDynamicsLoadData dynamicsLoadData,
      TeamDynamicsResultDTO dynamicsResultDTO, CategoryType categoryType) {
    
    List<CategoryPairData> categoryPairList = new ArrayList<CategoryPairData>();
    dynamicsResultDTO.getResults().computeIfAbsent(categoryType, k -> categoryPairList);
    
    Map<CategoryPairs, CategoryPairData> loadMap = dynamicsLoadData.getCategoryPairData().get(
        categoryType);
    
    Set<Entry<CategoryPairs, CategoryPairData>> entrySet = loadMap.entrySet();
    
    Comparator<Entry<CategoryPairs, CategoryPairData>> entryCompartor = (e1, e2) -> {
      int compareScore = ((Integer) e1.getValue().getTraitsMap().get(e1.getKey().getTraitType1())
          .size()).compareTo((Integer) e2.getValue().getTraitsMap()
          .get(e2.getKey().getTraitType1()).size());
      if (compareScore == 0) {
        return (e2.getKey().getTraitType1()).compareTo(e1.getKey().getTraitType1());
      }
      return compareScore;
    };
    List<Entry<CategoryPairs, CategoryPairData>> collect = entrySet.stream()
        .sorted(entryCompartor.reversed()).collect(Collectors.toList());
    
    // Entry<CategoryPairs, CategoryPairData> first = collect.get(0);
    
    collect.stream().forEach(entry -> {
      categoryPairList.add(entry.getValue());
    });
    
  }
}
