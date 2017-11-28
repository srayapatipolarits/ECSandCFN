package com.sp.web.theme;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.product.CompanyFactory;
import com.sp.web.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * SurePeopleMongoThemeSource class will provide the theme source for the SurePeople platform. It
 * will fetch he theme source from the theme using the cookie theme resolver and fetch the theme to
 * used for the company.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SurePeopleMongoThemeSource implements ThemeSource {
  
  @Autowired
  ThemeCacheableFactory themeCacheabeFacotry;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  HttpServletRequest httpServletRequest;
  
  /**
   * @see org.springframework.ui.context.ThemeSource#getTheme(java.lang.String)
   */
  @Override
  public Theme getTheme(String themeKey) {
    Theme theme = null;
    if (themeKey != null) {
      theme = themeCacheabeFacotry.getTheme(themeKey);
    }
    
    if (theme == null) {
//      CompanyDao company = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
      theme = themeCacheabeFacotry.getTheme(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
    }
//    setCompanyLogoInTheme(theme);
    return theme;
    
  }
  
  /**
   * @param theme
   */
  private void setCompanyLogoInTheme(Theme theme) {
    String companyId = getCompanyIdFromRequest();
    SurePeopleTheme peopleTheme = (SurePeopleTheme) theme;
    ThemeMessageSource messageSource = (ThemeMessageSource) peopleTheme.getMessageSource();
    if (StringUtils.isNotBlank(companyId)) {
      CompanyDao userCompany = companyFactory.getCompany(companyId);
      messageSource.getMessages().put(
          "logoUrl",
          messageSource.createMessageFormat(ImageUtils.getCompanyImage(userCompany),
              Constants.DEFAULT_COMPANY_LOGO_URL, null));
      if (theme.getName().equalsIgnoreCase(Constants.DEFAULT_THEME_NAME)
          && StringUtils.isBlank(userCompany.getLogoImage())) {
        messageSource.getMessages().put("logoCss",
            messageSource.createMessageFormat(null, Constants.DEFAULT_COMPANY_LOGO_CSS, null));
      } else {
        messageSource.getMessages().put("logoCss",
            messageSource.createMessageFormat("", Constants.DEFAULT_COMPANY_LOGO_CSS, null));
      }
      
    } else {
      if (theme.getName().equalsIgnoreCase(Constants.DEFAULT_THEME_NAME)) {
        messageSource.getMessages().put("logoCss",
            messageSource.createMessageFormat(null, Constants.DEFAULT_COMPANY_LOGO_CSS, null));
      } else {
        messageSource.getMessages().put("logoCss",
            messageSource.createMessageFormat("", Constants.DEFAULT_COMPANY_LOGO_CSS, null));
      }
    }
  }
  
  private String getCompanyIdFromRequest() {
    Cookie[] cookies = httpServletRequest.getCookies();
    String companyId = null;
    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equalsIgnoreCase("cid")) {
          companyId = cookie.getValue();
          break;
        }
      }
    }
    
    return companyId;
  }
}
