package com.sp.web.mvc.signin.oauth.mongodb;

import com.sp.web.mvc.signin.oauth.OAuth2AuthenticationAccessToken;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MongoOauth2AccessTokenRepository extends
    GenericMongoRepositoryImpl<OAuth2AuthenticationAccessToken> implements
    OAuth2AccessTokenRepository {
  
  @Override
  public OAuth2AuthenticationAccessToken findByTokenId(String tokenId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("tokenId").is(tokenId)),
        OAuth2AuthenticationAccessToken.class);
  }
  
  @Override
  public OAuth2AuthenticationAccessToken findByRefreshToken(String refreshToken) {
    return mongoTemplate.findOne(Query.query(Criteria.where("refreshToken").is(refreshToken)),
        OAuth2AuthenticationAccessToken.class);
  }
  
  @Override
  public OAuth2AuthenticationAccessToken findByAuthenticationId(String authenticationId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("authenticationId").is(authenticationId)),
        OAuth2AuthenticationAccessToken.class);
  }
  
  @Override
  public List<OAuth2AuthenticationAccessToken> findByClientIdAndUserName(String clientId,
      String userId) {
    return mongoTemplate.find(
        Query.query(Criteria.where("clientId").is(clientId).and("userId").is(userId)),
        OAuth2AuthenticationAccessToken.class);
  }
  
  @Override
  public List<OAuth2AuthenticationAccessToken> findByClientId(String clientId) {
    return mongoTemplate.find(Query.query(Criteria.where("clientId").is(clientId)),
        OAuth2AuthenticationAccessToken.class);
  }
  
}
