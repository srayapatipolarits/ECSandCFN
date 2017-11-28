package com.sp.web.model.external.rest;

import java.util.HashMap;
import java.util.Map;

public class PartnerRequest {
  
  private Map<String, Object> inputRequestMap;
  
  private String partnerId;
  
  private String companyId;
  
  public Map<String, Object> getInputRequestMap() {
    return inputRequestMap;
  }
  
  public void setInputRequestMap(Map<String, Object> inputRequestMap) {
    this.inputRequestMap = inputRequestMap;
  }
  
  /**
   * addToRequest method will add the request to the input.
   * 
   * @param key
   *          is the key
   * @param value
   *          is the value for the partner request.
   */
  public void addToRequest(String key, Object value) {
    
    if (inputRequestMap == null) {
      inputRequestMap = new HashMap<String, Object>();
    }
    inputRequestMap.put(key, value);
  }
  
  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }
  
  public String getPartnerId() {
    return partnerId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
}
