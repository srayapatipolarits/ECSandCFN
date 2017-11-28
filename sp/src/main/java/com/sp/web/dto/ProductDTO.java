package com.sp.web.dto;

import com.sp.web.model.Product;
import com.sp.web.model.ProductType;
import com.sp.web.model.ProductValidityType;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to share the product information.
 */
public class ProductDTO {
  
  /** The unique identifier. */
  private String id;

  /** Product name. */
  private String name;

  /** Unit price of the product. */
  private double unitPrice;

  /** product type. */
  private ProductType productType;

  /** PRoduct validity type. */
  private ProductValidityType validityType;
  
  /** Minimum employee who can avail this products. */
  private int minEmployee;
  
  /**
   * Constructor.
   * 
   * @param product
   *          - product to copy from
   */
  public ProductDTO(Product product) {
    BeanUtils.copyProperties(product, this);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public ProductValidityType getValidityType() {
    return validityType;
  }

  public void setValidityType(ProductValidityType validityType) {
    this.validityType = validityType;
  }

  public int getMinEmployee() {
    return minEmployee;
  }

  public void setMinEmployee(int minEmployee) {
    this.minEmployee = minEmployee;
  }

}
