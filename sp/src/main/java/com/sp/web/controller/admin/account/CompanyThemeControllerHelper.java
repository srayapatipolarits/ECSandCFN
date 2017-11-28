package com.sp.web.controller.admin.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.company.CompanyThemeDTO;
import com.sp.web.exception.SPException;
import com.sp.web.form.CompanyThemeForm;
import com.sp.web.model.CompanyTheme;
import com.sp.web.model.FileData;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.image.FileStorage;
import com.sp.web.service.stringtemplate.StringTemplate;
import com.sp.web.service.stringtemplate.StringTemplateFactory;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.ImageUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Helper clas for the {@link CompanyThemeController}.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class CompanyThemeControllerHelper {
  
  private static final String THEME_TEMPLATE = "templates/theme/companyThemeTemplate.stg";
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(CompanyThemeControllerHelper.class);
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private StringTemplateFactory stringTemplateFactory;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private ThemeCacheableFactory themeCacheableFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private UserRepository userRepository;
  
  /** file Storage to upload the file in repository */
  @Autowired
  @Qualifier("s3FileStore")
  private FileStorage fileStorage;
  
  /**
   * <code>createTheme</code> method will create the theme.
   *
   * @param user
   *          logged in user.
   * @param param
   *          contains the sytles map form.
   */
  public SPResponse saveTheme(User user, Object[] param) {
    
    CompanyThemeForm companyThemeForm = (CompanyThemeForm) param[0];
    
    /* create the theme */
    return saveTheme(user.getCompanyId(), companyThemeForm);
  }
  
  /**
   * <code>saveTheme</code> methdo will create/update the theme file and upload the file to the s3.
   * 
   * @param companyId
   *          for which theme is to be created.
   * @param companyThemeForm
   *          contains the theme form details.
   * @return the spResponse.
   */
  public SPResponse saveTheme(String companyId, CompanyThemeForm companyThemeForm) {
    
    CompanyDao company = companyFactory.getCompany(companyId);
    
    Assert.notNull(company, "Company must not be null !!!");
    
    CompanyTheme companyTheme = companyThemeForm.getCompanyTheme();
    
    company.setCompanyTheme(companyTheme);
    company.setCompanyThemeActive(true);
    
    StringTemplate companyTemplate = stringTemplateFactory.getStringTemplate(THEME_TEMPLATE,
        Constants.DEFAULT_LOCALE, "default", "CompanyThemeTemplate", false);
    companyTemplate.put("theme", companyTheme);
    String cssTemplateContent = companyTemplate.render();
    
    /* create the file and store it in the s3 */
    String themePathName = "theme/" + GenericUtils.normalize(company.getName()).toLowerCase()
        + "/" + company.getId() + "/" + company.getImageCount() + "/" + company.getId()
        + ".css";
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("themePathName: " + themePathName);
    }
    FileData fileData = new FileData(themePathName, cssTemplateContent.getBytes(), "text/css");
    
    String themeUrl = fileStorage.storeFile(fileData);
    companyTheme.setCssUrl("{0}/" + themeUrl);
    
    /* increase the image counter */
    company.incrementImageCount();
    
    /* update the company. */
    companyFactory.updateCompanyDao(company);
    
    /* evict the cache */
    themeCacheableFactory.clearCache(company.getId());
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Theme created/updated successfully for the company " + company.getName());
    }
    return new SPResponse().isSuccess();
    
  }
  
  /**
   * <code>getThemeDetail</code> method will give the theme detail for the company.
   *
   * @param user
   *          logged in user.
   */
  public SPResponse getThemeDetail(User user) {
    return getThemeDetail(user.getCompanyId());
  }
  
  /**
   * getThemeDetail method will return the theme detail for the company passed.
   * 
   * @param companyId
   *          for which theme detail is to be returned.
   * @return the theme detail.
   */
  public SPResponse getThemeDetail(String companyId) {
    
    CompanyDao company = companyFactory.getCompany(companyId);
    Assert.notNull(company, "Company must not be null !!!");
    CompanyThemeDTO themeDTO = new CompanyThemeDTO(company);
    if (company.getCompanyTheme() == null) {
      CompanyDao defaultCompany = companyFactory
          .getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
      themeDTO.setCompanyTheme(defaultCompany.getCompanyTheme());
    }
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_COMP_THEME_DETAIL, themeDTO);
    return response;
  }
  
  /**
   * resetTheme method will reset the theme to the default value.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          containing whether to activate or deactivate the theme.
   * @return the spResponse.
   */
  public SPResponse resetTheme(User user) {
    return resetTheme(user.getCompanyId());
  }
  
  /**
   * resetTheme method will reset the theme.
   * 
   * @param companyId
   *          for which theme is to be reset.
   * @return the json response whether service success or failure.
   */
  public SPResponse resetTheme(String companyId) {
    CompanyDao company = companyFactory.getCompany(companyId);
    Assert.notNull(company, "Company must not be null !!!");
    if (company.getCompanyTheme() != null) {
      String themeKey = company.getId();
      /* clearthe old cache theme */
      themeCacheableFactory.clearCache(themeKey);
    }
    if (company.getId().equalsIgnoreCase(Constants.COMP_THEME_DEFAULT_COMPANY_ID)) {
      createDefaultCompTheme(company);
    } else {
      company.setCompanyTheme(null);
      themeCacheableFactory.clearCache(companyId);
      // When reset the company theme flag should not be active.
//      company.setCompanyThemeActive(false);
    }
    companyFactory.updateCompanyDao(company);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * @param company
   */
  @JsonCreator
  private void createDefaultCompTheme(CompanyDao company) {
    String defaultCompanyThemeJson = environment.getProperty(Constants.DEFAULT_COMPANY_THEME);
    ObjectMapper om = new ObjectMapper();
    CompanyTheme defaultCompTheme = null;
    try {
      defaultCompTheme = om.readValue(defaultCompanyThemeJson, CompanyTheme.class);
    } catch (IOException e) {
      LOG.error("Unable to set the reset the default theme ");
      throw new SPException("Unable to reset the default theme", e);
    }
    company.setCompanyTheme(defaultCompTheme);
    companyFactory.updateCompanyDao(company);
  }
  
  /**
   * activateTheme method will activate the theme to be avaiable to edit by the companies
   * themseleves.
   * 
   * @param companyId
   *          for which theme is to be activated or deactivated.
   * @return the json response whether service success or failure.
   */
  public SPResponse activateTheme(String companyId) {
    companyFactory.addFeature(companyId, SPFeature.CompanyTheme);
    userFactory.addRoleToAdmins(companyId, RoleType.CompanyTheme);
    return new SPResponse().isSuccess();
  }
  
  /**
   * deActivateTheme method will deactivate the theme to the account administrators of the company
   * 
   * @param companyId
   *          for which theme is to be activated or deactivated.
   * @return the json response whether service success or failure.
   */
  public SPResponse deActivateTheme(String companyId) {
    companyFactory.removeFeature(companyId, SPFeature.CompanyTheme);
    userFactory.removeRoleFromAdmin(companyId, RoleType.CompanyTheme);
    return new SPResponse().isSuccess();
  }
  
  /**
   * clearThemeCache method will clear the theme cache for the company.
   * 
   * @param companyId
   *          is the id for which theme cache is to be removed.
   * @return the response.
   */
  public SPResponse clearThemeCache(String companyId) {
    Assert.hasText("Company must not be null !!!");
    themeCacheableFactory.clearCache(companyId);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>removeThemeSettings</code> method will remove all the setting for the company.
   * 
   * @param companyId
   *          for the which theme settings is to be removed.
   * @return the resposne.
   */
  public SPResponse removeThemeSettings(String companyId) {
    
    CompanyDao company = companyFactory.getCompany(companyId);
    Assert.notNull(company, "Company must not be null !!!");
    
    company.setCompanyTheme(null);
    company.getFeatureList().remove(SPFeature.CompanyTheme);
    userFactory.removeRoleFromAdmin(companyId, RoleType.CompanyTheme);
    if (company.getLogoURL() != null) {
      try {
        fileStorage.removeFile(ImageUtils.stripDomainFromImagePath(company.getLogoImage()));
      } catch (Exception e) {
        // Just log the s3 Error. No need to throw it.
        LOG.error("Error occured while deleting the media from s3-->" + company.getLogoImage());
      }
    }
    company.setLogoImage(null);
    company.setLogoURL(null);
    company.incrementImageCount();
    company.setCompanyThemeActive(false);
    companyFactory.updateCompanyDao(company);
    themeCacheableFactory.clearCache(company.getId());
    return new SPResponse().isSuccess();
  }
}
