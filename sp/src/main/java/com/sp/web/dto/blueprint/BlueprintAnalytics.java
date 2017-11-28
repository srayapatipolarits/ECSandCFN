package com.sp.web.dto.blueprint;

import com.sp.web.model.spectrum.SpectrumFilter;

import java.io.Serializable;
import java.util.List;

public class BlueprintAnalytics implements Serializable {
  
  private static final long serialVersionUID = 4828178512596553675L;
  
  private List<BlueprintUserAnalytics> userAnalytics;
  
  private long totalBlueprints;
  
  private long publishedBlueprints;
  
  private long completedBlueprints;
  
  private SpectrumFilter spectrumFilter;
  
  private long totalKpi;
  
  private long completedKpi;
  
  private String companyId;
  
  public List<BlueprintUserAnalytics> getUserAnalytics() {
    return userAnalytics;
  }
  
  public void setUserAnalytics(List<BlueprintUserAnalytics> userAnalytics) {
    this.userAnalytics = userAnalytics;
  }
  
  public long getTotalBlueprints() {
    return totalBlueprints;
  }
  
  public void setTotalBlueprints(long totalBlueprints) {
    this.totalBlueprints = totalBlueprints;
  }
  
  public long getPublishedBlueprints() {
    return publishedBlueprints;
  }
  
  public void setPublishedBlueprints(long publishedBlueprints) {
    this.publishedBlueprints = publishedBlueprints;
  }
  
  public long getCompletedBlueprints() {
    return completedBlueprints;
  }
  
  public void setCompletedBlueprints(long completedBlueprints) {
    this.completedBlueprints = completedBlueprints;
  }
  
  public SpectrumFilter getSpectrumFilter() {
    return spectrumFilter;
  }
  
  public void setSpectrumFilter(SpectrumFilter spectrumFilter) {
    this.spectrumFilter = spectrumFilter;
  }
  
  public long getTotalKpi() {
    return totalKpi;
  }
  
  public void setTotalKpi(long totalKpi) {
    this.totalKpi = totalKpi;
  }
  
  public long getCompletedKpi() {
    return completedKpi;
  }
  
  public void setCompletedKpi(long completedKpi) {
    this.completedKpi = completedKpi;
  }
  
  /**
   * add to the completed kpi.
   * 
   * @param size
   *          of completed kpi.
   */
  public void addCompletedKpi(int size) {
    completedKpi = completedKpi + size;
  }
  
  public void addTotalKpi(int totalKpiUser) {
    totalKpi = totalKpi + totalKpiUser;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  /**
   * 
   */
  public void addPublishedBlueprint() {
    publishedBlueprints = publishedBlueprints + 1;
    
  }
  
  /**
   * increase the total blueprint count.
   */
  public void addTotalBlueprints() {
    totalBlueprints = totalBlueprints + 1;
    
  }
  
  /**
   * Increase the blueprint completed count.
   */
  public void addCompletdBlueprint() {
    completedBlueprints = completedBlueprints + 1;
  }
  
}
