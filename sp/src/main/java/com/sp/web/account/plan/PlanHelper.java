package com.sp.web.account.plan;

import com.sp.web.model.Account;
import com.sp.web.model.account.SPPlan;

import java.math.BigDecimal;

/**
 * @author pruhil
 * 
 *         This is the helper interface for the various product plans related functionality like
 *         price calculations etc.
 */
public interface PlanHelper {
  
  /**
   * Get the amount to charge for the given account.
   * 
   * @param spPlan
   *          - spPlan
   * @param purchaseMemberUnits
   *          - no of members purchases units.
   * @param purchasedAdmins
   *          no of admin purchases
   * @return the amount to charge
   */
  BigDecimal getChargeAmount(SPPlan spPlan, long purchaseMemberUnits,
      long purchasedAdmins);
  
  BigDecimal getNextPayment(SPPlan spPlan, long purchaseMemberUnits,
      long purchasedAdmins);

  /**
   * getCharge method will give the pro rated charge amount for the user when the 
   * @param account for which charge amount is to be find.
   * @param spPlan plan for which prorated charge is to be find.
   * @return the prorate charge for the spplan.
   */
  BigDecimal getProratedChargeAmount(Account account, SPPlan spPlan);

  /**
   * getEditAccountChargeAmount will give the edit account charge amount of the plan.
   * @param account for which charge amount is to be found.
   * @param spPlan plan.
   * @param purchaseMemberUnits no of members units purchased
   * @param purchasedAdmins no of admin units purchased.
   * @param newAccount flag to tell new plan is added in the edit account
   * @return the charge amount.
   */
  BigDecimal getEditAccountChargeAmount(SPPlan existingPlan, SPPlan purchasedPlan, long purchaseMemberUnits,
      long purchasedAdmins, boolean newAccount);
  
}
