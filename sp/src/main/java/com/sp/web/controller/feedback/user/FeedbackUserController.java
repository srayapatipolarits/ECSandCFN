/**
 * 
 */
package com.sp.web.controller.feedback.user;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * <code>FeedbackUserController</code> class will confirm the profile details of the feedback user
 * 
 * @author pradeep
 *
 */
@Controller
public class FeedbackUserController {

  @Autowired
  private FeedbackUserControllerHelper feedbackUserControllerHelper;

  /**
   * <code>confirmProfileDetails</code> method will confirm the profile details
   * 
   * @param accepted
   *          flag
   * @param httpSession
   *          httpSession
   * @return the confirm profile details.
   */
  @RequestMapping(value = "/feedback/external/confirmProfileDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse confirmProfileDetails(@RequestParam String accepted, HttpSession httpSession,
      @RequestParam String firstName, @RequestParam String lastName,
      @RequestParam(required = false) String linkedin) {
    return ControllerHelper.doProcess(
        feedbackUserControllerHelper::confirmProfileDetailsExternalUser, accepted, httpSession,
        firstName, lastName, linkedin);
  }

  /**
   * View For archiveDetail.
   * 
   */
  @RequestMapping(value = "/external/thankyou", method = RequestMethod.GET)
  public String feedbackThankYou(Authentication token,
      @RequestParam(required = false) String theme) {
    User user = GenericUtils.getUserFromAuthentication(token);
    if (user == null || user.getType() == UserType.External) {
      return "externalFeedbackThankYou";
    } else {
      return "memberFeedbackThankYou";
    }
    
  }
}
