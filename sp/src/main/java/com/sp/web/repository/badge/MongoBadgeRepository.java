package com.sp.web.repository.badge;

import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * MongoBadgeRepository is the repository which fetches the badges data for the user from the mogno
 * database.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoBadgeRepository extends GenericMongoRepositoryImpl<UserBadgeActivity> implements
    BadgeRepository {
  
  /**
   * @see BadgeRepository#getUserBadges(String)
   * @param userId
   *          of the user.
   */
  @Override
  public UserBadgeActivity getUserBadges(String userId) {
    Assert.hasText(userId);
    return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)),
        UserBadgeActivity.class);
  }
  
  /**
   * @see BadgeRepository#getUserBadgesByCompany(String)
   * @param companyId
   *          of the user.
   */
  @Override
  public List<UserBadgeActivity> getUserBadgesByCompany(String companyId) {
    return mongoTemplate.find(Query.query(Criteria.where("companyId").is(companyId)),
        UserBadgeActivity.class);
  }
  
}
