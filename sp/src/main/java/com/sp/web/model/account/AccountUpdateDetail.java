package com.sp.web.model.account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AccountUpdateDetai stores the hisotry of the account updates for addition of members of
 * candidates to the plan.
 * 
 * @author pradeepruhil
 *
 */
public class AccountUpdateDetail implements Serializable {

  /**
   * Serial version id.
   */
  private static final long serialVersionUID = -5716932452878120126L;
  
  private LocalDateTime  createdOn;
  
  private long noOfAccounts;
  
  private BigDecimal unitPrice;
  
  private BigDecimal overridePrice;
  
  private AccountUpdateType accountUpdateType;

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public AccountUpdateDetail() {
   this.createdOn = LocalDateTime.now();
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public long getNoOfAccounts() {
    return noOfAccounts;
  }

  public void setNoOfAccounts(long noOfAccounts) {
    this.noOfAccounts = noOfAccounts;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getOverridePrice() {
    return overridePrice;
  }

  public void setOverridePrice(BigDecimal overridePrice) {
    this.overridePrice = overridePrice;
  }

  public void setAccountUpdateType(AccountUpdateType accountUpdateType) {
    this.accountUpdateType = accountUpdateType;
  }
  
  public AccountUpdateType getAccountUpdateType() {
    return accountUpdateType;
  }
  
  
  /**
   * AccountUpdateType contains the update to the plan for admin member or only member only.
   * 
   * @author pradeepruhil
   *
   */
  public static enum AccountUpdateType {
   
    PlanAdmin,
    
    PlanMember;
  }
}
