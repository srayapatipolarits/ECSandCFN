package com.sp.web.mvc.signin.oauth.mongodb;

import com.sp.web.mvc.signin.oauth.OAuth2AuthenticationRefreshToken;
import com.sp.web.repository.generic.GenericMongoRepository;

import org.springframework.stereotype.Repository;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 23/05/2013
 */
@Repository
public interface OAuth2RefreshTokenRepository extends
    GenericMongoRepository<OAuth2AuthenticationRefreshToken> {
  
  public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
}