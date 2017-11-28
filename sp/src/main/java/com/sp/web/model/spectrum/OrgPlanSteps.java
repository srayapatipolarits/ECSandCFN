package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>OrgPlanSteps</code> clas contains the org plan steps spectrum information for the completed
 * and things for the user.
 * 
 * @author pradeepruhil
 *
 */
public class OrgPlanSteps implements Serializable {
  
  private static final long serialVersionUID = 6187299226818881798L;

  private String stepName;
  
  private List<String> uids;
  
  private BigDecimal completionPercent;
  
  public String getStepName() {
    return stepName;
  }
  
  public void setStepName(String stepName) {
    this.stepName = stepName;
  }
  
  public void setUids(List<String> uids) {
    this.uids = uids;
  }
  
  public List<String> getUids() {
    if (uids == null) {
      uids = new ArrayList<String>();
    }
    return uids;
  }
  
  public BigDecimal getCompletionPercent() {
    return completionPercent;
  }
  
  public void setCompletionPercent(BigDecimal completionPercent) {
    this.completionPercent = completionPercent;
  }
  
}
