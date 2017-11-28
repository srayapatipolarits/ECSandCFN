package com.sp.web.mvc.signin;

import com.sp.web.Constants;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham The AJAX filter to check if the session time out has
 *         occurred during an AJAX request.
 */
public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private static final Logger log = Logger.getLogger(AjaxAwareAuthenticationEntryPoint.class);
      
  public AjaxAwareAuthenticationEntryPoint(String loginUrl) {
    super(loginUrl);
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
    boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
    
    if (log.isDebugEnabled()) {
      log.debug("Ajax aware called. Flag :" + isAjax + ": Request is :" + request.getRequestURI());
    }
    
    if (isAjax) {
      HttpSession session = request.getSession();
      if (log.isInfoEnabled()) {
        log.debug("Ajax request, Goint to session timeout, 408 session code.");
      }
      
      // 5 is the error message for session timeout.
      session.setAttribute(Constants.SESSION_TIMEOUT, "5");
      response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT);
    } else {
      super.commence(request, response, authException);

    }
  }
}
