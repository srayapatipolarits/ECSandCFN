package com.sp.web.utils;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.service.smartling.OrderedProperties;
import com.sp.web.service.smartling.OrderedProperties.OrderedPropertiesBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class AssessmentPropertiesFileTest {
  
  // Create a HashMap which will store keys and values
  // from xls file provided
  Map<String, String> properties = new HashMap<String, String>();
  
  private static final String PERSONLITY_KEYS_PREFIX = "profile.personality.";
  private static final String[] PERSONALITY_KEYS = { "WorkEnvironmentContribution",
      "EmotionalPosture", "DrivingIdeal", "AssessesOthersBy", "MotivationalStyle",
      "MostFavorableWorkEnvironment", "RelationalTaskOrientation", "MobilityPreference",
      "Over-uses", "WhenPressured", "UneasyWhen", "Leadership", "SummaryOfStrengths", "JobMatch",
      "CommunicatingWith", "DisagreeingWith", "NegativePerceptionsMayHaveOfOthers",
      "NegativePerceptionsOthersMayHaveOf", "NotableCharacteristics", "FundamentalDesiresDrive",
      "RecommendedImprovements", "HowToSuperviseAndEncourage" };
  
  @Test
  public void test() {
    Map<String, String> tempMap = new HashMap<>();
    tempMap.put("CommunicatingWith.heading", "Communicating With [Name]");
    tempMap.put("DisagreeingWith.heading", "Disagreeing With [Name]");
    tempMap.put("NegativePerceptionsMayHaveOfOthers.heading",
        "Negative Perceptions [Name] May Have Of Others");
    tempMap.put("NegativePerceptionsOthersMayHaveOf.heading",
        "Negative Perceptions Others May Have Of [Name]");
    
    Properties pro = new Properties();
    try {
        pro.load(new FileInputStream(
            new File(
                "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/properties/profileMessages.properties")));
      
      FileInputStream excelFile = new FileInputStream(new File("GenderAssessmentProject.xlsx"));
      Workbook workbook = new XSSFWorkbook(excelFile);
      Sheet datatypeSheet = workbook.getSheetAt(3);
      Iterator<Row> iterator = datatypeSheet.iterator();
      int lastRowNum = datatypeSheet.getLastRowNum();
      
      for (int i = 1; i <= lastRowNum; i++) {
        
        Row currentRow = datatypeSheet.getRow(i);
        
        Cell cell = currentRow.getCell(0);
        short lastCellNum = currentRow.getLastCellNum();
        String stringCellValue = cell.getStringCellValue();
        int columStart = 6;
        String capitalize = StringUtils.capitalize(stringCellValue.toLowerCase());
        if (capitalize.equalsIgnoreCase("Tough & tender")) {
          capitalize = "Tough_N_Tender";
        }
        PersonalityTypeMapper valueOf = PersonalityTypeMapper.valueOf(capitalize);
        PersonalityType mapsTo = valueOf.getMapsTo();
        
        for (int j = 6, k = 0; j < 29; j++) {
          
          if (j == 19) {
            continue;
          }
          String key = PERSONLITY_KEYS_PREFIX + mapsTo.toString() + "." + PERSONALITY_KEYS[k];
          
          Cell cellCol = currentRow.getCell(j);
          
          String value = cellCol.getStringCellValue();
          
          System.out.println(key + ": " + value);
          properties.put(key, value);
          k++;
        }
        
        Cell cellCol = currentRow.getCell(4);
        
        String value = cellCol.getStringCellValue();
        
        String lines[] = value.split("\\r?\\n");
        
        for (int sum = 0, index = 0; sum < lines.length; sum++) {
          
          String sumString = lines[sum];
          if (StringUtils.isBlank(sumString)) {
            continue;
          }
          
          String key = PERSONLITY_KEYS_PREFIX + mapsTo.toString() + ".summary" + (index + 1);
          index++;
          properties.put(key, sumString);
        }
        
        String typeKey = PERSONLITY_KEYS_PREFIX + mapsTo.toString() + ".type";
        String property = pro.getProperty(typeKey);
        properties.put(typeKey, property);
        
        tempMap.forEach((k, v) -> {
          
          String key = PERSONLITY_KEYS_PREFIX + mapsTo.toString() +"." + k;
          properties.put(key, v);
        });
      }
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Call writeToPropertiesFile method by passing the location of
    // the properties file. This method will store keys and values
    // from hashMap to properties file
    writeToPropertiesFile("resources.properties");
  }
  
  public void writeToPropertiesFile(String propertiesPath) {
    
    // Creating a new Properties object
    OrderedPropertiesBuilder builder = new OrderedPropertiesBuilder();
    builder.withOrdering(String.CASE_INSENSITIVE_ORDER);
    builder.withSuppressDateInComment(true);
    OrderedProperties props = builder.build();
    
    // Creating a File object which will point to location of
    // properties file
    File propertiesFile = new File(propertiesPath);
    
    try {
      
      // create a FileOutputStream by passing above properties file
      FileOutputStream xlsFos = new FileOutputStream(propertiesFile);
      
      // Taking hashMaps keys by first converting it to Set and than
      // taking iterator over it.
      Iterator mapIterator = properties.keySet().iterator();
      
      // looping over iterator properties
      while (mapIterator.hasNext()) {
        
        // extracting keys and values based on the keys
        String key = mapIterator.next().toString();
        
        String value = properties.get(key);
        
        // setting each properties file in props Object
        // created above
        props.setProperty(key, value);
        
      }
      
      // Finally storing the properties to real
      // properties file.
      props.store(xlsFos, null);
      
    } catch (FileNotFoundException e) {
      
      e.printStackTrace();
      
    } catch (IOException e) {
      
      e.printStackTrace();
      
    }
  }
}
