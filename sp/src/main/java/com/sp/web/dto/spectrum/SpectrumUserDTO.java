package com.sp.web.dto.spectrum;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.Gender;
import com.sp.web.model.User;
import com.sp.web.model.UserType;

import java.util.List;

/**
 * SpectrumUserDTO contains the user information for the showing data on the spectrum page.
 * 
 * @author pradeepruhil
 *
 */
public class SpectrumUserDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = 1749731725974113726L;
  
  private PersonalityType primaryPersonality;
  
  private PersonalityType underPressurePersonality;
  
  private Gender gender;
  
  private int age;
  
  private List<String> groups;
  
  private List<String> tags;
  
  private UserType userType;
  
  public SpectrumUserDTO(User user) {
    super(user);
    userType = user.getType();
  }
  
  public PersonalityType getPrimaryPersonality() {
    return primaryPersonality;
  }
  
  public void setPrimaryPersonality(PersonalityType primaryPersonality) {
    this.primaryPersonality = primaryPersonality;
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
  
  public void setUnderPressurePersonality(PersonalityType underPressurePersonality) {
    this.underPressurePersonality = underPressurePersonality;
  }
  
  public PersonalityType getUnderPressurePersonality() {
    return underPressurePersonality;
  }
  
  public void setUserType(UserType userType) {
    this.userType = userType;
  }
  
  public UserType getUserType() {
    return userType;
  }
}
