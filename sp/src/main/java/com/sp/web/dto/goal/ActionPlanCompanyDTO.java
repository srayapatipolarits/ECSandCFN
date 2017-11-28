package com.sp.web.dto.goal;

import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.Company;

/**
 * @author Dax Abraham
 * 
 *         This entity captures the company details required for displaying in the administration
 *         screens. This also captures the flag that indicates if the administrator has access to
 *         edit action plans or not.
 */
public class ActionPlanCompanyDTO extends BaseCompanyDTO {
  
  private boolean actionPlanAdminEnabled;
  
  public ActionPlanCompanyDTO(Company company) {
    super(company);
  }
  
  public boolean isActionPlanAdminEnabled() {
    return actionPlanAdminEnabled;
  }
  
  public void setActionPlanAdminEnabled(boolean actionPlanAdminEnabled) {
    this.actionPlanAdminEnabled = actionPlanAdminEnabled;
  }
}
