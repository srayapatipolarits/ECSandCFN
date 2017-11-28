package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ProfileBalance model for holding the profile balance.
 * 
 * @author pradeepruhil
 *
 */
public class HiringFilterProfileBalance implements Serializable {
  
  /**
   * defualt serial version id.
   */
  private static final long serialVersionUID = 3559847901095717128L;
  
  private List<PersonalityBalance> personalityBalances;
  
  private String id;
  
  private String companyId;
  
  private boolean personalityBalanceAllZero;
  
  public List<PersonalityBalance> getPersonalityBalances() {
    if (personalityBalances == null) {
      personalityBalances = new ArrayList<PersonalityBalance>();
    }
    return personalityBalances;
  }
  
  public void setPersonalityBalances(List<PersonalityBalance> personalityBalances) {
    this.personalityBalances = personalityBalances;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
  @Override
  public String toString() {
    return "HiringFilterProfileBalance [personalityBalances=" + personalityBalances + ", id=" + id
        + ", companyId=" + companyId + "]";
  }

  public void setPersonalityBalanceAllZero(boolean personalityBalanceAllZero) {
    this.personalityBalanceAllZero = personalityBalanceAllZero;
  }
  
  public boolean isPersonalityBalanceAllZero() {
    return personalityBalanceAllZero;
  }
}
