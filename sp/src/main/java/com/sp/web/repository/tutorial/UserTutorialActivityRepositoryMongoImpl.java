package com.sp.web.repository.tutorial;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

@Repository
public class UserTutorialActivityRepositoryMongoImpl extends GenericMongoRepositoryImpl<UserTutorialActivity>
    implements UserTutorialActivityRepository {

  @Override
  public UserTutorialActivity findByUserId(String userId) {
    return mongoTemplate.findOne(query(where(Constants.ENTITY_USER_ID).is(userId)),
        UserTutorialActivity.class);
  }
  
}
