package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GenderBalance for the user.
 * 
 * @author pradeepruhil
 *
 */
public class GenderBalance implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -6189204575596294422L;
  
  private BigDecimal male;
  
  private BigDecimal female;
  
  /**
   * Contructor for the gender balance.
   * 
   * @param maleCount
   *          total number of male.
   * @param femaleCount
   *          total number of female.
   */
  public GenderBalance(BigDecimal maleCount, BigDecimal femaleCount) {
    this.male = maleCount;
    this.female = femaleCount;
  }
  
  public GenderBalance() {

  }
  
  public BigDecimal getMale() {
    return male;
  }
  
  public void setMale(BigDecimal male) {
    this.male = male;
  }
  
  public BigDecimal getFemale() {
    return female;
  }
  
  public void setFemale(BigDecimal female) {
    this.female = female;
  }

  @Override
  public String toString() {
    return "GenderBalance [male=" + male + ", female=" + female + "]";
  }

  
}
