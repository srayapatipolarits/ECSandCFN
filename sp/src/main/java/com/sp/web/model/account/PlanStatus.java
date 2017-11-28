package com.sp.web.model.account;

/**
 * @author pradeep
 * 
 *         The various states for the account.
 */
public enum PlanStatus {
  /** Active Plan. */
  ACTIVE,
  
  /** inactive plan. **/
  INACTIVE,
  
  /** Expired Status. */
  EXPIRED,

  /** A cancel request for the plan has been made. */
  CANCEL,
  
  /** Trial plan for a compnay. */
  TRIAL, NEW, RENEWAL_PAYMENT_FAILED, BLOCKED,
}
