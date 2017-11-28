package com.sp.web.dto.hiring.match;

import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.Company;

import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for giving the company information for the hiring portrait along with any
 *         Documents that the company has.
 */
public class HiringPortraitCompanyDTO extends BaseCompanyDTO {
  
  private static final long serialVersionUID = 3642407336385977163L;
  private String documentUrl;

  /**
   * Constructor.
   * 
   * @param company
   *          - company
   * @param companyDocMap 
   *          - the company document map
   */
  public HiringPortraitCompanyDTO(Company company, Map<String, String> companyDocMap) {
    super(company);
    if (!CollectionUtils.isEmpty(companyDocMap)) {
      this.documentUrl = companyDocMap.get(company.getId());
    }
  }

  public String getDocumentUrl() {
    return documentUrl;
  }

  public void setDocumentUrl(String documentUrl) {
    this.documentUrl = documentUrl;
  }
  
}
