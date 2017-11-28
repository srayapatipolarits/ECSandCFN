package com.sp.web.dto;

import com.sp.web.dto.alternatebilling.SPPlanDTO;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.account.SPPlanType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The DTO bean for the account.
 */
public class AccountDTO {
  
  /* The account id */
  private String id;
  
  /* The account status */
  private AccountStatus status;
  
  /** Acount number. */
  private String accountNumber;
  
  private String partnerId;
  
  private Map<SPPlanType, SPPlanDTO> spPlans;
  
  private String planName;
  
  private boolean isDeactivated;
  
  private boolean restrictRelationShipAdvisor;
  
  /**
   * Constructor for account DTO.
   * 
   * @param account
   *          intializing the account DTO.
   */
  public AccountDTO(Account account) {
    setId(account.getId());
    setStatus(account.getStatus());
    setDeactivated(account.isDeactivated());
    setAccountNumber(account.getAccountNumber());
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public AccountStatus getStatus() {
    return status;
  }
  
  public void setStatus(AccountStatus status) {
    this.status = status;
  }
  
  /**
   * @param isDeactivated
   *          the isDeactivated to set
   */
  public void setDeactivated(boolean isDeactivated) {
    this.isDeactivated = isDeactivated;
  }
  
  /**
   * @return the isDeactivated
   */
  public boolean isDeactivated() {
    return isDeactivated;
  }
  
  public String getAccountNumber() {
    return accountNumber;
  }
  
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }
  
  public void setSpPlans(Map<SPPlanType, SPPlanDTO> spPlans) {
    this.spPlans = spPlans;
  }
  
  public Map<SPPlanType, SPPlanDTO> getSpPlans() {
    if (spPlans == null) {
      spPlans = new HashMap<>();
    }
    return spPlans;
  }
  
  public String getPlanName() {
    return planName;
  }
  
  public void setPlanName(String planName) {
    this.planName = planName;
  }
  
  /**
   * @param restrictRelationShipAdvisor
   *          the restrictRelationShipAdvisor to set
   */
  public void setRestrictRelationShipAdvisor(boolean restrictRelationShipAdvisor) {
    this.restrictRelationShipAdvisor = restrictRelationShipAdvisor;
  }
  
  /**
   * @return the restrictRelationShipAdvisor
   */
  public boolean isRestrictRelationShipAdvisor() {
    return restrictRelationShipAdvisor;
  }
  
  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }
  
  public String getPartnerId() {
    return partnerId;
  }
}
