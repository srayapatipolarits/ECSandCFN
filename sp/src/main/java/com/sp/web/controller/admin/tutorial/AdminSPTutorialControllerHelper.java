package com.sp.web.controller.admin.tutorial;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.dto.tutorial.SPTutorialDTO;
import com.sp.web.dto.tutorial.SPTutorialListingDTO;
import com.sp.web.form.tutorial.SPTutorialForm;
import com.sp.web.service.tutorial.AdminSPTutorialFactory;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The helper class for the admin tutorial controller class.
 */
@Component
public class AdminSPTutorialControllerHelper extends 
        GenericControllerHelper<SPTutorialDao, 
                                SPTutorialListingDTO, 
                                SPTutorialDTO, 
                                SPTutorialForm, 
                                AdminSPTutorialFactory> {

  private static final String MODULE_NAME = "tutorial";
  
  /**
   * Constructor.
   * 
   * @param moduleName
   *            - module name
   * @param factory
   *            - the factory class for tutorial
   */
  @Inject
  public AdminSPTutorialControllerHelper(AdminSPTutorialFactory factory) {
    super(MODULE_NAME, factory);
  }
  
}
