/**
 * 
 */
package com.sp.web.dto.audit;

import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.spectrum.TimeFilter;

import java.util.Set;

/**
 * @author pradeepruhil
 *
 */
public class AuditDetailDTO {
  
  private String activityDetails;
  
  private ServiceType serviceType;
  
  private String createdOn;
  
  private Set<TimeFilter> timeFilter;
  
  public String getActivityDetails() {
    return activityDetails;
  }
  
  public void setActivityDetails(String activityDetails) {
    this.activityDetails = activityDetails;
  }
  
  public ServiceType getServiceType() {
    return serviceType;
  }
  
  public void setServiceType(ServiceType serviceType) {
    this.serviceType = serviceType;
  }
  
  public String getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }
  
  /**
   * @param timeFilter
   *          the timeFilter to set
   */
  public void setTimeFilter(Set<TimeFilter> timeFilter) {
    this.timeFilter = timeFilter;
  }
  
  /**
   * @return the timeFilter
   */
  public Set<TimeFilter> getTimeFilter() {
    return timeFilter;
  }
}
