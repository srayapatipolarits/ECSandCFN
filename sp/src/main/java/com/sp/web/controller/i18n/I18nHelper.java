package com.sp.web.controller.i18n;

import com.sp.web.mvc.SPResponse;

/**
 * I18nHelper is the helper interface for the internalization.
 * 
 * 
 * @author pruhil
 *
 */
public interface I18nHelper {

  /**
   * <code>getMessages</code> method will return the corresponding.
   * 
   * @param params
   *          contains the parameters for the message to be returned in the format, {1=Message key,
   *          3=Message params variable, 2= locale if passed }
   * @return the label in the form json response
   */
  SPResponse getMessages(Object[] params);

  /**
   * <code>getMessages</code> method will return all the labels containing the key in the form of
   * json.
   * 
   * @param params
   *          contains the parameters for the message to be returned in the format, {1=Message key,
   *          3=Message params variable, 2= locale if passed }
   * @return the label in the form json response
   */
  SPResponse getAllMessages(Object[] params);

  /**
   * <code>getMessages</code> method will return all the labels containing the key in the form of
   * json.
   * 
   * @param params
   *          contains the parameters for the message to be returned in the format, {1=Message key,
   *          3=Message params variable, 2= locale if passed }
   * @return the label in the form json response
   */
  SPResponse getAllMessagesForUrl(Object[] params);
  
}
