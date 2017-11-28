package com.sp.web.utils;

import static com.sp.web.utils.LocaleHelper.locale;

import com.sp.web.Constants;
import com.sp.web.controller.i18n.SPResourceBundleMessageSource;
import com.sp.web.model.Gender;
import com.sp.web.model.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         Helper class to get messages.
 */
public final class MessagesHelper {
  
  private static final Logger LOG = Logger.getLogger(MessagesHelper.class);
  /**
   * The singleton instance of the helper.
   */
  private static MessagesHelper helper = new MessagesHelper();
  private static Map<String, Map<String, String>> localizedMaleGenderTextMap;
  private static Map<String, Map<String, String>> localizedFemaleGenderTextMap;
  
  private static Map<String, String[]> localizedGenderTextArrayMap;
  
  /**
   * The reference of the message source.
   */
  MessageSource messageSource;
  
  /**
   * Hidden constructor for the singleton.
   */
  private MessagesHelper() {
    messageSource = (MessageSource) ApplicationContextUtils.getApplicationContext().getBean(
        "messageSource");
    localizedMaleGenderTextMap = new LinkedHashMap<String, Map<String, String>>();
    localizedFemaleGenderTextMap = new LinkedHashMap<String, Map<String, String>>();
    Map<String, String> maleGenderTextMap = new LinkedHashMap<String, String>();
    Map<String, String> femaleGenderTextMap = new LinkedHashMap<String, String>();
    Map<String, String> neutralGenderTextMap = new LinkedHashMap<String, String>();
    final String[] maleFormat = Constants.ASSESSMENT_QUESTIONS_MALE_FORMAT;
    final String[] femaleFormat = Constants.ASSESSMENT_QUESTIONS_FEMALE_FORMAT;
    final String[] neutralFormat = Constants.ASSESSMENT_QUESTIONS_NEUTRAL_FORMAT;
    for (int i = 0; i < maleFormat.length; i++) {
      String maleText = maleFormat[i];
      String femaleText = femaleFormat[i];
      String neutralText = neutralFormat[i];
      String key = maleText + StringUtils.capitalize(femaleText)
          + StringUtils.capitalize(neutralText);
      maleGenderTextMap.put(key, maleText);
      femaleGenderTextMap.put(key, femaleText);
      neutralGenderTextMap.put(key, neutralText);
    }
    localizedFemaleGenderTextMap.put(Constants.DEFAULT_LOCALE, femaleGenderTextMap);
    localizedMaleGenderTextMap.put(Constants.DEFAULT_LOCALE, maleGenderTextMap);
    localizedGenderTextArrayMap = new HashMap<String, String[]>();
    localizedGenderTextArrayMap.put(Constants.DEFAULT_LOCALE + Gender.M, maleFormat);
    localizedGenderTextArrayMap.put(Constants.DEFAULT_LOCALE + Gender.F, femaleFormat);
    localizedGenderTextArrayMap.put(Constants.DEFAULT_LOCALE + Gender.N, neutralFormat);
  }
  
  /**
   * The static method to get the messages from the resource bundle.
   * 
   * @param key
   *          - the key for the messages
   * @param args
   *          - the optional arguments
   * @return the message from the bundle
   */
  public static String getMessage(String key, Object... args) {
    // if (LOG.isDebugEnabled()) {
    // LOG.debug("Key :" + key + ", Args" + Arrays.toString(args));
    // }
    return getMessage(key, locale(), args);
  }
  
  /**
   * The static method to get the messages from the resource bundle.
   * 
   * @param key
   *          - the key for the messages
   * @param args
   *          - the optional arguments
   * @param locale
   *          for which message is to be returned
   * @return the message from the bundle
   */
  public static String getMessage(String key, Locale locale, Object... args) {
    // check if the key is null
    if (key == null) {
      return null;
    }
    
    return helper.message(key, locale, args);
  }
  
  /**
   * Gets the message for the given key from the messages bundle and the locale of the user.
   * 
   * @param key
   *          - the key for the message
   * @return the string message
   */
  private String message(String key, Locale locale, Object[] args) {
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    
    // if (LOG.isDebugEnabled()) {
    // LOG.debug("Key " + key + ", Locale: " + locale.toString());
    // }
    try {
      return messageSource.getMessage(key, args, locale);
    } catch (NoSuchMessageException ex) {
      // LOG.error("Messages not found," + ex.getMessage() + ", Total Keys"
      // + ((SPResourceBundleMessageSource) messageSource).getKeys(locale).size(), ex);
      LOG.error("Messages not found," + ex.getMessage(), ex);
      // ((ReloadableResourceBundleMessageSource) messageSource).clearCacheIncludingAncestors();
      return messageSource.getMessage(key, args, locale);
    }
  }
  
  /**
   * Special handling for relationship messages.
   * 
   * @param key
   *          - key
   * @param args
   *          - replacement arguments
   * @param locale
   *          - locale
   * @return the relationship messages replaced
   */
  public static String getRelationshipMessage(String key, Object[] args, Locale locale) {
    return helper.message(key, locale, args);
  }
  
  /**
   * The static method to get all the keys from the resource bundle.
   * 
   * @param locale
   *          - the locale
   * @return all the keys for the given locale
   */
  public static Set<String> getKeys(Locale locale) {
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    return helper.getAllKeys(locale);
  }
  
  /**
   * The static method to get all the keys from the resource bundle.
   * 
   * @param locale
   *          - the locale
   * @return all the keys for the given locale
   */
  public static Set<String> getKeys(Locale locale, String messagesFile) {
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    return helper.getAllKeys(locale, messagesFile);
  }
  
  /**
   * Gets the message for the given key from the messages bundle and the locale of the user.
   * 
   * @param key
   *          - the key for the message
   * @return the string message
   */
  private Set<String> getAllKeys(Locale locale, String messagesPropertiesFile) {
    SPResourceBundleMessageSource bundleMessageSource = (SPResourceBundleMessageSource) messageSource;
    return bundleMessageSource.getKeys(locale, messagesPropertiesFile);
  }
  
  /**
   * Gets the message for the given key from the messages bundle and the locale of the user.
   * 
   * @param key
   *          - the key for the message
   * @return the string message
   */
  private Set<String> getAllKeys(Locale locale) {
    SPResourceBundleMessageSource bundleMessageSource = (SPResourceBundleMessageSource) messageSource;
    return bundleMessageSource.getKeys(locale);
  }
  
  /**
   * Formats the date in the given language and locale.
   * 
   * @param dateToFormat
   *          - date to format
   * @return the formatted date
   */
  public static String formatDate(Date dateToFormat) {
    return formatDate(new DateTime(dateToFormat));
  }
  
  /**
   * Formats the date in the given language and locale.
   * 
   * @param dateToFormat
   *          - date to format
   * @return the formatted date
   */
  public static String formatDate(DateTime dateToFormat) {
    return dateToFormat.toString(getMessage("default.date.format"), LocaleHelper.locale());
  }
  
  public static String formatDate(LocalDate dateToFormat) {
    return formatDate(dateToFormat, new DateTimeFormatterFactory(getMessage("default.date.format"))
        .createDateTimeFormatter().withLocale(LocaleHelper.locale()));
  }
  
  public static String formatDate(LocalDate dateToFormat, Locale locale) {
    return formatDate(dateToFormat, new DateTimeFormatterFactory(getMessage("default.date.format"))
        .createDateTimeFormatter().withLocale(locale));
  }
  
  /**
   * Format the date using the default .
   * 
   * @param dateToFormat
   *          - date to format
   * @return formated date
   */
  public static String formatDate(LocalDateTime dateToFormat) {
    return formatDate(dateToFormat, "default");
  }
  
  /**
   * Format the date.
   * 
   * @param dateToFormat
   *          - date to format
   * @param key
   *          - string key
   * @return formatted date
   */
  public static String formatDate(LocalDateTime dateToFormat, String key) {
    String messageKey = "dateTime.format";
    if (StringUtils.isNotBlank(key)) {
      messageKey = messageKey + "." + key;
    }
    return formatDate(dateToFormat, new DateTimeFormatterFactory(getMessage(messageKey))
        .createDateTimeFormatter().withLocale(LocaleHelper.locale()));
  }
  
  public static String formatDate(LocalDate dateToFormat, String dateFormat) {
    return dateToFormat.format(new DateTimeFormatterFactory(dateFormat).createDateTimeFormatter()
        .withLocale(LocaleHelper.locale()));
  }
  
  public static String formatDate(LocalDate dateToFormat, DateTimeFormatter formatter) {
    return dateToFormat.format(formatter);
  }
  
  public static String formatDate(LocalDateTime dateToFormat, DateTimeFormatter formatter) {
    return dateToFormat.format(formatter);
  }
  
  public static String formatDate(Date date, String dateFormatPattern) {
    SimpleDateFormat format = new SimpleDateFormat(dateFormatPattern);
    return format.format(date);
  }
  
  /**
   * The helper method to normalize the passed text with the gender of the passed user, also change
   * the user first name. The format of the user name must be [Name].
   * 
   * @param key
   *          - the key in the properties file
   * @param user
   *          - the user
   * @return the gender normalized string
   */
  public static String genderNormalizeFromKey(String key, User user) {
    return genderNormalize(getMessage(key), user);
  }
  
  /**
   * The helper method to normalize the passed text with the gender of the passed user, also change
   * the user first name. The format of the user name must be [Name].
   * 
   * @param key
   *          - the key in the properties file
   * @param user
   *          - the user
   * @return the gender normalized string
   */
  public static String genderNormalizeFromKey(String key, User user, Locale locale) {
    return genderNormalize(getMessage(key, locale), user);
  }
  
  /**
   * Helper method to normalize the gender in the message passed.
   * 
   * @param message
   *          - message to normalize
   * @param user
   *          - user
   * @return the gender normalized message
   */
  public static String genderNormalize(String message, User user) {
    return genderNormalize(message, user, user.getLocale());
    
  }
  
  /**
   * Helper method to normalize the gender in the message passed.
   * 
   * @param message
   *          - message to normalize
   * @param user
   *          - user
   * @return the gender normalized message
   */
  public static String genderNormalize(String message, User user, Locale locale) {
    Assert.notNull(user, "User is required !!!");
    MessageFormat temp = new MessageFormat(message);
    if (Constants.DEFAULT_LOCALE.equalsIgnoreCase(locale.toString())) {
      return replaceName(
          temp.format((user.getGender() == Gender.M) ? Constants.ASSESSMENT_QUESTIONS_MALE_FORMAT
              : Constants.ASSESSMENT_QUESTIONS_FEMALE_FORMAT), user.getFirstName());
    } else {
      return replaceName(temp.format(getGenderTextArray(user)), user.getFirstName());
    }
  }
  
  /**
   * Helper method to normalize the gender in the message passed.
   * 
   * @param message
   *          - message
   * @param userFirstName
   *          - users fist name
   * @param genderText
   *          - gender text replacement array
   * @return the formatted string
   */
  public static String genderNormalize(String message, String userFirstName, String[] genderText) {
    MessageFormat temp = new MessageFormat(message);
    return replaceName(temp.format(genderText), userFirstName);
  }
  
  /**
   * Gets the localized gender formatted text.
   * 
   * @param user
   *          - user
   * @return the array for the gender formatted text in the locale
   */
  public static String[] getGenderTextArray(User user) {
    return getGenderTextArray(user.getLocale(), user.getGender());
  }
  
  /**
   * Gets the localized gender formatted text.
   * 
   * @param locale
   *          - locale
   * @param gender
   *          - gender
   * @return - the array for the gender formatted text in the locale
   */
  public static String[] getGenderTextArray(Locale locale, Gender gender) {
    String[] genderStrings = localizedGenderTextArrayMap.get(locale.toString() + gender);
    if (genderStrings == null) {
      String[] genderFormat = new String[7];
      String keyString = gender == Gender.M ? "male" : (gender == Gender.F ? "female" : "neutral");
      for (int i = 0; i < 7; i++) {
        genderFormat[0] = MessagesHelper.getMessage("generic.gender." + keyString + "." + i);
      }
      localizedGenderTextArrayMap.put(locale.toString() + gender, genderFormat);
    }
    return genderStrings;
  }
  
  /**
   * Replace the first name in the message passed.
   * 
   * @param message
   *          - message
   * @param firstName
   *          - first name to replace
   * @return the updated message
   */
  private static String replaceName(String message, String firstName) {
    return message.replaceAll("\\[Name\\]", (firstName != null) ? firstName : "");
  }
  
  /**
   * Get the gender text like he/she, him/her, etc.
   * 
   * @param user
   *          - user
   * @return the map of the text like
   */
  public static Map<String, String> getGenderText(User user) {
    Map<String, String> map = (user.getGender() == Gender.M) ? localizedMaleGenderTextMap.get(user
        .getUserLocale()) : localizedFemaleGenderTextMap.get(user.getUserLocale());
    if (map == null) {
      /* initialize the locale */
      Map<String, String> maleGenderTextMap = new HashMap<String, String>();
      Map<String, String> femaleGenderTextMap = new HashMap<String, String>();
      for (int i = 0; i < 7; i++) {
        String maleTextKey = MessagesHelper.getMessage("generic.gender.male." + i, new Locale("en",
            "US"));
        String maleText = MessagesHelper.getMessage("generic.gender.male." + i, user.getLocale());
        String femaleTextKey = MessagesHelper.getMessage("generic.gender.female." + i, new Locale(
            "en", "US"));
        String femaleText = MessagesHelper.getMessage("generic.gender.female." + i,
            user.getLocale());
        String key = maleTextKey + StringUtils.capitalize(femaleTextKey);
        maleGenderTextMap.put(key, maleText);
        femaleGenderTextMap.put(key, femaleText);
      }
      localizedFemaleGenderTextMap.put(user.getUserLocale(), femaleGenderTextMap);
      localizedMaleGenderTextMap.put(user.getUserLocale(), maleGenderTextMap);
    }
    return (user.getGender() == Gender.M) ? localizedMaleGenderTextMap.get(user.getUserLocale())
        : localizedFemaleGenderTextMap.get(user.getUserLocale());
    
  }
  
  /**
   * Gets the map of the user localized gender text.
   * 
   * @param locale
   *          - locale
   * @param gender
   *          - gender
   * @return the map of the localized gender text
   */
  public static Map<String, String> getGenderText(Locale locale, Gender gender) {
    Map<String, String> map = (gender == Gender.M) ? localizedMaleGenderTextMap.get(locale
        .toString()) : localizedFemaleGenderTextMap.get(locale.toString());
    if (map == null) {
      /* initialize the locale */
      Map<String, String> maleGenderTextMap = new LinkedHashMap<String, String>();
      Map<String, String> femaleGenderTextMap = new LinkedHashMap<String, String>();
      for (int i = 0; i < 7; i++) {
        String maleTextKey = MessagesHelper.getMessage("generic.gender.male." + i, new Locale("en",
            "US"));
        String maleText = MessagesHelper.getMessage("generic.gender.male." + i, locale);
        String femaleTextKey = MessagesHelper.getMessage("generic.gender.female." + i, new Locale(
            "en", "US"));
        String femaleText = MessagesHelper.getMessage("generic.gender.female." + i, locale);
        String key = maleTextKey + StringUtils.capitalize(femaleTextKey);
        maleGenderTextMap.put(key, maleText);
        femaleGenderTextMap.put(key, femaleText);
      }
      localizedFemaleGenderTextMap.put(locale.toString(), femaleGenderTextMap);
      localizedMaleGenderTextMap.put(locale.toString(), maleGenderTextMap);
    }
    return (gender == Gender.M) ? localizedMaleGenderTextMap.get(locale.toString())
        : localizedFemaleGenderTextMap.get(locale.toString());
  }
  
  public static String format(String key, Object... params) {
    MessageFormat message = new MessageFormat(key);
    return message.format(params);
  }
}
