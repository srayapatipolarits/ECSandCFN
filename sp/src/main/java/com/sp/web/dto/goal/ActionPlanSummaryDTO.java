package com.sp.web.dto.goal;

import com.sp.web.model.goal.ActionPlan;

/**
 * @author pradeepruhil
 * 
 *         The action plan summary DTO.
 */
public class ActionPlanSummaryDTO extends BaseActionPlanDTO {
  
  private int totalKpi;
  private int completedKpi;

  /**
   * Constructor for action plan summary DTO.
   * 
   * @param ap
   *          - action plan
   * @param completedKpi
   *          - completed kpi's 
   */
  public ActionPlanSummaryDTO(ActionPlan ap, int completedKpi) {
    super(ap);
    this.totalKpi = ap.getActionCount();
    this.completedKpi = completedKpi;
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
  
}
