package com.sp.web.model;

/**
 * @author vikram
 * 
 *         The enumeration denoting the various types of roles in SP.
 */
public enum SPFeature {
  
  /** Spectrum feature. */
  Spectrum(RoleType.Spectrum),
  
  Pulse(RoleType.Pulse),
  
  Prism(RoleType.Prism),
  
  PrismLens(RoleType.PrismLens),
  
  RelationShipAdvisor(RoleType.RelationShipAdvisor),
  
  OrganizationPlan(RoleType.OrganizationPlan),
  
  Blueprint(RoleType.Blueprint),
  
  CompanyTheme(RoleType.CompanyTheme),
  
  EmailManagement(RoleType.EmailManagement),
  
  Competency(RoleType.Competency, RoleType.CompetencyAdmin),
  
  Erti(RoleType.Erti),
  
  SPTutorial(RoleType.Prism),
  
  Hiring(RoleType.Hiring);
  
  
  private RoleType[] roles;
  
  private SPFeature(RoleType... roles) {
    this.roles = roles;
  }
  
  public RoleType[] getRoles() {
    return roles;
  }
  
}
