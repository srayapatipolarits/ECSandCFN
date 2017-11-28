package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Age baalnce in the organization model.
 * 
 * @author pradeepruhil
 *
 */
public class AgeBalance implements Serializable {
  
  private static final long serialVersionUID = 3750589767553636277L;
  
  private AgeCategory ageCategory;
  
  private BigDecimal percent;
  
  public AgeCategory getAgeCategory() {
    return ageCategory;
  }
  
  public void setAgeCategory(AgeCategory ageCategory) {
    this.ageCategory = ageCategory;
  }
  
  public BigDecimal getPercent() {
    return percent;
  }
  
  public void setPercent(BigDecimal percent) {
    this.percent = percent;
  }

  @Override
  public String toString() {
    return "AgeBalance [ageCategory=" + ageCategory + ", percent=" + percent + "]";
  }
  
  
}
