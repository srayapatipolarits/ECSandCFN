package com.sp.web.assessment.processing;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.model.assessment.AssessmentProgressTracker;
import com.sp.web.model.assessment.PrismAssessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 *
 *         The Mongo DB implementation of Assessment Progress Store Repository.
 */
@Repository
public class MongoAssessmentProgressStoreRepository implements AssessmentProgressStoreRepoistory {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public AssessmentProgressTracker getAssessmentTracker(String userId) {
    return mongoTemplate
        .findOne(query(where("userId").is(userId)), AssessmentProgressTracker.class);
  }

  @Override
  public AssessmentProgressTracker add(AssessmentProgressTracker store) {
    mongoTemplate.insert(store);
    return store;
  }

  @Override
  public AssessmentProgressTracker update(AssessmentProgressTracker store) {
    mongoTemplate.save(store);
    return store;
  }

  @Override
  public void update(PrismAssessment assessment) {
    mongoTemplate.save(assessment);
  }
  
  @Override
  public void remove(AssessmentProgressTracker assessmentFromStore) {
    mongoTemplate.remove(assessmentFromStore);
  }

  @Override
  public PrismAssessment getPrismAssessment(String assessmentId) {
    return mongoTemplate.findById(assessmentId, PrismAssessment.class);
  }

}
