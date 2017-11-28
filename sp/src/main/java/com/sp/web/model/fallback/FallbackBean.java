package com.sp.web.model.fallback;

import java.util.Map;

/**
 * FallbackBean contains the fallback for the fallback scenarios.
 * 
 * @author pradeepruhil
 *
 */
public class FallbackBean {
  
  private String id;
  
  private int retryCount;
  
  private String fallbackProcessor;
  
  private Object fallbackData;
  
  private String companyId;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public int getRetryCount() {
    return retryCount;
  }
  
  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }
  
  public String getFallbackProcessor() {
    return fallbackProcessor;
  }
  
  public void setFallbackProcessor(String fallbackProcessor) {
    this.fallbackProcessor = fallbackProcessor;
  }
  
  public void setFallbackData(Object fallbackData) {
    this.fallbackData = fallbackData;
  }
  
  public Object getFallbackData() {
    return fallbackData;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
}
