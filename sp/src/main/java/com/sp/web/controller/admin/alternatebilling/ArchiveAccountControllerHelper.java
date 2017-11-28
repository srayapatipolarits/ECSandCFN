package com.sp.web.controller.admin.alternatebilling;

import com.sp.web.account.AccountRepository;
import com.sp.web.account.ExpiryAccountHelper;
import com.sp.web.dto.alternatebilling.AccountDetailsDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.archive.ArchiveRepository;
import com.sp.web.repository.archive.account.ArchiveAccountRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ArchiveAccountControllerHelper class contains the helper class for the accounts.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class ArchiveAccountControllerHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(ArchiveAccountControllerHelper.class);
  
  /** ExpiryAccountHelper is the class for expiry of the account. */
  @Autowired
  private ExpiryAccountHelper expiryAccountHelper;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private ArchiveRepository archiveRepository;
  
  @Autowired
  private ArchiveAccountRepository archiveAccountRepository;
  
  /**
   * <code>archiveAccount</code> method will archive the account.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the account id.
   * @return the response.
   */
  public SPResponse archiveAccount(User user, Object[] params) {
    
    String accountId = (String) params[0];
    
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    if (account == null) {
      throw new InvalidRequestException("Invalid Account or no account found for account id:- "
          + accountId);
    }
    
    /* only a deactivated account can be archived. */
    if (!account.isDeactivated()) {
      throw new InvalidRequestException("Account needs to be deactivated first");
    }
    
    expiryAccountHelper.expireAccount(account);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * GetAll Arhvied accounts will return all the archive accounts presne tin the system.
   * 
   * @param user
   *          logged in user
   * @param contains
   *          the account type.
   * @return the archived accounts.
   */
  public SPResponse getAllArchiveAccounts(User user, Object[] param) {
    
    AccountType accountType = (AccountType) param[0];
    
    List<Account> accounts = archiveRepository.getAllArchived("account", Account.class);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Archived acounts returned are " + accounts.size());
    }
    SPResponse response = new SPResponse();
    
    List<AccountDetailsDTO> accountsDto = accounts.stream()
        .filter(ac -> ac.getType() == accountType).map(ac -> {
          String accountId = ac.getId();
          Company company = null;
          
          try {
            company = archiveAccountRepository.getCompanyForAccount(accountId);
          } catch (InvalidRequestException ex) {
            return null;
          }
          AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO(ac, company);
          
          return accountDetailsDTO;
        }).filter(adto -> adto != null).collect(Collectors.toList());
    
    response.add("account", accountsDto);
    return response;
    
  }
  
}
