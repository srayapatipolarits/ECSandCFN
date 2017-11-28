package com.sp.web.dto.competency;

import com.sp.web.model.competency.CompetencyProfile;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the basic information on competency profiles.
 */
public class BaseCompetencyProfileDTO {
  
  private String id;
  private String name;
  
  /**
   * Constructor from competency profile.
   * 
   * @param competencyProfile
   *            - competency profile to copy from 
   */
  public BaseCompetencyProfileDTO(CompetencyProfile competencyProfile) {
    BeanUtils.copyProperties(competencyProfile, this, "competencyList");
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
