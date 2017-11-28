package com.sp.web.controller.i18n;

import com.sp.web.exception.SPException;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * <code>I18nHelper</code> class is helper class for the internalization.
 * 
 * @author pruhil
 *
 */
@Component
public class I18nMessagesHelper implements I18nHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(I18nMessagesHelper.class);
  
  private boolean fromCache;
  
  private Set<String> messageFileToIncluded;
  
  private Map<String, String> messageFilesPath = new HashMap<String, String>();
  
  @Autowired
  MessagesFactory messasgesFactory;
  
  /**
   * Constructor.
   * 
   * @param env
   *          - environment
   */
  @Inject
  public I18nMessagesHelper(Environment env) {
    this.fromCache = env.getProperty("i18n.enable.cache", Boolean.class, true);
    String sharePath = env.getProperty("sharedPath");
    String propertiesFilePath = sharePath.concat("/messages/properties");
    
    File fileDir = new File(propertiesFilePath);
    Collection<File> listFiles = FileUtils.listFiles(fileDir, TrueFileFilter.INSTANCE,
        TrueFileFilter.INSTANCE);
    messageFileToIncluded = new HashSet<String>();
    
    for (File file : listFiles) {
      String removeExtensionPath = FilenameUtils.removeExtension(file.getAbsolutePath());
      if (removeExtensionPath.contains("_")) {
        removeExtensionPath = removeExtensionPath.substring(0, removeExtensionPath.indexOf("_"));
      }
      messageFileToIncluded.add("file:" + removeExtensionPath);
      
      messageFilesPath
          .put(FilenameUtils.getBaseName(file.getName()), "file:" + removeExtensionPath);
    }
    
    if (messageFileToIncluded.isEmpty()) {
      throw new SPException("Configuration error, no properties file found in location :"
          + propertiesFilePath + ".");
    }
    
  }
  
  /**
   * <code>getMessages</code> method will return the messages of the properties in the form of the
   * json for the parameters key requested.
   * 
   * @param params
   *          Object array contains, the messages key, Locale and variable params to be replaced in
   *          the
   * @return the SP Repsonse
   */
  @Override
  public SPResponse getMessages(Object[] params) {
    
    final SPResponse response = new SPResponse();
    String key = (String) params[0];
    Locale locale = (Locale) params[1];
    String[] parameters = (String[]) params[2];
    if (LOG.isDebugEnabled()) {
      LOG.debug("Key: " + key + ", locale " + locale + ", parameters "
          + (parameters != null ? parameters.length : null));
    }
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    /* call the message helper to fetch the emssgae for the key */
    String message = MessagesHelper.getMessage(key, locale, (Object[]) parameters);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Message returned for the key " + key + ", is : " + message);
    }
    /* add the message in the key */
    response.add(key, message);
    return response;
  }
  
  /**
   * @see I18nHelper#getAllMessages(Object[]).
   * @param params
   *          contains the parameters for the message to be returned in the format, {1=Message key,
   *          2= locale, 3=Message params variable, if passed }
   */
  @SuppressWarnings("unchecked")
  @Override
  public SPResponse getAllMessagesForUrl(Object[] params) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Enter getAllMessage()");
    }
    
    final SPResponse spResponse = new SPResponse();
    
    String keyOrg = (String) params[0];
    Locale locale = (Locale) params[1];
    
    String[] parameters = (String[]) params[2];
    final List<String> additionalKeys = (List<String>) params[3];
    String localStorage = (String) params[4];
    if (LOG.isDebugEnabled()) {
      LOG.debug("Key: " + keyOrg + ", locale " + locale + ", parameters "
          + (parameters != null ? parameters.length : null) + ", localStorage" + localStorage);
    }
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    
    keyOrg = keyOrg.replace("/", ".");
    
    /* check if key start from sp, then replace */
    if (keyOrg.startsWith(".sp")) {
      keyOrg = keyOrg.replace(".sp.", "");
    }
    
    String key = keyOrg;
    
    /*
     * If URL contains process token URL, the strip the token parameter from the URL.
     */
    if (key.contains("processToken")) {
      /* get the first key to be added in the response */
      String[] paramArraySplit = StringUtils.splitByWholeSeparator(keyOrg, ".");
      key = paramArraySplit[0];
    }
    if (StringUtils.isNotEmpty(localStorage) && localStorage.equalsIgnoreCase("true")) {
      getAllMessages(spResponse, locale);
    } else {
      getAllMessagesDefault(key, spResponse, parameters, locale, additionalKeys);
    }
    
    return spResponse;
  }
  
  /**
   * @see I18nHelper#getAllMessages(Object[]).
   * @param params
   *          contains the parameters for the message to be returned in the format, {1=locale}
   */
  public SPResponse getAllMessages(Object[] params) {
    
    final SPResponse spResponse = new SPResponse();
    
    Locale locale = (Locale) params[0];
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    
    getAllMessages(spResponse, locale);
    
    return spResponse;
  }
  
  /**
   * <code>getAllMessagesLocalStorage</code> method will returns all the messages from files
   * configured in application.proeroties when the local storage is set to true.
   * 
   * @param spResponse
   *          spResponse
   * @param key
   *          for the url.
   * @param locale
   *          of the user.
   * @param additionalKeys
   *          additional keys
   * @param parameters
   *          parameteres if any wants to override.
   */
  private void getAllMessages(SPResponse spResponse, Locale locale) {
    
    if (fromCache) {
      
      Map<String, String> messagesFromMessagesFiles = messasgesFactory
          .getMessagesFromMessagesFiles(locale.toString());
      if (messagesFromMessagesFiles == null) {
        messagesFromMessagesFiles = messasgesFactory.getMessagesFromMessagesFiles(locale,
            messageFileToIncluded);
      }
      spResponse.setSuccess(messagesFromMessagesFiles);
    } else {
      /*
       * find all the keys present in the messages helper for the corresponding key
       */
      Set<String> keys = new HashSet<String>();
      for (String messageFile : messageFileToIncluded) {
        
        if (LOG.isDebugEnabled()) {
          LOG.debug("messageFile is " + messageFile);
        }
        
        keys.addAll(MessagesHelper.getKeys(locale, messageFile));
      }
      
      /* iterate the keys and create the sp resposne message */
      keys.stream().forEach(k -> spResponse.add(k, MessagesHelper.getMessage(k, locale)));
    }
    
  }
  
  private void getAllMessagesDefault(String key, SPResponse spResponse, String[] parameters,
      Locale locale, List<String> additionalKeys) {
    if (StringUtils.isNotBlank(key)) {
      
      switch (key) {
      case "profile":
        /*
         * Ignoring the profile messages as they are handled more dynamically in profile messages.
         */
        break;
      default:
        getGenericMessages(spResponse, parameters, locale, key);
      }
      
      // check if there are any additional keys to process
      // add the messages from the additional keys
      if (additionalKeys != null) {
        additionalKeys.forEach(k -> getGenericMessages(spResponse, parameters, locale, k));
      }
      
      if (LOG.isInfoEnabled()) {
        LOG.info("Exit getAllMessages()");
      }
    } else {
      spResponse.isSuccess();
    }
  }
  
  /**
   * <code>getGenericMessage</code> method will return the message for the internalization.
   * 
   * @param spResponse
   *          SP Response
   * @param parameters
   *          to be replaced with
   * @param locale
   *          of the user
   * @param key
   *          is the key to be searched in properties file.
   */
  public void getGenericMessages(SPResponse spResponse, String[] parameters, Locale locale,
      String key) {
    if (fromCache) {
      spResponse.add(messasgesFactory.getMessagesWith(key, locale));
    } else {
      /*
       * find all the keys present in the messages helper for the corresponding key
       */
      Set<String> keys = MessagesHelper.getKeys(locale);
      
      /* iterate the keys and create the sp resposne message */
      keys.stream()
          .filter(k -> k.contains(key))
          .forEach(
              k -> spResponse.add(k, MessagesHelper.getMessage(k, locale, (Object[]) parameters)));
    }
  }
  
  public Set<String> getMessageFileToIncluded() {
    return messageFileToIncluded;
  }
  
  public Map<String, String> getMessageFilesPath() {
    return messageFilesPath;
  }
}
