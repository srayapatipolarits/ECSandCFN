package com.sp.web.config;

import com.bullhornsdk.data.api.BullhornData;
import com.bullhornsdk.data.api.BullhornRestCredentials;
import com.bullhornsdk.data.api.StandardBullhornData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("PROD")
public class BullHornConfig {
  
  @Value("${sp.bullhorn.apiPassword}")
  private String apiPassword;
  
  @Value("${sp.bullhorn.authorizeUrl}")
  private String authorizeUrl;
  
  @Value("${sp.bullhorn.clientId}")
  private String clientId;
  
  @Value("${sp.bullhorn.clientsecret}")
  private String clientSecret;
  
  @Value("${sp.bullhorn.sessionMinutesToLive}")
  private String sessionMinutesToLive;
  
  @Value("${sp.bullhorn.tokenUrl}")
  private String tokenUrl;
  
  @Value("${sp.bullhorn.apiUserName}")
  private String apiUserName;
  
  @Value("${sp.bullhorn.loginurl}")
  private String loginUrl;
  
  /**
   * Provide your credentials and instantiate {@link StandardBullhornData} with those.
   * 
   * @return the BullHornData bean.
   */
  @Bean(name = "bullhornData")
  public BullhornData bullhornData() {
    BullhornRestCredentials creds = new BullhornRestCredentials();
    creds.setPassword(apiPassword);
    creds.setRestAuthorizeUrl(authorizeUrl);
    creds.setRestClientId(clientId);
    creds.setRestClientSecret(clientSecret);
    creds.setRestSessionMinutesToLive(sessionMinutesToLive);
    creds.setRestTokenUrl(tokenUrl);
    creds.setRestLoginUrl(loginUrl);
    creds.setUsername(apiUserName);
    return new StandardBullhornData(creds);
    
  }
}
