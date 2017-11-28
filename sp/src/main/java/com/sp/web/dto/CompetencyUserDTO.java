package com.sp.web.dto;

import com.sp.web.model.User;

/**
 * @author Dax Abraham
 * 
 *         The User DTO with user competency profiles.
 */
public class CompetencyUserDTO {
  private String userId;
  private String competencyProfileId;
  
  /**
   * Constructor from User.
   * 
   * @param member
   *          - member
   * @param competencyProfileMap 
   *          - competency profile map
   */
  public CompetencyUserDTO(User member) {
    this.userId = member.getId();
    this.competencyProfileId = member.getCompetencyProfileId();
  }

  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getCompetencyProfileId() {
    return competencyProfileId;
  }

  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
}
