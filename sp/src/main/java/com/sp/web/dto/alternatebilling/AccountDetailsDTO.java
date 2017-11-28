package com.sp.web.dto.alternatebilling;

import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.ProductValidityType;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.utils.MessagesHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>AccountDetailsDTO</code> will populate the account details.
 * 
 * @author pradeepruhil
 *
 */
public class AccountDetailsDTO {
  
  private String id;
  
  private String companyName;
  
  private String city;
  
  private String country;
  
  private String terms;
  
  private String accountType;
  
  private String accountNumber;
  
  private String endDate;
  
  private String accountStatus;
  
  private String memberName;
  
  private String memberEmail;
  
  private boolean isDeactivated;
  
  private Map<SPPlanType, SPPlanBaseDTO> spPlanMap;
  
  /**
   * Constructor.
   * 
   * @param productValidityType
   */
  public AccountDetailsDTO(Account account, Company company, ProductValidityType productValidityType) {
    
    this.id = company.getAccountId();
    this.companyName = company.getName();
    this.city = company.getAddress().getCity();
    this.country = company.getAddress().getCountry();
    this.terms = productValidityType.toString();
    this.endDate = MessagesHelper.formatDate(account.getExpiresTime());
    this.accountStatus = account.getStatus().toString();
    if (account.getPaymentType() != null) {
      this.accountType = account.getPaymentType().toString();
    }
    this.isDeactivated = account.isDeactivated();
    this.spPlanMap = new HashMap<SPPlanType, SPPlanBaseDTO>();
    account.getSpPlanMap().forEach((k, v) -> {
      spPlanMap.put(k, new SPPlanBaseDTO(v));
    });
    this.accountNumber = account.getAccountNumber();
  }
  
  /**
   * Constructor.
   * 
   * @param productValidityType
   */
  public AccountDetailsDTO(Account account, Company company) {
    
    this.id = company.getAccountId();
    this.companyName = company.getName();
    this.city = company.getAddress().getCity();
    this.country = company.getAddress().getCountry();
    this.accountStatus = account.getStatus().toString();
    if (account.getPaymentType() != null) {
      this.accountType = account.getPaymentType().toString();
    }
    this.isDeactivated = account.isDeactivated();
    
    this.spPlanMap = new HashMap<SPPlanType, SPPlanBaseDTO>();
    account.getSpPlanMap().forEach((k, v) -> {
      if(v !=null){
        spPlanMap.put(k, new SPPlanBaseDTO(v));  
      }
      
    });
    this.accountNumber = account.getAccountNumber();
  }
  
  /**
   * Constructor.
   */
  public AccountDetailsDTO(Account account, User user, ProductValidityType productValidityType) {
    
    this.id = user.getAccountId();
    this.memberEmail = user.getEmail();
    this.city = user.getAddress().getCity();
    this.country = user.getAddress().getCountry();
    this.terms = productValidityType.toString();
    this.endDate = MessagesHelper.formatDate(account.getExpiresTime());
    this.accountStatus = account.getStatus().toString();
    this.memberName = user.getFirstName() + " " + user.getLastName();
    if (account.getPaymentType() != null) {
      this.accountType = account.getPaymentType().toString();
    }
    this.isDeactivated = account.isDeactivated();
    this.spPlanMap = new HashMap<SPPlanType, SPPlanBaseDTO>();
    account.getSpPlanMap().forEach((k, v) -> {
      spPlanMap.put(k, new SPPlanBaseDTO(v));
    });
    this.accountNumber = account.getAccountNumber();
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getCompanyName() {
    return companyName;
  }
  
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }
  
  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public String getTerms() {
    return terms;
  }
  
  public void setTerms(String terms) {
    this.terms = terms;
  }
  
  public String getAccountType() {
    return accountType;
  }
  
  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }
  
  public String getAccountStatus() {
    return accountStatus;
  }
  
  public void setAccountStatus(String accountStatus) {
    this.accountStatus = accountStatus;
  }
  
  public String getMemberName() {
    return memberName;
  }
  
  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }
  
  public String getMemberEmail() {
    return memberEmail;
  }
  
  public void setMemberEmail(String memberEmail) {
    this.memberEmail = memberEmail;
  }
  
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
  
  public String getEndDate() {
    return endDate;
  }
  
  public void setDeactivated(boolean isDeactivated) {
    this.isDeactivated = isDeactivated;
  }
  
  public boolean isDeactivated() {
    return isDeactivated;
  }
  
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }
  
  public String getAccountNumber() {
    return accountNumber;
  }
  
  public void setSpPlanMap(Map<SPPlanType, SPPlanBaseDTO> spPlanMap) {
    this.spPlanMap = spPlanMap;
  }
  
  public Map<SPPlanType, SPPlanBaseDTO> getSpPlanMap() {
    if (spPlanMap == null) {
      spPlanMap = new HashMap<SPPlanType, SPPlanBaseDTO>();
    }
    return spPlanMap;
  }
  
}
