package com.sp.web.model.spectrum;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * SPAnalysisCategoryDTO.
 * 
 * @author pradeepruhil
 *
 */
public class SpectrumErtiAnalyticsFilter implements Serializable {
  
  private static final long serialVersionUID = 7157619816318900466L;
  
  private CategoryType categoryType;
  
  private List<TraitType> traitTypes;
  
  public CategoryType getCategoryType() {
    return categoryType;
  }
  
  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }
  
  /**
   * @param traitTypes
   *          the traitTypes to set
   */
  public void setTraitTypes(List<TraitType> traitTypes) {
    this.traitTypes = traitTypes;
  }
  
  /**
   * @return the traitTypes
   */
  public List<TraitType> getTraitTypes() {
    return traitTypes;
  }
  
  /**
   * Initialize the traits type.
   */
  public void initializeTraitsTYpe() {
    
    TraitType[] traitTypes = this.categoryType.getTraitTypes();
    this.traitTypes = Arrays.asList(traitTypes);
  }
  
  @Override
  public String toString() {
    return "SPAnalysisCategoryDTO [categoryType=" + categoryType + "]";
  }
  
}
