package com.sp.web.config;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.LocaleHelper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SPLocaleChangeInterceptor extends LocaleChangeInterceptor {
  
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    super.postHandle(request, response, handler, modelAndView);
    
    if (StringUtils.hasText(request.getParameter(getParamName()))) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      User userFromAuthentication = GenericUtils.getUserFromAuthentication(authentication);
      /* change the user locale */
      String localeString = request.getParameter(getParamName());

      localeString = LocaleHelper.isSupported(localeString);
      Locale locale = StringUtils.parseLocaleString(localeString);
      
      if (userFromAuthentication != null) {
        userFromAuthentication.getProfileSettings().setLocale(locale);
        UserFactory userFactory = ApplicationContextUtils.getBean(UserFactory.class);
        userFactory.updateUser(userFromAuthentication);
        
        /* update user in session */
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
      
      /* update the cookie */
      Cookie cookie = WebUtils.getCookie(request, "sp-locale");
      if (cookie == null) {
        cookie = new Cookie("sp-locale", Constants.DEFAULT_LOCALE);
      }
      cookie.setPath("/");
      if (locale == null) {
        locale = userFromAuthentication.getProfileSettings().getLocale();
        if (locale == null) {
          locale = new Locale("en", "US");
        }
      }
      
      cookie.setValue(locale.toString());
      response.addCookie(cookie);
    }
    
  }
}
