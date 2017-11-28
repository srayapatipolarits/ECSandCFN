package com.sp.web.model.badge;

import com.sp.web.model.SPFeature;

/**
 * BadgeType enum specifies the different types of badges supported in the Surepeople plateform.
 * 
 * @author pradeepruhil
 *
 */
public enum BadgeType {
  
  Erti(SPFeature.Erti),
  
  OrgPlan(SPFeature.OrganizationPlan),
  
  Tutorial(SPFeature.SPTutorial);
  
  private SPFeature spFeature;
  
  /** Constructor initalizing the enumeration. */
  private BadgeType(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  /**
   * Return the spfeature for the badge type.
   * 
   * @return the sp feature.
   */
  public SPFeature getSpFeature() {
    return spFeature;
  }
}
