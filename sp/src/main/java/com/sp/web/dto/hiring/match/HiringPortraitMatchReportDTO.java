package com.sp.web.dto.hiring.match;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.model.hiring.match.MatchCriteria;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO for the portrait match report information.
 */
public class HiringPortraitMatchReportDTO extends HiringPortraitBaseDTO {
  
  private static final long serialVersionUID = 9075607572473653539L;
  private int traitsCount;
  private Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData;
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          - portrait dao
   */
  public HiringPortraitMatchReportDTO(HiringPortraitDao portrait) {
    super(portrait);
  }
  
  public int getTraitsCount() {
    return traitsCount;
  }
  
  public void setTraitsCount(int traitsCount) {
    this.traitsCount = traitsCount;
  }
  
  public Map<CategoryType, Map<String, List<MatchCriteria>>> getPortraitMatchReportData() {
    return portraitMatchReportData;
  }
  
  public void setPortraitMatchReportData(
      Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData) {
    this.portraitMatchReportData = portraitMatchReportData;
  }
  
}
