package com.sp.web.model.account;

import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.log.LogActionType;

/**
 * SPPlanType indicates different plan type available in SurePeople Plateform.
 * 
 * @author pradeepruhil
 *
 */
public enum SPPlanType {
  
  /** Intelligent hiring plan is the plan for the People analytics platform in SurePeople.. */
  IntelligentHiring(LogActionType.HiringAccount, RoleType.Hiring, SPFeature.Hiring),
  
  
  /**
   * Primary plan for the surpeople plateform It is the ERT-i. It is the default plan which will given to each
   * company.
   */
  Primary(LogActionType.PrimaryAccount, RoleType.AccountAdministrator, SPFeature.Prism, SPFeature.OrganizationPlan,
      SPFeature.Blueprint, SPFeature.PrismLens, SPFeature.RelationShipAdvisor, SPFeature.Pulse,
      SPFeature.Spectrum, SPFeature.Competency, SPFeature.Erti);
  
  private SPFeature[] features;
  
  /**
   * adminRoleForPlan specifies the admin role for the plan.
   */
  private RoleType adminRoleForPlan;
  private LogActionType logActionType;
  
  private SPPlanType(LogActionType logActionType, RoleType adminRoleType, SPFeature... features) {
    this.features = features;
    this.adminRoleForPlan = adminRoleType;
    this.logActionType = logActionType;
  }
  
  /**
   * returns the spFeatures associated with the plans.
   * 
   * @return the features
   */
  public SPFeature[] getFeatures() {
    return features;
  }
  
  /**
   * @return the adminRoleForPlan
   */
  public RoleType getAdminRoleForPlan() {
    return adminRoleForPlan;
  }

  public LogActionType getLogActionType() {
    return logActionType;
  }
  
}
