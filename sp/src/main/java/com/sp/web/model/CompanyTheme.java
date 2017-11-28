package com.sp.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * CompanyTheme contains hold the colors, styling for the company specific branding.
 * 
 * @author pradeepruhil
 *
 */
public class CompanyTheme implements Serializable{
  
  /** Default serial version id.
   * 
   */
  private static final long serialVersionUID = 7152759026285137384L;

  /**
   * stylesMap contains the key value parameter for the color for different anchor styles.
   */
  private Map<String, String> stylesMap;
  
  /** Css URL for the company Theme. */
  private String cssUrl;
  
  
  /**
   * getStylesMap method will return the blank styles map in case it is null.
   * 
   * @return the styles map.
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
  
  public String getCssUrl() {
    return cssUrl;
  }
  
  public void setCssUrl(String cssUrl) {
    this.cssUrl = cssUrl;
  }
  
  @Override
  public String toString() {
    return "CompanyTheme [stylesMap=" + stylesMap + ", cssUrl=" + cssUrl   + "]";
  }
}
