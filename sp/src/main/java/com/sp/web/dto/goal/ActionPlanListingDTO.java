package com.sp.web.dto.goal;

import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.CompanyActionPlanSettings;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for action plan listing.
 */
public class ActionPlanListingDTO extends BaseActionPlanDTO {
  
  private boolean editAllowed;
  private boolean allMembers;
  private boolean allCompanies;
  private boolean companyAllMembers;
  private boolean onHold;
  
  /**
   * Default constructor.
   * 
   * @param ap
   *        - action plan
   */
  public ActionPlanListingDTO(ActionPlan ap) {
    super(ap);
  }
  
  /**
   * Constructor.
   * 
   * @param ap
   *          - action plan
   * @param capSettings
   *          - company action plan settings
   */
  public ActionPlanListingDTO(ActionPlan ap, CompanyActionPlanSettings capSettings) {
    this(ap);
    if (capSettings != null) {
      this.companyAllMembers = capSettings.isAllMembers();
      this.onHold = capSettings.isOnHold();
    }
  }
  
  public boolean isEditAllowed() {
    return editAllowed;
  }
  
  public void setEditAllowed(boolean editAllowed) {
    this.editAllowed = editAllowed;
  }
  
  public boolean isAllMembers() {
    return allMembers;
  }
  
  public void setAllMembers(boolean allMembers) {
    this.allMembers = allMembers;
  }

  public boolean isAllCompanies() {
    return allCompanies;
  }

  public void setAllCompanies(boolean allCompanies) {
    this.allCompanies = allCompanies;
  }

  public boolean isCompanyAllMembers() {
    return companyAllMembers;
  }

  public void setCompanyAllMembers(boolean companyAllMembers) {
    this.companyAllMembers = companyAllMembers;
  }

  public boolean isOnHold() {
    return onHold;
  }

  public void setOnHold(boolean onHold) {
    this.onHold = onHold;
  }
  
}
