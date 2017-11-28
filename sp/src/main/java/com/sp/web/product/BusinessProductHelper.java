package com.sp.web.product;

import com.sp.web.account.AccountRepository;
import com.sp.web.model.Account;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;
import com.sp.web.repository.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dax Abraham
 * 
 *         The business product helper.
 */
@Deprecated
//@Component("businessProductHelper")
public class BusinessProductHelper extends ProductHelperCommon implements ProductHelper {
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  UserRepository userReposiotry;              
  
  @Override
  @Deprecated
  public void updateAccount(Account account, Product product, Promotion promotion,
      long purchaseUnits) {
    
    // setting the available subscriptions count
    account.setAvailableSubscriptions(account.getAvailableSubscriptions()
        + Long.valueOf(purchaseUnits).intValue());
    
    // update the next recharge amount
    // get the user count
    long numMembers = 0; 
//    if (account.getStatus() != AccountStatus.NEW && account.getStatus() != AccountStatus.TRIAL) {
//      numMembers = userReposiotry.getNumberOfMembers(accountRepository.getCompanyForAccount(
//          account.getId()).getId());
//    }
    
    updateNextPayment(account, product, promotion, (numMembers + account.getAvailableSubscriptions()));
  }
}
