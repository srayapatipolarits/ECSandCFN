package com.sp.web.controller.relationship;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class RelationshipLoaderTest extends SPTestBase {

  private static final String COMPARE = "compare.";
  
  @Value("classpath:RelationshipAdvisorReport-Personality.tsv")
  private Resource relationshipReportFile;

  @Value("classpath:RelationshipManager-Report-ProfileStatements.tsv")
  private Resource relationshipReportProfileFile;
  
  @Test
  public void testDoNothing() {
    assertTrue(true);
  }

  //@Test
  public void test() throws IOException {
    List<String> relationshipReportFileContent = IOUtils.readLines(relationshipReportFile
        .getInputStream());
    
    List<String> relationshipReportProfileContent = IOUtils.readLines(relationshipReportProfileFile
        .getInputStream());

    assertThat(relationshipReportFileContent.size(), is(7));

    String[] primaryPersonalityTypeStr = relationshipReportFileContent.get(0).split("\t");
    String[] secondaryPersonalityTypeStr = relationshipReportFileContent.get(1).split("\t");
    
    String[] secondaryPersonalityEffort = relationshipReportFileContent.get(2).split("\t");
    String[] primaryPersonalityEffort = relationshipReportFileContent.get(3).split("\t");
    
    String[] secondaryPersonalityAvoid = relationshipReportFileContent.get(4).split("\t");
    String[] primaryPersonalityAvoid = relationshipReportFileContent.get(5).split("\t");

    int maxSize = primaryPersonalityTypeStr.length;

    assertThat(secondaryPersonalityTypeStr.length, is(maxSize));
    assertThat(secondaryPersonalityEffort.length, is(maxSize));
    assertThat(primaryPersonalityEffort.length, is(maxSize));
    assertThat(secondaryPersonalityAvoid.length, is(maxSize));
    assertThat(primaryPersonalityAvoid.length, is(maxSize));

    final Properties props = new Properties();
    try {
      for (int i = 1; i < maxSize; i++) {
        boolean ignore = false;
        
        // get the personality types
        PersonalityType primaryPersonalityType = PersonalityTypeMapper.valueOf(
            normalize(primaryPersonalityTypeStr[i])).getMapsTo();
        PersonalityType secondaryPeronsalityType = PersonalityTypeMapper.valueOf(
            normalize(secondaryPersonalityTypeStr[i])).getMapsTo();
        
        String prefix = COMPARE + primaryPersonalityType + "." + secondaryPeronsalityType;
        
        String secondaryEffortStr = secondaryPersonalityEffort[i];
        
        String primaryEffortStr = null;
        if (primaryPersonalityEffort.length <= i) {
          primaryEffortStr = secondaryEffortStr;
        } else {
          primaryEffortStr = primaryPersonalityEffort[i];
        }
        String secondaryAvoidStr = null;
        if (secondaryPersonalityAvoid.length > i) {
          secondaryAvoidStr = secondaryPersonalityAvoid[i];
        } else {
          ignore = true;
        }
        
        String primaryAvoidStr = null;
        if (primaryPersonalityAvoid.length > i) {
          primaryAvoidStr = primaryPersonalityAvoid[i];
        } else {
          primaryAvoidStr = secondaryAvoidStr;
        }
        
        if (!ignore) {
          primaryEffortStr = process(escape(primaryEffortStr));
          secondaryEffortStr = process(escape(secondaryEffortStr));
          primaryAvoidStr = process(escape(primaryAvoidStr));
          secondaryAvoidStr = process(escape(secondaryAvoidStr));
          
          props.put(prefix + ".primary.effort", primaryEffortStr);
          props.put(prefix + ".secondary.effort", secondaryEffortStr);
          props.put(prefix + ".primary.avoid", primaryAvoidStr);
          props.put(prefix + ".secondary.avoid", secondaryAvoidStr);
          
          // add the reverse only if the prmary and secondary are not same
          if (primaryPersonalityType != secondaryPeronsalityType) {
            prefix = COMPARE + secondaryPeronsalityType + "." + primaryPersonalityType;
            props.put(prefix + ".primary.effort", secondaryEffortStr);
            props.put(prefix + ".secondary.effort", primaryEffortStr);
            props.put(prefix + ".primary.avoid", secondaryAvoidStr);
            props.put(prefix + ".secondary.avoid", primaryAvoidStr);
          }
        }
      }
    } catch (Exception e1) {
      e1.printStackTrace();
      fail();
    }

    // load the profile statements
    relationshipReportProfileContent.forEach(s -> {
        String[] values = s.split("\t");
        PersonalityType personalityType = PersonalityTypeMapper.valueOf(normalize(values[0]))
            .getMapsTo();
        String str = genderNormalize(escape(values[1]));
        String tempStr = str.replaceAll("\\[Name\\]", "");
        if (tempStr.contains("[")) {
          System.out.println(tempStr);
          throw new RuntimeException("Unexpected :" + tempStr);
        }
        props.put(COMPARE + personalityType + ".title", str);
      });
    
    File file = new File("relationshipManagerCompare.properties");
    FileOutputStream fos = new FileOutputStream(file);
    try {
      props.store(fos, "Relationship Manager compare properties.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
        fos.flush();
        fos.close();
    }
  }

  private String normalize(String str) {
    String strToRet = str.trim();
    if (strToRet.equalsIgnoreCase("Tough & Tender")) {
      return PersonalityTypeMapper.Tough_N_Tender.toString();
    }
    return strToRet;
  }

  private String escape(String primaryEffortStr) {
    if (primaryEffortStr == null) {
      return "";
    }
    return primaryEffortStr.replaceAll("'", "''");
  }

  private String process(String primaryEffortStr) {
    List<String> bulletList = Arrays.asList(primaryEffortStr.split("\\*"));
    return bulletList.stream().filter(s -> s.trim().length() > 0).map(s -> {
        String str = "<p>" + genderNormalize(convertToName(s.trim())) + "</p>";
        String tempStr = str.replaceAll("\\[Name\\]", "").replaceAll("\\[Name2\\]", "");
        if (tempStr.contains("[")) {
          System.out.println(tempStr);
          throw new RuntimeException("Unexpected :" + tempStr);
        }
        validateClosure(str);
        return str;
      }).collect(Collectors.joining());
  }

  private void validateClosure(String tempStr) {
    int bracketCount = 0;
    for (int i = 0; i < tempStr.length(); i++) {
      switch (tempStr.charAt(i)) {
      case '[':
        bracketCount++;
        break;
      case '{':
        bracketCount++;
        break;
      case '}':
        bracketCount--;
        break;
      case ']':
        bracketCount--;
        break;
      default:
        // do nothing
      }
    }
    if (bracketCount != 0) {
      throw new RuntimeException(tempStr + ">>>>" + bracketCount);
    }
  }

  private String convertToName(String str) {
    return str.replaceAll("\\{0\\}", "[Name]")
              .replaceAll("\\{1\\}", "[Name]");
  }

  private String genderNormalize(String str) {
    return str.replaceAll("\\[he/she\\]", "{0}")
    .replaceAll("\\[He/She\\]", "{1}")
    .replaceAll("\\[him/her\\]", "{2}")
    .replaceAll("\\[himself/herself\\]", "{3}")
    .replaceAll("\\[his/her\\]", "{4}")
    .replaceAll("\\[his/hers\\]", "{5}")
    .replaceAll("\\[His/Her\\]", "{6}");
  }

}
