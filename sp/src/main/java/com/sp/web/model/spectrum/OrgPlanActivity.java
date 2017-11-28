package com.sp.web.model.spectrum;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Org Plan activity holds information for a Org plan.
 * 
 * @author pradeepruhil
 *
 */
public class OrgPlanActivity implements Serializable {
  
  /**
   * serial version id.
   */
  private static final long serialVersionUID = -8587403965456363121L;
  
  private Set<OrgPlanUserActivity> userActivities;
  
  private int totalKpi;
  
  private List<OrgPlanGroupActivity> orgPlanGroupActivities;
  
  private boolean group;
  
  private String planId;
  
  private String planName;
  
  private boolean onHold;
  
  private BigDecimal completedKpiPercent;
  
  @JsonIgnore
  private int completdKpi;
  
  private List<OrgPlanSteps> orgPlanSteps;
  
  public Set<OrgPlanUserActivity> getUserActivities() {
    if (userActivities == null) {
      userActivities = new HashSet<OrgPlanUserActivity>();
    }
    return userActivities;
  }
  
  public void setUserActivities(Set<OrgPlanUserActivity> userActivities) {
    this.userActivities = userActivities;
  }
  
  public int getTotalKpi() {
    return totalKpi;
  }
  
  public void setTotalKpi(int totalKpi) {
    this.totalKpi = totalKpi;
  }
  
  public List<OrgPlanGroupActivity> getOrgPlanGroupActivities() {
    if (orgPlanGroupActivities == null) {
      orgPlanGroupActivities = new ArrayList<>();
    }
    return orgPlanGroupActivities;
  }
  
  public void setOrgPlanGroupActivities(List<OrgPlanGroupActivity> orgPlanGroupActivities) {
    this.orgPlanGroupActivities = orgPlanGroupActivities;
  }
  
  public void setGroup(boolean group) {
    this.group = group;
  }
  
  public boolean isGroup() {
    return group;
  }
  
  public String getPlanId() {
    return planId;
  }
  
  public void setPlanId(String planId) {
    this.planId = planId;
  }
  
  public String getPlanName() {
    return planName;
  }
  
  public void setPlanName(String planName) {
    this.planName = planName;
  }
  
  /**
   * add the total Kpi to the total members.
   * 
   * @param size
   *          of the person.
   */
  public void addTotalKpi(int size) {
    this.totalKpi = totalKpi + size;
    
  }
  
  public void setCompletedKpiPercent(BigDecimal completedKpiPercent) {
    this.completedKpiPercent = completedKpiPercent;
  }
  
  public BigDecimal getCompletedKpiPercent() {
    return completedKpiPercent;
  }
  
  @Override
  public String toString() {
    return "OrgPlanActivity [userActivities=" + userActivities + ", totalKpi=" + totalKpi
    
    + ", orgPlanGroupActivities=" + orgPlanGroupActivities + ", group=" + group + ", planId="
        + planId + ", planName=" + planName + "]";
  }
  
  public void addCompletdKpi(int size) {
    this.completdKpi = completdKpi + size;
  }
  
  public int getCompletdKpi() {
    return completdKpi;
  }
  
  public void setOrgPlanSteps(List<OrgPlanSteps> orgPlanSteps) {
    this.orgPlanSteps = orgPlanSteps;
  }
  
  /**
   * return the list of orgplan steps in the org plan.
   * 
   * @return orgplan steps list.
   */
  public List<OrgPlanSteps> getOrgPlanSteps() {
    if (orgPlanSteps == null) {
      orgPlanSteps = new ArrayList<>();
    }
    return orgPlanSteps;
  }
  
  public void setOnHold(boolean onHold) {
    this.onHold = onHold;
  }
  
  public boolean isOnHold() {
    return onHold;
  }
  
  
}
