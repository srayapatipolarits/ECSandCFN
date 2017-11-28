package com.sp.web.mvc.signin;

import com.sp.web.Constants;
import com.sp.web.mvc.SPResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * SignInController renders the login form.
 * 
 * @author Pradeep
 * 
 */
@Controller
public class LoginController {
  
  /** INitializing the logger. */
  private static final Logger LOG = Logger.getLogger(LoginController.class);
  
  @Autowired
  private MessageSource message;
  
  /**
   * Render the signin form to the person as HTML in their web browser. Returns void and relies in
   * request-to-view-name translation to kick-in to resolve the view template to render.
   */
  @RequestMapping(value = "/signin", method = RequestMethod.GET)
  public String login(Model model, Locale locale, HttpSession session, HttpServletResponse response) {
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Enter login()");
    }
    
    String email = Optional.ofNullable((String) session.getAttribute(Constants.PARAM_EMAIL))
        .orElse("");
    model.addAttribute(Constants.PARAM_EMAIL, email);
    
    String error = Optional.ofNullable((String) session.getAttribute(Constants.PARAM_ERROR_CODE))
        .orElse("");
    
    session.removeAttribute(Constants.PARAM_ERROR_CODE);
    
    if (session.getAttribute(Constants.SESSION_TIMEOUT) != null) {
      error = (String) session.getAttribute(Constants.SESSION_TIMEOUT);
    } else if (session.getAttribute("logout") != null) {
      error = null;
    }
    
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
    response.setDateHeader("Expires", 0);
    
    /*
     * check if the current request to show the login page is authentication fialed or not, in case
     * it is show the error message
     */
    if (StringUtils.isNotBlank(error)) {
      if (error.equals("9")) {
        return "companyBlockedError";
      }
      
      if (error.equals("8")) {
        return "linkExpiredProfileIncomplete";
      }
      
      if (error.equals("4")) {
        return "companyMembersBlockedError";
      }
      if (error.equals("11")) {
        return "expireChangePassword";
      }
      if (error.equals("7")) {
        return "individualBlockedError";
      } else if (error.equals("6")) {
        model.addAttribute("login_message",
            message.getMessage("login.authentication.failed." + error, null, locale));
      } else {
        model.addAttribute("login_error",
            message.getMessage("login.authentication.failed." + error, null, locale));
        session.removeAttribute(Constants.SESSION_TIMEOUT);
        if (session.getAttribute("failedAttempts.message") != null) {
          model.addAttribute("failed_attempts", session.getAttribute("failedAttempts.message"));
          session.removeAttribute("failedAttempts.message");
        }
      }
    }
    
    response.setHeader("redirect", "true");
    return "homeNotSignedIn";
  }
  
  @RequestMapping(value = "/doLogout", method = RequestMethod.GET)
  public String doLogout(Model model, Locale locale) {
    return "redirect:/logout";
  }
  
  /**
   * The controller method to send the logout flag when the user is coming from an error.
   * 
   * @return response to do logout
   */
  @RequestMapping(value = "/doLogout", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse doLogoutPost() {
    SPResponse resp = new SPResponse();
    resp.doRedirect();
    return resp;
  }
  
}
