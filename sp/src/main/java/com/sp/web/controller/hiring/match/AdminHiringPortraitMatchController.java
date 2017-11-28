package com.sp.web.controller.hiring.match;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.hiring.match.AdminHiringPortraitListingDTO;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller class for the administration screens for the Portrait Match module.
 */
@Controller
@RequestMapping("/hiring/admin/match")
public class AdminHiringPortraitMatchController
    extends
    GenericController<HiringPortrait, AdminHiringPortraitListingDTO, 
                      AdminHiringPortriatDetailsDTO, AdminHiringPortraitMatchForm, 
                      AdminHiringPortraitMatchControllerHelper> {
  
  /**
   * Constructor.
   * 
   * @param helper
   *          - controller helper
   */
  @Inject
  public AdminHiringPortraitMatchController(AdminHiringPortraitMatchControllerHelper helper) {
    super(helper);
  }
  
  /**
   * Controller method to assign portrait to company.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return the response to the assign request
   */
  @RequestMapping(value = "/assignPortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse assignPortrait(AdminHiringPortraitMatchForm form, Authentication token) {
    return process(helper::assignPortrait, token, form);
  }
  
  /**
   * Controller method to remove portrait for company.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/removePortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removePortrait(AdminHiringPortraitMatchForm form, Authentication token) {
    return process(helper::removePortrait, token, form);
  }
  
  /**
   * Controller method to add company document URL for portrait.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/addDocumentUrl", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addDocumentUrl(AdminHiringPortraitMatchForm form, Authentication token) {
    return process(helper::addDocumentUrl, token, form);
  }

  /**
   * Controller method to remove company document URL for portrait.
   * 
   * @param form
   *          - form
   * @param token
   *          - logged in user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/removeDocumentUrl", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeDocumentUrl(AdminHiringPortraitMatchForm form, Authentication token) {
    return process(helper::removeDocumentUrl, token, form);
  }

  /**
   * Controller method to get all the companies with the hiring feature.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get all request
   */
  @RequestMapping(value = "/getCompanies", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompanies(Authentication token) {
    return process(helper::getCompanies, token);
  }
  
}
