package com.sp.web.dto.hiring.match;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO to store the portrait match result details.
 */
public class HiringPortraitMatchResultDTO implements Serializable {
  
  private static final long serialVersionUID = -6648248883493796692L;
  private int matchPercent;
  private Map<CategoryType, Map<String, MatchDetailsDTO>> details;
  
  public int getMatchPercent() {
    return matchPercent;
  }
  
  public void setMatchPercent(int matchPercent) {
    this.matchPercent = matchPercent;
  }
  
  public Map<CategoryType, Map<String, MatchDetailsDTO>> getDetails() {
    return details;
  }
  
  public void setDetails(Map<CategoryType, Map<String, MatchDetailsDTO>> details) {
    this.details = details;
  }

  /**
   * Add the match results for the given category.
   * 
   * @param categoryType
   *            - category type
   * @param categoryMatchResult
   *            - the match result
   */
  public void add(CategoryType categoryType, Map<String, MatchDetailsDTO> categoryMatchResult) {
    if (details == null) {
      details = new HashMap<CategoryType, Map<String,MatchDetailsDTO>>();
    }
    details.put(categoryType, categoryMatchResult);
  }
  
}
