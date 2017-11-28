package com.sp.web.controller.feedback;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SwotType;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The loader test class for SWOT copy from Excel.
 */
public class PersonalitySwotLoaderTest extends SPTestBase {

  @Autowired
  SPGoalFactory goalsFactory;
  
  @Value("classpath:sp-profile-swot.tsv")
  private Resource personalitySwotFile;

  @Test
  public void testDoNothing() {
    assertTrue(true);
  }

  @Test
  public void testLoadPersonalityLoadFromDb() {
    PersonalityPracticeArea personalityPracticeArea = goalsFactory
        .getPersonalityPracticeArea(PersonalityType.Actuary);
    assertNotNull(personalityPracticeArea.getSwotProfileMap());
  }

    
  @Test
  public void testLoadPersonalitySwot() {
    try {
      List<String> personalitySwotContent = IOUtils
          .readLines(personalitySwotFile.getInputStream());

      assertThat(personalitySwotContent.size(), is(greaterThan(0)));

      // removing the heading 
      personalitySwotContent.remove(0);
      
      ArrayList<String> dbCommands = new ArrayList<String>();
      
      ObjectMapper om = new ObjectMapper();
      
      for (String inputLine : personalitySwotContent) {
        log.info(inputLine);
        String[] rowContents = inputLine.split("\t");
        if (rowContents.length == 0) {
          continue;
        }
        String profileName = (String) normalize(rowContents[0]);
        PersonalityType personalityType = null;
        try {
          PersonalityTypeMapper typeMapper = PersonalityTypeMapper.valueOf(profileName);
          personalityType = typeMapper.getMapsTo();
        } catch (Exception e) {
          log.warn("Ignoring personality type :" + profileName);
          continue;
        }
        
        String[] strengthsArray = ((String) normalize(rowContents[1])).split(",");
        String[] weaknessArray = ((String) normalize(rowContents[2])).split(",");
        String[] opportunitiesArray = ((String) normalize(rowContents[3])).split(",");
        String[] threatsArray = ((String) normalize(rowContents[4])).split(",");
        
        Map<SwotType, List<String>> swotProfileMap = new HashMap<SwotType, List<String>>();
        
        swotProfileMap.put(SwotType.Strengths, getList(strengthsArray));
        swotProfileMap.put(SwotType.Weakness, getList(weaknessArray));
        swotProfileMap.put(SwotType.Opportunities, getList(opportunitiesArray));
        swotProfileMap.put(SwotType.Threats, getList(threatsArray));
        
        StringBuffer sb = new StringBuffer();
        sb.append("db.personalityPracticeArea.update({personalityType:")
          .append(addValue(personalityType, false))
          .append("}, ");
        sb.append("{$set:{swotProfileMap:")
          .append(om.writeValueAsString(swotProfileMap))
          .append("}})");
        dbCommands.add(sb.toString());
      }
      dbCommands.forEach(System.out::println);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  private List<String> getList(String[] strengthsArray) {
    return Arrays.stream(strengthsArray).map(String::trim).collect(Collectors.toList());
  }

  private Object addValue(Object value) {
    return addValue(value, true);
  }

  private Object addValue(Object value, boolean doAddComma) {
    return (value != null ? "\"" + normalize(value) + "\"" : null)
        + ((doAddComma) ? ", " : "");
  }
  
  private Object normalize(Object str) {
    //String strToRet = WordUtils.capitalizeFully(str.trim()).replaceAll("\"", "\\\"");
    if (str instanceof String) {
      return ((String)str).trim().replaceAll("\"", "\\\"");
    }
    return str;
  }

}
