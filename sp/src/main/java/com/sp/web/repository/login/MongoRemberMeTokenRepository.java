package com.sp.web.repository.login;

import com.sp.web.mvc.signin.SPRemberMeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoRemberMeTokenRepository class immplments the repository for the Remember me funcitonality.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoRemberMeTokenRepository implements RememberMeTokenRepository {
  
  /** Mongo tmeplate. */
  private MongoTemplate mongoTemplate;
  
  @Autowired
  public MongoRemberMeTokenRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }
  
  /**
   * 
   * @see com.sp.web.repository.login.RememberMeTokenRepository#findBySeries(java.lang.String)
   */
  @Override
  public SPRemberMeToken findBySeries(String series) {
    return mongoTemplate.findOne(Query.query(Criteria.where("series").is(series)),
        SPRemberMeToken.class);
  }
  
  /**
   * @see com.sp.web.repository.login.RememberMeTokenRepository#findByUsername(java.lang.String)
   */
  @Override
  public List<SPRemberMeToken> findByUsername(String username) {
    return mongoTemplate.find(Query.query(Criteria.where("username").is(username)),
        SPRemberMeToken.class);
  }
  
  /**
   * 
   * @see com.sp.web.repository.login.RememberMeTokenRepository#save(com.sp.web.mvc.signin.SPRemberMeToken
   *      )
   */
  @Override
  public void save(SPRemberMeToken spRemberMeToken) {
    mongoTemplate.save(spRemberMeToken);
  }
  
  /**
   * @see com.sp.web.repository.login.RememberMeTokenRepository#remove(com.sp.web.mvc.signin.
   *      SPRemberMeToken
   * 
   */
  @Override
  public void remove(SPRemberMeToken token) {
    mongoTemplate.remove(token);
  }
}
