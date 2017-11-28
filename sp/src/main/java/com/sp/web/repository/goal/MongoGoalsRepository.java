package com.sp.web.repository.goal;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * <code>MongoGoalRepository</code> class will load the goals and retreive the goals from the mongo.
 * 
 * @author pradeep
 *
 */
@Repository
public class MongoGoalsRepository implements GoalsRepository {
  
  /** Mongo Template to perform operation on mongo. */
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  @Qualifier("deletedTemplate")
  private MongoTemplate deletedTemplate;
  
  /**
   * <code>getGoals</code> method will return the goals for the requsted personlityTypes
   * 
   * @param personalityType
   *          for which goal is to be retreived.
   * @return the goal for which personalityType
   */
  public List<SPGoal> getGoals(PersonalityType personalityType) {
    return mongoTemplate.find(
        addSortToQuery(Query.query(Criteria.where("personalityType").is(personalityType)), "name"),
        SPGoal.class);
  }
  
  /**
   * <code>updateGoal</code> method will update the goal in the repository if exist or create a new
   * one.
   * 
   * @param goal
   *          to be updated/created
   * @return the goal
   */
  @Override
  public SPGoal updateGoal(SPGoal goal) {
    mongoTemplate.save(goal);
    return goal;
  }
  
  /**
   * <code>saveAllGoals</code> will save all the goals in the mongo.
   * 
   * @param spGoals
   *          to be saved.
   */
  public void saveAllGoals(List<SPGoal> spGoals) {
    mongoTemplate.insertAll(spGoals);
  }
  
  /**
   * @see com.sp.web.service.goals.GoalsRepository#findById(java.lang.String)
   */
  @Override
  public SPGoal findById(String goalId) {
    return mongoTemplate.findById(goalId, SPGoal.class);
  }
  
  @Override
  public List<SPGoal> findAllGoalsById(List<String> goalId) {
    return mongoTemplate.find(addSortToQuery(Query.query(Criteria.where("id").in(goalId)), "name"),
        SPGoal.class);
  }
  
  @Override
  public List<SPGoal> findAllGoalsById(Set<String> goalId) {
    return mongoTemplate.find(addSortToQuery(Query.query(Criteria.where("id").in(goalId)), "name"),
        SPGoal.class);
  }
  
  public SPGoal findGoalByName(String name) {
    return mongoTemplate.findOne(Query.query(Criteria.where("name").is(name)), SPGoal.class);
  }
  
  /**
   * 
   * @see com.sp.web.service.goals.GoalsRepository#findAllGoalsByNames(java.util.List)
   */
  @Override
  public List<SPGoal> findAllGoalsByNames(List<String> name) {
    return mongoTemplate.find(addSortToQuery(Query.query(Criteria.where("name").in(name)), "name"),
        SPGoal.class);
  }
  
  @Override
  public void updateGoals(List<SPGoal> spGoals) {
    
    if (!CollectionUtils.isEmpty(spGoals)) {
      spGoals.stream().forEach(sp -> mongoTemplate.save(sp));
      
    }
    
  }
  
  private Query addSortToQuery(Query query, String fieldName) {
    query.with(new Sort(Sort.Direction.ASC, fieldName));
    return query;
  }
  
  @Override
  public List<SPGoal> findAllGoals() {
    return mongoTemplate.findAll(SPGoal.class);
  }
  
  /**
   * @see com.sp.web.repository.goal.GoalsRepository#findAllGoalsByCategory(com.sp.web.model.goal.GoalCategory)
   */
  @Override
  public List<SPGoal> findAllGoalsByCategory(GoalCategory... category) {
    return mongoTemplate
        .find(
            addSortToQuery(Query.query(Criteria.where("category").in(Arrays.asList(category))),
                "name"), SPGoal.class);
  }
  
  @Override
  public PersonalityPracticeArea findPersonalityPracticeArea(String personalityType) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("personalityType").is(personalityType)),
        PersonalityPracticeArea.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.goal.GoalsRepository#savePersonalityPracticeArea(com.sp.web.model.goal
   * .PersonalityPracticeArea)
   */
  @Override
  public void updatePersonalityPracticeArea(PersonalityPracticeArea personalityPracticeArea) {
    mongoTemplate.save(personalityPracticeArea);
  }
  
  @Override
  public List<PersonalityPracticeArea> findAllPersonalityPracticeAreas() {
    return mongoTemplate.findAll(PersonalityPracticeArea.class);
  }
  
  @Override
  public void removeGoal(SPGoal spGoal) {
    mongoTemplate.remove(spGoal);
  }
  
  @Override
  public Blueprint getBlueprint(String blueprintId) {
    return mongoTemplate.findById(blueprintId, Blueprint.class);
  }
  
  @Override
  public void updateBlueprint(Blueprint blueprint) {
    mongoTemplate.save(blueprint);
  }
  
  @Override
  public SPGoal findGoalByName(String name, GoalCategory category) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("name").is(name).and("category").is(category)),
        SPGoal.class);
  }
}
