package com.sp.web.theme;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.CompanyTheme;
import com.sp.web.model.User;
import com.sp.web.product.CompanyFactory;
import com.sp.web.utils.GenericUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.context.Theme;

/**
 * ThemeCacheableFactory class will act as factory to clear the cache which will fetcht he theme to
 * used from the MOngo.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class ThemeCacheableFactory {
  
  /** INitializng the logger. */
  private static final Logger LOG = Logger.getLogger(ThemeCacheableFactory.class);
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private Environment environment;
  
  /**
   * <code>getTheme</code> method will return the theme for the user or the anoymouns user.
   * 
   * @param themeKey
   * 
   * @return the theme.
   */
  @Cacheable(value = "themeCache", key = "#themeKey", unless = "#result == null")
  public Theme getTheme(String themeKey) {
    
    Theme theme = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    /* check if user is logged in or */
    if (authentication instanceof UsernamePasswordAuthenticationToken
        || authentication instanceof RememberMeAuthenticationToken) {
      User user = GenericUtils.getUserFromAuthentication(authentication);
      String companyId = user.getCompanyId();
      
      /* check if user a business user or individual user */
      if (StringUtils.isNotBlank(companyId)) {
        CompanyDao company = companyFactory.getCompany(companyId);
        CompanyTheme companyTheme = company.getCompanyTheme();
        if (companyTheme != null) {
          MessageSource messageSource = new ThemeMessageSource(company);
          theme = new SurePeopleTheme(themeKey, messageSource);
          return theme;
        } else {
          /* Get the default company theme for the individual user */
          CompanyDao defaultCompany = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
          ThemeMessageSource defaultmMessageSource = new ThemeMessageSource(defaultCompany);
          theme = new SurePeopleTheme(themeKey, defaultmMessageSource);
          if (StringUtils.isNotEmpty(company.getLogoURL())) {
            defaultmMessageSource.getMessages().put("logoUrl",
                defaultmMessageSource.createMessageFormat(company.getLogoURL(), null, null));
            
          }
        }
          
      } else {
        /* Get the default company theme for the individual user */
        CompanyDao company = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
        MessageSource defaultmMessageSource = new ThemeMessageSource(company);
        theme = new SurePeopleTheme(themeKey, defaultmMessageSource);
      }
    } else {
      try {
        CompanyDao company = companyFactory.getCompany(themeKey);
        MessageSource messageSource = new ThemeMessageSource(company);
        theme = new SurePeopleTheme(themeKey, messageSource);
        
      } catch (Exception ex) {
        /* Theme for anonmous user/individual users */
        CompanyDao company = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
        
        MessageSource defaultmMessageSource = new ThemeMessageSource(company);
        theme = new SurePeopleTheme(themeKey, defaultmMessageSource);
      }
    }
    return theme;
    
  }
  
  /**
   * Clears the cache of themes. This should be called whenever the theme is updated in the mongo
   * for the company.
   */
  @CacheEvict(value = "themeCache", key = "#themeKey")
  public void clearCache(String themeKey) {
    
  }
  
  /**
   * getCompanyByIdForTheme method will defualt company in case company not exist for a user.
   * 
   * @param companyId
   *          companyid for the user.
   */
  public CompanyDao getCompanyByIdForTheme(String companyId) {
    CompanyDao company = null;
    try {
      CompanyDao companyMain = companyFactory.getCompany(companyId);
      
      // Creating a new object as don't want to update company witht he theme of default company if
      // it is not present, so that other logic worked.
      company = new CompanyDao(companyMain.getCompany());
    } catch (Exception ex) {
      // company not found, need to find the default company */
      company = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
      if (LOG.isInfoEnabled()) {
        LOG.info("Company not found, Returing the default theme.");
      }
    }
    if (company != null && company.getCompanyTheme() == null) {
      CompanyDao defautCommpany = companyFactory
          .getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
      company.setCompanyTheme(defautCommpany.getCompanyTheme());
    }
    return company;
  }
  
}
