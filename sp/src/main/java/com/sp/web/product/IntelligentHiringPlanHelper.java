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
 * IntelligentHiringPlanHelper will give the charge plan for the intelligent hiring.
 * 
 * @author pradeepruhil
 *
 */
@Component("intelligenthiringPlanHelper")
public class IntelligentHiringPlanHelper extends PlanCommonHelper {
  
  /** Initialize the logger. */
  private Logger log = Logger.getLogger(IntelligentHiringPlanHelper.class);
  
  /**
   * Calculates the amount to charge for the given plan, purchasedMemberUnits, purchaseAdmin and
   * spPlan
   * 
   * @param account
   *          - account
   * @param spPlan
   *          spPlan
   * @param purchaseMemberUnits
   *          - purchase units of members
   * @param purchasedAdmins
   *          purchased admin.
   * @return amount to charge
   */
  @Override
  public BigDecimal getChargeAmount(SPPlan spPlan, long purchaseMemberUnits, long purchasedAdmins) {
    
    if (log.isDebugEnabled()) {
      log.debug("SP Plan " + spPlan + ", purchageAdmins" + purchasedAdmins
          + ", purchased Members  " + purchaseMemberUnits);
    }
    BigDecimal unitMemberPrice = spPlan.getUnitMemberPrice();
    
    BigDecimal totalChargePrice = new BigDecimal(0);
    if (spPlan.isOverrideMemberPrice()) {
      totalChargePrice = spPlan.getOverrideMemberPrice();
    } else {
      totalChargePrice = unitMemberPrice.multiply(new BigDecimal(purchaseMemberUnits));
    }
    if (spPlan.getPlanStatus() == PlanStatus.NEW) {
      totalChargePrice = totalChargePrice.add(spPlan.getLicensePrice().multiply(
          new BigDecimal(getBillingCycleMultiplier(spPlan, spPlan.getBillingCycle()))));
    }
    
    totalChargePrice = totalChargePrice.add(getAdminChargeAmount(spPlan, purchasedAdmins));
    
    if (log.isDebugEnabled()) {
      log.debug("total Charge Amount : " + totalChargePrice);
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
  public BigDecimal getNextPayment(SPPlan spPlan, long purchaseMemberUnits, long purchasedAdmins) {
    
    final BillingCycle billingCycle = Optional.ofNullable(spPlan.getBillingCycle())
        .orElseThrow(
            () -> new InvalidRequestException("billingCycle type not set for plan :"
                + spPlan.getName()));
    int daysToCharge = (int) ChronoUnit.DAYS.between(
        DateTimeUtil.getLocalDate(spPlan.getNextPaymentDate()), spPlan.getAggreementEndDate());
    
    int billingCycleDays = getBillingCycleMultiplier(spPlan, billingCycle)
        * Constants.DAYS_OF_MONTHLY_BILLING;
    double multiplier = 0.0;
    if (daysToCharge < billingCycleDays) {
      multiplier = getBillingCycleMultiplier(billingCycle, 0, daysToCharge);
    } else {
      multiplier = getBillingCycleMultiplier(billingCycle, 0, billingCycleDays);
    }
    
    BigDecimal totalChargePrice = new BigDecimal(0);
    
    /* Add the license for SPPlateform */
    totalChargePrice = totalChargePrice.add(spPlan.getLicensePrice().multiply(
        new BigDecimal(multiplier)));
    
    /* check if admins are present or not */
    
    BigDecimal nextAdminCharge = getNextAdminCharge(spPlan, purchasedAdmins, multiplier);
    totalChargePrice = totalChargePrice.add(nextAdminCharge);
    return totalChargePrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
  
  /**
   * @see com.sp.web.account.plan.PlanHelper#getProratedChargeAmount(com.sp.web.model.Account,
   *      com.sp.web.model.account.SPPlan)
   */
  @Override
  public BigDecimal getProratedChargeAmount(Account account, SPPlan spPlan) {
    
    if (log.isDebugEnabled()) {
      log.debug("SP Plan " + spPlan);
    }
    
    BigDecimal totalChargePrice = new BigDecimal(0);
    
    final BillingCycle billingCycle = Optional.ofNullable(spPlan.getBillingCycle())
        .orElseThrow(
            () -> new InvalidRequestException("billingCycle type not set for plan :"
                + spPlan.getName()));
    
    SPPlan previousPlan = account.getSpPlanMap().get(spPlan.getPlanType());
    
    double multiplier = 0;
    // get the next recharge date
    LocalDateTime nextRechargeDate = Optional.ofNullable(
        DateTimeUtil.getLocalDateTime(previousPlan.getNextPaymentDate())).orElse(
        LocalDateTime.now());
    
    long days = ChronoUnit.DAYS.between(LocalDate.now(), nextRechargeDate.toLocalDate());
    
    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + spPlan.getUnitAdminPrice() + ": Duration :" + days);
    }
    
    multiplier = getBillingCycleMultiplier(billingCycle, multiplier, days);
    
    if (spPlan.isOverrideAdminPrice()) {
      totalChargePrice = totalChargePrice.add((spPlan.getOverrideAdminPrice().subtract(previousPlan
          .getPlanAdminNextChargeAmount())).multiply(new BigDecimal(multiplier)));
    }
    
    if (log.isDebugEnabled()) {
      log.debug("total Charge Amount : " + totalChargePrice);
    }
    return totalChargePrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
  
  /**
   * @see com.sp.web.account.plan.PlanHelper#getEditAccountChargeAmount(com.sp.web.model.Account,
   *      com.sp.web.model.account.SPPlan, long, long)
   */
  @Override
  public BigDecimal getEditAccountChargeAmount(SPPlan existingPlan, SPPlan purchasedPlan,
      long purchaseMemberUnits, long purchasedAdmins, boolean newAccount) {
    final BillingCycle billingCycle = Optional.ofNullable(existingPlan.getBillingCycle())
        .orElseThrow(
            () -> new InvalidRequestException("billingCycle type not set for plan :"
                + purchasedPlan.getName()));
    
    double multiplier = 0;
    // get the next recharge date
    LocalDateTime nextRechargeDate = Optional.ofNullable(
        DateTimeUtil.getLocalDateTime(existingPlan.getNextPaymentDate())).orElse(
        LocalDateTime.now());
    
    long days = ChronoUnit.DAYS.between(LocalDate.now(), nextRechargeDate.toLocalDate());
    
    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + purchasedPlan.getUnitMemberPrice() + ": Duration :" + days);
    }
    
    multiplier = getBillingCycleMultiplier(billingCycle, multiplier, days);
    BigDecimal totalChargePrice = new BigDecimal(0);
    
    /* Add the license for SPPlateform */
    if (newAccount) {
      totalChargePrice = totalChargePrice.add(existingPlan.getLicensePrice().multiply(
          new BigDecimal(multiplier)));
      
      if (purchasedPlan.isOverrideMemberPrice()) {
        totalChargePrice = totalChargePrice.add(purchasedPlan.getOverrideMemberPrice());
      } else {
        totalChargePrice = totalChargePrice.add(purchasedPlan.getUnitMemberPrice().multiply(
            new BigDecimal(purchaseMemberUnits)));
      }
      
    }
    if (purchasedPlan.isOverrideAdminPrice()) {
      if (newAccount) {
        totalChargePrice = totalChargePrice.add((purchasedPlan.getOverrideAdminPrice())
            .multiply(new BigDecimal(multiplier)));
      } else {
        totalChargePrice = totalChargePrice.add((purchasedPlan.getOverrideAdminPrice()
            .subtract(existingPlan.getPlanAdminNextChargeAmount())).multiply(new BigDecimal(
            multiplier)));
      }
      
    } else {
      totalChargePrice = totalChargePrice.add(purchasedPlan.getUnitAdminPrice().multiply(
          new BigDecimal(purchasedAdmins).multiply(new BigDecimal(multiplier))));
    }
    
    return totalChargePrice.setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE)
        .setScale(Constants.PRICE_PRECISION);
  }
}
