package com.sp.web.mvc.signin.oauth;

import com.sp.web.utils.GenericUtils;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;

public class OAuth2AuthenticationAccessToken implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 2489347777877048623L;
  private String id;
  private String tokenId;
  private OAuth2AccessToken oAuth2AccessToken;
  private String authenticationId;
  private String userName;
  private String clientId;
  private OAuth2Authentication authentication;
  private String refreshToken;
  
  public OAuth2AuthenticationAccessToken() {
  }
  
  public OAuth2AuthenticationAccessToken(final OAuth2AccessToken oAuth2AccessToken,
      final OAuth2Authentication authentication, final String authenticationId) {
    this.tokenId = oAuth2AccessToken.getValue();
    this.oAuth2AccessToken = oAuth2AccessToken;
    this.authenticationId = authenticationId;
    this.userName = GenericUtils.getUserIdFromAuthentication(authentication);
    this.clientId = authentication.getOAuth2Request().getClientId();
    this.authentication = authentication;
     this.refreshToken = oAuth2AccessToken.getRefreshToken().getValue();
  }
  
  public String getTokenId() {
    return tokenId;
  }
  
  public OAuth2AccessToken getoAuth2AccessToken() {
    return oAuth2AccessToken;
  }
  
  public String getAuthenticationId() {
    return authenticationId;
  }
  
  public String getUserName() {
    return userName;
  }
  
  public String getClientId() {
    return clientId;
  }
  
  public OAuth2Authentication getAuthentication() {
    return authentication;
  }
  
  public String getRefreshToken() {
    return refreshToken;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}