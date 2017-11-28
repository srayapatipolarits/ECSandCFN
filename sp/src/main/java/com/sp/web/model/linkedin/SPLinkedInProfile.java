package com.sp.web.model.linkedin;

import org.springframework.social.linkedin.api.LinkedInProfileFull;

import java.io.Serializable;

public class SPLinkedInProfile implements Serializable {
  
  private static final long serialVersionUID = -7173808273399597438L;
  
  private LinkedInOtherProfile otherProfileData;
  
  private LinkedInProfileFull linkedProfile;
  
  private String profileStatement;
  
  public void setLinkedProfile(LinkedInProfileFull linkedProfile) {
    this.linkedProfile = linkedProfile;
  }
  
  public LinkedInProfileFull getLinkedProfile() {
    return linkedProfile;
  }
  
  public void setOtherProfileData(LinkedInOtherProfile otherProfileData) {
    this.otherProfileData = otherProfileData;
  }
  
  public LinkedInOtherProfile getOtherProfileData() {
    return otherProfileData;
  }
  
  public void setProfileStatement(String profileStatement) {
    this.profileStatement = profileStatement;
  }
  
  public String getProfileStatement() {
    return profileStatement;
  }
  
}
