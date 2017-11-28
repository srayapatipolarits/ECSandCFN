package com.sp.web.mvc.signin.oauth.mongodb;

import com.sp.web.mvc.signin.oauth.OAuth2AuthenticationAccessToken;
import com.sp.web.repository.generic.GenericMongoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OAuth2AccessTokenRepository extends
    GenericMongoRepository<OAuth2AuthenticationAccessToken> {
  
  public OAuth2AuthenticationAccessToken findByTokenId(String tokenId);
  
  public OAuth2AuthenticationAccessToken findByRefreshToken(String refreshToken);
  
  public OAuth2AuthenticationAccessToken findByAuthenticationId(String authenticationId);
  
  public List<OAuth2AuthenticationAccessToken> findByClientIdAndUserName(String clientId,
      String userName);
  
  public List<OAuth2AuthenticationAccessToken> findByClientId(String clientId);
}