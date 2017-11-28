package com.sp.web.controller.email;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.email.EmailManagementForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 *
 *         The controller for email management.
 */
@Controller
public class EmailManagementController {
  
  @Autowired
  EmailManagementControllerHelper helper;
  
  /**
   * View For SystemAdmin EmailManager Home Page.
   * 
   */
  @RequestMapping(value = "/sysAdmin/emailManagement/home", method = RequestMethod.GET)
  public String emailsHome(Authentication token) {
    return "sysAdminEmailsHome";
  }
  
  /**
   * View For Account Admin Email Management Pages.
   * 
   */
  @RequestMapping(value = "/admin/company/emails/home", method = RequestMethod.GET)
  public String themingHome(Authentication token) {
    return "adminEmailsHome";
  }
  
  /**
   * View For Email Manager Update Settings Page.
   * 
   */
  @RequestMapping(value = "/sysAdmin/emailManagement/update", method = RequestMethod.GET)
  public String emailsUpdate(Authentication token) {
    return "emailsUpdate";
  }
  
  /**
   * Modal Popup View For Select Company for Email Manager.
   */
  @RequestMapping(value = "/sysAdmin/emailManagement/selectCompany", method = RequestMethod.GET)
  public String emailsOpenCompanyModal(Authentication token) {
    return "emailsOpenCompanyModal";
  } 
  
  /**
   * Modal Popup View For Select Email Templates for Email Manager.
   */
  @RequestMapping(value = "/emailManagement/selectEmails", method = RequestMethod.GET)
  public String selectEmailModal(Authentication token) {
    return "selectEmailModal";
  } 
  
  /**
   * Controller method to get the list of companies that do not have email override.
   * 
   * @param token
   *          - logged in user
   * @return the list of companies
   */
  @RequestMapping(value = "/sysAdmin/emailManagement/getCompanies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanies(Authentication token) {
    return process(helper::getCompanies, token);
  }
  
  /**
   * Controller method to get all the tasks for the logged in user.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get tasks request
   */
  @RequestMapping(value = "/sysAdmin/emailManagement/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    // get's all the email management configurations present in the system
    return process(helper::getAll, token);
  }
  
  /**
   * Controller method to get the email management for the current users company.
   * 
   * @param token
   *          - logged in user
   * @param companyId
   *          - company id         
   * @return
   *      the email management 
   */
  @RequestMapping(value = "/emailManagement/get", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse get(Authentication token, @RequestParam(required = false) String companyId) {
    // get's all the email management configurations present in the system
    return process(helper::get, token, companyId);
  }
  
  /**
   * Controller method to get the list of notifications configured in the system.
   * 
   * @param token
   *          - logged in user
   * @return the list of notifications
   */
  @RequestMapping(value = "/emailManagement/getNotifications", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getNotifications(Authentication token) {
    return process(helper::getNotifications, token);
  }
  
  /**
   * Controller method to create a new email notification.
   * 
   * @param token
   *          - logged in user
   * @return the response to the create request
   */
  @RequestMapping(value = "/emailManagement/createUpdate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createUpdate(@RequestBody EmailManagementForm emailManagementForm,
      Authentication token) {
    return process(helper::createUpdate, token, emailManagementForm);
  }
  
  /**
   * Controller method to delete the email configuration.
   *
   * @param emailManagementForm
   *          - email management form
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/emailManagement/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestBody EmailManagementForm emailManagementForm,
      Authentication token) {
    return process(helper::delete, token, emailManagementForm);
  }
  
  /**
   * Controller method to enable disable company access to email management.
   * 
   * @param emailManagementForm
   *            - email management form
   * @param token
   *            - logged in user
   * @return
   *      the status of the enable disable request
   */
  @RequestMapping(value = "/sysAdmin/emailManagement/companyPermission", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse companyPermission(@RequestBody EmailManagementForm emailManagementForm,
      Authentication token) {
    return process(helper::companyPermission, token, emailManagementForm);
  }
  
}