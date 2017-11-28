package com.sp.web.controller.systemadmin.company;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.CompanyThemeForm;
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
 * CompanyAdminThemeController holds the request for applying/ disabling and enabling theme for the
 * company from the sysAdmin inteface.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class CompanyAdminThemeController {
  
  @Autowired
  private CompanyAdminThemeControllerHelper companyThemeControllerHelper;
  
  /**
   * createTheme will create the theme for the company passed.
   * 
   * @param companyForm
   *          contains the styles values with the key.
   * @param token
   *          logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/createTheme", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public SPResponse createTheme(@RequestBody CompanyThemeForm companyForm,
      Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::createTheme, token, companyForm);
    
  }
  
  /**
   * getThemeDetail request will give the theme detail for the company passed to it.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/getThemeDetail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getThemeDetail(@RequestParam String companyId,
      Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::getThemeDetail, token, companyId);
  }
  
  /**
   * resetTheme request will reset the theme for the company passed.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/resetTheme", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse resetTheme(@RequestParam String companyId,
      Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::resetTheme, token, companyId);
  }
  
  /**
   * activate request will activate the theme to be edited by the company administrators by
   * themselves.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/activate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse activateTheme(@RequestParam String companyId,
      Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::activateTheme, token, companyId);
  }
  
  /**
   * deactivate request will deactivate the theme to be editted by the company administrators by
   * themseleves.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/deactivate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deActivateTheme(@RequestParam String companyId,
      Authentication token) {
    return ControllerHelper
        .process(companyThemeControllerHelper::deActivateTheme, token, companyId);
  }
  
  /**
   * clearThemeCache will clear the mememory cache.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/clearThemeCache", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse clearThemeCache(@RequestParam String companyId,
      Authentication token) {
    return ControllerHelper
        .process(companyThemeControllerHelper::clearThemeCache, token, companyId);
  }
  
  /**
   * Get a list of all the companies in the system. This will provide necessary details needed for
   * Admin Theming Interface
   * 
   * @param token
   *          logged in user.
   * @return the SPResponse for list of all companies.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllCompanies(Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::getAllCompanies, token);
  }
  
  /**
   * Get a list of all the companies which have theme activated in the system. This will provide
   * necessary details needed for Admin Theming Interface
   * 
   * @param token
   *          logged in user.
   * @return the SPResponse for list of all companies.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/getAllCustomizedCompany", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllCustomizedCompany(Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::getAllCustomizedCompany, token);
  }
  
  /**
   * Get a list of all the companies which are not customized in the system. This will provide
   * necessary details needed for Admin Theming Interface
   * 
   * @param token
   *          logged in user.
   * @return the SPResponse for list of all companies.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/getAllNotCustomizedCompany", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllNotCustomizedCompany(Authentication token) {
    return ControllerHelper
        .process(companyThemeControllerHelper::getAllNotCustomizedCompany, token);
  }
  
  /**
   * removeThemeSettings request will remove all the theme settings for the company and reset it to
   * the default.
   * 
   * @param companyId for which theme setting are to be removed.
   * @param token
   *          logged in user.
   * @return the SPResponse for list of all companies.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/removeThemeSettings", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeThemeSettings(@RequestParam String companyId,
      Authentication token) {
    return ControllerHelper
        .process(companyThemeControllerHelper::removeThemeSettings, token, companyId);
  }
  
  /**
   * View For SystemAdmin Theming Pages.
   * 
   */
  @RequestMapping(value = "/sysAdmin/company/theming/home", method = RequestMethod.GET)
  public String themingHome(Authentication token) {
    return "sysAdminThemingHome";
  }
  
  /**
   * View For Theme Update Settings Page.
   * 
   */
  @RequestMapping(value = "/sysAdmin/company/theming/update", method = RequestMethod.GET)
  public String themingUpdate(Authentication token) {
    return "themingUpdate";
  }
  
  /**
   * Modal Popup View For Select Company.
   */
  @RequestMapping(value = "/sysAdmin/company/theming/selectCompany", method = RequestMethod.GET)
  public String themingOpenCompanyModal(Authentication token) {
    return "themingOpenCompanyModal";
  }
}
