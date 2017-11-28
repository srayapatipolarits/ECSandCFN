package com.sp.web.controller.systemadmin.company;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SystemUserMigrationController will migrate the user from one company to another.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class SystemUserAdministratorController {
  
  @Autowired
  private SystemUserAdministratorControllerHelper migrationControllerHeler;
  
  @ResponseBody
  @RequestMapping(value = "/sysAdmin/hk/userMigration", method = RequestMethod.POST)
  public SPResponse migrateUser(@RequestParam String email, @RequestParam String newCompanyId,
      Authentication token) {
    return ControllerHelper.process(migrationControllerHeler::migrateUser, token, email,
        newCompanyId);
  }
  
  /**
   * Modal Popup View For Select Company for Email Manager.
   */
  @RequestMapping(value = "/sysAdmin/hk/selectCompany", method = RequestMethod.GET)
  public String openCompanyPopUp(Authentication token) {
    return "penCompanyModal";
  } 
  
  /**
   * Controller method to get the list of companies that do not have email override.
   * 
   * @param token
   *          - logged in user
   * @return the list of companies
   */
  @RequestMapping(value = "/sysAdmin/hk/getCompanies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanies(Authentication token) {
    return process(migrationControllerHeler::getCompanies, token);
  }
  
}
