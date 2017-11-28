package com.sp.web.controller.systemadmin.company;

import com.sp.web.Constants;
import com.sp.web.controller.admin.account.CompanyThemeController;
import com.sp.web.controller.admin.account.CompanyThemeControllerHelper;
import com.sp.web.dto.company.CompanyThemeDTO;
import com.sp.web.form.CompanyThemeForm;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper clas for the {@link CompanyThemeController}.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class CompanyAdminThemeControllerHelper {
  
  @Autowired
  private CompanyThemeControllerHelper companyThemeControllerHelper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * <code>createTheme</code> method will create the theme.
   *
   * @param user
   *          logged in user.
   * @param param
   *          contains the sytles map form.
   */
  public SPResponse createTheme(User user, Object[] param) {
    
    CompanyThemeForm companyThemeForm = (CompanyThemeForm) param[0];
    
    /* create the theme */
    return companyThemeControllerHelper
        .saveTheme(companyThemeForm.getCompanyId(), companyThemeForm);
  }
  
  /**
   * <code>getThemeDetail</code> method will give the theme detail for the company.
   *
   * @param user
   *          logged in user.
   */
  public SPResponse getThemeDetail(User user, Object[] params) {
    String companyId = (String) params[0];
    return companyThemeControllerHelper.getThemeDetail(companyId);
  }
  
  /**
   * resetTheme method will reset the theme for the company passed to it.
   * 
   * @param user
   *          sysmte admin user.
   * @param params
   *          contains the company id for whcih resetTheme is to be done.
   * @return the spResponse.
   */
  public SPResponse resetTheme(User user, Object[] params) {
    String companyId = (String) params[0];
    return companyThemeControllerHelper.resetTheme(companyId);
  }
  
  /**
   * activateTheme method will activate the theme for the company to modify the theme selves.
   * 
   * @param user
   *          sysmte admin user.
   * @param params
   *          contains the company id for which resetTheme is to be done.
   * @return the spResponse.
   */
  public SPResponse activateTheme(User user, Object[] params) {
    String companyId = (String) params[0];
    return companyThemeControllerHelper.activateTheme(companyId);
  }
  
  /**
   * deActivateTheme method will deactivate the theme for the company to modify the theme selves.
   * 
   * @param user
   *          sysmte admin user.
   * @param params
   *          contains the company id for which resetTheme is to be done.
   * @return the spResponse.
   */
  public SPResponse deActivateTheme(User user, Object[] params) {
    String companyId = (String) params[0];
    return companyThemeControllerHelper.deActivateTheme(companyId);
  }
  
  /**
   * ClearThemeCache method will clear the theme cache for the system.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the companyid.
   * @return response.
   */
  public SPResponse clearThemeCache(User user, Object[] params) {
    String companyId = (String) params[0];
    return companyThemeControllerHelper.clearThemeCache(companyId);
  }
  
  /**
   * This is the helper method to get all the companies for Admin Theming Interface.
   * 
   * @param user
   *          - logged in user
   * @return the list of Companies
   */
  public SPResponse getAllCompanies(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory.findAllCompanies();
    
    resp.add(Constants.PARAM_COMPANY_LIST, findAllCompanies.stream().map(CompanyThemeDTO::new)
        .filter(comp -> !comp.getId().equalsIgnoreCase("default")).collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * This is the helper method to get all the customized companies for Admin Theming Interface.
   * 
   * @param user
   *          - logged in user
   * @return the list of Companies
   */
  public SPResponse getAllCustomizedCompany(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory.findAllCompanies();
    
    resp.add(
        Constants.PARAM_COMPANY_LIST,
        findAllCompanies
            .stream()
            .map(CompanyThemeDTO::new)
            .filter(
                comp -> !comp.getId().equalsIgnoreCase("default") && comp.isCompanyThemeActive() == true)
            .collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * This is the helper method to get all the not customized companies for Admin Theming Interface.
   * 
   * @param user
   *          - logged in user
   * @return the list of Companies
   */
  public SPResponse getAllNotCustomizedCompany(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory.findAllCompanies();
    
    resp.add(
        Constants.PARAM_COMPANY_LIST,
        findAllCompanies
            .stream()
            .map(CompanyThemeDTO::new)
            .filter(
                comp -> !comp.getId().equalsIgnoreCase("default") && comp.isCompanyThemeActive() == false)
            .collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * This is the helper method to get all the not customized companies for Admin Theming Interface.
   * 
   * @param user
   *          - logged in user
   * @return the list of Companies
   */
  public SPResponse removeThemeSettings(User user, Object[] params) {
    String companyId = (String) params[0];
    return companyThemeControllerHelper.removeThemeSettings(companyId);
  }
  
}
