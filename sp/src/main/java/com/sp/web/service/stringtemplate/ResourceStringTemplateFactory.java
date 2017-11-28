package com.sp.web.service.stringtemplate;

import com.sp.web.Constants;
import com.sp.web.exception.SPException;
import com.sp.web.service.email.EmailTemplateFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.DateRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.StringRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A StringTemplate factory that creates template instances from a single Resource. It delegates the
 * implemenation of String template DelegateStringTemplate which acts a wrapper for the ST
 * framework.
 * 
 * 
 * @author pruhil
 */
@Component
public final class ResourceStringTemplateFactory implements StringTemplateFactory {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(ResourceStringTemplateFactory.class);
  
  /** STring template instance. */
  private Map<String, String> templateMap = new HashMap<>();
  
  @Autowired
  private EmailTemplateFactory emailTemplateFactory;
  
  /**
   * Creates a StringTemplateFactory that creates its StringTemplate instances from the resource.
   */
  @Bean(destroyMethod = "clearTemplateMap")
  public ResourceStringTemplateFactory stringTemplateFactory() {
    return new ResourceStringTemplateFactory();
  }
  
  /**
   * <code>getStringTemplate</code> method will create the wrapper for {@link ST}.
   * 
   * @param templateName
   *          name of the template for which template is to be returned
   * @return the StringTemplate method.
   */
  public StringTemplate getStringTemplate(String templateName, String locale, String companyId,
      String templateType, boolean isEmailTemplate) {
    if (StringUtils.isBlank(locale)) {
      locale = Constants.DEFAULT_LOCALE;
    }
    String key = locale.concat(templateName + companyId);
    if (templateMap.containsKey(key)) {
      return new DelegatingStringTemplate(loadStringTemplate(templateMap.get(key), templateName));
    }
    
    LOG.info("Getting the template for locale " + locale + ", companyId " + companyId);
    
    String emailTemplate = null;
    if (!locale.equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
      try {
        emailTemplate = emailTemplateFactory.getEmailTemplate(locale, templateName, companyId,
            templateType, isEmailTemplate);
        templateMap.put(key, emailTemplate);
      } catch (SPException ex) {
        // Exception occurred while getting the template from the smartlng. getting the defualt.
        LOG.info("Getting the default  template for locale " + locale + ", companyId " + companyId);
        emailTemplate = localizedTemplateString(templateName);
      }
      
    } else {
      emailTemplate = localizedTemplateString(templateName);
      
    }
    
    ST createPrototype = ResourceStringTemplateFactory.loadStringTemplate(emailTemplate,
        templateName);
    return new DelegatingStringTemplate(createPrototype);
  }
  
  /**
   * <code>createPrototyp</code> method will load the string template.
   * 
   * @param resource
   *          String template resource.
   * @return the {@link ST} for the resource.
   */
  private ST createPrototype(Resource resource, String key) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(getResourceReader(resource));
      if (LOG.isDebugEnabled()) {
        LOG.info("Reading the template resource file");
      }
      String templateString = readTemplate(key, getTemplateName(resource), reader);
      return ResourceStringTemplateFactory.loadStringTemplate(templateString,
          getTemplateName(resource));
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to read template resource " + resource, e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          throw new IllegalStateException("Unable to close template resource " + resource, e);
        }
      }
    }
  }
  
  /**
   * <code>getResourceReader</code> method will read the resource and give the Reader instance to
   * read the resource.
   * 
   * @param resource
   *          Spring IO resource method.
   * @return the Reader
   * @throws IOException
   *           in case resource is not found.
   */
  private Reader getResourceReader(Resource resource) throws IOException {
    if (resource instanceof EncodedResource) {
      return ((EncodedResource) resource).getReader();
    } else {
      return new EncodedResource(resource).getReader();
    }
  }
  
  /**
   * <code>getTemplateName</code> method will return the name of the template file.
   * 
   * @param resource
   *          Spring path resource
   * @return the template file name
   */
  private String getTemplateName(Resource resource) {
    return resource.getFilename();
  }
  
  /**
   * <code>loadTemplate</code> method will load the template and create a ST instance of it.
   * 
   * @param name
   *          of the template file
   * @param reader
   *          Template file buffered reader instance
   * @return the ST template
   * @throws IOException
   *           in case resource is not found or any IO error occures while reading the string
   *           template file.
   */
  private String readTemplate(String key, String name, BufferedReader reader) throws IOException {
    String line;
    String nl = System.getProperty("line.separator");
    StringBuffer buf = new StringBuffer(300);
    while ((line = reader.readLine()) != null) {
      buf.append(line);
      buf.append(nl);
    }
    String pattern = buf.toString().trim();
    if (pattern.length() == 0) {
      return null;
    }
    /* Storing the template in factory to avoid mutliple IO operations */
    templateMap.put(key, pattern);
    return pattern;
  }
  
  /**
   * <code>loadStringTemplate</code> method will initialize the {@link ST} with the templateString.
   * 
   * @param templateString
   *          is the content of template file.
   * @param templateName
   *          is the name of the template
   * @return the {@link ST} instance
   */
  public static ST loadStringTemplate(String templateString, String templateName) {
    /*
     * Initalizing the StringTemplate with the st template. Delimiter assigned to $ instead of
     * default <>
     */
    templateString = StringEscapeUtils.unescapeHtml4(templateString);
    ST template = new ST(templateString, '$', '$');
    template.groupThatCreatedThisInstance.registerRenderer(Date.class, new DateRenderer());
    template.groupThatCreatedThisInstance.registerRenderer(String.class, new StringRenderer());
    template.groupThatCreatedThisInstance.registerRenderer(LocalDateTime.class,
        new LocalDateTimeRenderer());
    
    template.impl.name = templateName;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Template " + templateName + ", loaded succesfully");
    }
    return template;
  }
  
  /**
   * <code>clearTemplateMap</code> method will clear the template stored in the map when bean is
   * destroyed by the containe
   */
  public void clearTemplateMap() {
    templateMap.clear();
  }
  
  @Override
  public String localizedTemplateString(String templateName) {
    String key = Constants.DEFAULT_LOCALE.concat(templateName);
    Resource resource = new ClassPathResource(templateName);
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(getResourceReader(resource));
      if (LOG.isDebugEnabled()) {
        LOG.info("Reading the template resource file");
      }
      return readTemplate(key, getTemplateName(resource), reader);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to read template resource " + resource, e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          throw new IllegalStateException("Unable to close template resource " + resource, e);
        }
      }
    }
  }
}
