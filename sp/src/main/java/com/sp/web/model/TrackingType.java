/**
 * 
 */
package com.sp.web.model;

/**
 * @author pradeep
 *
 */
public enum TrackingType {
  
  ARTICLES, REQUESTS, COMPLETED,
  
  /** Growth of SP360 deactivated. */
  DEACTIVATED,
  /** Growth or SP360 Pending track. */
  PENDING,
  /** Growth Declined. */
  DECLINED;
}
