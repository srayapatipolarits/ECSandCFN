package com.sp.web.model;

import com.sp.web.Constants;
import com.sp.web.utils.MessagesHelper;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

/**
 * SurePeople products beans holding information of the products.
 * 
 * @author pradeep
 *
 */
public class Product implements Serializable {

  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = -7550962370388112820L;

  /** The unique identifier. */
  private String id;

  /** Product name. */
  private String name;

  /** Unit price of the product. */
  private double unitPrice;

  /** product type. */
  private ProductType productType;

  /** Minimum employee who can avail this products. */
  private int minEmployee;

  /** PRoduct title key. */
  private String titleKey;

  /** Product validity key message label. */
  private String validityKey;

  /** Product unit key message label. */
  private String unitPriceKey;

  /** Product status. */
  private ProductStatus status;

  /** PRoduct validity type. */
  private ProductValidityType validityType;
  
  /** min number of hiring assessments. */
  private int minHiringEmployee;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public ProductType getProductType() {
    return productType;
  }

  public void setProductType(ProductType productType) {
    this.productType = productType;
  }

  public int getMinEmployee() {
    return minEmployee;
  }

  public void setMinEmployee(int minEmployee) {
    this.minEmployee = minEmployee;
  }

  public String getTitleKey() {
    return titleKey;
  }

  public void setTitleKey(String titleKey) {
    this.titleKey = titleKey;
  }

  /**
   * The validity of the product.
   * 
   * @return 
   *    the validityKey
   */
  public String getValidityKey() {

    validityType = Optional.ofNullable(validityType).orElse(ProductValidityType.YEARLY);
    LocalDate date = LocalDate.now();
    switch (validityType) {
    case MONTHLY:
    case YEARLY:
    default:
      date = date.plusDays(12 * Constants.DAYS_OF_MONTHLY_BILLING);
    }
    /* check if prodct is monthly or annual */
    validityKey = validityKey.replace("{0}", MessagesHelper.formatDate(date));
    return validityKey;
  }

  public void setValidityKey(String validityKey) {
    this.validityKey = validityKey;
  }

  public String getUnitPriceKey() {
    return unitPriceKey;
  }

  public void setUnitPriceKey(String unitKey) {
    this.unitPriceKey = unitKey;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + id + ":" + name + ":" + unitPrice + "]";
  }

  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(ProductStatus status) {
    this.status = status;
  }

  public ProductValidityType getValidityType() {
    return validityType;
  }

  public void setValidityType(ProductValidityType validityType) {
    this.validityType = validityType;
  }

  public boolean isActive() {
    return (status != null) ? (status == ProductStatus.Active) : false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Product)) {
      return false;
    }
    return id.equals(((Product) obj).getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public int getMinHiringEmployee() {
    return minHiringEmployee;
  }

  public void setMinHiringEmployee(int minHiringEmployee) {
    this.minHiringEmployee = minHiringEmployee;
  }

}
