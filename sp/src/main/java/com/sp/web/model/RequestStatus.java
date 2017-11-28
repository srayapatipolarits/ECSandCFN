package com.sp.web.model;

/**
 * RequestStatus of the Growth request whether feedback is given by the member or not.
 * 
 * @author pradeep
 *
 */
public enum RequestStatus {

  /** Growth request is completed. */
  ACTIVE,

  /** Growth request is pending. */
  DEACTIVE,

  COMPLETED,

  NOT_INITIATED,

  /** Expired status, when the request has passed the date */
  EXPIRED,

  /** Deleted when the request is deleted by the requestor */
  DELETED,

  /* When growht request is declined by the user */
  DECLINED;

}
