package com.sp.web.model;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.account.AccountUpdateDetail;
import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Account Bean holding the account information for the user.
 * 
 * @author pradeep
 *
 */
public class Account implements Serializable {
  
  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = -2350426429177680156L;
  
  /** Start date from which account will be active. */
  private Date startDate;
  
  /** Account Status. */
  private AccountStatus status;
  
  private String accountNumber;
  
  private Map<SPPlanType, SPPlan> spPlanMap;
  
  /** Products subscribed by the account. */
  @Deprecated
  private List<String> products;
  
  @Deprecated
  /** Promotions. */
  private List<String> promotions;
  
  /** account id. */
  private String id;
  
  /** available subscription for this account. */
  @Deprecated
  private int availableSubscriptions;
  
  /** the number of subscriptions for hiring tool. */
  @Deprecated
  private int hiringSubscription;
  
  /** The ID of the payment instrument. */
  @Deprecated
  private String paymentInstrumentId;
  
  /** The ID of the payment instrument. */
  @Deprecated
  private String previousPaymentInstrumentId;
  
  /** The start date of the billing cycle. */
  @Deprecated
  private Date billingCycleStartDate;
  
  /** The type of account. */
  private AccountType type;
  
  @Deprecated
  private String lastPaymentId;
  
  /** The date when the account will expire. */
  @Deprecated
  private Date expiresTime;
  
  /** Set the next payment date. */
  @Deprecated
  private Date nextPaymentDate;
  
  @Deprecated
  private double nextChargeAmount;
  
  private String authorizedNetProfileId;
  
  private String customerProfileId;
  
  private String referSource;
  
  /** Agreement term in years. */
  @Deprecated
  private int agreementTerm;
  /** PaymentType to identity account whether it account from wire transfer or credit card transfer. */
  private PaymentType paymentType;
  
  @Deprecated
  private Date aggreementEndDate;
  
  private boolean deactivated;
  
  private BillingCycle billingCycle;
  
  private List<String> tagsKeywords;
  
  private Map<SPPlanType, List<AccountUpdateDetail>> accountUpdateDetailHistory;
  
  /** Credit balance */
  @Deprecated
  private double creditBalance;
  
  public Date getStartDate() {
    return startDate;
  }
  
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
  
  public AccountStatus getStatus() {
    return status;
  }
  
  public void setStatus(AccountStatus status) {
    this.status = status;
  }
  
  @Deprecated
  public List<String> getProducts() {
    return Optional.ofNullable(products).orElseGet(() -> {
      products = new ArrayList<String>();
      return products;
    });
  }
  
  @Deprecated
  public void setProducts(List<String> products) {
    this.products = products;
  }
  
  @Deprecated
  public List<String> getPromotions() {
    return Optional.ofNullable(promotions).orElseGet(() -> {
      promotions = new ArrayList<String>();
      return promotions;
    });
  }
  
  @Deprecated
  public void setPromotions(List<String> promotions) {
    this.promotions = promotions;
  }
  
  @Deprecated
  public int getAvailableSubscriptions() {
    return availableSubscriptions;
  }
  
  @Deprecated
  public void setAvailableSubscriptions(int availableSubscriptions) {
    this.availableSubscriptions = availableSubscriptions;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  @Deprecated
  public int getHiringSubscription() {
    return hiringSubscription;
  }
  
  @Deprecated
  public int getMemberSubscription(SPPlanType spPlanType) {
    return getSpPlanMap().get(spPlanType).getNumMember();
  }
  
  @Deprecated
  public void setHiringSubscription(int hiringSubscritpion) {
    this.hiringSubscription = hiringSubscritpion;
  }
  
  @Deprecated
  public String getPaymentInstrumentId() {
    return paymentInstrumentId;
  }
  
  public void setPaymentInstrumentId(String paymentInstrumentId) {
    this.paymentInstrumentId = paymentInstrumentId;
  }
  
  /**
   * This method reserves a subscription i.e. reduces the subscription count by 1.
   */
  @Deprecated
  public void reserveSubscritption() {
    availableSubscriptions--;
  }
  
  /**
   * This method reserves a subscription i.e. reduces the subscription count by 1.
   */
  public void reserveSubscritption(SPPlanType planType) {
    SPPlan spPlan = getSpPlanMap().get(planType);
    if(spPlan !=null){
      spPlan.setNumMember(spPlan.getNumMember() - 1);  
    }
    
  }
  
  /**
   * This method increments the available subscriptions by 1.
   */
  public void addSubscription(SPPlanType planType) {
    SPPlan spPlan = getSpPlanMap().get(planType);
    spPlan.setNumMember(spPlan.getNumMember() + 1);
  }
  
  /**
   * This method increments the available subscriptions by 1.
   */
  @Deprecated
  public void addSubscription() {
    availableSubscriptions++;
  }
  
  public Date getBillingCycleStartDate() {
    return billingCycleStartDate;
  }
  
  public void setBillingCycleStartDate(Date billingCycleStartDate) {
    this.billingCycleStartDate = billingCycleStartDate;
  }
  
  public AccountType getType() {
    return type;
  }
  
  public void setType(AccountType type) {
    this.type = type;
  }
  
  /**
   * Removes the given promotion from the account.
   *
   * @param promotion
   *          - promotion to remove
   * @return true if the promotion was removed
   */
  @Deprecated
  public boolean removePromotion(Promotion promotion) {
    return promotions.remove(promotion.getId());
  }
  
  /**
   * Adds the given promotion to the promotion list.
   *
   * @param promotion
   *          - promotion to add
   */
  @Deprecated
  public void addPromotion(Promotion promotion) {
    promotions = Optional.ofNullable(promotions).orElse(new ArrayList<String>());
    promotions.add(promotion.getId());
  }
  
  public void setLastPaymentId(String paymentId) {
    this.lastPaymentId = paymentId;
  }
  
  public String getLastPaymentId() {
    return lastPaymentId;
  }
  
  @Deprecated
  public void addProduct(Product product) {
    getProducts().add(product.getId());
  }
  
  public Date getExpiresTime() {
    return expiresTime;
  }
  
  public Date getNextPaymentDate() {
    return nextPaymentDate;
  }
  
  public void setNextPaymentDate(Date nextPaymentDate) {
    this.nextPaymentDate = nextPaymentDate;
  }
  
  public void setExpiresTime(Date expiresTime) {
    this.expiresTime = expiresTime;
  }
  
  public void setNextChargeAmount(double nextChargeAmount) {
    this.nextChargeAmount = nextChargeAmount;
  }
  
  /**
   * getNextChargeAmount method will return the next amount to be charge for the account.
   * 
   * @return the next charge amount.
   */
  public double getNextChargeAmount() {
    return nextChargeAmount;
  }
  
  /**
   * This method reserves a hiring subscription.
   */
  @Deprecated
  public void reserveHiringSubscription() {
    if (hiringSubscription <= 0) {
      throw new InvalidRequestException("Hiring subscriptions not availalbe !!!");
    }
    hiringSubscription--;
  }
  
  /**
   * Adds a single subscription to the hiring subscription.
   */
  @Deprecated
  public void addHiringSubscription() {
    hiringSubscription++;
  }
  
  public void setCustomerProfileId(String customerProfileId) {
    this.customerProfileId = customerProfileId;
  }
  
  public String getCustomerProfileId() {
    return customerProfileId;
  }
  
  public void setAuthorizedNetProfileId(String authorizedNetProfileId) {
    this.authorizedNetProfileId = authorizedNetProfileId;
  }
  
  public String getAuthorizedNetProfileId() {
    return authorizedNetProfileId;
  }
  
  public String getReferSource() {
    return referSource;
  }
  
  public void setReferSource(String referSource) {
    this.referSource = referSource;
  }
  
  /**
   * @param paymentType
   *          the paymentType to set
   */
  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  /**
   * @return the paymentType
   */
  public PaymentType getPaymentType() {
    return paymentType;
  }
  
  /**
   * @param agreementTerm
   *          the agreementTerm to set
   */
  @Deprecated
  public void setAgreementTerm(int agreementTerm) {
    this.agreementTerm = agreementTerm;
  }
  
  /**
   * @return the agreementTerm
   */
  @Deprecated
  public int getAgreementTerm() {
    return agreementTerm;
  }
  
  /**
   * @param aggreementEndDate
   *          the aggreementEndDate to set
   */
  @Deprecated
  public void setAggreementEndDate(Date aggreementEndDate) {
    this.aggreementEndDate = aggreementEndDate;
  }
  
  /**
   * @return the aggreementEndDate
   */
  @Deprecated
  public Date getAggreementEndDate() {
    return aggreementEndDate;
  }
  
  /**
   * Return whether account is activated or deactivated.
   * 
   * @return the isDeactivated.
   */
  public boolean isDeactivated() {
    return deactivated;
  }
  
  /**
   * @param isDeactivated
   *          the isDeactivated to set.
   */
  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }
  
  public void setSpPlanMap(Map<SPPlanType, SPPlan> spPlanMap) {
    this.spPlanMap = spPlanMap;
  }
  
  /**
   * getSpPlan method returns the plans.
   * 
   * @return the spPlan.
   */
  public Map<SPPlanType, SPPlan> getSpPlanMap() {
    if (spPlanMap == null) {
      spPlanMap = new HashMap<SPPlanType, SPPlan>();
    }
    return spPlanMap;
  }
  
  public String getAccountNumber() {
    return accountNumber;
  }
  
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }
  
  public BillingCycle getBillingCycle() {
    return billingCycle;
  }
  
  public void setBillingCycle(BillingCycle billingCycle) {
    this.billingCycle = billingCycle;
  }
  
  public List<String> getTagsKeywords() {
    if (tagsKeywords == null) {
      tagsKeywords = new ArrayList<>();
    }
    return tagsKeywords;
  }
  
  public void setTagsKeywords(List<String> tagsKeywords) {
    this.tagsKeywords = tagsKeywords;
  }
  
  public void setAccountUpdateDetailHistory(
      Map<SPPlanType, List<AccountUpdateDetail>> accountUpdateDetailHistory) {
    this.accountUpdateDetailHistory = accountUpdateDetailHistory;
  }
  
  /**
   * returns blank account update detail history.
   * 
   * @return the update detail history.
   */
  public Map<SPPlanType, List<AccountUpdateDetail>> getAccountUpdateDetailHistory() {
    if (accountUpdateDetailHistory == null) {
      accountUpdateDetailHistory = new HashMap<>();
    }
    return accountUpdateDetailHistory;
  }
  
  public void setPreviousPaymentInstrumentId(String previousPaymentInstrumentId) {
    this.previousPaymentInstrumentId = previousPaymentInstrumentId;
  }
  
  public String getPreviousPaymentInstrumentId() {
    return previousPaymentInstrumentId;
  }
  
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  
  public double getCreditBalance() {
    return creditBalance;
  }
  
  public boolean isPeopleAnalyticsPlanAvailble() {
    return getSpPlanMap().containsKey(SPPlanType.IntelligentHiring);
  }

  public SPPlan getPlan(SPPlanType planType) {
    return getSpPlanMap().get(planType);
  }
  
}
