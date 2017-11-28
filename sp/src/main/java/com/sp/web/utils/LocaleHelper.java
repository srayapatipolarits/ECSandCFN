package com.sp.web.utils;

import com.sp.web.Constants;

import org.apache.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author Dax Abraham
 *
 *         The helper class for all the locale related functions.
 */
public class LocaleHelper {
  
  public static final Logger log = Logger.getLogger(LocaleHelper.class);
  
  /**
   * @return - the locale of the current user.
   */
  public static Locale locale() {
    
    Locale locale = LocaleContextHolder.getLocale();
    if (log.isDebugEnabled()) {
      log.debug("Locale for formatting date is " + locale);
    }
    return locale;
  }

  /**
   * Checks if the locale is supported, if not supported sends the default locale.
   * 
   * @param locale
   *          - locale to check
   * @return
   *    the default locale
   */
  public static String isSupported(String locale) {
    return Constants.SUPPORTED_LOCALE_SET.contains(locale) ? locale : Constants.DEFAULT_LOCALE;
  }
}
