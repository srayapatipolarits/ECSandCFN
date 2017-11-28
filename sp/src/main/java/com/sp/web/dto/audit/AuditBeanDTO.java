package com.sp.web.dto.audit;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.AccountType;
import com.sp.web.model.User;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.spectrum.TimeFilter;

import java.util.Set;

/**
 * @author pradeepruhil
 *
 */
public class AuditBeanDTO extends BaseUserDTO {
  
  private Set<ServiceType> serviceType;
  
  private AccountType accountType;
  
  private ActivityType activityType;
  
  private boolean isOnline;
  
  private boolean isOffline;
  
  private String companyName;
  
  private Set<TimeFilter> timeFilters;
  
  
  /**
   * 
   */
  public AuditBeanDTO() {
  }
  
  public AuditBeanDTO(User user) {
    super(user);
  }
  
  /**
   * @param serviceType the serviceType to set
   */
  public void setServiceType(Set<ServiceType> serviceType) {
    this.serviceType = serviceType;
  }
  
  /**
   * @return the serviceType
   */
  public Set<ServiceType> getServiceType() {
    return serviceType;
  }
  
  public AccountType getAccountType() {
    return accountType;
  }
  
  public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
  }
  
  public ActivityType getActivityType() {
    return activityType;
  }
  
  public void setActivityType(ActivityType activityType) {
    this.activityType = activityType;
  }
  
  public boolean isOnline() {
    return isOnline;
  }
  
  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }
  
  public String getCompanyName() {
    return companyName;
  }
  
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
  
  public enum ActivityType {
    Low, Medium, High;
  }
  
  /**
   * @param timeFilters the timeFilters to set
   */
  public void setTimeFilters(Set<TimeFilter> timeFilters) {
    this.timeFilters = timeFilters;
  }
  
  /**
   * @return the timeFilters
   */
  public Set<TimeFilter> getTimeFilters() {
    return timeFilters;
  }
  
  /**
   * @param isOffline the isOffline to set
   */
  public void setOffline(boolean isOffline) {
    this.isOffline = !isOnline;
  }
  
  /**
   * @return the isOffline
   */
  public boolean isOffline() {
    return !isOnline;
  }
}
