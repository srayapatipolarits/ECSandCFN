package com.sp.web.controller.reset;

import static com.sp.web.controller.ControllerHelper.doProcess;

import com.sp.web.Constants;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.password.ChangePasswordForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * <code>ResetController</code> perform the reset password of the user.
 * <p>
 * It will call the sent the user an email to rest and the password
 * </p>
 * 
 * @author pruhil
 */
@Controller
@Scope("session")
public class ResetController {
  
  /** ResetPassword heler. */
  @Autowired
  private ResetPasswordHelper resetPasswordHelper;
  
  /**
   * <code>resetForm</code> method will open the reset password form.
   * 
   * @return the reset form
   */
  @RequestMapping(value = "/reset", method = RequestMethod.GET)
  public String resetForm() {
    return "resetForm";
  }
  
  /**
   * <code>sendResetEmail</code> method will send the reset email if the email enerted is valid in
   * the system
   * 
   * @param email
   *          email to be sent
   * @return the success or failure respone.
   */
  @RequestMapping(value = "/sendResetEmail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse sendResetEmail(@RequestParam String email) {
    
    return doProcess(resetPasswordHelper::sendResetEmail, email);
  }
  
  /**
   * <code>changePassword</code>method will change the password and login the user.
   * 
   * @param resetPaswordForm
   *          - reset password form
   * @param httpSession
   *          - session
   * @return the response to the change password
   */
  @RequestMapping(value = "/newPassword", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse changePassword(@Valid ChangePasswordForm passwordForm, HttpSession httpSession) {
    
    return doProcess(resetPasswordHelper::setNewPassword, passwordForm.getNewPassword(),
        httpSession);
  }
  
  /**
   * View For enable cookie.
   * 
   */
  @RequestMapping(value = "/enable/cookie", method = RequestMethod.GET)
  public String validateCookieEnable(Authentication token,
      @RequestParam(required = false) String theme) {
    
    return "enableCookie";
  }
  
  /**
   * Change the password of the logged in user.
   * 
   */
  @ResponseBody
  @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
  public SPResponse changePassword(Authentication token, @Valid ChangePasswordForm passwordForm) {
    return ControllerHelper.process(resetPasswordHelper::changePassword, token,
        passwordForm.getOldPassword(), passwordForm.getNewPassword());
  }
  
  /**
   * Change the password of the logged in user.
   * 
   */
  @ResponseBody
  @RequestMapping(value = "/changePasword/expired", method = RequestMethod.POST)
  public SPResponse changePasswordExpired(@Valid ChangePasswordForm passwordForm, HttpSession httpSession) {
    String email = Optional.ofNullable((String) httpSession.getAttribute(Constants.PARAM_EMAIL))
        .orElse("");
    httpSession.removeAttribute(Constants.PARAM_ERROR_CODE);
    return ControllerHelper.doProcess(resetPasswordHelper::changePasswordExpired,
        passwordForm.getOldPassword(), passwordForm.getNewPassword(),email);
  }
  
  /**
   * Change the password of the logged in user.
   * 
   */
  @ResponseBody
  @RequestMapping(value = "/sysAdmin/changeSuperPassword", method = RequestMethod.POST)
  public SPResponse changeSuperPassword(Authentication token, @RequestParam String oldPassword,
      @RequestParam String newPassword) {
    return ControllerHelper.process(resetPasswordHelper::changeSuperPassword, token, oldPassword,
        newPassword);
  }
}
