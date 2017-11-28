package com.sp.web.dto.company;

import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.Company;
import com.sp.web.model.CompanyTheme;
import com.sp.web.model.SPFeature;
import com.sp.web.utils.ImageUtils;

/**
 * CompanyThemeDTO for company theme details.
 * 
 * @author pradeepruhil
 *
 */
public class CompanyThemeDTO extends BaseCompanyDTO {
  
  private CompanyTheme companyTheme;
  
  private boolean companyThemeActive;
  
  private boolean hasThemeAccess;
  
  private String logoURL;
  
  /**
   * Constructor.
   * 
   * @param company
   */
  public CompanyThemeDTO(Company company) {
    super(company);
    logoURL = ImageUtils.getCompanyImage(company);
    hasThemeAccess = company.getFeatureList().contains(SPFeature.CompanyTheme) ? true : false;
  }
  
  public void setCompanyTheme(CompanyTheme companyTheme) {
    this.companyTheme = companyTheme;
  }
  
  public CompanyTheme getCompanyTheme() {
    return companyTheme;
  }
  
  @Override
  public String toString() {
    return "CompanyThemeDTO [companyTheme=" + companyTheme + ", getCompanyTheme()="
        + getCompanyTheme() + ", getLogoURL()=" + getLogoURL() + ", getId()=" + getId()
        + ", getName()=" + getName() + ", getAddress()=" + getAddress() + ", getClass()="
        + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
  }
  
  public boolean isCompanyThemeActive() {
    return companyThemeActive;
  }
  
  public void setCompanyThemeActive(boolean companyThemeActive) {
    this.companyThemeActive = companyThemeActive;
  }
  
  public boolean isHasThemeAccess() {
    return hasThemeAccess;
  }
  
  public void setHasThemeAccess(boolean hasThemeAccess) {
    this.hasThemeAccess = hasThemeAccess;
  }
  
  public String getLogoURL() {
    return logoURL;
  }
  
  public void setLogoURL(String logoURL) {
    this.logoURL = logoURL;
  }
  
}
