package com.sp.web.service.email;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmailParams extends MessageParams {
  
  private List<String> tos;
  
  private String subject;
  
  private String from;
  
  private boolean viaFrom;
  
  private EmailParamType type;
  
  /**
   * If the user wants to send the email without using the template.
   */
  private String emailBody;
  
  private List<DataSourceAttahcment> dataSourceAttachments;
  
  /** Default Constructor. */
  public EmailParams() {
    type = EmailParamType.Default;
    setLocale(Constants.DEFAULT_LOCALE);
  }
  
  /**
   * Constructor to set the required fields.
   * 
   * @param templateName
   *          - template name
   * @param recipientList
   *          - recipient list
   * @param subject
   *          - subject
   */
  public EmailParams(String templateName, List<String> recipientList, String subject, String locale) {
    this();
    tos = recipientList;
    this.subject = subject;
    setTemplateName(templateName);
    setLocale(locale);
  }
  
  /**
   * Constructor for a single user.
   * 
   * @param emailTemplate
   *          - email template to user
   * @param email
   *          - email address
   * @param subject
   *          - subject
   */
  public EmailParams(String emailTemplate, String email, String subject, String locale) {
    this(emailTemplate, Arrays.asList(new String[] { email }), subject, locale);
  }
  
  /**
   * Get the recipient list.
   * 
   * @return the recipient list
   */
  public List<String> getTos() {
    if (tos == null) {
      tos = new ArrayList<>();
    }
    return tos;
  }
  
  public void setTos(List<String> tos) {
    this.tos = tos;
  }
  
  /**
   * Setting a single email to the to's list.
   * 
   * @param email
   *          - email
   */
  public void setTos(String email) {
    getTos().add(email);
  }
  
  public String getSubject() {
    return subject;
  }
  
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  public String getFrom() {
    return from;
  }
  
  public void setFrom(String from) {
    this.from = from;
  }
  
  /**
   * Adds the given parameter to the parameter list.
   * 
   * @param name
   *          - param name
   * @param param
   *          - param value
   */
  public void addParam(String name, Object param) {
    getValueMap().put(name, param);
  }
  
  /**
   * To add all the parameters from the given parameter map.
   * 
   * @param parameterMap
   *          - the parameters to add
   */
  public void addAllParams(Map<String, String[]> parameterMap) {
    
    parameterMap.entrySet().stream().forEach(en -> {
      if (en.getValue().length == 1) {
        getValueMap().put(en.getKey(), en.getValue()[0]);
      } else {
        getValueMap().putAll(parameterMap);
      }
    });
    
  }
  
  public String getEmailBody() {
    return emailBody;
  }
  
  public void setEmailBody(String emailBody) {
    this.emailBody = emailBody;
  }
  
  public Object getValue(String key) {
    return getValueMap().get(key);
  }
  
  /**
   * Validated the required parameters in the email params.
   */
  public void validate() {
    if (getTos().isEmpty()) {
      throw new InvalidRequestException("The tos list cannot be null !!!");
    }
  }
  
  public List<DataSourceAttahcment> getDataSourceAttachments() {
    return Optional.ofNullable(dataSourceAttachments).orElseGet(() -> {
      dataSourceAttachments = new ArrayList<DataSourceAttahcment>();
      return dataSourceAttachments;
    });
  }
  
  public void setDataSourceAttachments(List<DataSourceAttahcment> dataSourceAttachments) {
    this.dataSourceAttachments = dataSourceAttachments;
  }
  
  public void addDataSourceAttachment(DataSourceAttahcment dataSourceAttahcment) {
    getDataSourceAttachments().add(dataSourceAttahcment);
  }
  
  public boolean isViaFrom() {
    return viaFrom;
  }
  
  public void setViaFrom(boolean viaFrom) {
    this.viaFrom = viaFrom;
  }
  
  public EmailParamType getType() {
    return type;
  }
  
  public void setType(EmailParamType type) {
    this.type = type;
  }
  
  @Override
  public String toString() {
    return "EmailParams [tos=" + tos + ", subject=" + subject + ", from=" + from + ", viaFrom="
        + viaFrom + ", type=" + type + ", emailBody=" + emailBody + "]";
  }
  
}
