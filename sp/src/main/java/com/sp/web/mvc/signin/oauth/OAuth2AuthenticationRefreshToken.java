package com.sp.web.mvc.signin.oauth;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Oauth2AuthenticationRefresh token entity.
 * 
 * @author pradeepruhil
 *
 */
public class OAuth2AuthenticationRefreshToken {
  
  private String id;
  private String tokenId;
  private OAuth2RefreshToken oauth2RefreshToken;
  private OAuth2Authentication authentication;
  
  /**
   * Constructor.
   * 
   * @param oauth2RefreshToken
   *          refresh token.
   * @param authentication
   *          authentication.
   */
  public OAuth2AuthenticationRefreshToken(OAuth2RefreshToken oauth2RefreshToken,
      OAuth2Authentication authentication) {
    this.oauth2RefreshToken = oauth2RefreshToken;
    this.authentication = authentication;
    this.tokenId = oauth2RefreshToken.getValue();
  }
  
  public String getTokenId() {
    return tokenId;
  }
  
  public OAuth2RefreshToken getOauth2RefreshToken() {
    return oauth2RefreshToken;
  }
  
  public OAuth2Authentication getAuthentication() {
    return authentication;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}