package com.sp.web.product;

import com.sp.web.Constants;
import com.sp.web.account.plan.PlanCommonHelper;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.utils.DateTimeUtil;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * PrimaryPlan helper class is the helper class for the primary plan. It calculates the price and
 * the next payment for the plan.
 * 
 * @author pradeepruhil
 *
 */
@Component("primaryPlanHelper")
public class PrimaryPlanHelper extends PlanCommonHelper {
  
  /** Initialize the logger. */
  private Logger log = Logger.getLogger(PrimaryPlanHelper.class);
  
  /**
   * Calculates the amount to charge for the given plan, purchasedMemberUnits, purchaseAdmin and
   * spPlan
   * 
   * @param account
   *          - account
   * @param product
   *          - product
   * @param promotion
   *          - promotion
   * @param purchaseMemberUnits
   *          - purchase units of members
   * @param purchasedAdmins
   *          purchased admin.
   * @return amount to charge
   */
  @Override
  public BigDecimal getChargeAmount(SPPlan spPlan, long purchaseMemberUnits,
      long purchasedAdmins) {
    
    BigDecimal unitMemberPrice = spPlan.getUnitMemberPrice();
    
    final BillingCycle billingCycle = Optional.ofNullable(spPlan.getBillingCycle())
        .orElseThrow(
            () -> new InvalidRequestException("billingCycle type not set for plan :"
                + spPlan.getName()));
    
    double multiplier = 0;
    // get the next recharge date
    LocalDateTime nextRechargeDate = Optional.ofNullable(
        DateTimeUtil.getLocalDateTime(spPlan.getNextPaymentDate())).orElse(LocalDateTime.now());
    LocalDateTime today = LocalDateTime.now();
    
    long days = ChronoUnit.DAYS.between(LocalDate.now(), nextRechargeDate.toLocalDate());
    
    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + unitMemberPrice + ": Duration :" + days);
    }
    if (spPlan.getPlanStatus() == PlanStatus.NEW) {
      multiplier = getBillingCycleMultiplier(spPlan, billingCycle);
    } else {
      multiplier = getBillingCycleMultiplier(billingCycle, multiplier, days);
    }
    
    BigDecimal totalChargePrice = new BigDecimal(0);
    
    if (log.isDebugEnabled()) {
      log.debug("PrimaryPlanHelper#getChargeAmount multiplier  :" + multiplier);
    }
    
    if (spPlan.isOverrideMemberPrice()) {
      totalChargePrice = spPlan.getOverrideMemberPrice().multiply(new BigDecimal(multiplier));
    } else {
      totalChargePrice = unitMemberPrice.multiply(new BigDecimal(purchaseMemberUnits)
          .multiply(new BigDecimal(multiplier)));
    }
    if (spPlan.getPlanStatus() == PlanStatus.NEW) {
      totalChargePrice = totalChargePrice.add(spPlan.getLicensePrice().multiply(
          new BigDecimal(multiplier)));
    }
    
    totalChargePrice = totalChargePrice.add(getAdminChargeAmount(spPlan, purchasedAdmins));
    
    if (log.isDebugEnabled()) {
      log.debug("[total Charge Amount : " + totalChargePrice);
    }
    return totalChargePrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
  
  /**
   * Calculates the amount to charge for the given plan, purchasedMemberUnits, purchaseAdmin and
   * spPlan
   * 
   * @param existingPlan
   *          - account
   * @param updatedPlan
   *          new updated plan.
   * @param purchaseMemberUnits
   *          - purchase units of members
   * @param purchasedAdmins
   *          purchased admin.
   */
  @Override
  public BigDecimal getEditAccountChargeAmount(SPPlan existingPlan, SPPlan updatedPlan,
      long purchaseMemberUnits, long purchasedAdmins,boolean newAccount) {
    
    BigDecimal unitMemberPrice = updatedPlan.getUnitMemberPrice();
    
    final BillingCycle billingCycle = Optional.ofNullable(existingPlan.getBillingCycle()).orElseThrow(
        () -> new InvalidRequestException("billingCycle type not set for plan :"
            + updatedPlan.getName()));
    
    double multiplier = 0;
    // get the next recharge date
    LocalDateTime nextRechargeDate = Optional.ofNullable(
        DateTimeUtil.getLocalDateTime(existingPlan.getNextPaymentDate())).orElse(LocalDateTime.now());
    long days = ChronoUnit.DAYS.between(LocalDate.now(), nextRechargeDate.toLocalDate());
    
    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + unitMemberPrice + ": Duration :" + days);
    }
    
    multiplier = getBillingCycleMultiplier(billingCycle, multiplier, days);
    BigDecimal totalChargePrice = new BigDecimal(0);
    if (updatedPlan.isOverrideMemberPrice()) {
      
      totalChargePrice = totalChargePrice.add(
          (updatedPlan.getOverrideMemberPrice().subtract(existingPlan.getPlanMemberNextChargeAmount())))
          .multiply(new BigDecimal(multiplier));
    } else {
      totalChargePrice = totalChargePrice.add(unitMemberPrice.multiply(new BigDecimal(
          purchaseMemberUnits).multiply(new BigDecimal(multiplier))));
    }
    /* Add the license for SPPlateform */
    if (newAccount) {
      totalChargePrice = totalChargePrice.add(existingPlan.getLicensePrice().multiply(
          new BigDecimal(multiplier)));
    }
    if (updatedPlan.isOverrideAdminPrice()) {
      totalChargePrice = totalChargePrice.add((updatedPlan.getOverrideAdminPrice().subtract(existingPlan
          .getPlanAdminNextChargeAmount())).multiply(new BigDecimal(multiplier)));
    } else {
      totalChargePrice = totalChargePrice.add(updatedPlan.getUnitAdminPrice().multiply(
          new BigDecimal(purchasedAdmins).multiply(new BigDecimal(multiplier))));
    }
    
    return totalChargePrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
  
  /**
   * Updates the next payment date and next payment amount.
   * 
   * @param account
   *          - account to update S
   */
  public BigDecimal getNextPayment(SPPlan spPlan, long purchaseMemberUnits,
      long purchasedAdmins) {
    
    final BillingCycle billingCycle = Optional.ofNullable(spPlan.getBillingCycle())
        .orElseThrow(
            () -> new InvalidRequestException("billingCycle type not set for plan :"
                + spPlan.getName()));
    
    BigDecimal unitMemberPrice = spPlan.getUnitMemberPrice();
    int daysToCharge = (int) ChronoUnit.DAYS.between(
        DateTimeUtil.getLocalDate(spPlan.getNextPaymentDate()), spPlan.getAggreementEndDate());
    
    int billingCycleDays = getBillingCycleMultiplier(spPlan, billingCycle) * Constants.DAYS_OF_MONTHLY_BILLING;
    double multiplier = 0.0;
    if (daysToCharge < billingCycleDays) {
      multiplier = getBillingCycleMultiplier(billingCycle, 0, daysToCharge);
    } else {
      multiplier = getBillingCycleMultiplier(billingCycle, 0, billingCycleDays);
    }
    
    
    BigDecimal totalChargePrice = new BigDecimal(0);
    BigDecimal memberChargeAmount = new BigDecimal(0);
    if (spPlan.isOverrideMemberPrice()) {
      spPlan.setPlanMemberNextChargeAmount(spPlan.getOverrideMemberPrice());
      memberChargeAmount = memberChargeAmount.add(spPlan.getOverrideMemberPrice().multiply(
          new BigDecimal(multiplier)));
    } else {
      spPlan.setPlanMemberNextChargeAmount(unitMemberPrice.multiply(new BigDecimal(
          purchaseMemberUnits)));
      memberChargeAmount = unitMemberPrice.multiply(new BigDecimal(purchaseMemberUnits)
          .multiply(new BigDecimal(multiplier)));
    }
    totalChargePrice = totalChargePrice.add(memberChargeAmount);
    
    /* Add the license for SPPlateform */
    totalChargePrice = totalChargePrice.add(spPlan.getLicensePrice().multiply(
        new BigDecimal(multiplier)));
    
    /* check if admins are present or not */
    BigDecimal adminChargeAmount = getNextAdminCharge(spPlan, purchasedAdmins, multiplier);
    totalChargePrice = totalChargePrice.add(adminChargeAmount);
    
    return totalChargePrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
  
  /**
   * @see com.sp.web.account.plan.PlanHelper#getProratedChargeAmount(com.sp.web.model.Account,
   *      com.sp.web.model.account.SPPlan, boolean, boolean)
   */
  @Override
  public BigDecimal getProratedChargeAmount(Account account, SPPlan purchasedPlan) {
    BigDecimal unitMemberPrice = purchasedPlan.getUnitMemberPrice();
    
    final BillingCycle billingCycle = Optional.ofNullable(purchasedPlan.getBillingCycle()).orElseThrow(
        () -> new InvalidRequestException("billingCycle type not set for plan :"
            + purchasedPlan.getName()));
    
    SPPlan previousPlan = account.getSpPlanMap().get(purchasedPlan.getPlanType());
    
    double multiplier = 0;
    // get the next recharge date
    LocalDateTime nextRechargeDate = Optional.ofNullable(
        DateTimeUtil.getLocalDateTime(previousPlan.getNextPaymentDate())).orElse(LocalDateTime.now());
    
    long days = ChronoUnit.DAYS.between(LocalDate.now(), nextRechargeDate.toLocalDate());
    
    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + unitMemberPrice + ": Duration :" + days);
    }
    
    multiplier = getBillingCycleMultiplier(billingCycle, multiplier, days);
    BigDecimal totalMemberPrice = new BigDecimal(0);
    if (purchasedPlan.isOverrideMemberPrice()) {
      totalMemberPrice = (purchasedPlan.getOverrideMemberPrice().subtract(previousPlan
          .getPlanMemberNextChargeAmount())).multiply(new BigDecimal(multiplier));
    }
    if (purchasedPlan.isOverrideAdminPrice()) {
      totalMemberPrice = totalMemberPrice.add((purchasedPlan.getOverrideAdminPrice()
          .subtract(previousPlan.getPlanAdminNextChargeAmount())).multiply(new BigDecimal(
          multiplier)));
    }
    return totalMemberPrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
  
}
