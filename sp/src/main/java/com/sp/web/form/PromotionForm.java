package com.sp.web.form;

import com.sp.web.model.Promotion;
import com.sp.web.model.PromotionType;
import com.sp.web.promotions.PromotionStatus;
import com.sp.web.utils.DateTimeUtil;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * @author Dax Abraham
 *
 *         The promotions form.
 */
public class PromotionForm {
  
  private String startDate;
  @NotBlank
  private String endDate;
  @NotBlank
  private String message;
  @NotNull
  private PromotionType promotionType;
  @NotNull
  private PromotionStatus status;
  @NotBlank
  private String code;
  private int count;
  private double unitPrice;
  private Set<String> tags;
    
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public PromotionType getPromotionType() {
    return promotionType;
  }
  
  public void setPromotionType(PromotionType promotionType) {
    this.promotionType = promotionType;
  }
  
  public PromotionStatus getStatus() {
    return status;
  }
  
  public void setStatus(PromotionStatus status) {
    this.status = status;
  }
  
  public String getCode() {
    return code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  public double getUnitPrice() {
    return unitPrice;
  }
  
  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  /**
   * Get the promotion object from the given data in the form.
   * 
   * @return
   *      the promotion object
   */
  public Promotion getPromotion() {
    Promotion promotion = new Promotion();
    BeanUtils.copyProperties(this, promotion);
    
    promotion.setStartDate((getStartDate() != null) ? DateTimeUtil.getLocalDate(getStartDate())
        : LocalDate.now());
    promotion.setEndDate(DateTimeUtil.getLocalDate(getEndDate()));
    
    return promotion;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  /**
   * Update the given promotion object.
   * 
   * @param promotion
   *          - promotion to update
   */
  public void update(Promotion promotion) {
    // updating the promotion object with the allowed data to update
    promotion.setEndDate(DateTimeUtil.getLocalDate(getEndDate()));
    promotion.setMessage(getMessage());
    promotion.setCount(getCount());
    promotion.setTags(getTags());
    promotion.setStatus(getStatus());
  }
  
}
