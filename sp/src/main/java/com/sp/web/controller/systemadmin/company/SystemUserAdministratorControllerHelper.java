package com.sp.web.controller.systemadmin.company;

import com.sp.web.account.AccountRepository;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.exception.SPException;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.user.UserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemUserMigrationControllerHelper is the helper for the migration controller helper.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SystemUserAdministratorControllerHelper {
  
  @Autowired
  private UserRepository repository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * migrateUser user will migrate the user.
   * 
   * @param sysAdminUser
   *          syste admin user.
   * @param params
   *          contains the params.
   * @return the SPResponse.
   */
  public SPResponse migrateUser(User sysAdminUser, Object[] params) {
    
    String email = (String) params[0];
    
    Assert.hasText(email, "Email cannot be blank or empty");
    
    String newCompanyId = (String) params[1];
    Assert.hasText(newCompanyId, "New Company Id cannot be blank");
    
    /* validate the user */
    User userToMigrate = repository.findByEmail(email);
    
    Assert.notNull(userToMigrate, "User with the email " + email + " does not exist.");
    
    CompanyDao oldComp = companyFactory.getCompany(userToMigrate.getCompanyId());
    
    Assert.notNull(oldComp, "Old Company not found");
    
    if (userToMigrate.getCompanyId() == null || !userToMigrate.getCompanyId().equalsIgnoreCase(oldComp.getId())) {
      throw new SPException("User does belong to the old company or user is not a buisness user.");
    }
    
    CompanyDao newComp = companyFactory.getCompany(newCompanyId);
    Assert.notNull(newComp, "New Company not found");
    
    /* Check if slots availble in the company to migrate */
    int adminAccounts = repository.findByCompanyAndRole(newComp.getId(), RoleType.AccountAdministrator).size();
    Account newAccount = accountRepository.findAccountByCompanyId(newComp.getId());
    SPPlan spPlan = newAccount.getSpPlanMap().get(SPPlanType.Primary);
    if (spPlan.getNumMember() <= 0) {
      throw new SPException("No avaialble member slots availabe to transfer" );
    }
    newAccount.reserveSubscritption(SPPlanType.Primary);
    
    Account oldAccount = accountRepository.findAccountByCompanyId(oldComp.getId());
    oldAccount.addSubscription(SPPlanType.Primary);
    
    /* update the company */
    userToMigrate.setCompanyId(newCompanyId);
    if (!CollectionUtils.isEmpty(userToMigrate.getGroupAssociationList())) {
      /* Remove the old groups. */
      userToMigrate.getGroupAssociationList().clear();
    }
    
    if (!CollectionUtils.isEmpty(userToMigrate.getRoles())) {
      userToMigrate.getRoles().removeIf(rol -> !(rol == RoleType.User || rol == RoleType.AccountAdministrator));
      
    } else {
      userToMigrate.addRole(RoleType.User);
    }
    
    /* Remove the account administrator rolein case user has account administrator. */
    if (userToMigrate.getRoles().contains(RoleType.AccountAdministrator)) {
      if (spPlan.getNumAdmin() <= adminAccounts) {
        userToMigrate.removeRole(RoleType.AccountAdministrator);
      }
    }
    accountRepository.updateAccount(oldAccount);
    accountRepository.updateAccount(newAccount);
    userFactory.cleanUser(userToMigrate, userToMigrate);
    repository.updateUser(userToMigrate);
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to get the list of companies that do not have email override.
   * 
   * @param user
   *          - logged in user
   * @return the list of companies
   */
  public SPResponse getCompanies(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory.findAllCompanies();
    return resp.add(
        "companies",
        findAllCompanies
            .stream()
            .map(CompanyDTO::new)
            .collect(Collectors.toList()));
  }
}
