package com.sp.web.dto.hiring.match;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.role.HiringRoleBaseDTO;
import com.sp.web.model.hiring.match.MatchCriteria;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring portrait match details.
 */
public class HiringPortraitMatchDTO extends HiringPortraitMatchListingDTO {
  
  private static final long serialVersionUID = -6957153094986179781L;
  private String companyDocUrl;
  private int traitsCount;
  private Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData;
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          - portrait
   * @param roles
   *          - roles 
   * @param companyId 
   *          - company id
   */
  public HiringPortraitMatchDTO(HiringPortraitDao portrait, List<HiringRoleBaseDTO> roles, String companyId) {
    super(portrait);
    setRoles(roles);
    companyDocUrl = portrait.getCompanyDocumentUrl(companyId);
  }
  
  public String getCompanyDocUrl() {
    return companyDocUrl;
  }
  
  public void setCompanyDocUrl(String companyDocUrl) {
    this.companyDocUrl = companyDocUrl;
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

  public void setPortraitMatchReportData(Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData) {
    this.portraitMatchReportData = portraitMatchReportData;
  }
  
}
