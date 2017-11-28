package com.sp.web.model.account;

/**
 * BillingCycleType contains the different billing periods for accounts.
 * 
 * @author pradeepruhil
 *
 */
public enum BillingCycleType {
  
  Monthly(1),
  
  Quaterly(3),
  
  SemiAnnually(6),
  
  Anually(12),
  
  Custom(0);
  
  int months;
  
  /**
   * Constructors.
   */
  private BillingCycleType(int months) {
    this.months = months;
  }
  
  public int getMonths() {
    return months;
  }
}
