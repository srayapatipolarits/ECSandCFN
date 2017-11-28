package com.sp.web.repository.goal;

import com.sp.web.Constants;
import com.sp.web.model.goal.UserGoal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation of the user goal repository.
 */
@Repository
public class MongoUserGoalRepository implements UserGoalsRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.goal.UserGoalsRepository#findById(java.lang.String)
   */
  @Override
  public UserGoal findById(String goalId) {
    Assert.hasText(goalId, "Goal id is requried.");
    return mongoTemplate.findById(goalId, UserGoal.class);
  }
  
  @Override
  public void save(UserGoal userGoal) {
    mongoTemplate.save(userGoal);
  }
  
  @Override
  public List<UserGoal> getAllUserGoals() {
    return mongoTemplate.findAll(UserGoal.class);
  }
  
  /**
   * @see com.sp.web.repository.goal.UserGoalsRepository#remove(com.sp.web.model.goal.UserGoal)
   */
  @Override
  public void remove(String userGoalId) {
    mongoTemplate.remove(Query.query(Criteria.where("_id").is(userGoalId)), UserGoal.class);
  }
  
  /**
   * @see com.sp.web.repository.goal.UserGoalsRepository#getUsersForGoals(java.lang.String,
   * java.util.List)
   */
  @Override
  public List<UserGoal> getUsersForGoals(String goalId, List<String> users) {
    List<UserGoal> userGoals = mongoTemplate.find(Query.query(new Criteria().andOperator(
        Criteria.where(Constants.ENTITY_ID).in(users),
        Criteria.where("goalProgress").elemMatch(
            Criteria.where("goalId").is(goalId).and("selected").is(true)))), UserGoal.class);
    
    return userGoals;
  }
  
}
