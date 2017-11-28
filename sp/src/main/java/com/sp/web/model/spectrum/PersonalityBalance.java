package com.sp.web.model.spectrum;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.dto.BaseUserDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * PersonalityBalance is the personsaltiy balance.
 * 
 * @author pradeepruhil
 *
 */
public class PersonalityBalance implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -4602706577243757471L;
  
  private PersonalityType personalityType;
  
  private BigDecimal percent;
  
  private int number;
  
  private List<BaseUserDTO> usersId;
  
  private String videoUrl;
  
  private String personalityDescription;
  
  private String dimensionType;
  
  
  public PersonalityType getPersonalityType() {
    return personalityType;
  }
  
  public void setPersonalityType(PersonalityType personalityType) {
    this.personalityType = personalityType;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
  
  public void setPercent(BigDecimal percent) {
    this.percent = percent;
  }
  
  
  public int getNumber() {
    return number;
  }
  
  public BigDecimal getPercent() {
    return percent;
  }
  
  public void setUsersId(List<BaseUserDTO> usersId) {
    this.usersId = usersId;
  }
  
  public List<BaseUserDTO> getUsersId() {
    return usersId;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getPersonalityDescription() {
    return personalityDescription;
  }

  public void setPersonalityDescription(String personalityDescription) {
    this.personalityDescription = personalityDescription;
  }

  public String getDimensionType() {
    return dimensionType;
  }

  public void setDimensionType(String dimensionType) {
    this.dimensionType = dimensionType;
  }
  
  
}
