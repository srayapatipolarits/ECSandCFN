package com.sp.web.mvc.signin;

import com.sp.web.Constants;
import com.sp.web.audit.Audit;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.Company;
import com.sp.web.model.CompanyTheme;
import com.sp.web.model.User;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.usertracking.UserTrackingType;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.message.MessageHandlerType;
import com.sp.web.service.message.SPMessageEnvelop;
import com.sp.web.service.message.SPMessageGateway;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author pruhil
 *
 *         The handler for the authentication success cases.
 */
public class CustomAuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {
  
  private static final Logger log = Logger.getLogger(CustomAuthenticationSuccessHandler.class);
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private ThemeResolver themeResolver;
  
  @Resource
  private CookieLocaleResolver localeResolver;
  
  private int cidCookieMaxTime;
  
  @Autowired
  private SPMessageGateway messageGateway;
  
  @Override
  @Audit(type = ServiceType.AUTHENTICATION)
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    if (log.isDebugEnabled()) {
      log.debug("Authenticatio Suuccess !!!" + authentication);
    }
    User user = (User) authentication.getPrincipal();
    String companyId = user.getCompanyId();
    if (StringUtils.isNotEmpty(companyId)) {
      CompanyDao company = companyFactory.getCompany(companyId);
      CompanyTheme companyTheme = company.getCompanyTheme();
      
      /* update the deafult theme key for the company. */
      if (companyTheme == null) {
        Company defaultCompany = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
        companyTheme = defaultCompany.getCompanyTheme();
      }
      
      String themeKey = company.getId();
      String currentTheme = themeResolver.resolveThemeName(request);
      /* in case there is a change in the theme, then only update. */
      if (currentTheme != null && !currentTheme.equalsIgnoreCase(themeKey)) {
        themeResolver.setThemeName(request, response, themeKey);
      }
    }
    Cookie cookie = new Cookie("cid", companyId);
    cookie.setMaxAge(cidCookieMaxTime);
    cookie.setPath("/");
    response.addCookie(cookie);
    
    /* set the locale */
    
    localeResolver.setLocale(request, response, user.getProfileSettings().getLocale());

    final HttpSession session = request.getSession(false);
    
    SPMessageEnvelop messageEnvelop = new SPMessageEnvelop();
    messageEnvelop.setMessageHandler(MessageHandlerType.EngagementMatrix);
    messageEnvelop.addData("userId", user.getId());
    messageEnvelop.addData("activityType", UserTrackingType.LoggedIn);
    if (log.isDebugEnabled()) {
      log.debug("Tracking logged in user" + messageEnvelop);
    }
    messageGateway.sendMessage(messageEnvelop);
    
    if (session != null) {
      SavedRequest savedRequest = (SavedRequest) session
          .getAttribute("SPRING_SECURITY_SAVED_REQUEST");
      
      if (savedRequest == null) {
        super.onAuthenticationSuccess(request, response, authentication);
        return;
      } else {
        String redirectUrl = savedRequest.getRedirectUrl();
        String targetUrl = request.getParameter("targetParam");
        if (org.springframework.util.StringUtils.hasText(targetUrl)) {
          
          setTargetUrlParameter("targetParam");
          setDefaultTargetUrl(redirectUrl.concat(targetUrl));
          setAlwaysUseDefaultTargetUrl(true);
        }
        
      }
      
    }
    
    super.onAuthenticationSuccess(request, response, authentication);
    setDefaultTargetUrl("/");
    setAlwaysUseDefaultTargetUrl(false);
    
  }
  
  public void setCidCookieMaxTime(int cidCookieMaxTime) {
    this.cidCookieMaxTime = cidCookieMaxTime;
  }
  
  public int getCidCookieMaxTime() {
    return cidCookieMaxTime;
  }
}
