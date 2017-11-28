package com.sp.web.repository.external;

import com.sp.web.model.ThirdPartyUser;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository("thirdPartyRepository")
public class MongoThirdPartyRepository extends GenericMongoRepositoryImpl<ThirdPartyUser> implements
    ThirdPartyRepository {
  
  /**
   * remove the user by id.
   */
  @Override
  public void removeByUserlId(String spUserId) {
    mongoTemplate
        .remove(Query.query(Criteria.where("spUserId").is(spUserId)), ThirdPartyUser.class);
  }
  
  @Override
  public ThirdPartyUser findByExternalUid(String uid) {
    return mongoTemplate.findOne(Query.query(Criteria.where("uid").is(uid)), ThirdPartyUser.class);
  }
  
  @Override
  public ThirdPartyUser findBySpUserId(String spUserId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("spUserId").is(spUserId)),
        ThirdPartyUser.class);
  }
  
}
