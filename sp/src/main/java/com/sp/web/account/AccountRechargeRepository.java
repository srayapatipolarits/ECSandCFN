package com.sp.web.account;

import com.sp.web.model.Account;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The repository interface to management and update the account recharges.
 */
public interface AccountRechargeRepository {
  /**
   * Creates a payment record for the given payment request and response.
   * 
   * @param plan
   *          - the account to update
   * @param request
   *          - the payment request
   * @param response
   *          - the payment response
   * @return the newly created payment record
   */
  PaymentRecord addPaymentRecord(Account account, SPPlan plan, PaymentGatewayRequest request,
      PaymentGatewayResponse response);
  
  /**
   * Gets the payment records for the account.
   * 
   * @param account
   *          - account
   * @param spPlanType
   *          is the plan for which payment record is to be find.
   * @return list of payment record
   */
  List<PaymentRecord> getPaymentRecords(Account account, SPPlanType spPlanType);
  
  /**
   * Get the payment record for the given payment record id.
   * 
   * @param paymentRecordId
   *          - payment record id
   * @return the payment record
   */
  PaymentRecord findPaymentRecordById(String paymentRecordId);
  
  /**
   * The list of over due accounts.
   * 
   * @return list of accounts
   */
  Map<SPPlanType, List<Account>> getOverDueAccounts();
  
  /**
   * The list of trial accounts.
   * 
   * @return list of trial accounts
   */
  List<Account> getAllTrialAccounts();
  
  /**
   * Deletes the given payment record.
   * 
   * @param paymentRecord
   *          - payment record
   */
  void deletePaymentRecord(PaymentRecord paymentRecord);
  
  /**
   * Get the list of expired accounts.
   * 
   * @return - list of expired accounts
   */
  Map<SPPlanType, List<Account>> getAllPaymentExpiredAccounts();
  
  /**
   * Get the list of expired accounts.
   * 
   * @return - list of expired accounts
   */
  Map<SPPlanType, List<Account>> getAllAccountsToBeRecharged();
  
  /**
   * Get the list of accounts.
   * 
   * @return list of expired accounts
   */
  Map<SPPlanType, List<Account>> getAllExpiredAccounts();
  
}
