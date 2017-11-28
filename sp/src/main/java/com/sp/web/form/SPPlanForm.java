package com.sp.web.form;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.BillingCycleType;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * SPPlanForm contains the plan form.
 * 
 * @author pradeepruhil
 *
 */
public class SPPlanForm {
  
  private Set<SPFeature> features;
  
  private String comment;
  
  private String name;
  
  private String spPlanLicensePrice;
  
  private String numAdmin;
  
  private String unitAdminPrice;
  
  private String overrideAdminPrice;
  
  private String isOverrideAdminAmount;
  
  private String isBundle;
  
  private String isActive;
  
  private String numCandidates;
  
  private String pricePerCandidate;
  
  private String overrideCandidatePrice;
  
  private String isOverrideAmount;
  
  private SPPlanType planType;
  
  private String aggreementTerm;
  
  private double creditAmount;
  
  /** if this is a trial signup. */
  private String isTrial;
  
  private String paymentAggrement;
  
  private BillingCycleType billingCycleType;
  
  private int noOfMonths;
  
  private List<String> tags;
  /**
   * Payment details.
   */
  
  private String nameOnCard;
  
  private String cardNumber;
  
  private String month;
  
  private String year;
  
  private String cvv;
  
  private String country;
  
  private String zip;
  
  private String cnr;
  
  private PaymentType paymentType;
  
  private boolean newPlan;
  
  public Set<SPFeature> getFeatures() {
    return features;
  }
  
  public void setFeatures(Set<SPFeature> features) {
    this.features = features;
  }
  
  public String getSpPlanLicensePrice() {
    return spPlanLicensePrice;
  }
  
  public void setSpPlanLicensePrice(String spPlanLicensePrice) {
    this.spPlanLicensePrice = spPlanLicensePrice;
  }
  
  public String getNumAdmin() {
    return numAdmin;
  }
  
  public void setNumAdmin(String numAdmin) {
    this.numAdmin = numAdmin;
  }
  
  public String getUnitAdminPrice() {
    if (StringUtils.isEmpty(unitAdminPrice)) {
      this.unitAdminPrice = "0";
    }
    return unitAdminPrice;
  }
  
  public void setUnitAdminPrice(String unitAdminPrice) {
    this.unitAdminPrice = unitAdminPrice;
  }
  
  public String getOverrideAdminPrice() {
    if (StringUtils.isEmpty(overrideAdminPrice)) {
      this.overrideAdminPrice = "0";
    }
    return overrideAdminPrice;
  }
  
  public void setOverrideAdminPrice(String overrideAdminPrice) {
    this.overrideAdminPrice = overrideAdminPrice;
  }
  
  public String isOverrideAdminAmount() {
    return isOverrideAdminAmount;
  }
  
  public void setOverrideAdminAmount(String isOverrideAdminAmount) {
    this.isOverrideAdminAmount = isOverrideAdminAmount;
  }
  
  public String isBundle() {
    return isBundle;
  }
  
  public void setBundle(String isBundle) {
    this.isBundle = isBundle;
  }
  
  public String getNumCandidates() {
    if (StringUtils.isEmpty(numCandidates)) {
      this.numCandidates = "0";
    }
    return numCandidates;
  }
  
  public void setNumCandidates(String numCandidates) {
    this.numCandidates = numCandidates;
  }
  
  public String getPricePerCandidate() {
    if (StringUtils.isEmpty(pricePerCandidate)) {
      this.pricePerCandidate = "0";
    }
    return pricePerCandidate;
  }
  
  public void setPricePerCandidate(String pricePerCandidate) {
    this.pricePerCandidate = pricePerCandidate;
  }
  
  /**
   * @return the isBundle
   */
  public String getIsBundle() {
    return isBundle;
  }
  
  /**
   * @return the isOverrideAdminAmount
   */
  public String getIsOverrideAdminAmount() {
    return isOverrideAdminAmount;
  }
  
  /**
   * @return the isOverrideAmount
   */
  public String getIsOverrideAmount() {
    return isOverrideAmount;
  }
  
  public void setOverrideAmount(String isOverrideAmount) {
    this.isOverrideAmount = isOverrideAmount;
  }
  
  public SPPlanType getPlanType() {
    return planType;
  }
  
  public void setPlanType(SPPlanType planType) {
    this.planType = planType;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setOverrideCandidatePrice(String overrideCandidatePrice) {
    this.overrideCandidatePrice = overrideCandidatePrice;
  }
  
  public String getOverrideCandidatePrice() {
    if (StringUtils.isEmpty(overrideCandidatePrice)) {
      this.overrideCandidatePrice = "0";
    }
    return overrideCandidatePrice;
  }
  
  /**
   * @return the SPPlan for the plan form.
   */
  public SPPlan getSPPlan() {
    
    if (Integer.valueOf(getNumCandidates()) < 0) {
      throw new InvalidRequestException(
          "Member to add must be greater than zero or equal !!! for planType: " + getPlanType());
    }
    SPPlan spPlan = new SPPlan();
    // spPlan.setFeatures(getFeatures());
    if (StringUtils.isNotBlank(getSpPlanLicensePrice())) {
      spPlan.setLicensePrice(new BigDecimal(getSpPlanLicensePrice()));
    }
    
    spPlan.setName(getName());
    spPlan.setOverrideAdminPrice(new BigDecimal(getOverrideAdminPrice()));
    spPlan.setOverrideMemberPrice(new BigDecimal(getOverrideCandidatePrice()));
    spPlan.setPlanType(getPlanType());
    spPlan.setNumAdmin(Integer.valueOf(getNumAdmin()));
    spPlan.setNumMember(Integer.valueOf(getNumCandidates()));
    spPlan.setUnitAdminPrice(new BigDecimal(getUnitAdminPrice()));
    spPlan.setUnitMemberPrice(new BigDecimal(getPricePerCandidate()));
    spPlan.setBundle(Boolean.valueOf(isBundle()));
    spPlan.setFeatures(getFeatures());
    spPlan.setActive(Boolean.valueOf(getIsActive()));
    if (StringUtils.isNotBlank(getAggreementTerm())) {
      spPlan.setAgreementTerm((Integer.valueOf(getAggreementTerm())));
    }
    spPlan.setTagsKeywords(getTags());
    spPlan.setBillingCycle(new BillingCycle(getBillingCycleType(), getNoOfMonths()));
    spPlan.setCreditBalance(getCreditAmount());
    return spPlan;
  }
  
  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }
  
  public String getIsActive() {
    return isActive;
  }
  
  /**
   * @param aggreementTerm
   *          the aggreementTerm to set
   */
  public void setAggreementTerm(String aggreementTerm) {
    this.aggreementTerm = aggreementTerm;
  }
  
  /**
   * @return the aggreementTerm
   */
  public String getAggreementTerm() {
    return aggreementTerm;
  }
  
  public double getChargeAmount() {
    return creditAmount;
  }
  
  public void setChargeAmount(double chargeAmount) {
    this.creditAmount = chargeAmount;
  }
  
  public String getNameOnCard() {
    return nameOnCard;
  }
  
  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }
  
  public String getCardNumber() {
    return cardNumber;
  }
  
  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }
  
  public String getMonth() {
    return month;
  }
  
  public void setMonth(String month) {
    this.month = month;
  }
  
  public String getYear() {
    return year;
  }
  
  public void setYear(String year) {
    this.year = year;
  }
  
  public String getCvv() {
    return cvv;
  }
  
  public void setCvv(String cvv) {
    this.cvv = cvv;
  }
  
  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public String getZip() {
    return zip;
  }
  
  public void setZip(String zip) {
    this.zip = zip;
  }
  
  /**
   * @return the isTrial
   */
  public String getIsTrial() {
    return isTrial;
  }
  
  public void setIsTrial(String isTrial) {
    this.isTrial = isTrial;
  }
  
  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  public PaymentType getPaymentType() {
    return paymentType;
  }
  
  public void setCnr(String cnr) {
    this.cnr = cnr;
  }
  
  public String getCnr() {
    return cnr;
  }
  
  public void setPaymentAggrement(String paymentAggrement) {
    this.paymentAggrement = paymentAggrement;
  }
  
  public String getPaymentAggrement() {
    return paymentAggrement;
  }
  
  public double getCreditAmount() {
    return creditAmount;
  }
  
  public void setCreditAmount(double creditAmount) {
    this.creditAmount = creditAmount;
  }
  
  public BillingCycleType getBillingCycleType() {
    return billingCycleType;
  }
  
  public void setBillingCycleType(BillingCycleType billingCycleType) {
    this.billingCycleType = billingCycleType;
  }
  
  public void setNoOfMonths(int noOfMonths) {
    this.noOfMonths = noOfMonths;
  }
  
  public int getNoOfMonths() {
    return noOfMonths;
  }
  
  public List<String> getTags() {
    return tags;
  }
  
  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  /**
   * @return a new payment instrument model object from the form data.
   */
  public PaymentInstrument createPaymentInstrument() {
    PaymentInstrument paymentInstrument;
    if (paymentType == null || paymentType == PaymentType.CREDIT_CARD) {
      paymentInstrument = new PaymentInstrument();
      paymentInstrument.setCardNumber(cardNumber);
      paymentInstrument.setCountry(country);
      paymentInstrument.setCvv(cvv);
      paymentInstrument.setMonth(month);
      paymentInstrument.setNameOnCard(nameOnCard);
      paymentInstrument.setYear(year);
      paymentInstrument.setZip(zip);
      
    } else {
      CreditNotePaymentInstrument creditNotePI = new CreditNotePaymentInstrument();
      creditNotePI.setPaymentType(paymentType);
      // creditNotePI.setCreditBalance(creditAmount);
      creditNotePI.setFinancialReferenceNo(cnr);
      paymentInstrument = creditNotePI;
    }
    paymentInstrument.setPaymentType(paymentType);
    paymentInstrument.setComment(comment);
    return paymentInstrument;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setNewPlan(boolean newPlan) {
    this.newPlan = newPlan;
  }
  
  public boolean isNewPlan() {
    return newPlan;
  }
  
  @Override
  public String toString() {
    return "SPPlanForm [features=" + features + ", comment=" + comment + ", name=" + name
        + ", spPlanLicensePrice=" + spPlanLicensePrice + ", numAdmin=" + numAdmin
        + ", unitAdminPrice=" + unitAdminPrice + ", overrideAdminPrice=" + overrideAdminPrice
        + ", isOverrideAdminAmount=" + isOverrideAdminAmount + ", isBundle=" + isBundle
        + ", isActive=" + isActive + ", numCandidates=" + numCandidates + ", pricePerCandidate="
        + pricePerCandidate + ", overrideCandidatePrice=" + overrideCandidatePrice
        + ", isOverrideAmount=" + isOverrideAmount + ", planType=" + planType + ", aggreementTerm="
        + aggreementTerm + ", creditAmount=" + creditAmount + ", isTrial=" + isTrial
        + ", paymentAggrement=" + paymentAggrement + ", billingCycleType=" + billingCycleType
        + ", noOfMonths=" + noOfMonths + ", tags=" + tags + ", nameOnCard=" + nameOnCard
        + ", cardNumber=" + cardNumber + ", month=" + month + ", year=" + year + ", cvv=" + cvv
        + ", country=" + country + ", zip=" + zip + ", cnr=" + cnr + ", paymentType=" + paymentType
        + "]";
  }
  
}
