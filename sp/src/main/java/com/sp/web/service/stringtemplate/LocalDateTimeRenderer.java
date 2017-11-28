package com.sp.web.service.stringtemplate;

import com.sp.web.utils.MessagesHelper;

import org.stringtemplate.v4.AttributeRenderer;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * <code>LocalDateTimeRednere</code> for String template.
 * 
 * @author pradeepruhil
 *
 */
public class LocalDateTimeRenderer implements AttributeRenderer {

  @Override
  public String toString(Object object, String formatString, Locale locale) {
    LocalDateTime localDateTime;
    if (formatString == null) {
      formatString = "default";
    }

    if (object instanceof LocalDateTime) {
      localDateTime = ((LocalDateTime) object);
    } else {
      localDateTime = LocalDateTime.parse(object.toString());
    }
    return MessagesHelper.formatDate(localDateTime, formatString);
  }

}
