package com.sp.web.model.spectrum;

import java.util.Map;

/**
 * HiringFilterInsigthts contains the model for the hiring insights.
 * 
 * @author pradeepruhil
 *
 */
public class HiringFilterInsights {
  
  private String companyId;
  
  private String id;
  
  private Map<TimeFilter, HiringInsights> hiringInsightsMap;
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setHiringInsightsMap(Map<TimeFilter, HiringInsights> hiringInsightsMap) {
    this.hiringInsightsMap = hiringInsightsMap;
  }
  
  public Map<TimeFilter, HiringInsights> getHiringInsightsMap() {
    return hiringInsightsMap;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}
