/**
 * 
 */
package com.sp.web.dto.dashboard;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;

/**
 * @author pradeepruhil
 *
 */
public class GrowthDashboardDTO extends BaseUserDTO {

  private String growthRequestId;
  
  public GrowthDashboardDTO(User user) {
    load(user);
  }

  private String[] requestProgressStatus;
  
  private RequestStatus requestStatus;

  /**
   * @param requestProgressStatus
   *          the requestProgressStatus to set.
   */
  public void setRequestProgressStatus(String[] requestProgressStatus) {
    this.requestProgressStatus = requestProgressStatus;
  }

  public String[] getRequestProgressStatus() {
    return requestProgressStatus;
  }
  
  public void setGrowthRequestId(String growthRequestId) {
    this.growthRequestId = growthRequestId;
  }
  
  public String getGrowthRequestId() {
    return growthRequestId;
  }
  
  /**
   * @param requestStatus the requestStatus to set.
   */
  public void setRequestStatus(RequestStatus requestStatus) {
    this.requestStatus = requestStatus;
  }
  
  public RequestStatus getRequestStatus() {
    return requestStatus;
  }
}
