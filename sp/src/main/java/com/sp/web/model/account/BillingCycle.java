package com.sp.web.model.account;

import java.io.Serializable;

/**
 * BillingCycle represent the various billing Cycle for the accounts to be billed.
 * 
 * @author pradeepruhil
 *
 */
public class BillingCycle implements Serializable {
  
  private static final long serialVersionUID = -8805286341524818813L;
  
  private BillingCycleType billingCycleType;
  
  private int noOfMonths;
  
  public BillingCycle() {
  }
  
  /**
   * Billing ccyple constructor
   * 
   * @param billingCycleType
   *          biling cycle type.
   * @param noOfMonths
   *          billing cycle periods.
   */
  public BillingCycle(BillingCycleType billingCycleType, int noOfMonths) {
    this.billingCycleType = billingCycleType;
    this.noOfMonths = noOfMonths;
  }
  
  public BillingCycleType getBillingCycleType() {
    return billingCycleType;
  }
  
  public void setBillingCycleType(BillingCycleType billingCycleType) {
    this.billingCycleType = billingCycleType;
  }
  
  public int getNoOfMonths() {
    return noOfMonths;
  }
  
  public void setNoOfMonths(int noOfMonths) {
    this.noOfMonths = noOfMonths;
  }
  
}
