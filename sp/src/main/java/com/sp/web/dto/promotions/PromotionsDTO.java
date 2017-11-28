package com.sp.web.dto.promotions;

import com.sp.web.dto.ProductDTO;
import com.sp.web.model.Promotion;
import com.sp.web.utils.MessagesHelper;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The promotions DTO.
 */
public class PromotionsDTO extends PromotionsSummaryDTO {

  private LocalDate startDate;
  
  private String startDateFormatted;

  /** unit price. */
  private double unitPrice;

  /** the message to be displayed for the promotion. */
  private String message;
  
  /**
   * Constructor.
   * 
   * @param promotion
   *          - the promotion
   * @param productMap
   *          - the product map 
   */
  public PromotionsDTO(Promotion promotion, Map<String, ProductDTO> productMap) {
    super(promotion, productMap);

    if (getStartDate() != null) {
      this.startDateFormatted = MessagesHelper.formatDate(startDate);
    }
  }
  
  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public String getStartDateFormatted() {
    return startDateFormatted;
  }

  public void setStartDateFormatted(String startDateFormatted) {
    this.startDateFormatted = startDateFormatted;
  }
  
}
