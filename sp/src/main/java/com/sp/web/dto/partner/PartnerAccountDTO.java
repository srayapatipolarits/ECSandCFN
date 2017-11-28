package com.sp.web.dto.partner;

import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.Company;
import com.sp.web.model.partner.account.PartnerAccount;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * PartnerAccountDTO is the DTO for partner Account DTO.
 * 
 * @author pradeepruhil
 *
 */
public class PartnerAccountDTO implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private BaseCompanyDTO companyDTO;
  
  private String id;
  
  private String partnerId;
  
  private boolean active;
  
  public PartnerAccountDTO(PartnerAccount account, Company company) {
    this.companyDTO = new BaseCompanyDTO(company);
    BeanUtils.copyProperties(account, this);
  }
  
  public BaseCompanyDTO getCompanyDTO() {
    return companyDTO;
  }
  
  public void setCompanyDTO(BaseCompanyDTO companyDTO) {
    this.companyDTO = companyDTO;
  }
  
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
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
}
