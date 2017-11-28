package com.sp.web.dto.goal;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SwotType;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The personality practice area mapping DTO.
 */
public class PersonalityPracticeAreaDTO {

  private String id;
  
  private PersonalityType personalityType;
  private PersonalityTypeMapper mappedPersonality;
  private List<BaseGoalDto> goals;
  private Map<SwotType, List<String>> swotProfileMap;

  /**
   * Constructor.
   * 
   * @param ppa
   *          - personality practice area
   * @param goalFactory
   *          - goal factory reference
   */
  public PersonalityPracticeAreaDTO(PersonalityPracticeArea ppa, SPGoalFactory goalFactory) {
    BeanUtils.copyProperties(ppa, this);
    this.mappedPersonality = PersonalityTypeMapper.getPersonalityTypeMapper(ppa.getPersonalityType());
    final List<String> goalIds = ppa.getGoalIds();
    if (goalIds != null) {
      goals = goalIds.stream().map(goalFactory::getGoal).map(BaseGoalDto::new)
          .collect(Collectors.toList());
    }
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

  public List<BaseGoalDto> getGoals() {
    return goals;
  }

  public void setGoals(List<BaseGoalDto> goals) {
    this.goals = goals;
  }

  public Map<SwotType, List<String>> getSwotProfileMap() {
    return swotProfileMap;
  }

  public void setSwotProfileMap(Map<SwotType, List<String>> swotProfileMap) {
    this.swotProfileMap = swotProfileMap;
  }

  public PersonalityTypeMapper getMappedPersonality() {
    return mappedPersonality;
  }

  public void setMappedPersonality(PersonalityTypeMapper mappedPersonality) {
    this.mappedPersonality = mappedPersonality;
  }
  
}
