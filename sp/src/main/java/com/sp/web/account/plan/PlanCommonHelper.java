package com.sp.web.account.plan;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.utils.DateTimeUtil;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * PlanCommonHelper abstract class gives the high level functionaltiy for the plan.
 * 
 * @author pradeepruhil
 *
 */
public abstract class PlanCommonHelper implements PlanHelper {
  
  private static final Logger log = Logger.getLogger(PlanCommonHelper.class);
  
  /**
   * Calculates the amount to charge for the given plan, purchasedMemberUnits, purchaseAdmin and
   * spPlan
   * 
   * @param account
   *          - account
   * @param spPlan
   *          SPPlan
   * @param purchasedAdmins
   *          purchased admin.
   * @return amount to charge
   */
  public BigDecimal getAdminChargeAmount(SPPlan spPlan, long purchasedAdmins) {
    
    final BillingCycle billingCycle = Optional.ofNullable(spPlan.getBillingCycle())
        .orElseThrow(
            () -> new InvalidRequestException("billingCycle type not set for plan :"
                + spPlan.getName()));
    
    double multiplier = 0;
    // get the next recharge date
    LocalDateTime nextRechargeDate = Optional.ofNullable(
        DateTimeUtil.getLocalDateTime(spPlan.getNextPaymentDate())).orElse(LocalDateTime.now());
    
    long days = ChronoUnit.DAYS.between(LocalDate.now(), nextRechargeDate.toLocalDate());
    
    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + spPlan.getUnitAdminPrice() + ": Duration :" + days);
    }
    
    if (spPlan.getPlanStatus() == PlanStatus.NEW) {
      multiplier = getBillingCycleMultiplier(spPlan, billingCycle);
    } else {
      multiplier = getBillingCycleMultiplier(billingCycle, multiplier, days);
    }
    
    BigDecimal totalMemberPrice = new BigDecimal(0);
    
    if (log.isDebugEnabled()) {
      log.debug("PlanCommonHelper#getAdminChargeAmount multiplier  :" + multiplier);
    }
    /* check if admins are present or not */
    
    if (purchasedAdmins > 0) {
      
      if (spPlan.isOverrideAdminPrice()) {
        totalMemberPrice = totalMemberPrice.add(spPlan.getOverrideAdminPrice()
            .multiply(new BigDecimal(multiplier)));
            
      } else {
        totalMemberPrice = totalMemberPrice.add((spPlan.getUnitAdminPrice())
            .multiply(new BigDecimal(purchasedAdmins)).multiply(new BigDecimal(multiplier)));
      }
    }
    
    if (log.isDebugEnabled()) {
      log.debug("PlanCommonHelper#getAdminChargeAmount charge amount  :" + totalMemberPrice
          + ", Plna " + spPlan.getPlanType());
    }

    return totalMemberPrice;
  }
  
  /**
   * getBillingCycleMultiplier method will add the billing cycle and wil return the multiplier for
   * the prorated price during addition of member or candidate in between the month.
   * 
   * @param billingCycle
   *          billing cycle for which multiplier is to be found.
   * @param multiplier
   *          to be multiplied
   * @param days
   *          to find the mulitplier
   * @return the multiplier.
   */
  protected double getBillingCycleMultiplier(final BillingCycle billingCycle, double multiplier,
      long days) {
    switch (billingCycle.getBillingCycleType()) {
    case Monthly:
      if (days <= 0) {
        multiplier = 1;
      } else {
        // prorate the charge
        multiplier = (double) (days) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    case Quaterly:
      if (days <= 0) {
        multiplier = 3;
      } else {
        // prorate the charge
        multiplier = (double) (days) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    case SemiAnnually:
      if (days <= 0) {
        multiplier = 6;
      } else {
        // prorate the charge
        multiplier = (double) (days) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    case Anually:
      if (days <= 0) {
        multiplier = 12;
      } else {
        // prorate the charge
        multiplier = (double) (days) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    case Custom:
      if (days <= 0) {
        multiplier = billingCycle.getNoOfMonths();
      } else {
        // prorate the charge
        multiplier = (double) (days) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    default:
      throw new InvalidRequestException("Do not know how to process :"
          + billingCycle.getBillingCycleType());
    }
    return multiplier;
  }
  
  /**
   * <code>getNextAdminCharge</code> method will return the admin price for the payment for the next
   * admin cycle.
   * 
   * @param spPlan
   *          for which next admin charge is to be found.
   * @param purchasedAdmins
   *          not of admins purchased.
   * @param multiplier
   *          for the next admin.
   * @param totalChargePrice
   *          of the next admin.
   * @return the big decimal.
   */
  protected BigDecimal getNextAdminCharge(SPPlan spPlan, long purchasedAdmins, double multiplier) {
    BigDecimal adminChargeAmount = new BigDecimal(0);
    if (purchasedAdmins > 0) {
      if (spPlan.isOverrideAdminPrice()) {
        spPlan.setPlanAdminNextChargeAmount(spPlan.getOverrideAdminPrice());
        adminChargeAmount = adminChargeAmount.add(spPlan.getOverrideAdminPrice().multiply(new BigDecimal(multiplier)));
      } else {
        spPlan.setPlanAdminNextChargeAmount(adminChargeAmount.add(spPlan.getUnitAdminPrice()
            .multiply(new BigDecimal(purchasedAdmins))));
        adminChargeAmount = adminChargeAmount.add(spPlan.getUnitAdminPrice()
            .multiply(new BigDecimal(purchasedAdmins)).multiply(new BigDecimal(multiplier)));
      }
    }
    return adminChargeAmount;
  }
  
  /**
   * getBillingCycleMultiper method will return the multiplier for the billing cycle.
   * @param spPlan for which multiplier is to be returned.
   * @param billingCycle for the account.
   * @return the multiplier.
   */
  protected int getBillingCycleMultiplier(SPPlan spPlan, final BillingCycle billingCycle) {
    int multiplier = 0;
    
    // calculating the next recharge amount
    switch (billingCycle.getBillingCycleType()) {
    case Monthly:
      multiplier = 1;
      break;
    case Quaterly:
      multiplier = 3;
      break;
    case SemiAnnually:
      multiplier = 6;
      break;
    case Anually:
      multiplier = 12;
      break;
    case Custom:
      multiplier = spPlan.getBillingCycle().getNoOfMonths();
      break;
    default:
      throw new InvalidRequestException("Don't know how to process :"
          + spPlan.getBillingCycle().getBillingCycleType());
    }
    if (log.isDebugEnabled()) {
      log.debug("IntelligenentHiringPlan#getBillingCycleMultiplier multiplier  :" + multiplier);
    }
    return multiplier;
  }
  
}
