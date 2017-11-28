package com.sp.web.mvc.signin.oauth.mongodb;

import com.sp.web.mvc.signin.oauth.OAuth2AuthenticationRefreshToken;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoOauth2RefreshTokenRepository extends
    GenericMongoRepositoryImpl<OAuth2AuthenticationRefreshToken> implements
    OAuth2RefreshTokenRepository {
  
  @Override
  public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId) {
    
    return mongoTemplate.findOne(Query.query(Criteria.where("tokenId").is(tokenId)),
        OAuth2AuthenticationRefreshToken.class);
  }
  
}
