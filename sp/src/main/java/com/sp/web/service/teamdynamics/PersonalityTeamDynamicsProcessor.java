package com.sp.web.service.teamdynamics;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryPairs;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dto.teamdynamics.PersonalityDynamicResult;
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
 * PersonalityTeamDynamicsProcessor method will load the category pair data for the personality
 * under pressure and primary.
 * 
 * @author pradeepruhil
 *
 */
@Component("personalityTeamDynamicsProcessor")
public class PersonalityTeamDynamicsProcessor implements TeamDynamicsProcessor {
  
  @Override
  public void loadData(User user, TeamDynamicsLoadData dynamicsLoadData, CategoryType categoryType) {
    
    AnalysisBean analysis = user.getAnalysis();
    
    RangeType rangeType = categoryType == CategoryType.Personality ? RangeType.Primary
        : RangeType.UnderPressure;
    PersonalityBeanResponse personalityBeanResponse = analysis.getPersonality().get(rangeType);
    BigDecimal[] segmentGraph = personalityBeanResponse.getSegmentGraph();
    
    for (int i = 0; i < segmentGraph.length; i++) {
      
      float traitsFloat = segmentGraph[i].floatValue();
      if (traitsFloat > 0.70) {
        
        switch (i) {
        case 0:
          dynamicsLoadData.addCategoryPairData(user, CategoryPairs.Powerfull, categoryType, false,
              TraitType.Powerfull);
          break;
        case 1:
          dynamicsLoadData.addCategoryPairData(user, CategoryPairs.Versatile, categoryType, false,
              TraitType.Versatile);
          break;
        case 2:
          dynamicsLoadData.addCategoryPairData(user, CategoryPairs.Adaptable, categoryType, false,
              TraitType.Adaptable);
          break;
        case 3:
          dynamicsLoadData.addCategoryPairData(user, CategoryPairs.Precise, categoryType, false,
              TraitType.Precise);
          break;
        
        default:
          break;
        }
      }
    }
    
  }
  
  @Override
  public void processResult(TeamDynamicsLoadData dynamicsLoadData,
      TeamDynamicsResultDTO dynamicsResultDTO, CategoryType categoryType) {
    
    dynamicsResultDTO.getResults().computeIfAbsent(categoryType,
        k -> new HashMap<CategoryPairs, CategoryPairData>());
    
    final Map<CategoryPairs, CategoryPairData> resultCategoryPairMap = (Map<CategoryPairs, CategoryPairData>) dynamicsResultDTO
        .getResults().get(categoryType);
    int totalUsers = dynamicsLoadData.getUsers().size();
    Map<CategoryPairs, CategoryPairData> loadMap = dynamicsLoadData.getCategoryPairData().get(
        categoryType);
    
    /*
     * In personality,there is no pairing of traits. So each category pair is trait single trait
     * itself.
     */
    loadMap
        .forEach((pair, pairData) -> {
          
          final PersonalityDynamicResult teamDynamicsResult = (PersonalityDynamicResult) resultCategoryPairMap
              .computeIfAbsent(pair, k -> new PersonalityDynamicResult());
          
          Map<TraitType, List<String>> traitsMap = pairData.getTraitsMap();
          teamDynamicsResult.setTraitsMap(traitsMap);
          List<String> usersId = traitsMap.get(pair.getTraitType1());
          
          int size = usersId.size();
          BigDecimal percentComplete = (new BigDecimal(size).divide(new BigDecimal(totalUsers), 2,
              RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.UP));
          
          if (percentComplete.floatValue() >= 50 && percentComplete.floatValue() < 60) {
            teamDynamicsResult.setShowStrength(true);
          } else if (percentComplete.floatValue() >= 60 && percentComplete.floatValue() < 75) {
            teamDynamicsResult.setShowLibalities(true);
            teamDynamicsResult.setColor(true);
            teamDynamicsResult.setShowStrength(true);
            teamDynamicsResult.setColor("yellow");
          } else if (percentComplete.floatValue() > 74.99 && percentComplete.floatValue() < 90) {
            teamDynamicsResult.setShowLibalities(true);
            teamDynamicsResult.setShowStrength(true);
            teamDynamicsResult.setColor(true);
            teamDynamicsResult.setColor("orange");
          } else if (percentComplete.floatValue() > 89.99 && percentComplete.floatValue() <= 100) {
            teamDynamicsResult.setShowLibalities(true);
            teamDynamicsResult.setColor(true);
            teamDynamicsResult.setColor("red");
            teamDynamicsResult.setShowStrength(true);
          } else {
            teamDynamicsResult.setShowSpecialMessage(true);
          }
          
        });
  }
}
