package com.sp.web.service.email.transform;

import com.sp.web.Constants;
import com.sp.web.exception.SPException;
import com.sp.web.model.Company;
import com.sp.web.service.email.DataSourceAttahcment;
import com.sp.web.service.email.EmailParamType;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.email.MessageParams;
import com.sp.web.service.email.SPTransformer;
import com.sp.web.service.stringtemplate.StringTemplate;
import com.sp.web.service.stringtemplate.StringTemplateFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.Transformer;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * EmailTransformer class will transform the email template and will send the email to the output
 * channel to send the email asynchronously.
 * 
 * @author pruhil
 */
public class EmailTransformer implements SPTransformer {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(EmailTransformer.class);
  /**
   * StringTemplateLaoder will load the resource for the email template.
   */
  @Autowired
  private StringTemplateFactory stringTemplateFactory;
  
  @Autowired
  private JavaMailSender mailSender;
  
  private InternetAddress defaultFrom;
  private String baseUrl;
  private DateTimeFormatter dateFormatter;
  
  /**
   * Constructor.
   * 
   * @param environment
   *          - environment props
   */
  @Inject
  public EmailTransformer(Environment environment) {
    String defaultFromStr = environment.getProperty("default.fromAddress");
    try {
      defaultFrom = new InternetAddress(defaultFromStr);
    } catch (AddressException e) {
      throw new SPException("Email default from not set or incorrect in application.properties :"
          + defaultFromStr);
    }
    baseUrl = environment.getProperty("base.serverUrl", "http://www.surepeople.com/");
    dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
  }
  
  /**
   * <code>populateStringTemplate</code> method will add the dynamic variable to the string
   * template.
   * 
   * @param stringTemplate
   *          String template
   * @param paramsMap
   *          map containing the value
   */
  private void populateStringTemplate(StringTemplate stringTemplate, Map<String, Object> paramsMap) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("String template Params map is " + paramsMap);
    }
    
    for (Map.Entry<String, Object> mapEntry : paramsMap.entrySet()) {
      if (mapEntry.getValue() instanceof String) {
        String value = (String) mapEntry.getValue();
        value = GenericUtils.replaceSpecialChar(value);
        
        stringTemplate.put(mapEntry.getKey(), value);
      } else if (mapEntry.getValue() instanceof String[]) {
        if (((String[]) mapEntry.getValue()).length > 0) {
          String value = ((String[]) mapEntry.getValue())[0];
          value = GenericUtils.replaceSpecialChar(value);
          stringTemplate.put(mapEntry.getKey(), value);
        }
        
      } else {
        stringTemplate.put(mapEntry.getKey(), mapEntry.getValue());
      }
      
    }
  }
  
  /**
   * <code>transformMessage</code> method will create the mail message to be sent over the email.
   * 
   * @param messageParams
   *          MessageParms
   * @return the Mail Message to be sent
   */
  @Override
  @Transformer
  public MailMessage transformMessage(MessageParams messageParams) {
    
    EmailParams emailParams = (EmailParams) messageParams;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Inside transformMessage method, messageParams " + messageParams);
    }
    
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      String[] array = emailParams.getTos().stream().toArray(String[]::new);
      message.setTo(array);
      
      String from = emailParams.getFrom();
      if (from != null) {
        message.setFrom(new InternetAddress(from));
      } else if (emailParams.isViaFrom()) {
        String fromm = MessagesHelper.getMessage("via.FromAddress",
            emailParams.getValue(Constants.PARAM_NAME));
        message.setFrom(fromm);
        from = fromm;
      } else {
        message.setFrom(defaultFrom);
        from = defaultFrom.toString();
      }
      message.setReplyTo(from);
      String subject = StringUtils.replacePattern(emailParams.getSubject(), "ï¿½", "\u00AE");
      subject = StringUtils.replacePattern(subject, "Â®", "\u00AE");
      if (StringUtils.isNotBlank(System.getProperty("enviornmentName"))) {
        
        message.setSubject(System.getProperty("enviornmentName") + "|" + subject);
      } else {
        message.setSubject(subject);
      }
      String emailBody = emailParams.getEmailBody();
      
      final String templateName = emailParams.getTemplateName();
      if (emailBody == null && !StringUtils.isEmpty(templateName)) {
        // adding the server url to the email params
        emailParams.addParam(Constants.PARAM_BASE_SERVER_URL, baseUrl);
        
        Boolean isEmailManagement = (Boolean) emailParams.getValue(Constants.PARAM_ISEMAIL_MANANGEMENT);
        if (isEmailManagement == null) {
          isEmailManagement = false;
        }
        /* load the string template for the email present in the class path */
        StringTemplate stringTemplate = stringTemplateFactory.getStringTemplate(templateName,
            emailParams.getLocale(),
            ((Company) emailParams.getValue(Constants.PARAM_COMPANY)).getId(),
            emailParams.getValue(Constants.PARAM_NOTIFICATION_TYPE).toString(),isEmailManagement);
        if (LOG.isDebugEnabled()) {
          LOG.debug("PopulatingStringtemplate method will populate the template with the values map");
        }
        populateStringTemplate(stringTemplate, emailParams.getValueMap());
        // StringTemplate footerTemplate =
        // stringTemplateFactory.getStringTemplate("templates/email/common/footer.stg");
        // stringTemplate.put("footer", footerTemplate);
        
        emailBody = stringTemplate.render();
        if (emailParams.getValue(Constants.PARAM_ISEMAIL_MANANGEMENT) != null) {
          emailBody = stringTemplate.render(emailBody);
        }
        
      }
      
      if (LOG.isDebugEnabled()) {
        LOG.debug("Email body to be sent is " + emailBody);
      }
      String textHtml = Jsoup.clean(emailBody, Whitelist.none());
      message.setText(textHtml, emailBody);
      
      // check if any attachments
      if (!emailParams.getDataSourceAttachments().isEmpty()) {
        // adding the attachments to the email
        for (DataSourceAttahcment dsa : emailParams.getDataSourceAttachments()) {
          message.addAttachment(dsa.getFileName(), dsa.getDataSource());
        }
      }
      
      if (emailParams.getType() == EmailParamType.Meeting) {
        addMeetingInvite(mimeMessage, emailParams, from);
      }
      
      MailMessage mailMessage = new MimeMailMessage(message);
      return mailMessage;
    } catch (Exception e) {
      LOG.error("exception occurred while sending email", e);
      throw new SPException("Error occurred while getting the information", e);
    }
  }
  
  /**
   * Method to add the meeting invite the mail message.
   * 
   * @param mimeMessage
   *          - email message
   * @param emailParams
   *          - email params
   * @param from
   *          - from address
   */
  private void addMeetingInvite(MimeMessage mimeMessage, EmailParams emailParams, String from)
      throws Exception {
    mimeMessage.addHeaderLine("method=REQUEST");
    mimeMessage.addHeaderLine("charset=UTF-8");
    mimeMessage.addHeaderLine("component=VEVENT");
    
    StringBuffer sb = new StringBuffer();
    
    sb.append("BEGIN:VCALENDAR\n" + "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n"
        + "VERSION:2.0\n" + "METHOD:REQUEST\n" + "BEGIN:VEVENT\n"
        + "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:");
    
    // adding to's
    sb.append(emailParams.getTos().stream().collect(Collectors.joining(","))).append("\n");
    
    sb.append("ORGANIZER;CN=" + emailParams.getValue(Constants.PARAM_FIRSTNAME) + " "
        + emailParams.getValue(Constants.PARAM_LASTNAME));
    
    // adding from
    if (StringUtils.isNotEmpty(from)) {
      int emailStartIndex = from.indexOf("<") + 1;
      int emailEndIndex = from.indexOf(">");
      sb.append(":MAILTO:" + from.substring(emailStartIndex, emailEndIndex) + "\n");
    }
    // adding start date time
    addValue("DTSTART:",
        formatDateTime((LocalDateTime) emailParams.getValue(Constants.PARAM_START_DATE)), sb);
    
    // adding end date time
    addValue("DTEND:",
        formatDateTime((LocalDateTime) emailParams.getValue(Constants.PARAM_END_DATE)), sb);
    
    // adding location
    addValue("LOCATION:", (String) emailParams.getValue(Constants.PARAM_MEETING_INVITE_LOCATION),
        sb);
    
    sb.append("TRANSP:OPAQUE\n" + "SEQUENCE:0\n" + "UID:" + UUID.randomUUID().toString() + "\n"
        + " 000004377FE5C37984842BF9440448399EB02\n");
    
    // adding time stamp
    addValue("DTSTAMP:", formatDateTime(LocalDateTime.now()), sb);
    
    sb.append("CATEGORIES:Meeting\n");
    
    // adding description
    addValue("DESCRIPTION:", (String) emailParams.getValue(Constants.PARAM_MEETING_INVITE_AGENDA),
        sb);
    
    // adding description
    addValue("SUMMARY:", " ", sb);
    
    sb.append("PRIORITY:1\n" + "CLASS:PUBLIC\n" + "BEGIN:VALARM\n" + "TRIGGER:-PT1440M\n"
        + "ACTION:DISPLAY\n" + "END:VALARM\n" + "END:VEVENT\n" + "END:VCALENDAR");
    
    // Create the message part
    BodyPart messageBodyPart = new MimeBodyPart();
    
    // Fill the message
    messageBodyPart.setHeader("Content-Class", "urn:content-  classes:calendarmessage");
    messageBodyPart.setHeader("Content-ID", "calendar_message");
    messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(sb.toString(),
        "text/calendar")));// very important
    
    // Create a Multipart
    Multipart multipart = new MimeMultipart();
    
    // Add part one
    multipart.addBodyPart(messageBodyPart);
    
    // Put parts in message
    mimeMessage.setContent(multipart);
  }
  
  /**
   * Add the value to the string buffer.
   * 
   * @param key
   *          - key
   * @param value
   *          - value
   * @param sb
   *          - string buffer
   */
  private void addValue(String key, String value, StringBuffer sb) {
    sb.append(key).append(value).append("\n");
  }
  
  /**
   * Get the VCalendar formatted date time.
   * 
   * @param dateTime
   *          - date time to format
   * @return the formatted date time
   */
  private String formatDateTime(LocalDateTime dateTime) {
    return dateFormatter.format(dateTime);
  }
  
}
