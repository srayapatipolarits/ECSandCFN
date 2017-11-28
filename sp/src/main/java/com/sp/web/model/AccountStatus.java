package com.sp.web.model;

/**
 * @author pradeep
 * 
 *         The various states for the account.
 */
public enum AccountStatus {
  /** Valid account. */
  VALID,

  /** Suspended Status. */
  SUSPENDED,

  /** Expired Status. */
  EXPIRED,

  /** Assisted status for premium customer with users greater than a certain limit. */
  ASSISTED,

  /** Newly created account w/o payment status updated. */
  NEW,
  
  /** When the payment status of the account has failed. */
  RENEWAL_PAYMENT_FAILED,
  
  /** A cancel request for the account has been made. */
  CANCEL,
  
  /** For trial accounts. */
  TRIAL,
  
  /** Account is blocked. */
  BLOCKED;
}
