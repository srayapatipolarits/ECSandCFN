package com.sp.web.controller.profile;

import static com.sp.web.controller.ControllerHelper.doProcess;
import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.Constants;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 *
 *         The assessment controller to copy the users assessment either from hiring candidate, or
 *         another user's assessment.
 */
@Controller
public class CopyAssessmentController {

  @Autowired
  private CopyAssessmentControllerHelper helper;

  /**
   * The controller method to copy the assessments.
   *  
   * @param token
   *          - logged in user
   * @return
   *      the response to the copy assessment request
   */
  @RequestMapping(value = "/profileCopy/verifyEmail", method = RequestMethod.GET)
  public String verifyEmailView() {
    return "verifyEmail";
  }

  @RequestMapping(value = "/profileCopy/verifyEmailInvite", method = RequestMethod.GET)
  public String verifyEmailInviteView() {
    return "verifyEmailInvite";
  }

  @RequestMapping(value = "/profileCopy/verifySuccess", method = RequestMethod.GET)
  public String verifySuccessView() {
    return "verifySuccess";
  }

  @RequestMapping(value = "/profileCopy/verifyFailed", method = RequestMethod.GET)
  public String verifyFailedView() {
    return "verifyFailed";
  }  
  
  /**
   * Send the copy assessment request.
   * 
   * @param copyFromEmail
   *             - copy from email
   * @param token
   *             - current user
   * @return
   *    the response to the copy request
   */
  @RequestMapping(value = "/profileCopy/copyAssessment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse copyAssessment(@RequestParam String copyFromEmail, Authentication token) {
    return process(helper::copyAssessment, token, copyFromEmail);
  }
  
  /**
   * Helper method to process the authorization for the copy profile request. 
   * 
   * @param session
   *          - session
   * @return
   *      the response to the authorize request
   */
  @RequestMapping(value = "/profileCopy/authorizeCopyProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse authorizeCopyProfile(HttpSession session) {

    // process the send notification method
    return doProcess(helper::authorizeCopyProfile, session.getAttribute(Constants.PARAM_TOKEN));
  }
  
}
