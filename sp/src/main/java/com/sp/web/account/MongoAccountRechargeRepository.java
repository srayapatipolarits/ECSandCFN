package com.sp.web.account;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.SequenceId;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the account recharge repository interfaces.
 */
@Repository
public class MongoAccountRechargeRepository implements AccountRechargeRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;
  
  @Override
  public PaymentRecord addPaymentRecord(Account account, SPPlan spPlan,
      PaymentGatewayRequest request, PaymentGatewayResponse response) {
    // create a new record and save to database
    PaymentRecord record = new PaymentRecord(account, spPlan, request, response);
    record.setOrderNumber(getNextOrderNumber());
    mongoTemplate.save(record);
    // update the last payment details in the account
    spPlan.setLastPaymentId(record.getId());
    return record;
  }
  
  @Override
  public List<PaymentRecord> getPaymentRecords(Account account, SPPlanType planType) {
    final Query query = query(where("accountId").is(account.getId()).and("planType").is(planType));
    query.with(new Sort(Direction.ASC, "createdOn"));
    return mongoTemplate.find(query, PaymentRecord.class);
    // return mongoTemplate.find(query(where("accountId").is(account.getId())),
    // PaymentRecord.class);
  }
  
  /**
   * Get the order number from the sequence.
   * 
   * @return the order number
   */
  public long getNextOrderNumber() {
    
    String key = "orderNo";
    
    // get sequence id
    Query query = new Query(Criteria.where("_id").is(key));
    
    // increase sequence id by 1
    Update update = new Update();
    update.inc("seq", 1);
    
    // return new increased id
    FindAndModifyOptions options = new FindAndModifyOptions();
    options.returnNew(true);
    
    // this is the magic happened.
    SequenceId seqId = mongoTemplate.findAndModify(query, update, options, SequenceId.class);
    
    // if no id, throws SequenceException
    // optional, just a way to tell user when the sequence id is failed to generate.
    if (seqId == null) {
      throw new InvalidRequestException("Unable to get sequence id for key : " + key);
    }
    
    return seqId.getSeq();
  }
  
  @Override
  public PaymentRecord findPaymentRecordById(String paymentRecordId) {
    return mongoTemplate.findById(
        Optional.ofNullable(paymentRecordId).orElseThrow(
            () -> new InvalidRequestException("Payment record id cannot be null !!!")),
        PaymentRecord.class);
  }
  
  @Override
  public Map<SPPlanType, List<Account>> getOverDueAccounts() {
    Map<SPPlanType, List<Account>> accounts = new HashMap<SPPlanType, List<Account>>();
    
    List<Account> ertiAccounts = mongoTemplate.find(
        query(where("spPlanMap.Primary.nextPaymentDate").lte(DateTime.now().toDate())),
        Account.class);
    
    List<Account> peopleAnalyticsAccounts = mongoTemplate.find(
        query(where("spPlanMap.IntelligentHiring.nextPaymentDate").lte(DateTime.now().toDate())),
        Account.class);
    accounts.put(SPPlanType.Primary, ertiAccounts);
    accounts.put(SPPlanType.IntelligentHiring, peopleAnalyticsAccounts);
    return accounts;
    
  }
  
  @Override
  public List<Account> getAllTrialAccounts() {
    return mongoTemplate.find(query(where("status").is(AccountStatus.TRIAL)), Account.class);
  }
  
  @Override
  public void deletePaymentRecord(PaymentRecord paymentRecord) {
    mongoTemplate.remove(paymentRecord);
  }
  
  @Override
  public Map<SPPlanType, List<Account>> getAllPaymentExpiredAccounts() {
    Map<SPPlanType, List<Account>> accounts = new HashMap<SPPlanType, List<Account>>();
    
    List<Account> ertiAccounts = mongoTemplate.find(
        query(where("spPlanMap.Primary.planStatus").is(PlanStatus.RENEWAL_PAYMENT_FAILED)),
        Account.class);
    List<Account> peopleAnalyticsAccounts = mongoTemplate
        .find(
            query(where("spPlanMap.IntelligentHiring.planStatus").is(
                PlanStatus.RENEWAL_PAYMENT_FAILED)), Account.class);
    accounts.put(SPPlanType.Primary, ertiAccounts);
    accounts.put(SPPlanType.IntelligentHiring, peopleAnalyticsAccounts);
    return accounts;
  }
  
  @Override
  public Map<SPPlanType, List<Account>> getAllExpiredAccounts() {
    Map<SPPlanType, List<Account>> accounts = new HashMap<SPPlanType, List<Account>>();
    List<Account> ertiAccounts = mongoTemplate.find(
        query(where("spPlanMap.Primary.planStatus").is(PlanStatus.EXPIRED)), Account.class);
    List<Account> peopleAnalyticsAccounts = mongoTemplate.find(
        query(where("spPlanMap.IntelligentHiring.planStatus").is(PlanStatus.EXPIRED)),
        Account.class);
    
    accounts.put(SPPlanType.Primary, ertiAccounts);
    accounts.put(SPPlanType.IntelligentHiring, peopleAnalyticsAccounts);
    return accounts;
  }
  
  /**
   * @see com.sp.web.account.AccountRechargeRepository#getAllAccountsToBeRecharged()
   */
  @Override
  public Map<SPPlanType, List<Account>> getAllAccountsToBeRecharged() {
    Map<SPPlanType, List<Account>> accounts = new HashMap<SPPlanType, List<Account>>();
    DateTime dateTime = DateTime.now();
    dateTime = dateTime.plusDays(Constants.DEFAULT_ACCOUNT_PRE_RECHARGE_NOTIFICATION);
    List<Account> ertiAccounts = mongoTemplate.find(
        query(where("spPlanMap.Primary.nextPaymentDate").gte(DateTime.now().toDate()).lte(
            dateTime.toDate())), Account.class);
    List<Account> peopleAnalyticsAccounts = mongoTemplate.find(
        query(where("spPlanMap.IntelligentHiring.nextPaymentDate").gte(DateTime.now().toDate())
            .lte(dateTime.toDate())), Account.class);
    
    accounts.put(SPPlanType.Primary, ertiAccounts);
    accounts.put(SPPlanType.IntelligentHiring, peopleAnalyticsAccounts);
    return accounts;
  }
}
