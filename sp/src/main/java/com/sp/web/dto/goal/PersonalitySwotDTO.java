package com.sp.web.dto.goal;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SwotType;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * PersonalitySwotDTO contains the swot data for the the personality.
 * 
 * @author pradeepruhil
 *
 */
public class PersonalitySwotDTO {
  private String id;
  
  private PersonalityType personalityType;
  
  private Map<SwotType, List<String>> swotProfileMap;

  
  /**
   * Constructor for the personality.
   */
  public PersonalitySwotDTO(PersonalityPracticeArea practiceArea) {
    BeanUtils.copyProperties(practiceArea, this);
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PersonalityType getPersonalityType() {
    return personalityType;
  }

  public void setPersonalityType(PersonalityType personalityType) {
    this.personalityType = personalityType;
  }

  public Map<SwotType, List<String>> getSwotProfileMap() {
    return swotProfileMap;
  }

  public void setSwotProfileMap(Map<SwotType, List<String>> swotProfileMap) {
    this.swotProfileMap = swotProfileMap;
  }
  
}
