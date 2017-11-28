package com.sp.web.controller.goal;

import static org.junit.Assert.assertNotNull;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoalsPersonalityTypeLoader extends SPTestBase {

  @Value("classpath:GrowthOpportunitiesPerProfile.tsv")
  private Resource growthOpportunitesFile;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Test
  public void testGetThemes() throws IOException {
    List<String> growthOpportunitesFileContents = IOUtils.readLines(growthOpportunitesFile
        .getInputStream());

    // removing the heading 
    growthOpportunitesFileContents.remove(0);
    
    Map<SPGoal, List<PersonalityType>> personalityGoalsMap = new HashMap<SPGoal, List<PersonalityType>>(); 
    
    for (int i = 0; i < 16; i++) {
      String growthOppoLine = growthOpportunitesFileContents.get(i);
      String[] split = growthOppoLine.split("\t");
      String personalityTypeStr = split[0];
      String[] goalsNameList = split[1].split("-");
      if (personalityTypeStr.equalsIgnoreCase("Tough & Tender")) {
        personalityTypeStr = "Tough_N_Tender";
      }
      PersonalityType personalityType = PersonalityTypeMapper.valueOf(personalityTypeStr).getMapsTo();
      for (int j = 0; j < goalsNameList.length; j++) {
        String goalNameStr = goalsNameList[j];
        if (StringUtils.isNotEmpty(goalNameStr)) {
          SPGoal findGoalByName = goalsRepository.findGoalByName(goalNameStr.trim());
          if(findGoalByName!=null){
            assertNotNull(goalNameStr, findGoalByName);
            List<PersonalityType> personalityList = personalityGoalsMap.get(findGoalByName);
            if (personalityList == null) {
              personalityList = new ArrayList<PersonalityType>();
              personalityGoalsMap.put(findGoalByName, personalityList);
            }
            personalityList.add(personalityType);  
          }
          
        }
      }      
    }
    
    for (SPGoal goal : personalityGoalsMap.keySet()) {
      List<PersonalityType> list = personalityGoalsMap.get(goal);
      System.out.println("db.sPGoal.update({name:\""
          + goal.getName()
          + "\"}, {$set:{"
          + "\"personalityType\" : ["
          + list.stream().map(p -> "\"" + p + "\"")
              .collect(Collectors.joining(",")) + "]}});");
    }
    
  }
  
}
