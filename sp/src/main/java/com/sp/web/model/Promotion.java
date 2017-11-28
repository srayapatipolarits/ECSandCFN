package com.sp.web.model;

import com.sp.web.promotions.PromotionStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 *
 *         The model class for promotions.
 */
public class Promotion implements Serializable {

  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 8316616927146276609L;

  private String id;

  /** Promotion Code. */
  private String code;

  /** Promotion name. */
  private String name;

  /** Start date. */
  private LocalDate startDate;

  /** End date. */
  private LocalDate endDate;

  /** Type of promotion. */
  private PromotionType promotionType;

  /** PRoducts to which this promotion to be applied. */
  private List<String> productIdList;

  /** unit price. */
  private double unitPrice;

  /** the status of the promotion. */
  private PromotionStatus status;

  /** the message to be displayed for the promotion. */
  private String message;
  
  private int count;
  
  private Set<String> tags;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PromotionType getPromotionType() {
    return promotionType;
  }

  public void setPromotionType(PromotionType promotionType) {
    this.promotionType = promotionType;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getProductIdList() {
    return productIdList;
  }

  public void setProductIdList(List<String> productIdList) {
    this.productIdList = productIdList;
  }

  public PromotionStatus getStatus() {
    return status;
  }

  public void setStatus(PromotionStatus status) {
    this.status = status;
  }

  /**
   * @return - true if the promotion is valid.
   */
  public boolean isValid() {
    return (status != null) ? (status == PromotionStatus.Active) : false;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
}
