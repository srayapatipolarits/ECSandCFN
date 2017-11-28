/**
 * 
 */
package com.sp.web.model;

/**
 * @author pradeepruhil
 *
 */
public enum SPRoleGroups {
  
  Admin(RoleType.SysAccount, RoleType.SysMembers, RoleType.SysMedia, RoleType.SysThemes, 
      RoleType.SysEmails, RoleType.SysLogs),
  
  Prism(RoleType.SysPrism),
  
  Learning(RoleType.SysErti, RoleType.SysOrgPlans, RoleType.SysKnowledgeCenter, RoleType.SysMessagePost),
  
  PeopleAnalytics(RoleType.SysPeopleAnalytics),
  
  Performance(RoleType.SysCompetency, RoleType.SysBluePrint, RoleType.SysPulse),
  
  Billing(RoleType.BillingAdmin);
  
  private RoleType[] roles;
  
  private SPRoleGroups(RoleType... roleTypes) {
    this.roles = roleTypes;
    
  }
  
  /**
   * @return the roles
   */
  public RoleType[] getRoles() {
    return roles;
  }
  
}
