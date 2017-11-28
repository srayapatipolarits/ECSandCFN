package com.sp.web.model.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.model.PaymentType;
import com.sp.web.model.SPFeature;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <code>SPPlan</code> class contains the information about the plan type and there pricing
 * information.
 * 
 * @author pradeepruhil
 *
 */
public class SPPlan implements Serializable {
  
  private static final long serialVersionUID = -3111525293622223841L;
  
  private String name;
  
  /** Active flag stats that the plan is active or not. */
  private PlanStatus planStatus;
  
  private BigDecimal licensePrice;
  
  private BigDecimal unitMemberPrice;
  
  private BigDecimal overrideMemberPrice;
  
  private BigDecimal unitAdminPrice;
  
  private BigDecimal overrideAdminPrice;
  
  private int numMember;
  
  private double creditBalance;
  
  private int numAdmin;
  
  private Set<SPFeature> features;
  
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDateTime aggreementEndDate;
  
  private SPPlanType planType;
  
  private BigDecimal planMemberNextChargeAmount;
  
  private BigDecimal planAdminNextChargeAmount;
  
  /** The ID of the payment instrument. */
  private String paymentInstrumentId;
  
  /** The ID of the payment instrument. */
  private String previousPaymentInstrumentId;
  
  /** The start date of the billing cycle. */
  private Date billingCycleStartDate;
  
  private boolean bundle;
  
  private boolean active;
  
  private String lastPaymentId;
  
  /** The date when the account will expire. */
  private Date expiresTime;
  
  /** Set the next payment date. */
  private Date nextPaymentDate;
  
  private double nextChargeAmount;
  
  private String authorizedNetProfileId;
  
  private String customerProfileId;
  
  private String referSource;
  
  /** Agreement term in years. */
  private int agreementTerm;
  /** PaymentType to identity account whether it account from wire transfer or credit card transfer. */
  private PaymentType paymentType;
  
  private boolean deactivated;
  
  private BillingCycle billingCycle;
  
  private List<String> tagsKeywords;
  
  @Deprecated
  private int aggrementTerm;
  
  public SPPlan() {
    
  }
  
  /**
   * update the spPlan with the new Plan.
   */
  public SPPlan(SPPlan newPlan) {
    BeanUtils.copyProperties(newPlan, this);
    setOverrideMemberPrice(newPlan.getOverrideMemberPrice());
    setOverrideAdminPrice(newPlan.getOverrideAdminPrice());
    setPlanAdminNextChargeAmount(newPlan.getPlanAdminNextChargeAmount());
    setPlanMemberNextChargeAmount(newPlan.getPlanMemberNextChargeAmount());
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Returns the plateform license price.
   * 
   * @return the license price .
   */
  public BigDecimal getLicensePrice() {
    if (licensePrice == null) {
      licensePrice = new BigDecimal(0.0);
    }
    return licensePrice;
  }
  
  public void setLicensePrice(BigDecimal licensePrice) {
    this.licensePrice = licensePrice;
  }
  
  public BigDecimal getUnitMemberPrice() {
    return unitMemberPrice;
  }
  
  public void setUnitMemberPrice(BigDecimal unitMemberPrice) {
    this.unitMemberPrice = unitMemberPrice;
  }
  
  /**
   * Returns the override Member price. IN case override member price is not present, then return 0.
   * 
   * @return override member price.
   */
  public BigDecimal getOverrideMemberPrice() {
    if (overrideMemberPrice == null) {
      overrideMemberPrice = new BigDecimal(0);
    }
    return overrideMemberPrice;
  }
  
  public void setOverrideMemberPrice(BigDecimal overrideMemberPrice) {
    this.overrideMemberPrice = overrideMemberPrice;
  }
  
  /**
   * Returns the unitAdmin price, 0 in case it is null
   * 
   * @return the unit admin price.
   */
  public BigDecimal getUnitAdminPrice() {
    if (unitAdminPrice == null) {
      unitAdminPrice = new BigDecimal(0);
    }
    return unitAdminPrice;
  }
  
  public void setUnitAdminPrice(BigDecimal unitAdminPrice) {
    this.unitAdminPrice = unitAdminPrice;
  }
  
  /**
   * Returns the override Admin price. IN case override admin price is not present, then return 0
   * 
   * @return override admin price.
   */
  public BigDecimal getOverrideAdminPrice() {
    if (overrideAdminPrice == null) {
      overrideAdminPrice = new BigDecimal(0);
    }
    return overrideAdminPrice;
  }
  
  public void setOverrideAdminPrice(BigDecimal overrideAdminPrice) {
    this.overrideAdminPrice = overrideAdminPrice;
  }
  
  public int getNumMember() {
    return numMember;
  }
  
  public void setNumMember(int numMember) {
    this.numMember = numMember;
  }
  
  public int getNumAdmin() {
    return numAdmin;
  }
  
  public void setNumAdmin(int numAdmin) {
    this.numAdmin = numAdmin;
  }
  
  public Set<SPFeature> getFeatures() {
    if (features == null) {
      features = new HashSet<SPFeature>();
    }
    return features;
  }
  
  public void setFeatures(Set<SPFeature> features) {
    this.features = features;
  }
  
  public SPPlanType getPlanType() {
    return planType;
  }
  
  public void setPlanType(SPPlanType planType) {
    this.planType = planType;
  }
  
  /**
   * Return true if override Admin price is present or else false.
   * 
   * @return true or false.
   */
  public boolean isOverrideAdminPrice() {
    if (getOverrideAdminPrice().intValue() > 0) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Return true if override Member price is present or else false.
   * 
   * @return true or false.
   */
  public boolean isOverrideMemberPrice() {
    if (getOverrideMemberPrice().intValue() > 0) {
      return true;
    } else {
      return false;
    }
  }
  
  public void setPlanStatus(PlanStatus planStatus) {
    this.planStatus = planStatus;
  }
  
  public void setAggreementEndDate(LocalDateTime aggreementEndDate) {
    this.aggreementEndDate = aggreementEndDate;
  }
  
  public LocalDateTime getAggreementEndDate() {
    return aggreementEndDate;
  }
  
  public PlanStatus getPlanStatus() {
    return planStatus;
  }
  
  /**
   * @param planAdminNextChargeAmount
   *          the planAdminNextChargeAmount to set
   */
  public void setPlanAdminNextChargeAmount(BigDecimal planAdminNextChargeAmount) {
    this.planAdminNextChargeAmount = planAdminNextChargeAmount;
  }
  
  /**
   * @return the planAdminNextChargeAmount
   */
  public BigDecimal getPlanAdminNextChargeAmount() {
    if (planAdminNextChargeAmount == null) {
      planAdminNextChargeAmount = new BigDecimal(0);
    }
    return planAdminNextChargeAmount;
  }
  
  public void setPlanMemberNextChargeAmount(BigDecimal planMemberNextChargeAmount) {
    this.planMemberNextChargeAmount = planMemberNextChargeAmount;
  }
  
  public BigDecimal getPlanMemberNextChargeAmount() {
    if (planMemberNextChargeAmount == null) {
      planMemberNextChargeAmount = new BigDecimal(0);
    }
    return planMemberNextChargeAmount;
  }
  
  public void updateSPPlan(SPPlan spPlan) {
    BeanUtils.copyProperties(spPlan, this);
  }
  
  public void setBundle(boolean bundle) {
    this.bundle = bundle;
  }
  
  public boolean isBundle() {
    return bundle;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public String getPaymentInstrumentId() {
    return paymentInstrumentId;
  }
  
  public void setPaymentInstrumentId(String paymentInstrumentId) {
    this.paymentInstrumentId = paymentInstrumentId;
  }
  
  public String getPreviousPaymentInstrumentId() {
    return previousPaymentInstrumentId;
  }
  
  public void setPreviousPaymentInstrumentId(String previousPaymentInstrumentId) {
    this.previousPaymentInstrumentId = previousPaymentInstrumentId;
  }
  
  public Date getBillingCycleStartDate() {
    return billingCycleStartDate;
  }
  
  public void setBillingCycleStartDate(Date billingCycleStartDate) {
    this.billingCycleStartDate = billingCycleStartDate;
  }
  
  public String getLastPaymentId() {
    return lastPaymentId;
  }
  
  public void setLastPaymentId(String lastPaymentId) {
    this.lastPaymentId = lastPaymentId;
  }
  
  public Date getExpiresTime() {
    return expiresTime;
  }
  
  public void setExpiresTime(Date expiresTime) {
    this.expiresTime = expiresTime;
  }
  
  public Date getNextPaymentDate() {
    return nextPaymentDate;
  }
  
  public void setNextPaymentDate(Date nextPaymentDate) {
    this.nextPaymentDate = nextPaymentDate;
  }
  
  public double getNextChargeAmount() {
    return nextChargeAmount;
  }
  
  public void setNextChargeAmount(double nextChargeAmount) {
    this.nextChargeAmount = nextChargeAmount;
  }
  
  public String getAuthorizedNetProfileId() {
    return authorizedNetProfileId;
  }
  
  public void setAuthorizedNetProfileId(String authorizedNetProfileId) {
    this.authorizedNetProfileId = authorizedNetProfileId;
  }
  
  public String getCustomerProfileId() {
    return customerProfileId;
  }
  
  public void setCustomerProfileId(String customerProfileId) {
    this.customerProfileId = customerProfileId;
  }
  
  public String getReferSource() {
    return referSource;
  }
  
  public void setReferSource(String referSource) {
    this.referSource = referSource;
  }
  
  public int getAgreementTerm() {
    return agreementTerm;
  }
  
  public void setAgreementTerm(int agreementTerm) {
    this.agreementTerm = agreementTerm;
  }
  
  public PaymentType getPaymentType() {
    return paymentType;
  }
  
  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  public boolean isDeactivated() {
    return deactivated;
  }
  
  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }
  
  public BillingCycle getBillingCycle() {
    return billingCycle;
  }
  
  public void setBillingCycle(BillingCycle billingCycle) {
    this.billingCycle = billingCycle;
  }
  
  public List<String> getTagsKeywords() {
    if (tagsKeywords == null) {
      tagsKeywords = new ArrayList<String>();
    }
    return tagsKeywords;
  }
  
  public void setTagsKeywords(List<String> tagsKeywords) {
    this.tagsKeywords = tagsKeywords;
  }
  
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  
  public double getCreditBalance() {
    return creditBalance;
  }
  
  @Override
  public String toString() {
    return "SPPlan [name=" + name + ", planStatus=" + planStatus + ", licensePrice=" + licensePrice
        + ", unitMemberPrice=" + unitMemberPrice + ", overrideMemberPrice=" + overrideMemberPrice
        + ", unitAdminPrice=" + unitAdminPrice + ", overrideAdminPrice=" + overrideAdminPrice
        + ", numMember=" + numMember + ", numAdmin=" + numAdmin + ", features=" + features
        + ", aggreementEndDate=" + aggreementEndDate + ", planType=" + planType
        + ", planMemberNextChargeAmount=" + planMemberNextChargeAmount
        + ", planAdminNextChargeAmount=" + planAdminNextChargeAmount + ", paymentInstrumentId="
        + paymentInstrumentId + ", previousPaymentInstrumentId=" + previousPaymentInstrumentId
        + ", billingCycleStartDate=" + billingCycleStartDate + ", bundle=" + bundle + ", active="
        + active + ", lastPaymentId=" + lastPaymentId + ", expiresTime=" + expiresTime
        + ", nextPaymentDate=" + nextPaymentDate + ", nextChargeAmount=" + nextChargeAmount
        + ", authorizedNetProfileId=" + authorizedNetProfileId + ", customerProfileId="
        + customerProfileId + ", referSource=" + referSource + ", agreementTerm=" + agreementTerm
        + ", paymentType=" + paymentType + ", deactivated=" + deactivated + ", billingCycle="
        + billingCycle + ", tagsKeywords=" + tagsKeywords + "]";
  }

  public void reserveAdminAccount() {
    numAdmin--;
  }

  public void releaseAdminAccount() {
    numAdmin++;
  }
  
  @Deprecated
  public void setAggrementTerm(int aggrementTerm) {
    this.aggrementTerm = aggrementTerm;
  }
  
  @Deprecated
  public int getAggrementTerm() {
    return aggrementTerm;
  }
  
}
