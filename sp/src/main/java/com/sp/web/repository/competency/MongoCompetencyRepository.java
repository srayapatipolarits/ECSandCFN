package com.sp.web.repository.competency;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The Mongo implementation of the competency repository interface.
 */
@Repository
public class MongoCompetencyRepository implements CompetencyRepository {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Override
  public List<CompetencyProfile> getAll() {
    return mongoTemplate.findAll(CompetencyProfile.class);
  }
  
  @Override
  public CompetencyProfile findById(String competencyId) {
    return mongoTemplate.findById(competencyId, CompetencyProfile.class);
  }
  
  @Override
  public <T> void update(T objectToUpdate) {
    mongoTemplate.save(objectToUpdate);
  }
  
  @Override
  public <T> void delete(T objectToDelete) {
    mongoTemplate.remove(objectToDelete);
  }
  
  @Override
  public List<CompetencyProfile> getCompanyCompetencyProfiles(String companyId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        CompetencyProfile.class);
  }
  
  @Override
  public CompetencyEvaluation getCompetencyEvaluation(String competencyEvaluationId) {
    return mongoTemplate.findById(competencyEvaluationId, CompetencyEvaluation.class);
  }
  
  @Override
  public void removeAllEvaluationRequests(String companyId) {
    mongoTemplate.remove(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        CompetencyEvaluationRequest.class);
  }
  
  @Override
  public CompetencyEvaluationRequest getEvaluationRequest(String feedbackUserId) {
    return mongoTemplate.findOne(
        query(where(Constants.ENTITY_FEEDBACK_USER_ID).is(feedbackUserId)),
        CompetencyEvaluationRequest.class);
  }
  
  @Override
  public UserCompetencyEvaluationDetails getCompetencyEvaluationDetailsById(
      String evaluationDetailsId) {
    return mongoTemplate.findById(evaluationDetailsId, UserCompetencyEvaluationDetails.class);
  }
  
  @Override
  public List<CompetencyEvaluation> getCurrentCompetencyEvaluations() {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPLETED).is(false)),
        CompetencyEvaluation.class);
  }
  
  /**
   * @see com.sp.web.repository.competency.CompetencyRepository#getAllCompletedCompetancyEvaluations(java.lang.String)
   */
  @Override
  public List<CompetencyEvaluation> getAllCompletedCompetancyEvaluations(String companyId) {
    MongoConverter converter = mongoTemplate.getConverter();
    
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(CompetencyEvaluation.class));
    
    List<DBObject> dbObjectList = collection
        .find(
            (query(where(Constants.ENTITY_COMPLETED).is(true).and(Constants.ENTITY_COMPANY_ID)
                .is(companyId))).getQueryObject()).sort(new BasicDBObject("endedOn", -1)).toArray();
    List<CompetencyEvaluation> companyCompetency = dbObjectList.stream()
        .map(dbObject -> converter.read(CompetencyEvaluation.class, dbObject))
        .collect(Collectors.toList());
    
    return companyCompetency;
  }
  
  @Override
  public List<UserCompetencyEvaluationDetails> getAllUserCompetencyEvaluationDetails(String userId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_USER_ID).is(userId)),
        UserCompetencyEvaluationDetails.class);
  }
  
  @Override
  public UserCompetency getUserCompetencyEvaluation(String userId) {
    return mongoTemplate.findOne(query(where("userId").is(userId)), UserCompetency.class);
  }
  
  @Override
  public void save(UserCompetency userCompetency) {
    mongoTemplate.save(userCompetency);
  }
  
  @Override
  public List<SpectrumCompetencyProfileEvaluationResults> getAllSpectrumCompetencyProfileEvaluationResults(
      String companyId) {
    return mongoTemplate.find(query(where("companyId").is(companyId)),
        SpectrumCompetencyProfileEvaluationResults.class);
  }
  
  @Override
  public SpectrumCompetencyProfileEvaluationResults findSpectrumCompetencyProfileEvaluationResult(
      String competencyProfileId) {
    return mongoTemplate.findOne(query(where("competencyProfileId").is(competencyProfileId)),
        SpectrumCompetencyProfileEvaluationResults.class);
  }
  
  @Override
  public int deleteAllUserCompetencyEvaluationDetails(String userId) {
    return mongoTemplate.remove(query(where(Constants.ENTITY_USER_ID).is(userId)),
        UserCompetencyEvaluationDetails.class).getN();
  }
  
  @Override
  public List<UserCompetency> getAllUserCompetencies(String companyId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        UserCompetency.class);
  }
}
