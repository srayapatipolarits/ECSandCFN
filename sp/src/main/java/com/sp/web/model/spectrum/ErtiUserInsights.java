package com.sp.web.model.spectrum;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.Gender;
import com.sp.web.model.User;
import com.sp.web.model.UserType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author pradeepruhil
 *
 */
public class ErtiUserInsights extends BaseUserDTO {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -461138615797615414L;
  
  private Gender gender;
  
  private int age;
  
  private List<String> groups;
  
  private List<String> tags;
  
  private Map<TraitType, BigDecimal> ertiData;
  
  private Map<TraitType, BigDecimal> ertiUnderPressureData;
  
  private PersonalityType primary;
  
  private PersonalityType underPresssure;
  
  private UserType userType;
  
  /**
   * COnstructor for the user.
   */
  public ErtiUserInsights(User user) {
    super(user);
    this.userType = user.getType();
  }
  
  /**
   * getErtiData map.
   * 
   * @return the map.
   */
  public Map<TraitType, BigDecimal> getErtiData() {
    if (ertiData == null) {
      ertiData = new TreeMap<>();
    }
    return ertiData;
  }
  
  public void setErtiData(Map<TraitType, BigDecimal> ertiData) {
    this.ertiData = ertiData;
  }
  
  public Gender getGender() {
    return gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public int getAge() {
    return age;
  }
  
  public void setAge(int age) {
    this.age = age;
  }
  
  public List<String> getGroups() {
    return groups;
  }
  
  public void setGroups(List<String> groups) {
    this.groups = groups;
  }
  
  public List<String> getTags() {
    return tags;
  }
  
  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  public Map<TraitType, BigDecimal> getErtiUnderPressureData() {
    if (ertiUnderPressureData == null) {
      ertiUnderPressureData = new HashMap<>();
    }
    return ertiUnderPressureData;
  }
  
  public void setErtiUnderPressureData(Map<TraitType, BigDecimal> ertiUnderPressureData) {
    this.ertiUnderPressureData = ertiUnderPressureData;
  }
  
  public PersonalityType getPrimary() {
    return primary;
  }
  
  public void setPrimary(PersonalityType primary) {
    this.primary = primary;
  }
  
  public PersonalityType getUnderPresssure() {
    return underPresssure;
  }
  
  public void setUnderPresssure(PersonalityType underPresssure) {
    this.underPresssure = underPresssure;
  }
  
  public void setUserType(UserType userType) {
    this.userType = userType;
  }
  
  public UserType getUserType() {
    return userType;
  }
  
}
