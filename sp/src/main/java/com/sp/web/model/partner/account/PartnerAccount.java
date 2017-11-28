package com.sp.web.model.partner.account;

import java.io.Serializable;

public class PartnerAccount implements Serializable {
  
  /**
   * default serial version id.
   */
  private static final long serialVersionUID = 4662180621950440273L;
  
  /** partner account id. */
  private String id;
  
  private String partnerId;
  
  private String companyId;
  
  private boolean active;
  
  /** Partner Action Processor. */
  private String actionProcessor;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getPartnerId() {
    return partnerId;
  }
  
  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public void setActionProcessor(String actionProcessor) {
    this.actionProcessor = actionProcessor;
  }
  
  public String getActionProcessor() {
    return actionProcessor;
  }
  
}
