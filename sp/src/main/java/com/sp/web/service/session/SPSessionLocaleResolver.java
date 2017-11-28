package com.sp.web.service.session;

import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

// The Spring SessionLocaleResolver loads the default locale prior
// to the requests locale, we want the reverse.
@Component("localeResolver")
public class SPSessionLocaleResolver extends
    org.springframework.web.servlet.i18n.SessionLocaleResolver {
  
  public SPSessionLocaleResolver() {
    this.setDefaultLocale(new Locale("en", "US"));
  }
  
  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
    if (locale == null) {
      locale = determineDefaultLocale(request);
    }
    return locale;
  }
  
  @Override
  protected Locale determineDefaultLocale(HttpServletRequest request) {
    Locale defaultLocale = request.getLocale();
    if (defaultLocale == null) {
      defaultLocale = getDefaultLocale();
    }
    return defaultLocale;
  }
  
}