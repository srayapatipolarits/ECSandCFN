package com.sp.web.assessment.questions;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         This enumeration captures the different types of assessments supported in the system.
 */
public enum AssessmentType {
  
  Default(CategoryType.Personality), 
  Motivation(CategoryType.MotivationWhy, CategoryType.MotivationHow), 
  FundamentalNeeds(CategoryType.FundamentalNeeds),
  Personality(CategoryType.Personality),
  /**
   * Please note that the Processing and Decision Making scales need to be always together as the
   * questions are mixed.
   */
  PDM(CategoryType.Processing, CategoryType.DecisionMaking), 
  LearningStyle(CategoryType.LearningStyle), 
  ConflictManagement(CategoryType.ConflictManagement), 
  All(
      CategoryType.Processing, CategoryType.ConflictManagement, CategoryType.MotivationWhy,
      CategoryType.MotivationHow, CategoryType.Personality, CategoryType.FundamentalNeeds,
      CategoryType.DecisionMaking);
  
  private List<CategoryType> categories;
  
  private AssessmentType(CategoryType... categoryTypes) {
    this.categories = Arrays.asList(categoryTypes);
  }
  
  public List<CategoryType> getCategories() {
    return categories;
  }
}
