package com.sp.web.controller.admin.account;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.CompanyThemeForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * CompanyThemeController holds the request for applying/ disabling and enabling theme for the
 * company.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class CompanyThemeController {
  
  @Autowired
  private CompanyThemeControllerHelper companyThemeControllerHelper;
  
  /**
   * saveTheme will update/save the theme for the company passed.
   * 
   * @param companyForm
   *          contains the styles values with the key.
   * @param token
   *          logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/admin/company/saveTheme", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public SPResponse saveTheme(@RequestBody CompanyThemeForm companyThemeForm,
      Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::saveTheme, token,
        companyThemeForm);
    
  }
  
  /**
   * getThemeDetail request will give the theme detailfor the logged in user.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/admin/company/getThemeDetail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getThemeDetail(Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::getThemeDetail, token);
  }
  
  /**
   * resetTheme request will give the theme detailfor the logged in user.
   * 
   * @param token
   *          logged in user.
   * @return the SPREsponse for the theme detail.
   */
  @RequestMapping(value = "/admin/company/resetTheme", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse resetTheme(Authentication token) {
    return ControllerHelper.process(companyThemeControllerHelper::resetTheme, token);
  }
  
  /**
   * View For Account Admin Theming Pages.
   * 
   */
  @RequestMapping(value = "/admin/company/theming/home", method = RequestMethod.GET)
  public String themingHome(Authentication token) {
    return "themingHome";
  }
}
