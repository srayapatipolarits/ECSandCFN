package com.sp.web.controller.admin.account;

import com.sp.web.dto.AccountDTO;
import com.sp.web.model.Account;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The DTO to hold the Business account details.
 *
 */
public class BusinessAccountDTO extends AccountDTO {
  
  /* the hiring product details */
  private Map<String, Object> hiringProduct;
  
  
  
  /**
   * Constructor to create the DTO from the account object.
   * 
   * @param account
   *          - the account
   * @param spPlanType
   */
  public BusinessAccountDTO(Account account, SPPlanType spPlanType) {
    super(account);
  }
  
  public BusinessAccountDTO(Account account) {
    super(account);
  }
  
  public Map<String, Object> getHiringProduct() {
    return Optional.ofNullable(hiringProduct).orElseGet(() -> {
      hiringProduct = new HashMap<String, Object>();
      return hiringProduct;
    });
  }
  
  public void setHiringProduct(Map<String, Object> hiringProduct) {
    this.hiringProduct = hiringProduct;
  }
}
