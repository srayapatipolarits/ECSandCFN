package com.sp.web.dto;

import com.sp.web.dao.CompanyDao;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the company model object.
 */
public class CompanyLogoDTO extends BaseCompanyDTO {

  private static final long serialVersionUID = 3220365859515085883L;
  private String logoURL;
  private boolean actionPlanAdminEnabled;
  
  /**
   * Constructor to create the company DTO.
   * 
   * @param company
   *          - company
   */
  public CompanyLogoDTO(CompanyDao company) {
    super(company);
  }

  public String getLogoURL() {
    return logoURL;
  }

  public void setLogoURL(String logoURL) {
    this.logoURL = logoURL;
  }

  public boolean isActionPlanAdminEnabled() {
    return actionPlanAdminEnabled;
  }

  public void setActionPlanAdminEnabled(boolean actionPlanAdminEnabled) {
    this.actionPlanAdminEnabled = actionPlanAdminEnabled;
  }
}
