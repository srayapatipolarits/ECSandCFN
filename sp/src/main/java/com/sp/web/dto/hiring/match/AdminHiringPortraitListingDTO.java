package com.sp.web.dto.hiring.match;

import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.product.CompanyFactory;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring portrait listing in the administration screens.
 */
public class AdminHiringPortraitListingDTO extends AdminHiringPortraitBaseDTO {
  
  private static final long serialVersionUID = 3685776228355675886L;
  private List<HiringPortraitCompanyDTO> companies;
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          - hiring portrait
   * @param companyFactory
   *          - company factory instance
   */
  public AdminHiringPortraitListingDTO(HiringPortrait portrait, CompanyFactory companyFactory) {
    super(portrait);
    if (CollectionUtils.isNotEmpty(portrait.getCompanyIds())) {
      final Map<String, String> companyDocMap = portrait.getCompanyDocMap();
      setCompanies(portrait.getCompanyIds().stream().map(companyFactory::getCompany)
          .filter(Objects::nonNull).map(c -> new HiringPortraitCompanyDTO(c, companyDocMap))
          .collect(Collectors.toList()));
    }
  }

  public List<HiringPortraitCompanyDTO> getCompanies() {
    return companies;
  }

  public void setCompanies(List<HiringPortraitCompanyDTO> companies) {
    this.companies = companies;
  }
}
