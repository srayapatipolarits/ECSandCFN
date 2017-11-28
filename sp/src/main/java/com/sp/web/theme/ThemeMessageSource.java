package com.sp.web.theme;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.utils.ImageUtils;

import org.springframework.context.support.AbstractMessageSource;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ThemeMesssageSource contains the theme parameters which will be used in jsp.
 * 
 * @author pradeepruhil
 *
 */
public class ThemeMessageSource extends AbstractMessageSource implements Serializable {
  
  private static final long serialVersionUID = 232538384644296254L;
  private final Map<String, MessageFormat> messages;
  
  /**
   * Constructor for the theme message source.
   * 
   * @param company
   */
  public ThemeMessageSource(CompanyDao company) {
    messages = new HashMap<String, MessageFormat>();
    messages.put("cssStyle",
        createMessageFormat(company.getCssURL(), null));
    messages.put(
        "logoUrl",
        createMessageFormat(ImageUtils.getCompanyImage(company),
            Constants.DEFAULT_COMPANY_LOGO_URL, null));
  }
  
  public ThemeMessageSource() {
    messages = new HashMap<String, MessageFormat>();
  }
  
  public ThemeMessageSource(Map<String, MessageFormat> messages) {
    this.messages = messages;
  }
  
  /**
   * 
   * @see org.springframework.context.support.AbstractMessageSource#resolveCode(java.lang.String,
   *      java.util.Locale)
   */
  @Override
  protected MessageFormat resolveCode(String code, Locale locale) {
    return messages.get(code);
  }
  
  /**
   * Create a MessageFormat for the given message and Locale.
   * 
   * @param msg
   *          the message to create a MessageFormat for
   * @param locale
   *          the Locale to create a MessageFormat for
   * @param default message
   * @return the MessageFormat instance
   */
  public MessageFormat createMessageFormat(String msg, String defaultMessage, Locale locale) {
    return new MessageFormat((msg != null ? msg : defaultMessage), locale);
  }
  
  public Map<String, MessageFormat> getMessages() {
    return messages;
  }
  
  
}
