package com.sp.web.controller.admin.alternatebilling;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.AccountType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Archive Account controller contains the function for the archives and unarchive of the account.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class ArchiveAccountController {
  
  /** Archive helper for the accounts. */
  @Autowired
  private ArchiveAccountControllerHelper archiveAccountControllerHelper;
  
  /**
   * Archive account function will archive the accounts
   * 
   * @param accountId
   *          to be archived.
   * @param token
   *          user logged in.
   * @return the spresponse.
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/archive/archiveAccount", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse archiveAccount(@RequestParam String accountId,
      Authentication token) {
    return ControllerHelper.process(archiveAccountControllerHelper::archiveAccount, token,
        accountId);
  }
  
  /**
   * Archive account function will archive the accounts
   * 
   * @param token
   *          user logged in.
   * @return the spresponse.
   */
  @RequestMapping(value = "/sysAdmin/alternateBilling/archive/getAllArchiveAccounts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllArchiveAccounts(@RequestParam AccountType accountType,
      Authentication token) {
    return ControllerHelper.process(archiveAccountControllerHelper::getAllArchiveAccounts, token,accountType);
  }
  
}
