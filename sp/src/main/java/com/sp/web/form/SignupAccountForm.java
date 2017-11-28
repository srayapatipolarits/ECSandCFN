package com.sp.web.form;

import java.util.List;

/**
 * @author daxabraham
 * 
 *         This is the form bean for getting the values for the account during the signup process.
 */
public class SignupAccountForm {
  
  private boolean enhancedPasswordSecurity;
  
  private List<SPPlanForm> planForms;
  
  private String accId;
  
  public void setPlanForms(List<SPPlanForm> planForms) {
    this.planForms = planForms;
  }
  
  public List<SPPlanForm> getPlanForms() {
    return planForms;
  }
  
  public void setEnhancedPasswordSecurity(boolean enhancedPasswordSecurity) {
    this.enhancedPasswordSecurity = enhancedPasswordSecurity;
  }
  
  public boolean isEnhancedPasswordSecurity() {
    return enhancedPasswordSecurity;
  }
  
  @Override
  public String toString() {
    return "SignupAccountForm [enhancedPasswordSecurity=" + enhancedPasswordSecurity
        + ", planForms=" + planForms + "]";
  }
  
  public void setAccId(String accId) {
    this.accId = accId;
  }
  
  public String getAccId() {
    return accId;
  }
}
