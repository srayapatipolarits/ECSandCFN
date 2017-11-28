package com.sp.web.dto.goal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sp.web.model.Company;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.CompanyActionPlanSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The DTO class for Action Plan.
 */
public class CompanyActionPlanListingDTO {
  
  private ActionPlanCompanyDTO company;
  private List<? extends BaseActionPlanDTO> actionPlanList;
  
  /**
   * Constructor.
   * 
   * @param company
   *          - the company
   * @param actionPlanList
   *          - action plan list
   */
  public CompanyActionPlanListingDTO(Company company, List<? extends BaseActionPlanDTO> actionPlanList) {
    this(company);
    this.actionPlanList = actionPlanList;
  }

  /**
   * Constructor from company.
   * 
   * @param company
   *          - company
   */
  public CompanyActionPlanListingDTO(Company company) {
    this.company = new ActionPlanCompanyDTO(company);
  }

  public List<? extends BaseActionPlanDTO> getActionPlanList() {
    return actionPlanList;
  }
  
  public void setActionPlanList(List<? extends BaseActionPlanDTO> actionPlanList) {
    this.actionPlanList = actionPlanList;
  }

  public ActionPlanCompanyDTO getCompany() {
    return company;
  }

  public void setCompany(ActionPlanCompanyDTO company) {
    this.company = company;
  }
  
  @JsonIgnore
  public String getCompanyId() {
    return company.getId();
  }

  /**
   * Add the given action plan for the company action plan listing.
   * 
   * @param ap
   *        - action plan
   * @param capSettings
   *        - company action plan settings
   */
  @SuppressWarnings("unchecked")
  public void addActionPlan(ActionPlan ap, CompanyActionPlanSettings capSettings) {
    getOrCreateActionPlanListing();
    ((List<ActionPlanListingDTO>)actionPlanList).add(new ActionPlanListingDTO(ap, capSettings));
  }
  
  /**
   * Add the given action plan listing.
   * 
   * @param actionPlanListing
   *            - action plan listing
   */
  @SuppressWarnings("unchecked")
  public void addActionPlan(ActionPlanListingDTO actionPlanListing) {
    getOrCreateActionPlanListing();
    ((List<ActionPlanListingDTO>)actionPlanList).add(actionPlanListing);
  }

  private void getOrCreateActionPlanListing() {
    if (actionPlanList == null) {
      actionPlanList = new ArrayList<ActionPlanListingDTO>();
    }
  }

}
