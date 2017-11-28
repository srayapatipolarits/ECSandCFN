package com.sp.web.model.external.rest;

import com.sp.web.mvc.SPResponse;

/**
 * PartnerResponse method will return the response for the partner.
 * 
 * @author pradeepruhil
 *
 */
public class PartnerResponse {
  
  private SPResponse response;
  
  private String modelView;
  
  private boolean jsonResponse;
  
  public SPResponse getResponse() {
    return response;
  }
  
  public void setResponse(SPResponse response) {
    this.response = response;
  }
  
  public String getModelView() {
    return modelView;
  }
  
  public void setModelView(String modelView) {
    this.modelView = modelView;
  }
  
  public boolean isJsonResponse() {
    return jsonResponse;
  }
  
  public void setJsonResponse(boolean jsonResponse) {
    this.jsonResponse = jsonResponse;
  }
  
}
