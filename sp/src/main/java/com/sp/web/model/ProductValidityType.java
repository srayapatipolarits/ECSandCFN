package com.sp.web.model;

/**
 * ProductValidity Type defines the duration the product is valid for when a user register for a product.
 * 
 * @author pradeep
 */
public enum ProductValidityType {

  /** PRoduct is valid for 1 month and will expires after 1 month of subscription.d */
  MONTHLY,

  /** Product will expires every year from day of subscription. */
  YEARLY;
}
