package com.sp.web.controller.i18n;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Locale;

/**
 * <code>MessageController</code> class provides the internalization for the SP Platerform.
 * 
 * @author pruhil
 *
 */
@Controller
public class MessageController {
  
  /** I18n Helper class. */
  @Autowired
  private I18nHelper i18nHelper;
  
  /**
   * <code>getMessage</code> method will return the single message key requested in the request
   * params.
   * 
   * @param key
   *          against which message to be returned
   * @param params
   *          to be replaced dynamically in the content labels
   * @param locale
   *          for which messages to be returned. Defaut is User Request.
   * @return the messages label json.
   */
  @RequestMapping(value = "/message", method = RequestMethod.POST)
  @ResponseBody
  @Audit(skip = true)
  public SPResponse getMessage(@RequestParam("key") String key,
      @RequestParam("params") String[] params, @RequestParam(required = false) Locale locale) {
    return ControllerHelper.doProcess(i18nHelper::getMessages, key, locale, params);
  }
  
  /**
   * <code>getAllMessages</code> method will return all the messages lables which matches with key
   * passed
   * 
   * @param locale
   *          if passed
   * @return the JSON response.
   */
  @RequestMapping(value = "/messages", method = RequestMethod.POST)
  @ResponseBody
  @Audit(skip = true)
  public SPResponse getAllMessages(@RequestParam(required = false) Locale locale) {
    return ControllerHelper.doProcess(i18nHelper::getAllMessages, locale);
  }
  
  /**
   * <code>getAllMessages</code> method will return all the messages lables which matches with key
   * passed
   * 
   * @param key
   *          for which labels are to be returned
   * @param params
   *          for dynamically replacing the parameters
   * @param locale
   *          if passed
   * @return the JSON response.
   */
  @RequestMapping(value = "/messagesForUrl", method = RequestMethod.POST)
  @ResponseBody
  @Audit(skip = true)
  public SPResponse getMessagesForUrl(@RequestParam("url") String key,
      @RequestParam(value = "params", required = false) String[] params,
      @RequestParam(required = false) Locale locale,
      @RequestParam(required = false) List<String> additionalKeys,
      @RequestParam(required = false, defaultValue = "false") String localStorage) {
    return ControllerHelper.doProcess(i18nHelper::getAllMessagesForUrl, key, locale, params,
        additionalKeys, localStorage);
  }
  
}
