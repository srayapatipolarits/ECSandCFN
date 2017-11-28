package com.sp.web.controller.hiring;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.form.hiring.user.ShareUserProfileForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Dax Abraham
 * 
 *         Controller class to share the hiring candidates portrait with other users in the company
 *         or a coach/expert.
 * 
 */
@Controller
public class HiringSharePortraitController {

  @Autowired
  private HiringSharePortraitControllerHelper helper;

  /**
   * The controller method to get the hiring users profile token.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getSharePortraitList", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserShareList(@RequestParam String userId, Authentication token) {
    return process(helper::getUserShareList, token, userId);
  }  

  /**
   * The controller method to get the hiring users profile token.
   * 
   * @param form
   *          - form for the share request
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getSharePortraitDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareUserProfile(ShareUserProfileForm form, Authentication token) {
    return process(helper::shareUserProfile, token, form);
  }  

  /**
   * The controller method to get the hiring users profile token.
   * 
   * @param userId
   *          - user id to delete
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/deleteSharePortrait", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteUserShare(@RequestParam String userId, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::deleteUserShare, token, userId);
  }  
  
  /**
   * Controller method to get the details for the user share portrait functionality.
   * 
   * @return the response to the get full profile request
   */
  @RequestMapping(value = "/hiring/ext/portrait/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserSharePortraitDetails(Authentication token) {
    
    // process the send notification method
    return process(helper::getUserSharePortraitDetails, token);
  }

  /**
   * Controller method to get the details for the user share portrait functionality.
   * 
   * @return the response to the get full profile request
   */
  @RequestMapping(value = "/hiring/ext/portrait/lens/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserSharePortraitLensDetails(HiringLensForm form, Authentication token) {
    
    // process the send notification method
    return process(helper::getUserSharePortraitLensDetails, token, form);
  }
}
