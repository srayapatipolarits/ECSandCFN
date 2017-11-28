package com.sp.web.controller.admin.tutorial;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.dto.tutorial.SPTutorialDTO;
import com.sp.web.dto.tutorial.SPTutorialListingDTO;
import com.sp.web.form.tutorial.SPTutorialForm;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller for the administrative interface for SPTutorial.
 */
@Controller
@RequestMapping("/sysAdmin/tutorial")
public class AdminSPTutorialController extends 
            GenericController<SPTutorialDao, 
                              SPTutorialListingDTO,
                              SPTutorialDTO, 
                              SPTutorialForm, 
                              AdminSPTutorialControllerHelper> {

  /**
   * Constructor.
   * 
   * @param helper
   *          - the admin tutorial controller helper
   */
  @Inject
  public AdminSPTutorialController(AdminSPTutorialControllerHelper helper) {
    super(helper);
  }
  
  /**
   * Tutorial Landing Page
   */
  @RequestMapping(value = "/home", method = RequestMethod.GET)
  public String home(Authentication token) {
    return "base";
  }
  
}
