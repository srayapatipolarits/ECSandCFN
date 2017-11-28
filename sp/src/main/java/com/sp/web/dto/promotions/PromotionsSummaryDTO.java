package com.sp.web.dto.promotions;

import com.sp.web.dto.ProductDTO;
import com.sp.web.model.Promotion;
import com.sp.web.model.PromotionType;
import com.sp.web.promotions.PromotionStatus;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the promotions summary.
 */
public class PromotionsSummaryDTO {
  
  private String id;
  private String code;
  private PromotionStatus status;
  private PromotionType promotionType;
  private List<ProductDTO> productList;
  private LocalDate endDate = null;
  private String endDateFormatted;
  private int count;
  private Set<String> tags;

  /**
   * Constructor.
   * 
   * @param promotion
   *          - promotion
   */
  public PromotionsSummaryDTO(Promotion promotion) {
    BeanUtils.copyProperties(promotion, this);
    if (this.endDate != null) {
      endDateFormatted = MessagesHelper.formatDate(this.endDate);
    }
  }

  /**
   * Constructor.
   * 
   * @param promotion
   *            - promotion
   * @param productMap
   *            - product map
   */
  public PromotionsSummaryDTO(Promotion promotion, Map<String, ProductDTO> productMap) {
    this(promotion);
    this.productList = promotion.getProductIdList().stream().map(pId -> {
        return productMap.get(pId);
      }).collect(Collectors.toList());
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getCode() {
    return code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public PromotionStatus getStatus() {
    return status;
  }
  
  public void setStatus(PromotionStatus status) {
    this.status = status;
  }
  
  public PromotionType getPromotionType() {
    return promotionType;
  }
  
  public void setPromotionType(PromotionType promotionType) {
    this.promotionType = promotionType;
  }

  public List<ProductDTO> getProductList() {
    return productList;
  }

  public void setProductList(List<ProductDTO> productList) {
    this.productList = productList;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String getEndDateFormatted() {
    return endDateFormatted;
  }

  public void setEndDateFormatted(String endDateFormatted) {
    this.endDateFormatted = endDateFormatted;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
  
}
