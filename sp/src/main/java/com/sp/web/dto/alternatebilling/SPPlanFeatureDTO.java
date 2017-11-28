package com.sp.web.dto.alternatebilling;

import com.sp.web.model.SPFeature;
import com.sp.web.model.account.SPPlanType;

/**
 * SPPlanFeature DTO to be used during signup of new accounts.
 * 
 * @author pradeepruhil
 *
 */
public class SPPlanFeatureDTO {
  
  /**
   * Constructor.
   */
  public SPPlanFeatureDTO(SPPlanType planType) {
    this.spPlanType = planType;
    this.spFeatures = planType.getFeatures();
  }
  
  private SPPlanType spPlanType;
  
  private SPFeature[] spFeatures;
  
  public SPPlanType getSpPlanType() {
    return spPlanType;
  }
  
  public void setSpPlanType(SPPlanType spPlanType) {
    this.spPlanType = spPlanType;
  }
  
  public SPFeature[] getSpFeatures() {
    return spFeatures;
  }
  
  public void setSpFeatures(SPFeature[] spFeatures) {
    this.spFeatures = spFeatures;
  }
  
}
