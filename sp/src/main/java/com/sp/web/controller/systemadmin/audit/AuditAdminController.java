package com.sp.web.controller.systemadmin.audit;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author pradeepruhil
 *
 */
@Controller
@Audit(type = ServiceType.All)
public class AuditAdminController {
  
  @Autowired
  private AuditControllerHelper auditControllerHelper;
  
  /**
   * getAuditHome method will return the result for the home.
   * 
   * @param token
   *          logged in user.
   * @return the SPResponse.
   */
  
  @RequestMapping(value = "/sysAdmin/audit/getAuditHome", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAuditHome(@RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String endDate, Authentication token) {
    return ControllerHelper.process(auditControllerHelper::getAuditHome, token,fromDate,endDate);
  }
  
  @RequestMapping(value = "/sysAdmin/audit/getAllServices", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllServices(Authentication token) {
    return ControllerHelper.process(auditControllerHelper::getAllServices, token);
  }
  
  /**
   * <code>getDetails</code> method will return the details of the audit user.
   * 
   * @param email
   *          for which details are to be fetched.
   * @param token
   *          username password authentication token.
   * @return the Audit log. details response.
   */
  @RequestMapping(value = "/sysAdmin/audit/getAuditDetail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAuditDetails(@RequestParam String email,@RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String endDate,
      Authentication token) {
    return ControllerHelper.process(auditControllerHelper::getAuditDetails, token, email,fromDate,endDate);
  }
  
  /**
   * GetAuditHome method will return the audit home view.
   * 
   * @return the audit home view.
   */
  @RequestMapping(value="/sysAdmin/audit/home")
  public String getAuditHomeView(Authentication token) {
    return "auditHome";
  }
  
  /**
   * getAuditDetail method will return the audit home view.
   * 
   * @return the audit home view.
   */
  @RequestMapping(value="/sysAdmin/audit/detail")
  public String getAuditDetailView(Authentication token) {
    return "auditDetail";
  }
 
  
  /**
   * getAuditDetail method will return the audit home view.
   * 
   * @return the audit home view.
   */
  @RequestMapping(value=" /sysAdmin/audit/advancedFilterPopup")
  public String getAuditAdvancedPopup(Authentication token) {
    return "auditAdvancedPopup";
  }
}
