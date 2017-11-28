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
 *         The DTO class for the hiring portrait details for displaying in portrait match details,
 *         etc.
 */
public class HiringPortraitDetailsDTO extends HiringPortraitBaseDTO {

  private static final long serialVersionUID = 6136547636570085653L;
  private Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData;

  /**
   * Constructor.
   * 
   * @param portrait
   *            - portrait
   */
  public HiringPortraitDetailsDTO(HiringPortraitDao portrait) {
    super(portrait);
  }

  public Map<CategoryType, Map<String, List<MatchCriteria>>> getPortraitMatchReportData() {
    return portraitMatchReportData;
  }

  public void setPortraitMatchReportData(Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData) {
    this.portraitMatchReportData = portraitMatchReportData;
  }
  
}
