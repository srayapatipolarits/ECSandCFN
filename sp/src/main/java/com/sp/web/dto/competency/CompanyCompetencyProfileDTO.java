package com.sp.web.dto.competency;

import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.competency.CompetencyProfile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the company and competency profile.
 */
public class CompanyCompetencyProfileDTO {
  
  private BaseCompanyDTO company;
  private List<BaseCompetencyProfileDTO> competencyProfileList;
  
  /**
   * Constructor from company and competency profile list.
   * 
   * @param company
   *          - company
   * @param competencyProfileList
   *          - competency profile list
   */
  public CompanyCompetencyProfileDTO(CompanyDao company, List<CompetencyProfile> competencyProfileList) {
    this.company = new BaseCompanyDTO(company);
    this.competencyProfileList = competencyProfileList.stream().collect(
        Collectors.mapping(BaseCompetencyProfileDTO::new, Collectors.toList()));
  }

  public BaseCompanyDTO getCompany() {
    return company;
  }
  
  public void setCompany(BaseCompanyDTO company) {
    this.company = company;
  }
  
  public List<BaseCompetencyProfileDTO> getCompetencyProfileList() {
    return competencyProfileList;
  }
  
  public void setCompetencyProfileList(List<BaseCompetencyProfileDTO> competencyProfileList) {
    this.competencyProfileList = competencyProfileList;
  }
}
