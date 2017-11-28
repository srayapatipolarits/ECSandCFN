package com.sp.web.service.email;

import java.util.HashMap;
import java.util.Map;

public class MessageParams {
  
  private Map<String, Object> valueMap;
  
  private String templateName;
  
  private String locale;
  
  /**
   * The reference of the value map.
   * 
   * @return the value map
   */
  public Map<String, Object> getValueMap() {
    if (valueMap == null) {
      valueMap = new HashMap<>();
    }
    return valueMap;
  }
  
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }
  
  public String getTemplateName() {
    return templateName;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  public String getLocale() {
    return locale;
  }
}
