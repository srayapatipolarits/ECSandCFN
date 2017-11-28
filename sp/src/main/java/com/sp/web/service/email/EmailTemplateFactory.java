package com.sp.web.service.email;

import com.sp.web.exception.SPException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * EmailTemplateFactory class provide the template as per locale.
 * 
 * @author pradeepruhil
 *
 */
@Service
public class EmailTemplateFactory {
  
  private static final Logger log = Logger.getLogger(EmailTemplateFactory.class);
  
  @Autowired
  private Environment enviornment;
  
  /**
   * getEmailTemplate method will return the template for the given locale.
   * 
   * @param locale
   *          for which template is to be retreived.
   * @param templateName
   *          name of the template.
   * @param notificationType
   *          notification type.
   * @param companyId
   *          company Id
   * @param isEmailTemplate
   *          whether template is valid or not.
   * @return the template translated string.
   */
  public String getEmailTemplate(String locale, String templateName, String companyId,
      String notificationType, boolean isEmailTemplate) {
    StringBuilder queryParams = new StringBuilder("templateName=" + templateName);
    
    String smartlingEmail = String.format(enviornment.getProperty("smartlingTemplateUrl"), locale);
    String url = null;
    if (isEmailTemplate) {
      url = smartlingEmail + companyId + "/" + notificationType + "?" + queryParams.toString();
    } else {
      url = smartlingEmail + notificationType + "?" + queryParams.toString();
    }
    
    String output = null;
    try {
      
      RequestConfig.Builder requestBuilder = RequestConfig.custom();
      requestBuilder = requestBuilder.setConnectTimeout(30000);
      requestBuilder = requestBuilder.setConnectionRequestTimeout(30000);
      
      HttpClientBuilder clientBuilder = HttpClientBuilder.create();
      clientBuilder.setDefaultRequestConfig(requestBuilder.build());
      HttpClient client = clientBuilder.build();
      
      HttpGet getRequest = new HttpGet(url);
      getRequest.addHeader("Host", enviornment.getProperty("base.hostName"));
      getRequest
          .addHeader(
              "User-Agent",
              "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      HttpResponse response = client.execute(getRequest);
      
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : "
            + response.getStatusLine().getStatusCode());
      }
      
      HttpEntity entity = response.getEntity();
      output = EntityUtils.toString(entity, "UTF-8");
      output = StringEscapeUtils.unescapeJava(output);
      
      if (log.isDebugEnabled()) {
        log.debug("Response returened is  " + output);
      }

    } catch (Exception ex) {
      log.error("Exception occurred while getting the localized emal template from Smartling", ex);
      throw new SPException(ex);
    }
    
    return output;
  }
}
