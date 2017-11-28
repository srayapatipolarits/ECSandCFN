package com.sp.web.utils;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Dax Abraham
 *
 *         The utility class to process urls.
 */
public class UrlUtils {
  
  private static final UrlValidator urlValidator = new UrlValidator(
      new String[] { "http", "https" });
  
  /**
   * Utility method to check if the given URL is a linked in URL.
   * 
   * @param webURL
   *          - web url
   * @return the flag to indicate if it is a linked in url
   */
  public static boolean isLinkedInUrl(String webURL) {
    return webURL.toLowerCase().contains("linkedin");
  }
  
  /**
   * Normalizes the web url i.e. adds an http:// before it in case it does not have any.
   * 
   * @param webURL
   *          - web url
   * @return the normalized web URL
   */
  public static String normailzeUrl(String webURL) {
    if (webURL != null && !webURL.trim().isEmpty()) {
      if (!urlValidator.isValid(webURL)) {
        return "http://" + webURL;
      }
    }
    return webURL;
  }
  
  /**
   * getReltive URL will return the relative URL from the absolute URL.
   * 
   * @param absouteUrl
   *          absolute url
   * @return the relative url.
   */
  public static String getRelativeUrl(String absouteUrl) {
    String relativeUrl = null;
    try {
      URL url = new URL(absouteUrl);
      String hostWithPort = url.getAuthority();
      
      relativeUrl = absouteUrl.substring(absouteUrl.indexOf(hostWithPort) + hostWithPort.length());
      
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return relativeUrl;
  }
  
}
