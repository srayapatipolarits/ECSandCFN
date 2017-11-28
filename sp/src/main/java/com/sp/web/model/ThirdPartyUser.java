package com.sp.web.model;

import com.sp.web.form.external.rest.UserForm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ThirdPartyUser implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 1303457755993824504L;
  
  private String uid;
  
  private String companyId;
  
  private String email;
  
  private String id;
  
  private String spUserId;
  
  private UserType userType;
  
  private Map<String, Object> referenceData;
  
  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getSpUserId() {
    return spUserId;
  }
  
  public void setSpUserId(String spUserId) {
    this.spUserId = spUserId;
  }
  
  public UserType getUserType() {
    return userType;
  }
  
  public void setUserType(UserType userType) {
    this.userType = userType;
  }
  
  public void setReferenceData(Map<String, Object> referenceData) {
    this.referenceData = referenceData;
  }
  
  public Map<String, Object> getReferenceData() {
    if (referenceData == null) {
      referenceData = new HashMap<String, Object>();
    }
    return referenceData;
  }
  
  public static ThirdPartyUser createUser(UserForm userForm, User hiringUser) {
    ThirdPartyUser thirdPartyUser = new ThirdPartyUser();
    thirdPartyUser.setCompanyId(hiringUser.getCompanyId());
    thirdPartyUser.setEmail(hiringUser.getEmail());
    thirdPartyUser.setSpUserId(hiringUser.getId());
    thirdPartyUser.setUserType(hiringUser.getType());
    thirdPartyUser.setUid(userForm.getUid());
    return thirdPartyUser;
  }
}
