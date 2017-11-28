package com.sp.web.controller.token;

import com.sp.web.Constants;
import com.sp.web.controller.profile.ProfileControllerHelper;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.exception.SPException;
import com.sp.web.model.Token;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.pdf.PdfSourceType;
import com.sp.web.service.token.SPTokenFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author pruhil
 * 
 *         The generic token controller.
 */
@Controller
public class TokenController {
  
  private static final Logger LOG = Logger.getLogger(TokenController.class);
  
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  @Autowired
  private ThemeResolver themeResolver;
  
  @Autowired
  private ProfileControllerHelper helper;
  
  /**
   * <code>tokenRequest</code> method will handle all the token request and will call the token
   * processor to process the request.
   * 
   * @param token
   *          - token
   * @return the view to redirect to
   */
  @RequestMapping(value = "/processToken/{token}", method = RequestMethod.GET)
  public String tokenRequest(@PathVariable String token, HttpSession session, ModelMap model,
      HttpServletRequest request, HttpServletResponse response) {
    
    String viewToRedirect = null;
    try {
      // get the logged in user
      Token processedToken = spTokenFactory.processToken(token);
      session.setAttribute(Constants.PARAM_TOKEN, processedToken);
      /** in case of rediect not allowing request parameters to get appended by spring. */
      if (!processedToken.getRedirectToView().contains("redirect:")) {
        model.addAllAttributes(processedToken.getParamsMap());
      }
      themeResolver.setThemeName(request, response, processedToken.getThemeKey());
      // Set to expire far in the past.
      response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
      
      // Set standard HTTP/1.1 no-cache headers.
      response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
      
      // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
      response.addHeader("Cache-Control", "post-check=0, pre-check=0");
      
      // Set standard HTTP/1.0 no-cache header.
      response.setHeader("Pragma", "no-cache");
      
      /* set the cookie in case of hiring user */
      if (processedToken.getParam(Constants.PARAM_LOCALE) != null) {
        String locale = processedToken.getParamAsString(Constants.PARAM_LOCALE);
        Cookie cookie = WebUtils.getCookie(request, "sp-locale");
        if (cookie == null) {
          cookie = new Cookie("sp-locale", locale);
        }
        cookie.setPath("/");
        cookie.setValue(locale);
        response.addCookie(cookie);
        
      }
      viewToRedirect = processedToken.getRedirectToView();
    } catch (InvalidTokenException invalidTokenExp) {
      LOG.warn("Unable to process the request ", invalidTokenExp);
      viewToRedirect = invalidTokenExp.getRedirectView();
    } catch (SPException exp) {
      LOG.warn("Unable to process the request ", exp);
      // model.addAttribute(Constants.PARAM_ERROR, exp.getMessage());
    } catch (Exception e) {
      LOG.warn("Unable to process the request ", e);
      // model.addAttribute(Constants.PARAM_ERROR, e.getCause().getMessage());
    }
    return (viewToRedirect == null) ? Constants.VIEW_TOKEN_ERROR : viewToRedirect;
  }
}