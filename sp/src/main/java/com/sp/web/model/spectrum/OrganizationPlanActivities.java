package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * OrganizationPlanActivities contains the organization plan activies.
 * 
 * @author pradeepruhil
 *
 */
public class OrganizationPlanActivities implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 8426393500350630127L;
  
  private String companyId;
  
  private SpectrumFilter spectrumFilter;
  
  private int totalKpi;
  
  private int completedKpi;
  
  private List<OrgPlanActivity> orgPlanActivities;
  
  private int totalOrgPlan;
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public SpectrumFilter getSpectrumFilter() {
    return spectrumFilter;
  }
  
  public void setSpectrumFilter(SpectrumFilter spectrumFilter) {
    this.spectrumFilter = spectrumFilter;
  }
  
  public int getTotalKpi() {
    return totalKpi;
  }
  
  public void setTotalKpi(int totalKpi) {
    this.totalKpi = totalKpi;
  }
  
  public int getCompletedKpi() {
    return completedKpi;
  }
  
  public void setCompletedKpi(int completedKpi) {
    this.completedKpi = completedKpi;
  }
  
  public List<OrgPlanActivity> getOrgPlanActivities() {
    if (this.orgPlanActivities == null) {
      this.orgPlanActivities = new ArrayList<OrgPlanActivity>();
    }
    return orgPlanActivities;
  }
  
  public void setOrgPlanActivities(List<OrgPlanActivity> orgPlanActivities) {
    this.orgPlanActivities = orgPlanActivities;
  }
  
  public int getTotalOrgPlan() {
    return totalOrgPlan;
  }
  
  public void setTotalOrgPlan(int totalOrgPlan) {
    this.totalOrgPlan = totalOrgPlan;
  }
  
}
