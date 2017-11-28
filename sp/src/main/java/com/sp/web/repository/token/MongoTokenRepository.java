package com.sp.web.repository.token;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.model.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * <code>MongoTokenRepository</code> provides implementation for storing.
 * 
 * @author pruhil
 * 
 */
@Repository
public class MongoTokenRepository implements TokenRepository {

  /** Mongo template to perform crud operation on mongo db. */
  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * @see com.sp.web.service.token.TokenRepository#persistToken(com.sp.web.model .Token)
   *      <code>persistToken</code> method will persist the token in the db
   * 
   * @param token
   *          to be persisted.
   */
  @Override
  public void persistToken(Token token) {
    mongoTemplate.save(token);
  }

  /**
   * 
   * @see com.sp.web.service.token.TokenRepository#findTokenById(java.lang.String)
   */
  @Override
  public Token findTokenById(String tokenID) {
    return mongoTemplate.findOne(query(where("tokenId").is(tokenID)), Token.class);
  }

  /**
   * 
   * @see com.sp.web.service.token.TokenRepository#deleteToken(com.sp.web.model .Token)
   */
  @Override
  public void deleteToken(Token token) {
    mongoTemplate.remove(token);
  }

  /**
   * @see com.sp.web.service.token.TokenRepository#saveTokenInformation(com.sp.web.model .Token)
   */
  @Override
  public Token updateToken(Token token) {
    mongoTemplate.save(token);
    return token;
  }

  @Override
  public Token findById(String tokenId) {
    return mongoTemplate.findById(tokenId, Token.class);
  }
}
