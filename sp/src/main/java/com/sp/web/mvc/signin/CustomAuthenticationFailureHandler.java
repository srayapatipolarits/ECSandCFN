package com.sp.web.mvc.signin;

import com.sp.web.Constants;
import com.sp.web.exception.SignInFailedException;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dax Abraham
 *
 *         The handler for the authentication failure cases.
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
  
  private static final Logger log = Logger.getLogger(CustomAuthenticationFailureHandler.class);
  
  private static final String BASE_SIGNIN_LOGIN_ERROR_URL = "/signin";
  
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    setDefaultFailureUrl(BASE_SIGNIN_LOGIN_ERROR_URL);
    super.onAuthenticationFailure(request, response, exception);
    
    if (log.isDebugEnabled()) {
      log.debug("Login failed !!!", exception);
    }
    
    request.getSession().setAttribute(Constants.PARAM_EMAIL,
        request.getParameter(Constants.PARAM_EMAIL));
    String errorCode = "1";
    if (exception.getClass().isAssignableFrom(UsernameNotFoundException.class)) {
      errorCode = "1";
    } else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
      errorCode = "2";
      request.getSession().setAttribute("failedAttempts.message", exception.getMessage());
    } else if (exception.getClass().isAssignableFrom(SignInFailedException.class)) {
      switch (((SignInFailedException) exception).getReason()) {
      case CompanyBlocked:
        errorCode = "9";
        break;
      case CompanyMemberBlocked:
        errorCode = "4";
        break;
      case IndividualBlocked:
        errorCode = "7";
        break;
      case UserAccountLocked:
        errorCode = "10";
        break;
      case PasswordExpired:
        errorCode = "11";
        break;
      default:
        errorCode = "3";
      }
    }
    
    request.getSession().setAttribute(Constants.PARAM_ERROR_CODE, errorCode);
  }
}
