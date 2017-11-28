package com.sp.web.repository.goal;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * The Mongo implementation of the action plan repository interface.
 * 
 * @author Dax Abraham
 *
 */
@Repository
public class MongoActionPlanRepository implements ActionPlanRepository {
  
  /** Mongo Template to perform operation on mongo. */
  @Autowired
  @Qualifier("mongoTemplate")
  private MongoTemplate mongoTemplate;
  
  @Autowired
  @Qualifier("deletedTemplate")
  private MongoTemplate deletedTemplate;
  
  @Override
  public List<ActionPlan> findAllActionPlans() {
    return mongoTemplate.findAll(ActionPlan.class);
  }
  
  @Override
  public ActionPlan getActionPlan(String actionPlanId) {
    return mongoTemplate.findById(actionPlanId, ActionPlan.class);
  }
  
  @Override
  public void updateActionPlan(ActionPlan actionPlan) {
    mongoTemplate.save(actionPlan);
  }
  
  @Override
  public void updateUserActionPlan(UserActionPlan userActionPlan) {
    mongoTemplate.save(userActionPlan);
  }
  
  @Override
  public UserActionPlan userActionPlanFindById(String userActionPlanId) {
    return mongoTemplate.findById(userActionPlanId, UserActionPlan.class);
  }
  
  @Override
  public void deleteUserActionPlan(UserActionPlan userActionPlan) {
    mongoTemplate.remove(userActionPlan);
  }
  
  @Override
  public List<ActionPlan> findByCompanyId(String companyId) {
    Assert.hasText(companyId, "Company id is required.");
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_ACTIVE).is(true))), ActionPlan.class);
  }
  
  @Override
  public List<ActionPlan> findAllByCompanyId(String companyId) {
    Assert.hasText(companyId, "Company id is required.");
    return mongoTemplate.find(
        query(new Criteria().orOperator(where(Constants.ENTITY_COMPANY_IDS).in(companyId),
            where(Constants.ENTITY_ALL_COMPANY).is(true),
            where(Constants.ENTITY_CREATED_BY_COMPANY_ID).is(companyId))), ActionPlan.class);
  }
  
  @Override
  public ActionPlan findByName(String name, String createdByCompanyId) {
    return mongoTemplate.findOne(
        query(where(Constants.ENTITY_NAME).regex(name, "i").andOperator(
            where(Constants.ENTITY_CREATED_BY_COMPANY_ID).is(createdByCompanyId))),
        ActionPlan.class);
  }
  /**
   * @see com.sp.web.repository.goal.ActionPlanRepository#deleteActionPlan(com.sp.web.model.goal.ActionPlan)
   */
  @Override
  public void deleteActionPlan(ActionPlan actionPlan) {
    deletedTemplate.save(actionPlan);
    mongoTemplate.remove(actionPlan);
  }
  
  @Override
  public List<ActionPlan> findAllGlobalActionPlans() {
    return mongoTemplate.find(query(where(Constants.ENTITY_TYPE).is(ActionPlanType.SurePeople)),
        ActionPlan.class);
  }

  @Override
  public List<ActionPlan> findAllByStepType(StepType[] stepTypes) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_STEP_TYPE).in(Arrays.asList(stepTypes))), ActionPlan.class);
  }
}
