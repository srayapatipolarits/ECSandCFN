package com.sp.web.model.account;

import java.io.Serializable;

/**
 * <code>PlanAvailableSubscription</code> contains the available subscription for the plan.
 * 
 * @author pradeepruhil
 *
 */
public class PlanAvailableSubscription implements Serializable {
  
  private static final long serialVersionUID = -2457411641187054661L;
  
  private SPPlanType planType;
  
  private long availableMemberSubscription;
  
  private long availableAdminSubscription;
  
  public SPPlanType getPlanType() {
    return planType;
  }
  
  public void setPlanType(SPPlanType planType) {
    this.planType = planType;
  }
  
  public long getAvailableMemberSubscription() {
    return availableMemberSubscription;
  }
  
  public void setAvailableMemberSubscription(long availableMemberSubscription) {
    this.availableMemberSubscription = availableMemberSubscription;
  }
  
  public long getAvailableAdminSubscription() {
    return availableAdminSubscription;
  }
  
  public void setAvailableAdminSubscription(long availableAdminSubscription) {
    this.availableAdminSubscription = availableAdminSubscription;
  }
  
}
