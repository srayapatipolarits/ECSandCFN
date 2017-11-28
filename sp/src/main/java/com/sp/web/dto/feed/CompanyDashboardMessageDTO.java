package com.sp.web.dto.feed;

import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.feed.DashboardMessage;

/**
 * @author Dax Abraham
 * 
 *         The DTO class that stores the dashboard messages as well as the company information.
 */
public class CompanyDashboardMessageDTO {
  
  private BaseCompanyDTO company;
  private DashboardMessageCommentsLikesDTO dashboardMessage;
  
  /**
   * Constructor.
   * 
   * @param company
   *          - company
   * @param dashboardMessage
   *          - dashboard message
   */
  public CompanyDashboardMessageDTO(CompanyDao company, DashboardMessage dashboardMessage,
      String userId) {
    this.company = new BaseCompanyDTO(company);
    this.dashboardMessage = new DashboardMessageCommentsLikesDTO(dashboardMessage, userId);
  }
  
  public BaseCompanyDTO getCompany() {
    return company;
  }
  
  public void setCompany(BaseCompanyDTO company) {
    this.company = company;
  }
  
  public DashboardMessageCommentsLikesDTO getDashboardMessage() {
    return dashboardMessage;
  }
  
  public void setDashboardMessage(DashboardMessageCommentsLikesDTO dashboardMessage) {
    this.dashboardMessage = dashboardMessage;
  }
}
