package com.sp.web.form.admin.personality;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.goal.SwotType;

import java.util.List;
import java.util.Map;

/**
 * personalitySwot form.
 * 
 * @author pradeepruhil
 *
 */
public class PersonalitySwotForm {
  
  private PersonalityType personalityType;
  
  private Map<SwotType, List<String>> swotProfileMap;

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
