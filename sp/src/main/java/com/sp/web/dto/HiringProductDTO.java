package com.sp.web.dto;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Product;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the hiring product.
 */
public class HiringProductDTO {

  /** The unique identifier. */
  private String id;

  /** Product name. */
  private String name;

  /** Unit price of the product. */
  private double unitPrice;

  /** Minimum employee who can avail this products. */
  private int batchSize;

  /**
   * The DTO constructor to create copy the data from the product object.
   * 
   * @param product
   *        - the product
   */
  public HiringProductDTO(Product product) {
    if (product == null) {
      throw new InvalidRequestException("Hiring product not found !!!");
    }
    // copy the data
    BeanUtils.copyProperties(product, this);
    setBatchSize(product.getMinEmployee());
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
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
}
