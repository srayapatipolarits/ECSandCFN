package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.Map;

/**
 * Learner base model class.
 * 
 * @author pradeepruhil
 *
 */
public class LearnerStatus implements Serializable {
  
  private String id;
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -5757090160315281794L;
  
  private String companyId;
  
  private Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap;
  
  private boolean prismLensActive;
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public Map<TimeFilter, LearnerStausTracker> getLearnerStatusTrackerMap() {
    return learnerStatusTrackerMap;
  }
  
  public void setLearnerStatusTrackerMap(
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    this.learnerStatusTrackerMap = learnerStatusTrackerMap;
  }
  
  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setPrismLensActive(boolean prismLensActive) {
    this.prismLensActive = prismLensActive;
  }
  
  public boolean isPrismLensActive() {
    return prismLensActive;
  }
}
