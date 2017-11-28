package com.sp.web.account;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.WriteResult;
import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.AssistedAccount;
import com.sp.web.model.Company;
import com.sp.web.model.DeletedUser;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentType;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.SequenceId;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.demoaccount.DemoUser;
import com.sp.web.repository.payment.PaymentInstrumentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author pradeep
 * 
 *         The repository class for managing the account entities.
 */
@Repository
public class MongoAccountRepository implements AccountRepository {
  
  /**
   * Intializing the logger private static final Logger LOG = Logger
   * .getLogger(MongoAccountRepository.class);
   */
  
  /** Mongo Template for accessing data from mongo. */
  private final MongoTemplate mongoTemplate;
  
  @Autowired
  MessageSource messages;
  
  @Autowired
  PaymentInstrumentRepository paymentInstrumentRepository;
  
  @Autowired
  public MongoAccountRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }
  
  @Override
  public Company findCompanyById(String companyId) {
    return mongoTemplate.findById(
        Optional.ofNullable(companyId).orElseThrow(
            () -> new InvalidRequestException("Company name cannot be null !!!")), Company.class);
  }
  
  @Override
  public List<Company> findCompanyById(Set<String> companyIds) {
    return mongoTemplate.find(query(where(Constants.ENTITY_ID).in(companyIds)), Company.class);
  }
  
  @Override
  public Company findCompanyByIdValidated(String companyId) {
    return Optional.ofNullable(findCompanyById(companyId)).orElseThrow(
        () -> new InvalidRequestException("Company not found :" + companyId));
  }
  
  @Override
  public Company createCompany(Company company) {
    mongoTemplate.insert(company);
    return company;
  }
  
  @Override
  public Account createAccount(Account account) {
    mongoTemplate.insert(account);
    return account;
  }
  
  @Override
  public PaymentInstrument createPaymentInstrument(PaymentInstrument paymentInstrument) {
    mongoTemplate.insert(paymentInstrument);
    return paymentInstrument;
  }
  
  @Override
  public boolean removeAccount(Account account) {
    return remove(account);
  }
  
  @Override
  public boolean removePaymentInstrument(PaymentInstrument paymentInstrument) {
    return remove(paymentInstrument);
  }
  
  @Override
  public boolean removeCompany(Company company) {
    return remove(company);
  }
  
  /**
   * Removes the given object from the db.
   * 
   * @param obj
   *          - object to remove
   * @return - true if successfully removed
   */
  private boolean remove(Object obj) {
    WriteResult result = mongoTemplate.remove(obj);
    return (result.getN() > 0);
  }
  
  @Override
  public AssistedAccount createAssistedAccount(AssistedAccount account) {
    mongoTemplate.insert(account);
    return account;
  }
  
  @Override
  public Account findAccountByCompanyId(String companyId) {
    // get the account
    return mongoTemplate
        .findById(findCompanyByIdValidated(companyId).getAccountId(), Account.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#updateAccount(com.sp.web.model.Account )
   */
  @Override
  public Account updateAccount(Account accountToUpdate) {
    mongoTemplate.save(accountToUpdate);
    return accountToUpdate;
  }
  
  @Override
  public List<User> getAllMembersForCompany(String companyId) {
    return mongoTemplate.find(query(where("companyId").is(companyId)), User.class);
  }
  
  @Override
  public List<User> search(String companyId, String searchString, String[] fieldsToInclude) {
    
    String[] searchParts = searchString.split(" ");
    int counter = 0;
    StringBuilder sb = new StringBuilder();
    for (String queryStr : searchParts) {
      if (counter > 0) {
        sb.append("|");
      }
      sb.append("(^").append(queryStr.trim()).append(")");
      counter++;
    }
    
    Query searchQuery = new Query();
    searchQuery.addCriteria(new Criteria().orOperator(where("lastName").regex(sb.toString(), "i"),
        where("firstName").regex(sb.toString(), "i"), where("companyId").is(companyId)));
    searchQuery.with(new Sort(Sort.Direction.DESC, "lastName"));
    if (fieldsToInclude != null) {
      Arrays.asList(fieldsToInclude).stream().forEach(f -> searchQuery.fields().include(f));
    }
    searchQuery.fields().include("firstName");
    return mongoTemplate.find(searchQuery, User.class);
  }
  
  @Override
  public PaymentInstrument findPaymentInstrumentById(String paymentInstrumentId) {
    return paymentInstrumentRepository.findById(Optional.ofNullable(paymentInstrumentId)
        .orElseThrow(() -> new InvalidRequestException("Payment instrument cannot be null !!!")));
  }
  
  @Override
  public Company updateCompany(Company company) {
    mongoTemplate.save(company);
    return company;
  }
  
  @Override
  public Account findValidatedAccountByCompanyId(String companyId) {
    return Optional.ofNullable(findAccountByCompanyId(companyId)).orElseThrow(
        () -> new InvalidRequestException("Account not set for company :" + companyId));
  }
  
  @Override
  public Account findValidatedAccountByAccountId(String accountId) {
    return Optional.ofNullable(findAccountByAccountId(accountId)).orElseThrow(
        () -> new InvalidRequestException("Account not found for account id:" + accountId));
  }
  
  /**
   * Get the account for the given account id.
   * 
   * @param accountId
   *          - account id
   * @return the account with the given account id
   */
  private Account findAccountByAccountId(String accountId) {
    return mongoTemplate.findById(
        Optional.ofNullable(accountId).orElseThrow(
            () -> new InvalidRequestException("Account Id must not be null !!!")), Account.class);
  }
  
  @Override
  public void updatePaymentInstrument(SPPlan spPlan, PaymentInstrument pi) {
    paymentInstrumentRepository.save(pi);
    if (spPlan.getPaymentType() != null && spPlan.getPaymentType() != PaymentType.CREDIT_CARD) {
      spPlan.setPaymentInstrumentId(pi.getId());
    }
  }
  
  @Override
  public Company getCompanyForAccount(String accountId) {
    return Optional
        .ofNullable(
            mongoTemplate.findOne(
                query(where("accountId").is(
                    Objects.requireNonNull(accountId, "Account id cannot be null !!!"))),
                Company.class)).orElseThrow(
            () -> new InvalidRequestException("Company not found for account :" + accountId));
  }
  
  @Override
  public List<Company> findCompanyByAccountId(List<String> accountIdList) {
    return mongoTemplate.find(query(where(Constants.ENTITY_ACCOUNT_ID).in(accountIdList)),
        Company.class);
  }
  
  @Override
  public List<DeletedUser> getAllDeletedMemberForCompany(String companyId) {
    return mongoTemplate.find(query(where("companyId").is(companyId)), DeletedUser.class);
  }
  
  @Override
  public void removeDeletedUser(DeletedUser deletedUser) {
    mongoTemplate.remove(deletedUser);
  }
  
  /**
   * @see com.sp.web.account.AccountRepository#createDemoAccount(com.sp.web.model.demoaccount.DemoUser)
   */
  @Override
  public void createDemoAccount(DemoUser demoUser) {
    mongoTemplate.save(demoUser);
  }
  
  @Override
  public List<Company> findAllCompanies() {
    return mongoTemplate.findAll(Company.class);
  }
  
  /**
   * @see com.sp.web.account.AccountRepository#getAllAccountForAccountType(com.sp.web.model.AccountType)
   */
  @Override
  public List<Account> getAllAccountForAccountType(AccountType accountType) {
    return mongoTemplate.find(Query.query(Criteria.where("type").is(accountType)), Account.class);
  }
  
  /**
   * findCompanyByAccountId will return the company by account id/
   * 
   * @param accountId
   *          from which company to be retrieved.
   * @return company.
   */
  public Company findCompanybyAccountId(String accountId) {
    /* find the companyId associate with the accountId */
    return mongoTemplate.findOne(Query.query(Criteria.where("accountId").is(accountId)),
        Company.class);
  }
  
  /**
   * <code>getAdministratorUsersForAccount</code> method will return the users for account.
   * 
   * @see com.sp.web.account.AccountRepository#getAdminstratorUsersForAccount(java.lang.String)
   */
  @Override
  public List<User> getAdminstratorUsersForCompany(String companyId) {
    
    /*
     * find all the users where company id is passed above and users have role " Account
     * Administrator
     */
    return mongoTemplate.find(
        Query.query(Criteria
            .where("companyId")
            .is(companyId)
            .andOperator(
                Criteria.where(Constants.ENTITY_USER_ROLES).in(RoleType.AccountAdministrator))),
        User.class);
  }
  
  /**
   * @see com.sp.web.account.AccountRepository#getNextAccountNumber()
   */
  @Override
  public String getNextAccountNumber() {
    
    String key = "accountNo";
    
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
    return "SP" + Constants.ACCOUNT_FORMATTER.format(seqId.getSeq());
  }
  
  /**
   * @see com.sp.web.account.AccountRepository#findAdminsForPlans(java.util.Set)
   */
  @Override
  public Map<SPPlanType, List<User>> findAdminsForPlans(Set<SPPlanType> plansType, String companyId) {
    Map<SPPlanType, List<User>> adminUsersMap = new HashMap<SPPlanType, List<User>>();
    plansType.stream().forEach(
        plan -> {
          List<User> adminUsers = mongoTemplate.find(
              Query.query(Criteria.where("companyId").is(companyId)
                  .and(Constants.ENTITY_USER_ROLES).in(plan.getAdminRoleForPlan())), User.class);
          adminUsersMap.put(plan, adminUsers);
        });
    
    return adminUsersMap;
  }
  
  @Override
  public List<Company> findCompaniesByFeature(SPFeature[] featureType) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_FEATURE_LIST).in(Arrays.asList(featureType))), Company.class);
  }
  
  @Override
  public List<User> findAdminsForPlan(SPPlanType plansType, String companyId) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and(Constants.ENTITY_USER_ROLES)
            .in(plansType.getAdminRoleForPlan())), User.class);
  }
}
