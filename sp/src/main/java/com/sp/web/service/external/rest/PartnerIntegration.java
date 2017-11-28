package com.sp.web.service.external.rest;

public enum PartnerIntegration {
  
  Medix("medixActionProcessor");
  
  private String actionProcessor;
  
  private PartnerIntegration(String actionProcessor) {
    this.actionProcessor = actionProcessor;
  }
  
  public String getActionProcessor() {
    return actionProcessor;
  }
  
  public void setActionProcessor(String actionProcessor) {
    this.actionProcessor = actionProcessor;
  }
  
}
