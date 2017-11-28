package com.sp.web.controller.social;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * LinkedInController will connect to the linkedin and will get the connection of the logged in
 * user.
 * 
 * @author pradeep
 *
 */
@Controller
public class LinkedInController {
  
  /**
   * LinkedInControllerHelper.
   */
  @Inject
  private LinkedinControllerHelper linkedinControllerHelper;
  
  /**
   * <code>connections</code> method will return the json response of the user connections.
   * 
   * @param token
   *          of the user
   * @return the user.
   */
  @RequestMapping(value = "/linkedin/getProfileFull", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getUserProfile(Authentication token,
      @RequestParam(required = false, defaultValue = "false") boolean updateProfile) {
    return ControllerHelper.process(linkedinControllerHelper::getUserProfileFull, token,
        updateProfile);
  }
  
  /**
   * <code>getLinkedInView</code> method will whehter will show which view we have to return.
   * 
   * @param token
   *          of the user
   * @return the user.
   */
  @RequestMapping(value = "/resume/resumeDetailsView", method = RequestMethod.GET)
  public String getLinkedInView(Authentication token) {
    SPResponse process = ControllerHelper.process(linkedinControllerHelper::getLinkedInView, token);
    String redirectView = (String) process.getSuccess().get("redirectUrl");
    return redirectView;
    
  }
  
}
