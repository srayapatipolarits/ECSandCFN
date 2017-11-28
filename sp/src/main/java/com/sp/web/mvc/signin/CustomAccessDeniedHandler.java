package com.sp.web.mvc.signin;

import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.utils.GenericUtils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 *
 *         The overloaded access denied handler.
 */
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
  
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    HttpSession session = request.getSession();
    SecurityContext securityContext = (SecurityContext) session
        .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    User user = GenericUtils.getUserFromAuthentication(securityContext.getAuthentication());
    
    if (!user.hasRole(RoleType.User)) {
      setErrorPage("/doLogout");
    } else {
      
      if (user.hasRole(RoleType.User)
          && (user.getUserStatus() == UserStatus.ASSESSMENT_PENDING || user.getUserStatus() == UserStatus.ASSESSMENT_PROGRESS)) {
        setErrorPage("/home");
      }
    }
    super.handle(request, response, accessDeniedException);
  }
}
