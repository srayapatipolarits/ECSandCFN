package com.sp.web.dto.promotions;

import com.sp.web.model.Promotion;
import com.sp.web.promotions.PromotionStatus;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         This is the DTO bean for sharing the promotions information to front end system.
 */
public class PromotionRegistrationDTO {

  private String id;
  
  /** Promotion name. */
  private String name;

  /** unit price. */
  private double unitPrice;

  /** the status of the promotion. */
  private PromotionStatus status;

  /** the message to be displayed for the promotion. */
  private String message;

  /**
   * The constructor that copies the given bean to this bean.
   */
  public PromotionRegistrationDTO(Promotion promotion) {
    BeanUtils.copyProperties(promotion, this);
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

  public PromotionStatus getStatus() {
    return status;
  }

  public void setStatus(PromotionStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}