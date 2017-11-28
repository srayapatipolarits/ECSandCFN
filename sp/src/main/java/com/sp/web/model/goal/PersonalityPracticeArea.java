package com.sp.web.model.goal;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.service.translation.Translable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>PersonalityPracticeArea</code> contains mapping for personality and goal associated with it
 * in the order of weight.
 * Goal present first in the list is having highest weight.
 * 
 * @author pradeepruhil
 *
 */
public class PersonalityPracticeArea implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 7946602766514452648L;
  
  private String id;
  
  private PersonalityType personalityType;
  
  private List<String> goalIds;
  
  @Translable
  private Map<SwotType, List<String>> swotProfileMap;
  
  public PersonalityType getPersonalityType() {
    return personalityType;
  }
  
  public void setPersonalityType(PersonalityType personalityType) {
    this.personalityType = personalityType;
  }
  
  public List<String> getGoalIds() {
    return goalIds;
  }
  
  public void setGoalIds(List<String> goalIds) {
    this.goalIds = goalIds;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Returns the swot profile.
   * 
   * @return the swot profile.
   */
  public Map<SwotType, List<String>> getSwotProfileMap() {
    if (swotProfileMap == null) {
      swotProfileMap = new HashMap<SwotType, List<String>>();
    }
    return swotProfileMap;
  }
  
  public void setSwotProfileMap(Map<SwotType, List<String>> swotProfileMap) {
    this.swotProfileMap = swotProfileMap;
  }
}
