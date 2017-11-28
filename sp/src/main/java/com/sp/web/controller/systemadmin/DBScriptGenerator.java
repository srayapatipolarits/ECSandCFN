package com.sp.web.controller.systemadmin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.exception.SPException;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.User;
import com.sp.web.repository.user.UserRepository;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The helper class to generate the DB Scripts.
 */
@Component
public class DBScriptGenerator {
  
  private static final Logger log = Logger.getLogger(DBScriptGenerator.class);
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private AccountRechargeRepository accountRechargeRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  private static final ObjectMapper om = new ObjectMapper();

  public String getScriptForCompany(String companyId, List<String> products) {
    final StringBuffer sb = new StringBuffer();
    Company company = accountRepository.findCompanyByIdValidated(companyId);
    addObjectToResponse(sb, company, "company");
    // get all the users for the company
    Account account = accountRepository.findAccountByCompanyId(companyId);
    account.setHiringSubscription(10);
    addAccount(sb, account);
    
    // get all the users and add them
    List<User> userList = accountRepository.getAllMembersForCompany(companyId);
    userList.forEach(u -> addUser(sb, u));
    return sb.toString();
  }
  
  private void addUser(StringBuffer sb, User user) {
    user.setGroupAssociationList(null);
    user.setUserGoalId(null);
    user.setTokenUrl(null);
    user.setTaskList(null);
    if (user.getCreatedOn() == null) {
      user.setCreatedOn(LocalDate.now());
    }
    if (user.getDob() == null) {
      user.setDob(LocalDate.now());
    }
    addObjectToResponse(sb, user, "user");
  }

  private void addAccount(StringBuffer sb, Account account) {
    account.setAuthorizedNetProfileId(null);
    account.setCustomerProfileId(null);
    addObjectToResponse(sb, account, "account");
    // adding payment instrument
    PaymentInstrument paymentInstrument = accountRepository.findPaymentInstrumentById(account.getPaymentInstrumentId());
    addObjectToResponse(sb, paymentInstrument, "paymentInstrument");
    // adding the last payment record
    PaymentRecord paymentRecord = accountRechargeRepository.findPaymentRecordById(account.getLastPaymentId());
    if (paymentRecord != null) {
      addObjectToResponse(sb, paymentRecord, "paymentRecord");
    }
  }

  private void addObjectToResponse(StringBuffer sb, Object obj, String tableName) {
    try {
      String writeValueAsString = om.writeValueAsString(obj);
      if (tableName.equalsIgnoreCase("user")) {
        writeValueAsString = updateCreatedOnDate("createdOn", writeValueAsString);
        writeValueAsString = updateDOB("dob", ",\"groupAssociationList\"", writeValueAsString);
      }
      writeValueAsString = updateId(writeValueAsString);
      sb.append("db.").append(tableName).append(".insert(")
          .append(writeValueAsString)
          .append(");\n");
    } catch (JsonProcessingException e) {
      log.warn("Error writing object.", e);
      throw new SPException(e);
    }
  }

  private String updateDOB(String keyStr, String endStr,
      String writeValueAsString) {
    String key = "\"" + keyStr + "\"" + ":";
    int indexOf = writeValueAsString.indexOf(key);
    if (indexOf != -1) {
      String prefix = writeValueAsString.substring(0, indexOf);
      String value = writeValueAsString.substring(indexOf + key.length(),
          writeValueAsString.indexOf(endStr, indexOf));
      try {
        JSONObject job = new JSONObject(value);
        String dateStr = String.format("%s-%02d-%02dT00:00:00",
            job.get("year"), job.get("monthValue"), job.get("dayOfMonth")).toString();
        LocalDateTime.parse(dateStr);
        String postFix = writeValueAsString.substring(indexOf + key.length()
            + value.length());
        return prefix + "" + key + "ISODate(\"" + dateStr + "Z\")" + postFix;
      } catch (Exception e) {
        System.out.println(writeValueAsString);
        System.out.println(value);
        e.printStackTrace();
      }
    }
    return writeValueAsString;
  }

  private String updateId(String writeValueAsString) {
    String key = "\"id\"" + ":";
    int indexOf = writeValueAsString.indexOf(key);
    String prefix = writeValueAsString.substring(0, indexOf);
    String value = writeValueAsString.substring(indexOf + key.length(),
        writeValueAsString.indexOf(",", indexOf));
    String postFix = writeValueAsString.substring(indexOf + key.length()
        + value.length());
    return prefix + "\"_id\":ObjectId(" + value + ")" + postFix;
  }
  
  private String updateCreatedOnDate(String keyStr, String writeValueAsString) {
    String key = "\"" + keyStr + "\"" + ":";
    int indexOf = writeValueAsString.indexOf(key);
    if (indexOf != -1) {
      String prefix = writeValueAsString.substring(0, indexOf);
      String value = writeValueAsString.substring(indexOf + key.length(),
          writeValueAsString.indexOf("},\"id\"", indexOf) + 1);
      try {
        JSONObject job = new JSONObject(value);
        String dateStr = String.format("%s-%02d-%02dT00:00:00",
            job.get("year"), job.get("monthValue"), job.get("dayOfMonth")).toString();
        LocalDateTime.parse(dateStr);
        String postFix = writeValueAsString.substring(indexOf + key.length()
            + value.length());
        return prefix + "" + key + "ISODate(\"" + dateStr + "Z\")" + postFix;
      } catch (Exception e) {
        System.out.println(writeValueAsString);
        System.out.println(value);
        e.printStackTrace();
      }
    }
    return writeValueAsString;
  }
  

  public String getScriptForUser(String email, List<String> products) {
    final StringBuffer sb = new StringBuffer();
    
    User user = userRepository.findByEmailValidated(email);
    addUser(sb, user);
    
    if( user.getAccountId() != null) {
      Account account = accountRepository.findValidatedAccountByAccountId(user.getAccountId());
      account.setProducts(products);
      addAccount(sb, account);
    }
    return sb.toString();
  }

}
