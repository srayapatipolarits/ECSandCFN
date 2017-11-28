package com.sp.web.controller.hiring.match;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.hiring.match.AdminHiringPortraitListingDTO;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.match.AdminHiringPortraitMatchFactory;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller helper class for the admin portrait match services.
 */
@Component
public class AdminHiringPortraitMatchControllerHelper
    extends
    GenericControllerHelper<HiringPortrait, AdminHiringPortraitListingDTO, 
    AdminHiringPortriatDetailsDTO, AdminHiringPortraitMatchForm, AdminHiringPortraitMatchFactory> {
  
  private static final String MODULE_NAME = "portraitMatch";
  
  /**
   * Constructor.
   * 
   * @param factory
   *          - admin portrait match factory
   */
  @Inject
  public AdminHiringPortraitMatchControllerHelper(AdminHiringPortraitMatchFactory factory) {
    super(MODULE_NAME, factory);
  }
  
  /**
   * Controller helper method for assigning company to portrait.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the assign request
   */
  public SPResponse assignPortrait(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    AdminHiringPortraitMatchForm form = (AdminHiringPortraitMatchForm) params[0];
    form.validateAssign();

    factory.assignPortraitToCompany(user, form.getCompanyId(), form.getId());
    return resp.isSuccess();
  }
  
  /**
   * Controller helper method for assigning company to portrait.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the assign request
   */
  public SPResponse removePortrait(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    AdminHiringPortraitMatchForm form = (AdminHiringPortraitMatchForm) params[0];
    form.validateAssign();
    
    factory.removePortraitForCompany(form.getCompanyId(), form.getId());
    return resp.isSuccess();
  }
  
  /**
   * Controller helper method for add the document URL for company in the portrait.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the add request
   */
  public SPResponse addDocumentUrl(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    AdminHiringPortraitMatchForm form = (AdminHiringPortraitMatchForm) params[0];
    form.validateDocumentUrl();

    factory.addCompanyDocumentUrlToPortrait(form.getCompanyId(), form.getId(), form.getDocumentUrl());
    return resp.isSuccess();
  }
  
  /**
   * Controller helper method for remove the document URL for company in the portrait.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the remove request
   */
  public SPResponse removeDocumentUrl(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    AdminHiringPortraitMatchForm form = (AdminHiringPortraitMatchForm) params[0];
    form.validateAssign();

    factory.removeCompanyDocumentUrlFromPortrait(form.getCompanyId(), form.getId());
    return resp.isSuccess();
  }  

  /**
   * Helper method to get the companies with hiring.
   * 
   * @param user
   *          - user
   * @return
   *    the list of companies with hiring
   */
  public SPResponse getCompanies(User user) {
    final SPResponse resp = new SPResponse();
    return resp.add(MODULE_NAME + "Companies", factory.getAllCompaniesWithHiring());
  }  
 
}
