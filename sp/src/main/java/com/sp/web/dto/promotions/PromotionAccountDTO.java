package com.sp.web.dto.promotions;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.AccountType;
import com.sp.web.model.Address;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

/**
 * @author Dax Abraham
 *
 *         The details of the account where the promotion is being used, this could be either
 *         individual or company.
 */
public class PromotionAccountDTO {
  
  /**
   * Account related information.
   */
  private String id;
  private AccountStatus status;
  private String startDate;
  private AccountType type;
  private String expiresTime;
  
  private Address  address;
  
  /**
   * Company related information.
   */
  private String name;
  
  /**
   * User related information.
   */
  private BaseUserDTO user;

  /**
   * Create a new promotion account detail object. 
   * 
   * @param account
   *          - account
   * @param company
   *          - company
   */
  public PromotionAccountDTO(Account account, Company company) {
    updateAccount(account);
    this.name = company.getName();
    this.address = company.getAddress();
  }

  /**
   * Create a new promtion account DTO.
   * 
   * @param account
   *          - account
   * @param usr
   *          - user
   */
  public PromotionAccountDTO(Account account, User usr) {
    updateAccount(account);
    this.user = new BaseUserDTO(usr);
  }

  private void updateAccount(Account account) {
    this.id = account.getId();
    this.status = account.getStatus();
    this.startDate = MessagesHelper.formatDate(account.getStartDate());
    this.type = account.getType();
    this.expiresTime = MessagesHelper.formatDate(account.getExpiresTime());
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
  
  public String getStartDate() {
    return startDate;
  }
  
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }
  
  public AccountType getType() {
    return type;
  }
  
  public void setType(AccountType type) {
    this.type = type;
  }
  
  public String getExpiresTime() {
    return expiresTime;
  }
  
  public void setExpiresTime(String expiresTime) {
    this.expiresTime = expiresTime;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public BaseUserDTO getUser() {
    return user;
  }
  
  public void setUser(BaseUserDTO user) {
    this.user = user;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(Address address) {
    this.address = address;
  }
  
  /**
   * @return the address
   */
  public Address getAddress() {
    return address;
  }
}
