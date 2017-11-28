package com.sp.web.service.stringtemplate;


/**
 * Factory for a fresh StringTemplate instance that can be used in the local thread of execution.
 * 
 * @author pruhil
 */
public interface StringTemplateFactory {
  
  /**
   * Get a fresh StringTemplate instance that can be safely used in the current thread of execution.
   * StringTemplates should not be shared between threads.
   * 
   * @param templateName
   *          name of the template.
   * @param locale
   *          for which template is to be retrieved.
   * @param templateType
   *          templateType
   * @param isEmailManagement
   *          wheather email maangement is present or not for the company.
   * @return the {@link StringTemplate}
   */
  StringTemplate getStringTemplate(String templateName, String locale, String companyId,
      String templateType, boolean isEmailManagement);
  
  /**
   * localizedTemplateString method will return the localized template string for the passed locale.
   * 
   * @param templateName
   *          name of the template.
   * @return the template string.
   */
  String localizedTemplateString(String templateName);
}