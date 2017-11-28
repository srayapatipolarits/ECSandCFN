package com.sp.web.controller.goal;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class PracticeAreaLoaderTest extends SPTestBase {

  @Autowired
  SPGoalFactory goalsFactory;
  
  @Value("classpath:PracticeAreaDevelopmentStrategies.tsv")
  private Resource practiceAreaDevelopmentStrategiesFile;

  @Value("classpath:PersonalityPracticeAreasMatrix.tsv")
  private Resource personalityPracticeAreaMatrixFile;
  
  @Test
  public void testDoNothing() {
    assertTrue(true);
  }

  @Test
  public void testLoadPracticeAreas() {
    try {
      List<String> practiceAreaDevelopmentStrategiesContent = IOUtils
          .readLines(practiceAreaDevelopmentStrategiesFile.getInputStream());

      assertThat(practiceAreaDevelopmentStrategiesContent.size(), is(greaterThan(0)));

      // removing the heading 
      practiceAreaDevelopmentStrategiesContent.remove(0);
      
      ArrayList<String> dbCommands = new ArrayList<String>();
      
      for (String inputLine : practiceAreaDevelopmentStrategiesContent) {
        String[] rowContents = inputLine.split("\t");
        String newPracticeAreaName = (String) normalize(rowContents[0]);
        String oldPracticeAreaName = (String) normalize(rowContents[1]);
        String description = (String) normalize(rowContents[2]);
        StringBuffer sb = new StringBuffer();
        sb.append("db.sPGoal.");
        if (oldPracticeAreaName.equals("New")) {
          sb.append("insert({name:").append(addValue(newPracticeAreaName));
          addValues(rowContents, description, sb);
        } else {
          if (oldPracticeAreaName.length() != 0) {
            sb.append("update({name:")
              .append(addValue(oldPracticeAreaName, false))
              .append("}, {$set:{")
              .append("name:").append(addValue(newPracticeAreaName));
          } else {
            sb.append("update({name:")
              .append(addValue(newPracticeAreaName, false))
              .append("}, {$set:{");
          }
          addValues(rowContents, description, sb);
          sb.append("}");
        }
        sb.append(")");
        dbCommands.add(sb.toString());
      }
      dbCommands.forEach(System.out::println);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testLoadPersonalityPracticeAreas() {
    try {
      List<String> personalityPracticeAreaContent = IOUtils
          .readLines(personalityPracticeAreaMatrixFile.getInputStream());

      assertThat(personalityPracticeAreaContent.size(), is(greaterThan(0)));

      // removing the heading 
      personalityPracticeAreaContent.remove(0);
      
      Properties props = new Properties();
      List<SPGoal> allGoals = goalsFactory.getAllGoals();
      Map<String, List<SPGoal>> goalsMap = allGoals.stream().collect(Collectors.groupingBy(SPGoal::getName));
      
      for (String inputLine : personalityPracticeAreaContent) {
        String[] rowContents = inputLine.split("\t");
        String profileName = (String) normalize(rowContents[0]);
        String[] practiceAreas = rowContents[1].split("\\*");
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for (String practiceArea : practiceAreas) {
          if (index != 0) {
            sb.append(",");
          }
          practiceArea = practiceArea.trim();
          if (practiceArea.length() == 0) {
            continue;
          }
          if (!goalsMap.containsKey(practiceArea)) {
            fail("Practice area not found :" + practiceArea);
          }
          sb.append(escape(practiceArea));
          index++;
        }
        if (profileName.equals("Tough & Tender")) {
          profileName = PersonalityType.Tough_N_Tender.toString();
        }
        profileName = PersonalityTypeMapper.valueOf(profileName).getMapsTo().toString();
        props.put(profileName, sb.toString());
      }
      
      for (PersonalityType personalityType : PersonalityType.values()) {
        switch (personalityType) {
        case Invalid:
          break;
        case Overshift:
          break;
        case Undershift:
          break;
        case Tight:
          break;
        default:
          String property = props.getProperty(personalityType.toString());
          if (property == null) {
            fail("Mapping not found for :" + personalityType);
          }
          break;
        }
      }
      props.forEach((key, val) -> System.out.println("practiceArea." + key + "=" + val));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  private void addValues(String[] rowContents, String description, StringBuffer sb) {
    sb.append("description:").append(addValue(description))
      .append("category:").append(addValue(GoalCategory.GrowthAreas))
      .append("status:").append(addValue(GoalStatus.ACTIVE))
      .append("developmentStrategyList:[");
    int idIndex = 0;
    String[] developmentStrategyArr = rowContents[3].split("\\*");
    int dsLength = developmentStrategyArr.length - 1;
    for (String devStrategyLine : developmentStrategyArr) {
      if (devStrategyLine.trim().length() == 0) {
        continue;
      }
      sb.append("{id:").append(addValue(idIndex++))
        .append("dsDescription:").append(addValue(devStrategyLine))
        .append("isActive:").append(addValue(true, false));
      if (idIndex == dsLength) {
        sb.append("}");
      } else {
        sb.append("}, ");
      }
    }
    
    sb.append("]}");
  }

  private Object addValue(Object oldPracticeAreaName) {
    return addValue(oldPracticeAreaName, true);
  }

  private Object addValue(Object oldPracticeAreaName, boolean doAddComma) {
    return (oldPracticeAreaName != null ? "\"" + normalize(oldPracticeAreaName) + "\"" : null)
        + ((doAddComma) ? ", " : "");
  }
  
  private Object normalize(Object str) {
    //String strToRet = WordUtils.capitalizeFully(str.trim()).replaceAll("\"", "\\\"");
    if (str instanceof String) {
      return ((String)str).trim().replaceAll("\"", "\\\"");
    }
    return str;
  }

  private String escape(String str) {
    if (str == null) {
      return "";
    }
    return str.replaceAll("'", "''");
  }

}
