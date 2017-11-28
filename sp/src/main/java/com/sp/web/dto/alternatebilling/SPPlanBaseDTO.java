package com.sp.web.dto.alternatebilling;

import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

/**
 * SPPlanBaseDTO is the base class for the base plan. It contains the agreement term and agreement
 * date dto to be shown in the listing page.
 * 
 * @author pradeepruhil
 *
 */
public class SPPlanBaseDTO {
  
  private SPPlanType planType;
  
  private int agreementTerm;
  
  private String agreementEndDate;
  
  private boolean active;
  
  private BillingCycle billingCycle;
  
  public SPPlanBaseDTO(SPPlan spPlan) {
    BeanUtils.copyProperties(spPlan, this);
    if (spPlan.getAggreementEndDate() != null) {
      this.agreementEndDate = MessagesHelper
          .formatDate(spPlan.getAggreementEndDate().toLocalDate());
    }
  }
  
  public SPPlanType getPlanType() {
    return planType;
  }
  
  public void setPlanType(SPPlanType planType) {
    this.planType = planType;
  }
  
  public void setAgreementTerm(int agreementTerm) {
    this.agreementTerm = agreementTerm;
  }
  
  public int getAgreementTerm() {
    return agreementTerm;
  }
  
  public String getAgreementEndDate() {
    return agreementEndDate;
  }
  
  public void setAgreementEndDate(String agreementEndDate) {
    this.agreementEndDate = agreementEndDate;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public boolean isActive() {
    return active;
  }
 
  public void setBillingCycle(BillingCycle billingCycle) {
    this.billingCycle = billingCycle;
  }
  
  public BillingCycle getBillingCycle() {
    return billingCycle;
  }
}
