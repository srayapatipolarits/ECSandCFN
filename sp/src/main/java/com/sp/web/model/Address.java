package com.sp.web.model;

import java.io.Serializable;

/**
 * @author pradeep
 * 
 *         The address entity bean.
 */
public class Address implements Serializable {

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -8731813782775990698L;

  /** country. */
  private String country;

  /** Address line 1 of user/company. */
  private String addressLine1;

  /** addressLine2. */
  private String addressLine2;

  /** city. */
  private String city;

  /** State. */
  private String state;

  /** zip code. */
  private String zipCode;

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }
}
