package com.sp.web.controller.profile;

import static com.sp.web.controller.ControllerHelper.doProcess;
import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 *
 *         The profile controller.
 */
@Controller
public class ProfileController {
  
  @Autowired
  private ProfileControllerHelper profileControllerHelper;
  
  /**
   * The controller method to get the profile landing page.
   *
   * @param token
   *          - logged in user
   * @return the view to load
   */
  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  public String sendNotification(Authentication token) {
    
    // process the send notification method
    return "profile";
  }
  
  @RequestMapping(value = "/profilePublic/{profileToken}", method = RequestMethod.GET)
  public String publicProfileView() {
    return "sharePortraitPublic";
  }
  
  @RequestMapping(value = "/spCertificate/{certificateToken}", method = RequestMethod.GET)
  public String certificateView() {
    return "spCertificate";
  }
  
  /**
   * The controller method to get the logged in user profile.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get profile request
   */
  @RequestMapping(value = "/signedIn/getProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProfile(Authentication token) {
    
    // process the send notification method
    return process(profileControllerHelper::getProfile, token);
  }
  
  /**
   * Service to get the logged in user analysis.
   * 
   * @param token
   *          - logged in user
   * @return - the response to the get analysis request
   */
  @RequestMapping(value = "/signedIn/getAnalysis", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse sendMemberAnalysis(Authentication token) {
    
    // process the send notification method
    return process(profileControllerHelper::getAnalysis, token);
  }
  
  /**
   * Service to get the logged in user analysis.
   * 
   * @param token
   *          - logged in user
   * @return - the response to the get analysis request
   */
  @RequestMapping(value = "/signedIn/getAnalysisFull", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse sendUserFullAnalysis(Authentication token,
      @RequestParam(required = false) String email) {
    
    // process the send notification method
    return process(profileControllerHelper::getFullAnalysis, token, email);
  }
  
  /**
   * Service to get the logged in user analysis.
   * 
   * @param token
   *          - logged in user
   * @return - the response to the get analysis request
   */
  @RequestMapping(value = "/profile/getPersonalityAnalysis", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPersonalityAnalysis(Authentication token,
      @RequestParam(required = false) String email) {
    
    // process the send notification method
    return process(profileControllerHelper::getPersonalityAnalysis, token, email);
  }
  
  /**
   * Controller method to the public profile view from token for hiring user.
   * 
   * @param profileToken
   *          - profile token
   * @return the response to the get full analysis request
   */
  @RequestMapping(value = "/profilePublic/{profileToken}", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPublicProfile(@PathVariable String profileToken) {
    
    // process the send notification method
    return doProcess(profileControllerHelper::getAnalysisFullPublic, profileToken);
  }
  
  /**
   * Get the public profile for the public certificate link.
   * 
   * @param certificateToken
   *          - certificate token
   * @return the response to the get certificate token request
   */
  @RequestMapping(value = "/spCertificate/{certificateToken}", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCertificateProfile(@PathVariable String certificateToken) {
    
    // process the send notification method
    return doProcess(profileControllerHelper::getAnalysisFullCertificate, certificateToken);
  }
  
  /**
   * Service to get the logged in user analysis.
   * 
   * @param token
   *          - logged in user
   * @return - the response to the get analysis request
   */
  @RequestMapping(value = "/profile/hiring/getHiringPersonalityAnalysis", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getHiringPersonalityAnalysis(Authentication token,
      @RequestParam(required = false) String id) {
    
    // process the send notification method
    return process(profileControllerHelper::getHiringUserFullAnalysis, token, id);
  }
  
}