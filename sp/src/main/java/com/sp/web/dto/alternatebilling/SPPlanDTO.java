package com.sp.web.dto.alternatebilling;

import com.sp.web.dto.PaymentInstrumentDTO;
import com.sp.web.dto.PaymentRecordDTO;
import com.sp.web.model.PaymentType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.account.AccountUpdateDetail;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>SPPlanDTO</code> is the DTO for the spPlan.
 * 
 * @author pradeepruhil
 *
 */
public class SPPlanDTO extends SPPlanBaseDTO {
  
  /** Active flag stats that the plan is active or not. */
  private PlanStatus planStatus;
  
  private BigDecimal licensePrice;
  
  private BigDecimal unitMemberPrice;
  
  private BigDecimal overrideMemberPrice;
  
  private BigDecimal unitAdminPrice;
  
  private BigDecimal overrideAdminPrice;
  
  private int availalbeMemberSubscriptions;
  
  private int availalbeAdminSubscriptions;
  
  private List<SPFeature> features;
  
  private long numberOfMembers;
  
  private int numActive;
  
  private int numDeleted;
  
  private String planName;
  
  private int agreementTerm;
  
  private boolean bundle;
  
  
  private Map<String, Object> planInfo;
  
  private List<AccountUpdateDetail> updateDetails;
  
  private BigDecimal planMemberNextChargeAmount;
  
  private BigDecimal planAdminNextChargeAmount;
  
  /* The payment instrument associated with the account */
  private PaymentInstrumentDTO paymentInstrument;
  
  private PaymentRecordDTO lastPayment;
  
  private List<String> tagsKeywords;
  
  public PaymentInstrumentDTO getPaymentInstrument() {
    return paymentInstrument;
  }
  
  public void setPaymentInstrument(PaymentInstrumentDTO paymentInstrument) {
    this.paymentInstrument = paymentInstrument;
  }
  
  public PaymentRecordDTO getLastPayment() {
    return lastPayment;
  }
  
  public void setLastPayment(PaymentRecordDTO lastPayment) {
    this.lastPayment = lastPayment;
  }
  
  public PaymentType getPaymentType() {
    return paymentType;
  }
  
  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  public String getAggreementEndDate() {
    return aggreementEndDate;
  }
  
  public void setAggreementEndDate(String aggreementEndDate) {
    this.aggreementEndDate = aggreementEndDate;
  }
  
  public double getCreditBalance() {
    return creditBalance;
  }
  
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  
  public boolean isPreviousPaymentInstrumentPresent() {
    return previousPaymentInstrumentPresent;
  }
  
  public void setPreviousPaymentInstrumentPresent(boolean previousPaymentInstrumentPresent) {
    this.previousPaymentInstrumentPresent = previousPaymentInstrumentPresent;
  }
  
  private PaymentType paymentType;
  
  private String aggreementEndDate;
  
  private double creditBalance;
  
  private boolean previousPaymentInstrumentPresent;
  
  private long remainingChargableDays;
  
  
  public SPPlanDTO(SPPlan spPlan) {
    super(spPlan);
    getFeatures().addAll(spPlan.getFeatures());
    this.overrideAdminPrice = spPlan.getOverrideAdminPrice();
    this.overrideMemberPrice = spPlan.getOverrideMemberPrice();
    this.planStatus = spPlan.getPlanStatus();
    setPlanAdminNextChargeAmount(spPlan.getPlanAdminNextChargeAmount());
    setPlanMemberNextChargeAmount(spPlan.getPlanMemberNextChargeAmount());
    setAggreementEndDate(MessagesHelper.formatDate(spPlan.getAggreementEndDate().toLocalDate()));
    setAgreementTerm(spPlan.getAgreementTerm());
    setTagsKeywords(spPlan.getTagsKeywords());
    setCreditBalance(spPlan.getCreditBalance());
    if (StringUtils.isNotBlank(spPlan.getPreviousPaymentInstrumentId())) {
      setPreviousPaymentInstrumentPresent(true);
    }
    this.planName = spPlan.getName();
    
    
    if (spPlan.getNextPaymentDate() != null) {
      LocalDate nextRechargeDate = LocalDate.fromDateFields(spPlan.getNextPaymentDate());
      Days daysBetween = Days.daysBetween(LocalDate.now(), nextRechargeDate);
      this.remainingChargableDays = daysBetween.getDays();
    }
    
  }
  
  public PlanStatus getPlanStatus() {
    return planStatus;
  }
  
  public void setPlanStatus(PlanStatus planStatus) {
    this.planStatus = planStatus;
  }
  
  public BigDecimal getLicensePrice() {
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
  
  public BigDecimal getOverrideMemberPrice() {
    return overrideMemberPrice;
  }
  
  public void setOverrideMemberPrice(BigDecimal overrideMemberPrice) {
    this.overrideMemberPrice = overrideMemberPrice;
  }
  
  public BigDecimal getUnitAdminPrice() {
    return unitAdminPrice;
  }
  
  public void setUnitAdminPrice(BigDecimal unitAdminPrice) {
    this.unitAdminPrice = unitAdminPrice;
  }
  
  public BigDecimal getOverrideAdminPrice() {
    return overrideAdminPrice;
  }
  
  public void setOverrideAdminPrice(BigDecimal overrideAdminPrice) {
    this.overrideAdminPrice = overrideAdminPrice;
  }
  
  /**
   * send the blank list. in case of empty.
   * 
   * @return
   */
  public List<SPFeature> getFeatures() {
    if (features == null) {
      features = new ArrayList<>();
    }
    return features;
  }
  
  public void setFeatures(List<SPFeature> features) {
    this.features = features;
  }
  
  public int getAvailalbeMemberSubscriptions() {
    return availalbeMemberSubscriptions;
  }
  
  public void setAvailalbeMemberSubscriptions(int availalbeMemberSubscriptions) {
    this.availalbeMemberSubscriptions = availalbeMemberSubscriptions;
  }
  
  public int getAvailalbeAdminSubscriptions() {
    return availalbeAdminSubscriptions;
  }
  
  public void setAvailalbeAdminSubscriptions(int availalbeAdminSubscriptions) {
    this.availalbeAdminSubscriptions = availalbeAdminSubscriptions;
  }
  
  public long getNumberOfMembers() {
    return numberOfMembers;
  }
  
  public void setNumberOfMembers(long numberOfMembers) {
    this.numberOfMembers = numberOfMembers;
  }
  
  public int getNumActive() {
    return numActive;
  }
  
  public void setNumActive(int numActive) {
    this.numActive = numActive;
  }
  
  public int getNumDeleted() {
    return numDeleted;
  }
  
  public void setNumDeleted(int numDeleted) {
    this.numDeleted = numDeleted;
  }
  
  public String getPlanName() {
    return planName;
  }
  
  public void setPlanName(String planName) {
    this.planName = planName;
  }
  
  public void setBundle(boolean bundle) {
    this.bundle = bundle;
  }
  
  public boolean isBundle() {
    return bundle;
  }
  
  public void setUpdateDetails(List<AccountUpdateDetail> updateDetails) {
    this.updateDetails = updateDetails;
  }
  
  public List<AccountUpdateDetail> getUpdateDetails() {
    if (updateDetails == null) {
      updateDetails = new ArrayList<AccountUpdateDetail>();
    }
    return updateDetails;
  }
  
  public void setPlanAdminNextChargeAmount(BigDecimal planAdminNextChargeAmount) {
    this.planAdminNextChargeAmount = planAdminNextChargeAmount;
  }
  
  public BigDecimal getPlanAdminNextChargeAmount() {
    return planAdminNextChargeAmount;
  }
  
  public void setPlanMemberNextChargeAmount(BigDecimal planMemberNextChargeAmount) {
    this.planMemberNextChargeAmount = planMemberNextChargeAmount;
  }
  
  public BigDecimal getPlanMemberNextChargeAmount() {
    return planMemberNextChargeAmount;
  }
  
  public void setTagsKeywords(List<String> tagsKeywords) {
    this.tagsKeywords = tagsKeywords;
  }
  
  public List<String> getTagsKeywords() {
    return tagsKeywords;
  }
  
  /**
   * @param agreementTerm
   *          the agreementTerm to set
   */
  public void setAgreementTerm(int agreementTerm) {
    this.agreementTerm = agreementTerm;
  }
  
  /**
   * @return the agreementTerm
   */
  public int getAgreementTerm() {
    return agreementTerm;
  }
  
  public void setPlanInfo(Map<String, Object> planInfo) {
    this.planInfo = planInfo;
  }
  
  public Map<String, Object> getPlanInfo() {
    if(planInfo == null){
      planInfo = new HashMap<String, Object>();
    }
    return planInfo;
  }
  
  
  public void setRemainingChargableDays(long remainingChargableDays) {
    this.remainingChargableDays = remainingChargableDays;
  }
  
  public long getRemainingChargableDays() {
    return remainingChargableDays;
  }
}
