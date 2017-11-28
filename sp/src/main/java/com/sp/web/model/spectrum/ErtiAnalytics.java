package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.List;

/**
 * ErtiAnalytics will return the erti analytics for the company.
 * @author pradeepruhil
 *
 */
public class ErtiAnalytics implements Serializable {
  
  private static final long serialVersionUID = 4068382468801338389L;
  
  private List<ErtiUserInsights> userAnalytics;
  
  private SpectrumFilter spectrumFilter;
  
  private String companyId;
  
  public List<ErtiUserInsights> getUserAnalytics() {
    return userAnalytics;
  }
  
  public void setUserAnalytics(List<ErtiUserInsights> userAnalytics) {
    this.userAnalytics = userAnalytics;
  }
  
  public SpectrumFilter getSpectrumFilter() {
    return spectrumFilter;
  }
  
  public void setSpectrumFilter(SpectrumFilter spectrumFilter) {
    this.spectrumFilter = spectrumFilter;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
}
