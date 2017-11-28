package com.sp.web.model;

/**
 * @author pradeep
 * 
 *         The enumeration denoting the various types of roles in SP.
 */
public enum RoleType {

  /** Super administrator role of the system. */
  SuperAdministrator(false,true),

  /** The super administrator of an account. */
  AccountAdministrator(true),

  /** Administrator role. */
  Administrator,

  /** Support. */
  Support,

  /** User. */
  User,

  /** Group Lead User. */
  GroupLead,

  /** The role to provide the user permissions to administer their own accounts. */
  IndividualAccountAdministrator,

  /** Access to the hiring tool. */
  Hiring(true),
  
  /** Role to access the spmatch for creation of ptp. */
  SPMatchAdmin(true),

  /** The role for the hiring candidates. */
  HiringCandidate, 

  /** The role for the hiring candidates. */
  HiringEmployee, 
  
  /** The role for the feedback user. */
  FeedbackUser,
  
  /** Role for growth External users who will be giving growth assessment feedback. */
  GrowthExternal,
  
  Spectrum(true), 
  
  Pulse(true),
  
  Demo,
  
  Prism,
  
  PrismLens,
  
  OrganizationPlan,
  
  Blueprint, RelationShipAdvisor,
  
  CompanyTheme(true),
  
  BillingAdmin(false,true),
  
  EmailManagement(true), 
  
  Competency, 
  
  CompetencyAdmin(true),
  
  Erti,
  
  /* System Administration Roles for the users */
  SysAdminMemberRole,
  SysAccount(false,true),
  SysMembers(false,true),
  SysMedia(false,true),
  SysThemes(false,true),
  SysEmails(false,true),
  SysLogs(false,true),
  SysPrism(false,true),
  SysErti(false,true),
  SysKnowledgeCenter(false,true),
  SysOrgPlans(false,true),
  SysBluePrint(false,true),
  SysCompetency(false,true),
  SysPulse(false,true),
  SysMessagePost(false,true),
  SysPermissionsManager(false,false), 
  HiringPortraitShare(false, false),
  SysPeopleAnalytics(false,true),
  RestHiringCreate(),
  PartnerCandidate();
  
  
  
  private boolean adminRole;
  
  private boolean sysAdminRole;
  
  private RoleType() {
    adminRole = false;
  }
  
  private RoleType(boolean adminRole) {
    this.adminRole = adminRole;
  }

  public boolean isAdminRole() {
    return adminRole;
  }
  
  private RoleType(boolean adminRole, boolean sysAdminRole) {
    this.adminRole = adminRole;
    this.sysAdminRole = sysAdminRole;
  }
  
  public boolean isSysAdminRole() {
    return sysAdminRole;
  }

}
