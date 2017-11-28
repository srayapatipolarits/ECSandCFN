package com.sp.web.controller.utility;

import com.sp.web.Constants;
import com.sp.web.exception.SPException;
import com.sp.web.model.ContentReference;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.og.SPOgFactory;
import com.sp.web.utils.UrlUtils;
import com.sp.web.utils.og.OpenGraph;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 * 
 *         The utilities controller helper.
 */
@Component
public class UtilitiesControllerHelper {
  
  private static final Logger log = Logger.getLogger(UtilitiesControllerHelper.class);
  
  @Autowired
  private SPOgFactory ogFactory;
  
  /**
   * Helper method to get open graph details.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the process OG request
   */
  public SPResponse processOG(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String url = (String) params[0];
    Assert.hasText(url, "Url not found.");
    boolean newTab = true;
    try {
      OpenGraph og = null;
      if (url.contains("/sp/")) {
        /* strip the host name */
        og = new OpenGraph(true);
        ogFactory.getOGData(og, UrlUtils.getRelativeUrl(url),user);
        newTab = false;
      } else {
        og = new OpenGraph(url, true);
      }
      
      ContentReference contentRef = og.getContentRef();
      contentRef.setNewTab(newTab);
      resp.add(Constants.PARAM_CONTENT_REFERENCE,contentRef);
    } catch (Exception e) {
      log.warn("Unable to process URL :" + url, e);
      resp.addError(new SPException(e));
    }
    return resp;
  }
}
