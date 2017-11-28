package com.sp.web.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.model.partner.account.PartnerAccount;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.Assert;

@JsonDeserialize(as = PartnerAccountForm.class)
public class PartnerAccountForm implements GenericForm<PartnerAccount> {
  
  private String companyId;
  private boolean active;
  private String id;
  
  @Override
  public void validate() {
    Assert.hasText(companyId, "Company id not found.");
  }
  
  @Override
  public void validateUpdate() {
    Assert.hasText(id, "Partner id is not present");
    Assert.hasText(companyId, "Company id not found.");
  }
  
  @Override
  public void validateGet() {
    
  }
  
  @Override
  public PartnerAccount create(User user) {
    PartnerAccount partnerAccount = new PartnerAccount();
    partnerAccount.setActive(isActive());
    partnerAccount.setCompanyId(getCompanyId());
    partnerAccount.setPartnerId(RandomStringUtils.random(15, true, true));
    return partnerAccount;
  }
  
  @Override
  public void update(User user, PartnerAccount instanceToUpdate) {
    instanceToUpdate.setPartnerId(RandomStringUtils.random(15, true, true));
    instanceToUpdate.setActive(isActive());
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}
