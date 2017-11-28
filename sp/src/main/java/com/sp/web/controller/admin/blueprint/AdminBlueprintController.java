package com.sp.web.controller.admin.blueprint;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.blueprint.BlueprintSettingsForm;
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
 * <code>Action Plan Controller</code> provides admin services for blueprint settings.
 * 
 * @author vikram
 *
 */
@Controller
public class AdminBlueprintController {
  /**
   * AdminBlueprintControllerHelper is the controller helper class.
   */
  @Autowired
  private AdminBlueprintControllerHelper helper;
  
  /**
   * Method to get the all the blueprints for all companies.
   * 
   * @param token
   *          - logged in user
   * @return the list of Companies and there blueprint settings
   */
  @RequestMapping(value = "/sysAdmin/blueprint/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    return process(helper::getAll, token);
  }
  
  /**
   * Method to get all the companies who have purchased Blueprint feature.
   * 
   * @param token
   *          - logged in user
   * @return the list of Companies
   */
  @RequestMapping(value = "/sysAdmin/blueprint/getCompanies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanies(Authentication token) {
    return process(helper::getCompanies, token);
  }
  
  /**
   * Controller method to update blueprint settings.
   * 
   * @param blueprintSettingsForm
   *          - action plan data to update
   * @param token
   *          - the logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/sysAdmin/blueprint/createOrUpdate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createOrUpdate(@RequestBody BlueprintSettingsForm blueprintSettingsForm,
      Authentication token) {
    return process(helper::createOrUpdate, token, blueprintSettingsForm);
  }
  
  /**
   * This is the controller method to get the details of blueprint settings for respective company
   * id.
   * 
   * @param companyId
   *          - the company id
   * @param token
   *          - logged in user
   * @return the details of blueprint settings for respective company id
   */
  @RequestMapping(value = "/sysAdmin/blueprint/getDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDetails(@RequestParam String companyId,
      Authentication token) {
    return process(helper::getDetails, token, companyId);
  }
  
  /**
   * The controller method to delete blueprint settings for the given company id.
   * 
   * @param companyId
   *          - company
   * @param token
   *          - logged in user
   * @return the response for the delete operation
   */
  @RequestMapping(value = "/sysAdmin/blueprint/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam String companyId, Authentication token) {
    // process the get hiring subscriptions request
    return process(helper::delete, token, companyId);
  }
  
  /**
   * View For Blueprint Admin Landing Page.
   * 
   */
  @RequestMapping(value = "/sysAdmin/blueprint/home", method = RequestMethod.GET)
  public String bluePrintHome(Authentication token) {
    return "blueprintHome";
  }
  
  /**
   * View For Blueprint Update Settings Page.
   * 
   */
  @RequestMapping(value = "/sysAdmin/blueprint/update", method = RequestMethod.GET)
  public String bluePrintUpdate(Authentication token) {
    return "blueprintUpdate";
  }
  
  /**
   * Modal Popup View For Select Company.
   */
  @RequestMapping(value = "/sysAdmin/blueprint/selectCompany", method = RequestMethod.GET)
  public String openCompanyModal(Authentication token) {
    return "openCompanyModal";
  }
  
}
