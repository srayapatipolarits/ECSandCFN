/**
 * 
 */
package com.sp.web.theme;

import org.springframework.context.MessageSource;
import org.springframework.ui.context.Theme;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * SurePeopleTheme is the cachable theme which stores the css url for the theme key.
 * 
 * @author pradeepruhil
 *
 */
public class SurePeopleTheme implements Serializable, Theme {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -4594315201154657247L;
  
  private String name;
  
  private MessageSource messageSource;
  
  /**
   * Create a SurePeopleTheme.
   * 
   * @param name
   *          the name of the theme
   * @param messageSource
   *          the MessageSource that resolves theme messages
   */
  public SurePeopleTheme(String name, MessageSource messageSource) {
    Assert.notNull(name, "Name must not be null");
    Assert.notNull(messageSource, "MessageSource must not be null");
    this.name = name;
    this.messageSource = messageSource;
  }
  
  /**
   * Default constructor.
   */
  public SurePeopleTheme() {
  }
  
  @Override
  public String getName() {
    return this.name;
  }
  
  @Override
  public MessageSource getMessageSource() {
    return this.messageSource;
  }
  
}
