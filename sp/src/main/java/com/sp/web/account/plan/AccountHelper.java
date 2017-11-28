package com.sp.web.account.plan;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.account.AccountUpdateDetail;
import com.sp.web.model.account.AccountUpdateDetail.AccountUpdateType;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.DateTimeUtil;
import com.sp.web.utils.GenericUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <code>AccountHelper</code> is the helper class for the account. It will provides method to update
 * the account next payment, charge amount and other attribtutes.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class AccountHelper {
  
  private static final String PLAN_HELPER_SUFFIX = "PlanHelper";
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  UserRepository userReposiotry;
  
  /**
   * Get the amount to charge for the given account.
   * 
   * @param account
   *          - account
   * @param spPLans
   *          list of spPlans
   * @return the amount to charge
   */
  public Double getChargeAmount(Account account, List<SPPlan> spPLans) {
    
    BigDecimal totalChargeAmount = new BigDecimal(0.0);
    
    for (SPPlan spPlan : spPLans) {
      PlanHelper planHelper = getPlanHelper(spPlan);
      BigDecimal planChargeAmount = planHelper.getChargeAmount(spPlan, spPlan.getNumMember(),
          spPlan.getNumAdmin());
      totalChargeAmount = totalChargeAmount.add(planChargeAmount);
    }
    
    return totalChargeAmount.doubleValue();
  }
  
  /**
   * Get the amount to charge for the given account.
   * 
   * @param account
   *          - account
   * @return the amount to charge
   */
  public Double getChargeAmount(Account account) {
    
    BigDecimal totalChargeAmount = new BigDecimal(0.0);
    for (Map.Entry<SPPlanType, SPPlan> spPlan : account.getSpPlanMap().entrySet()) {
      PlanHelper planHelper = getPlanHelper(spPlan.getValue());
      BigDecimal planChargeAmount = planHelper.getChargeAmount(spPlan.getValue(), spPlan.getValue()
          .getNumMember(), spPlan.getValue().getNumAdmin());
      totalChargeAmount = totalChargeAmount.add(planChargeAmount);
    }
    
    return totalChargeAmount.doubleValue();
  }
  
  public Double getChargeAmount(SPPlan spPlan) {
    
    PlanHelper planHelper = getPlanHelper(spPlan);
    BigDecimal planChargeAmount = planHelper.getChargeAmount(spPlan, spPlan.getNumMember(),
        spPlan.getNumAdmin());
    return planChargeAmount.doubleValue();
  }
  
  /**
   * Get the amount to charge for the given account.
   * 
   * @param account
   *          - account
   * @param spPlan
   *          SPPlan for which to find the charge amount.
   * @param purchaseMemberUnits
   *          no of members units which are puchased.
   * @param purchasedAdmins
   *          no of admins purchased.
   * @return the amount to charge
   */
  public Double getChargeAmount(SPPlan spPlan, long purchaseMemberUnits, long purchasedAdmins) {
    
    PlanHelper planHelper = getPlanHelper(spPlan);
    BigDecimal planChargeAmount = planHelper.getChargeAmount(spPlan, spPlan.getNumMember(),
        spPlan.getNumAdmin());
    
    return planChargeAmount.doubleValue();
  }
  
  /**
   * Get the amount to charge for the given account.
   * 
   * @param account
   *          - account
   * @param purchasedPlan
   *          SPPlan for which to find the charge amount.
   * @param purchasedSpPlan
   * @param purchaseMemberUnits
   *          no of members units which are puchased.
   * @param purchasedAdmins
   *          no of admins purchased.
   * @return the amount to charge
   */
  public Double getEditChargeAmount(SPPlan existingPlan, SPPlan purchasedSpPlan,
      long purchaseMemberUnits, long purchasedAdmins, boolean newPlan) {
    
    PlanHelper planHelper = getPlanHelper(existingPlan);
    purchasedSpPlan.setNextPaymentDate(existingPlan.getNextPaymentDate());
    purchasedSpPlan.setBillingCycle(existingPlan.getBillingCycle());
    purchasedSpPlan.setLicensePrice(existingPlan.getLicensePrice());
    purchasedSpPlan.setPlanStatus(existingPlan.getPlanStatus());
    BigDecimal planChargeAmount = planHelper.getEditAccountChargeAmount(existingPlan,
        purchasedSpPlan, purchasedSpPlan.getNumMember(), purchasedSpPlan.getNumAdmin(), newPlan);
    return planChargeAmount.doubleValue();
  }
  
  /**
   * Get the prorated charge amount to charge for the given account.
   * 
   * @param account
   *          - account
   * @param purchasedPlan
   *          SPPlan for which to find the charge amount.
   * @return the amount to charge
   */
  public Double getProratedChargeAmount(Account account, SPPlan purchasedPlan) {
    
    PlanHelper planHelper = getPlanHelper(purchasedPlan);
    BigDecimal planChargeAmount = planHelper.getProratedChargeAmount(account, purchasedPlan);
    return planChargeAmount.doubleValue();
  }
  
  /**
   * updateSPPlan method will update the plan with new plan value.
   * 
   * @param spPlan
   *          is the existing spplan
   * @param newPlan
   *          to be used.
   */
  public void updateSPPlan(Account account, SPPlan spPlan, SPPlan newPlan) {
    
    /* in case the plan passed is null, it means it a new plan which is getting added to the account */
    if (spPlan == null) {
      account.getSpPlanMap().put(newPlan.getPlanType(), newPlan);
      return;
    }
    
    if (newPlan.getNumMember() > 0) {
      
      spPlan.setNumMember(spPlan.getNumMember() + newPlan.getNumMember());
      
      if (newPlan.isOverrideMemberPrice() && spPlan.isOverrideMemberPrice()) {
        /* add the override admin price to the existing override plan. */
        spPlan.setOverrideMemberPrice(newPlan.getOverrideMemberPrice().add(
            spPlan.getOverrideMemberPrice()));
      } else if (newPlan.isOverrideMemberPrice() && !spPlan.isOverrideMemberPrice()) {
        BigDecimal newPlanOverridePrice = spPlan.getPlanMemberNextChargeAmount().add(
            newPlan.getOverrideMemberPrice());
        spPlan.setOverrideMemberPrice(newPlanOverridePrice);
      }
    }
    
    if (newPlan.getNumAdmin() > 0) {
      spPlan.setNumAdmin(spPlan.getNumAdmin() + newPlan.getNumAdmin());
      if (newPlan.isOverrideAdminPrice() && spPlan.isOverrideAdminPrice()) {
        /* add the override admin price to the existing override plan. */
        spPlan.setOverrideAdminPrice(newPlan.getOverrideAdminPrice().add(
            spPlan.getOverrideAdminPrice()));
      } else if (newPlan.isOverrideAdminPrice() && !spPlan.isOverrideAdminPrice()) {
        BigDecimal newPlanOverridePrice = spPlan.getPlanAdminNextChargeAmount().add(
            newPlan.getOverrideAdminPrice());
        spPlan.setOverrideAdminPrice(newPlanOverridePrice);
      }
      
    }
    account.getSpPlanMap().put(spPlan.getPlanType(), spPlan);
    
  }
  
  /**
   * Updates the given account with the purchase units.
   * 
   * @param account
   *          - account
   */
  public void updateAccount(Account account, SPPlan spPlan) {
    
    setNextPaymentDate(spPlan);
    
    BigDecimal totalNextChargeAmount = new BigDecimal(0.0);
    PlanHelper planHelper = getPlanHelper(spPlan);
    LocalDateTime aggreementEndDate = spPlan.getAggreementEndDate();
    if (aggreementEndDate != null) {
      /*
       * check if the plan is active, in case of inactive we need to exclude the amount to get
       * inclued after the agrreement end date
       */
      if (aggreementEndDate.toLocalDate().isBefore(LocalDate.now())
          && spPlan.getPlanStatus() == PlanStatus.CANCEL) {
        Company company = accountRepository.findCompanybyAccountId(account.getId());
        company.getFeatureList().removeAll(spPlan.getFeatures());
        List<User> users = userReposiotry.findUsers("companyId", company.getId());
        users.stream().forEach(usr -> {
          spPlan.getFeatures().stream().forEach(feat -> {
            for (RoleType role : feat.getRoles()) {
              usr.removeRole(role);
            }
            
          });
          userReposiotry.updateUser(usr);
        });
        spPlan.setPlanStatus(PlanStatus.EXPIRED);
      }
    }
    if (spPlan.getPlanStatus() == PlanStatus.INACTIVE
        || spPlan.getPlanStatus() == PlanStatus.CANCEL
        || spPlan.getPlanStatus() == PlanStatus.EXPIRED) {
      return;
    }
    // update the next recharge amount
    // get the user count
    long numMembers = 0;
    if (spPlan.getPlanStatus() != PlanStatus.NEW && spPlan.getPlanStatus() != PlanStatus.TRIAL) {
      numMembers = userReposiotry.getNumberOfMembers(
          accountRepository.getCompanyForAccount(account.getId()).getId(), spPlan.getPlanType());
    }
    BigDecimal nextPayment = planHelper.getNextPayment(spPlan,
        (numMembers + spPlan.getNumMember()), spPlan.getNumAdmin());
    totalNextChargeAmount = totalNextChargeAmount.add(nextPayment);
    spPlan.setNextChargeAmount(totalNextChargeAmount.doubleValue());
  }
  
  /**
   * Updates the given account with the purchase units.
   * 
   * @param account
   *          - account
   * @param spPlan
   *          Plan to be updated.
   * @param noOfMember
   *          no of member.
   * @param noOfAdmin
   *          no of admin.
   */
  public void updateAccountUpdateDetails(Account account, SPPlan spPlan, int noOfMember,
      int noOfAdmin) {
    
    List<AccountUpdateDetail> accountUpdateDetails = account.getAccountUpdateDetailHistory().get(
        spPlan.getPlanType());
    if (accountUpdateDetails == null) {
      accountUpdateDetails = new ArrayList<>();
      account.getAccountUpdateDetailHistory().put(spPlan.getPlanType(), accountUpdateDetails);
    }
    /* Update the payment history */
    if (spPlan.getNumMember() > 0) {
      AccountUpdateDetail accountUpdateDetail = new AccountUpdateDetail();
      accountUpdateDetail.setAccountUpdateType(AccountUpdateType.PlanMember);
      accountUpdateDetail.setNoOfAccounts(noOfMember);
      accountUpdateDetail.setOverridePrice(spPlan.getOverrideMemberPrice());
      accountUpdateDetail.setUnitPrice(spPlan.getUnitMemberPrice());
      account.getAccountUpdateDetailHistory().get(spPlan.getPlanType()).add(accountUpdateDetail);
    }
    
    /* Update the payment history */
    if (spPlan.getNumAdmin() > 0) {
      AccountUpdateDetail accountUpdateDetail = new AccountUpdateDetail();
      accountUpdateDetail.setAccountUpdateType(AccountUpdateType.PlanAdmin);
      accountUpdateDetail.setNoOfAccounts(noOfAdmin);
      accountUpdateDetail.setOverridePrice(spPlan.getOverrideAdminPrice());
      accountUpdateDetail.setUnitPrice(spPlan.getUnitAdminPrice());
      account.getAccountUpdateDetailHistory().get(spPlan.getPlanType()).add(accountUpdateDetail);
    }
  }
  
  /**
   * Gets the product helper for the given product.
   * 
   * @param spPlan
   *          - product
   * @return the product helper
   */
  private PlanHelper getPlanHelper(SPPlan spPlan) {
    try {
      return (PlanHelper) ApplicationContextUtils.getBean(spPlan.getPlanType().toString()
          .toLowerCase()
          + PLAN_HELPER_SUFFIX);
    } catch (Exception e) {
      throw new InvalidRequestException("Plan helper not found for plan type:"
          + spPlan.getPlanType());
    }
  }
  
  /**
   * <code>setNextPayment</code> method will set the next payment date depending on the multiplier
   * on the account.
   * 
   * @param account
   *          for which next payment date is to be set.
   */
  public void setNextPaymentDate(SPPlan spPlan) {
    
    int daysToBeAdded = getBillingCycleMultiplier(spPlan);
    DateTime start = new DateTime(spPlan.getBillingCycleStartDate());
    
    if (spPlan.getNextPaymentDate() == null) {
      // setting the next payment date
      DateTime dateTime = DateTime.now();
      spPlan.setNextPaymentDate(dateTime.plusDays(daysToBeAdded).toDate());
      if (spPlan.getBillingCycleStartDate() == null) {
        spPlan.setBillingCycleStartDate(dateTime.toDate());
      }
      spPlan.setExpiresTime(start.plusDays(
          Constants.DAYS_OF_MONTHLY_BILLING * 12 * spPlan.getAgreementTerm()).toDate());
    } else {
      int days = Days.daysBetween(org.joda.time.LocalDate.now(),
          org.joda.time.LocalDate.fromDateFields(spPlan.getNextPaymentDate())).getDays();
      if (days <= 0) {
        
        org.joda.time.LocalDate lastPaymentDate = org.joda.time.LocalDate.fromDateFields(spPlan
            .getNextPaymentDate());
        spPlan.setNextPaymentDate(lastPaymentDate.plusDays(daysToBeAdded).toDate());
      }
    }
    
  }
  
  /**
   * getBillingCycleMultipier gives the multiper fo rthe account.
   * 
   * @param account
   *          for which mulitplier to be found.
   * @return the multiplier.
   */
  public int getBillingCycleMultiplier(SPPlan spPlan) {
    long multiplier = 0;
    
    // calculating the next recharge amount
    switch (spPlan.getBillingCycle().getBillingCycleType()) {
    case Monthly:
      multiplier = updateProratedMultplier(spPlan, 1);
      break;
    case Quaterly:
      multiplier = updateProratedMultplier(spPlan, 3);
      break;
    case SemiAnnually:
      multiplier = updateProratedMultplier(spPlan, 6);
      break;
    case Anually:
      multiplier = updateProratedMultplier(spPlan, 12);
      
      break;
    case Custom:
      multiplier = updateProratedMultplier(spPlan, spPlan.getBillingCycle().getNoOfMonths());
      break;
    default:
      throw new InvalidRequestException("Don't know how to process :"
          + spPlan.getBillingCycle().getBillingCycleType());
    }
    return (int) multiplier;
  }
  
  /**
   * updateProratedMultiplier method will update the multiplier for the prorated case.
   * 
   * @param account
   *          for which multiplier is to be prorated..
   * @param multiplier
   *          multiplier
   * @return the udpate profile multiplier.
   */
  private long updateProratedMultplier(SPPlan spPlan, long multiplier) {
    /*
     * check if the no of months is equal or greater then months left from the account
     * agreementEndDate
     */
    long days = 0;
    LocalDateTime agreementEndDate = spPlan.getAggreementEndDate();
    LocalDate lastPaymentDate = LocalDate.now();
    if (spPlan.getNextPaymentDate() != null) {
      lastPaymentDate = DateTimeUtil.getLocalDate(spPlan.getNextPaymentDate());
    }
    long daysRemaining = ChronoUnit.DAYS.between(lastPaymentDate, agreementEndDate.toLocalDate());
    if (daysRemaining < (multiplier * Constants.DAYS_OF_MONTHLY_BILLING)) {
      days = daysRemaining;
    } else {
      days = (multiplier * Constants.DAYS_OF_MONTHLY_BILLING);
    }
    return days;
  }
}
