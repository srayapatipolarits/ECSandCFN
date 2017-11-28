package com.sp.web.controller.i18n;

import com.sp.web.Constants;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The messages factory to cache the internationalized messages.
 */
@Component
public class MessagesFactory {
  
  private static final Logger log = Logger.getLogger(MessagesFactory.class);
  
  @Autowired
  private Environment  enviornment;
  
  @Autowired
  private I18nMessagesHelper inHelper;
  
  /**
   * Get the messages with the given key and locale.
   * 
   * @param key
   *          - the key
   * @param locale
   *          - locale
   * @return
   *      all the messages properties with the given key
   */
  @Cacheable("i18nMessages")
  public Map<String, String> getMessagesWith(String key, Locale locale) {
    /*
     * find all the keys present in the messages helper for the corresponding key
     */
    Set<String> keys = MessagesHelper.getKeys(locale);
    
    final Map<String, String> props = new HashMap<String, String>();

    keys.stream().filter(k -> k.contains(key)).forEach(k -> {
        String message = MessagesHelper.getMessage(k, locale);
        props.put(k, message);
      });
    return props;
  }
  
  
  @CacheEvict(value = { "i18nMessages"}, allEntries = true)
  public void resetCache() {
    log.info("Cache reset called.");
  }

  /**
   * The traits messages.
   * @param locale 
   * 
   * @return
   *      - the traits messages
   */
  @Cacheable(value = "i18nMessages", key = "#root.methodName+#locale")
  public Map<String, String> getTraitsMessages(String locale) {
    final Map<String, String> traitsMap = new HashMap<String, String>();
    /*
     * Adding the traits localized messages to be shown on profile in case profile messages are
     * different
     */
    String traitMessge =  inHelper.getMessageFilesPath().get(Constants.PROFILE_TRAITS_MESSAGE_BASE_NAME);
    Set<String> keys = MessagesHelper.getKeys(LocaleHelper.locale(),traitMessge
        );
    /* iterate the keys and create the sp resposne message */
    keys.stream().forEach(k -> {
        traitsMap.put(k, MessagesHelper.getMessage(k, LocaleHelper.locale()));
      });
    
    return traitsMap;
  }
  
  /**
   * The traits messages.
   * 
   * @return
   *      - the traits messages
   */
  @Cacheable(value = "i18nMessages", key = "#root.methodName")
  public Map<String, String> getPersonalityTypeMessages() {
    final Map<String, String> personalityTypeMap = new HashMap<String, String>();
    /*
     * Adding the traits localized messages to be shown on profile in case profile messages are
     * different
     */
    String personalitytype = inHelper.getMessageFilesPath().get(Constants.PERSONALITY_TYPE_BASE_NAME);
    Set<String> keys = MessagesHelper.getKeys(LocaleHelper.locale(),
        personalitytype);
    /* iterate the keys and create the sp resposne message */
    keys.stream().forEach(k -> {
        personalityTypeMap.put(k, MessagesHelper.getMessage(k, LocaleHelper.locale()));
      });
    
    return personalityTypeMap;
  }
  
  /**
   * This method gets the messages properties from cache if not found sends null.
   * 
   * @param locale
   *          - locale
   * @return all the messages properties with the given key
   */
  @Cacheable(value = "i18nMessages", key = "#locale")
  public Map<String, String> getMessagesFromMessagesFiles(String locale) {
    return null;
  }
  
  /**
   * Get the messages with the given key and locale.
   * 
   * @param locale
   *          - locale
   * @return
   *      all the messages properties with the given key
   */
  @Cacheable(value = "i18nMessages", key = "#locale.toString()")
  public Map<String, String> getMessagesFromMessagesFiles(Locale locale, Set<String> messagesFiles) {
    /*
     * find all the keys present in the messages helper for the corresponding key
     */
    
    Set<String> keys = new HashSet<String>();
    for (String messageFile : messagesFiles) {
      
      keys.addAll(MessagesHelper.getKeys(locale,messageFile));
    }
    
    
    final Map<String, String> props = new HashMap<String, String>();

    keys.stream().forEach(k -> {
        String message = MessagesHelper.getMessage(k, locale);
        props.put(k, message);
      });
    return props;
  }
  
  
}
