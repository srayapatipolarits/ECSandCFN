package com.sp.web.controller.admin.blueprint;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.blueprint.BlueprintSettingsDTO;
import com.sp.web.form.blueprint.BlueprintSettingsForm;
import com.sp.web.model.Company;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.blueprint.BlueprintSettings;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.blueprint.BlueprintFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminBlueprintControllerHelper is the helper class for the blueprint admin services.
 * 
 * @author vikram
 *
 */
@Component
public class AdminBlueprintControllerHelper {

  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(AdminBlueprintControllerHelper.class);
 
  @Autowired
  private BlueprintFactory blueprintFactory;
  
  @Autowired
  private CompanyFactory companyFactory;

  /**
   * This is the helper method to get all blueprints configured in the system.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get all call
   */
  public SPResponse getAll(User user) {
    final SPResponse resp = new SPResponse();
    
    List<BlueprintSettings> blueprintSettingsList = blueprintFactory.getAllBlueprintSettings();
    
    // get all company & blueprint settings if Blueprint feature is enabled for that company
    // Set settings id and company in DTO
    resp.add(Constants.PARAM_BLUEPRINT_COMPANY_lIST, blueprintSettingsList.stream().map(bpset -> {
        CompanyDao company = companyFactory.getCompany(bpset.getCompanyId());
        Assert.notNull(company, "Company not found.");
        BaseCompanyDTO companyDTO = new BaseCompanyDTO(company);
        return companyDTO;
      }).collect(Collectors.toList()));

    return resp;
  }
  
  /**
   * This is the helper method to get all the companies who have purchased Blueprint feature.
   * 
   * @param user
   *          - logged in user
   * @return the list of Companies
   */
  public SPResponse getCompanies(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory.findCompaniesByFeature(SPFeature.Blueprint);
    final List<String> existingCompanyIdList = blueprintFactory.getAllBlueprintSettings().stream()
        .map(BlueprintSettings::getCompanyId).collect(Collectors.toList());    
    
    resp.add(
        "companies",
        findAllCompanies.stream().filter(c -> !existingCompanyIdList.contains(c.getId()))
            .map(CompanyDTO::new).collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * Helper to update the blueprint settings.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params containing the form data
   *          
   * @return the response to the create request
   */
  public SPResponse createOrUpdate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    BlueprintSettingsForm blueprintSettingsForm = (BlueprintSettingsForm) params[0];
    
    // if company id not found in form, will set company id as Default, create a new Blueprint
    // settings as default
    // if company id not found in settings table, it means its a new request fr creating company
    // blueprint settings
    
    final String companyId = blueprintSettingsForm.getCompanyId();
    Assert.hasText(companyId, "Company id is required.");
    
    if (log.isDebugEnabled()) {
      log.debug("Updating blueprint Settings for Company id: " + companyId);
    }

    // validate the company
    if (!companyId.equals(Constants.BLUEPRINT_DEFAULT_COMPANY_ID)) {
      CompanyDao company = companyFactory.getCompany(companyId);
      Assert.notNull(company, "Company not found.");
    }
    
    // get the blueprint settings and update it to the DB
    blueprintFactory.updateBlueprintSettings(blueprintSettingsForm.updateBlueprint(blueprintFactory
        .getBlueprintSettings(companyId)));

    return resp.isSuccess();
  }
  
  /**
   * Helper method to get blueprint settings.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return blueprint settings
   */
  public SPResponse getDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String companyId = (String) params[0];
    Assert.hasText(companyId, "Company id is required.");
    
    if (log.isDebugEnabled()) {
      log.debug("Get blueprint Settings Details for Company id: " + companyId);
    }
    
    BlueprintSettings blueprintSettings = blueprintFactory.getBlueprintSettings(companyId);
    Assert.notNull(blueprintSettings, "Blueprint Settings not found.");
    CompanyDao company = null;
    if (!companyId.equals(Constants.BLUEPRINT_DEFAULT_COMPANY_ID)) {
      company = companyFactory.getCompany(companyId);
    }
    
    if (company != null) {
      return resp.add(Constants.PARAM_BLUEPRINT_SETTINGS, new BlueprintSettingsDTO(
          blueprintSettings, company));
    } else {
      return resp.add(Constants.PARAM_BLUEPRINT_SETTINGS, new BlueprintSettingsDTO(
          blueprintSettings));
      
    }
   
  }
  
  /**
   * Helper method to delete the blueprint settings for the given company id.
   * 
   * @param user
   *           - user
   * @param params
   *          - params
   * @return
   *    the success or failure flags
   */
  public SPResponse delete(User user, Object[] params) {
    
    String companyId = (String) params[0];
    Assert.hasText(companyId, "Company id is required.");
    
    if (log.isDebugEnabled()) {
      log.debug("Delete blueprint Settings for Company id: " + companyId);
    }
    
    SPResponse resp = new SPResponse();
    
    Assert.isTrue(!companyId.equals(Constants.BLUEPRINT_DEFAULT_COMPANY_ID),
        "Cannot delete default blueprint settings.");
    
    blueprintFactory.deleteBlueprintSettings(companyId);
    
    return resp.isSuccess();
  }
}
