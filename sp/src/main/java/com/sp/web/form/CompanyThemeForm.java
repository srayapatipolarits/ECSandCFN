package com.sp.web.form;

import com.sp.web.model.CompanyTheme;

import java.util.HashMap;
import java.util.Map;

/**
 * CompanyThemeForm contains the parameters for the theme created for the company.
 * 
 * @author pradeepruhil
 *
 */
public class CompanyThemeForm {
  
  private String companyId;
  
  private Map<String, String> stylesMap;
  
  /**
   * get the styles form.
   * 
   * @return the styles form.
   */
  public Map<String, String> getStylesMap() {
    if (stylesMap == null) {
      stylesMap = new HashMap<>();
    }
    return stylesMap;
  }
  
  public void setStylesMap(Map<String, String> stylesMap) {
    this.stylesMap = stylesMap;
  }
  
  /**
   * getCompanyTheme method will create the company theme.
   * 
   * @return the company theme.
   */
  public CompanyTheme getCompanyTheme() {
    CompanyTheme companyTheme = new CompanyTheme();
    companyTheme.setStylesMap(getStylesMap());
    return companyTheme;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
}
