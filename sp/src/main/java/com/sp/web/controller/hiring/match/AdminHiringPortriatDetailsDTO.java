package com.sp.web.controller.hiring.match;

import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.hiring.match.AdminHiringPortraitBaseDTO;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import java.math.BigDecimal;
import java.util.Map;

public class AdminHiringPortriatDetailsDTO extends AdminHiringPortraitBaseDTO {

  private static final long serialVersionUID = 8300764948024958030L;
  private Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap;
  private Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap;
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          - hiring portriat
   */
  public AdminHiringPortriatDetailsDTO(HiringPortrait portrait) {
    super(portrait);
  }

  public Map<CategoryType, Map<String, PortraitDataMatch>> getCategoryDataMap() {
    return categoryDataMap;
  }

  public void setCategoryDataMap(Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap) {
    this.categoryDataMap = categoryDataMap;
  }

  public Map<CategoryPriority, BigDecimal> getCategoryPriorityWeightMap() {
    return categoryPriorityWeightMap;
  }

  public void setCategoryPriorityWeightMap(Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap) {
    this.categoryPriorityWeightMap = categoryPriorityWeightMap;
  }
  
}
