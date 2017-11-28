package com.sp.web.mvc.home;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for the homepage of the application.
 * 
 * @author Dax Abraham
 */
@Controller
public class HomeController {
  
  @Autowired
  private HomeHelper homeHelper;
  
  /**
   * Renders the home page as HTML in thw web browser. The home page is different based on whether
   * the user is signed in or not.
   */
  
  @RequestMapping(value = "/home", method = RequestMethod.GET)
  public String home(Authentication token) {
    
    if (token.getPrincipal() == null) {
      return "homeNotSignedIn";
    }
    
    User user = GenericUtils.getUserFromAuthentication(token);
    
    if (user.getUserStatus() == null) {
      return "error";
    }
    
    if (user.hasRole(RoleType.SuperAdministrator)) {
      return "redirect:sysAdmin/home";
    }
    
    if (!user.hasAnyRole(RoleType.User, RoleType.Hiring)) {
      return "redirect:logout";
    }
    
    switch (user.getUserStatus()) {
    case ASSESSMENT_PENDING:
      return "assessmentPending";
    case VALID:
      if (user.hasRole(RoleType.Hiring) && !user.hasRole(RoleType.User)) {
        return "redirect:pa/dashboard";
      } else {
        return "redirect:dashboard";
      }
    case ASSESSMENT_PROGRESS:
      return "assessmentInProgress";
    case PROFILE_INCOMPLETE:
      return "incompleteProfile";
    default:
      if (user.hasRole(RoleType.Hiring) && !user.hasRole(RoleType.User)) {
        return "redirect:pa/dashboard";
      }
      return "homeSignedIn";
    }
  }
  
  /**
   * Renders the home page as HTML in thw web browser. The home page is different based on whether
   * the user is signed in or not.
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String home() {
    return "redirect:home";
    
  }
  
  /**
   * 
   * @param token
   * @return
   */
  @RequestMapping(value = "/incompleteProfile")
  public @ResponseBody SPResponse incompleteProfile(Authentication token) {
    if (token == null) {
      return new SPResponse();
    }
    return ControllerHelper.process(homeHelper::sendInCompleteProfile, token);
  }
  
  /**
   * Renders the home page as HTML in thw web browser. The home page is different based on whether
   * the user is signed in or not.
   */
  @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
  public String accessDenied() {
    return "redirect:logout";
    
  }
}
