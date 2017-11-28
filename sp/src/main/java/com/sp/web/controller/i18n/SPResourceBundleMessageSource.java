package com.sp.web.controller.i18n;

import org.apache.log4j.Logger;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * SPResourceBundleMessageSource is a realoadable resource bundle message source, whihc provides
 * method to return the all keys present in the message properties file.
 * 
 * @author pruhil
 *
 */
public class SPResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
  
  private static final Logger log = Logger.getLogger(SPResourceBundleMessageSource.class);
  
  /**
   * <code>getkeys</code> method will return the keys from ther resource bundle.
   * 
   * @param basename
   *          of the package from where resource bundle is installed.
   * @param locale
   *          of the request
   * @return the keys the messages
   */
  public Set<String> getKeys(Locale locale) {
    PropertiesHolder properties = getMergedProperties(locale);
    if (log.isDebugEnabled()) {
      log.debug("Locale " + locale + ", Properties : " + properties);
    }
    if (properties == null || properties.getProperties() == null) {
      return new HashSet<String>();
    }
    return properties.getProperties().stringPropertyNames();
  }
  
  /**
   * <code>getkeys</code> method will return the keys from ther resource bundle.
   * 
   * @param basename
   *          of the package from where resource bundle is installed.
   * @param locale
   *          of the request
   * @return the keys the messages
   */
  public Set<String> getKeys(Locale locale, String messagesProperties) {
    PropertiesHolder properties = getProperties(messagesProperties);
    
    if (log.isDebugEnabled()) {
      log.debug("Locale " + locale + ", Properties : " + properties);
    }
    
    if (properties == null || properties.getProperties() == null) {
      return new HashSet<String>();
    }
    return properties.getProperties().stringPropertyNames();
  }
}
