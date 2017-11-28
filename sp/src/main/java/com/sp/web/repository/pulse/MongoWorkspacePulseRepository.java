package com.sp.web.repository.pulse;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the workspace pulse repository.
 */
@Repository
public class MongoWorkspacePulseRepository implements WorkspacePulseRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public PulseQuestionSet findPulseQuestionSetByName(String pulseQuestionSetName) {
    return mongoTemplate.findOne(query(where("name").is(pulseQuestionSetName)), PulseQuestionSet.class);
  }

  @Override
  public PulseQuestionSet findPulseQuestionSetById(String pulseQuestionSetId) {
    return mongoTemplate.findById(pulseQuestionSetId, PulseQuestionSet.class);
  }

  @Override
  public PulseRequest findPulseRequest(String pulseQuestionSetId, String companyId) {
    return mongoTemplate.findOne(
        query(where("pulseQuestionSetId").is(pulseQuestionSetId).and(Constants.ENTITY_COMPANY_ID)
            .is(companyId)), PulseRequest.class);
  }

  @Override
  public PulseRequest findPulseRequest(String companyId) {
    return mongoTemplate.findOne(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        PulseRequest.class);
  }
  
  @Override
  public PulseRequest createPulseRequest(PulseRequest pulseRequest) {
    mongoTemplate.save(pulseRequest);
    return pulseRequest;
  }

  @Override
  public List<PulseResults> getAllPulseResults(String pulseQuestionSetId, String companyId) {
    Query queryObject = query(where("pulseQuestionSetId").is(pulseQuestionSetId).andOperator(
        where("companyId").is(companyId)));
    return getAllPulseResults(queryObject);
  }

  @Override
  public List<PulseResults> getAllPulseResults(String companyId) {
    Query queryObject = query(where("companyId").is(companyId));
    return getAllPulseResults(queryObject);
  }

  /**
   * Gets all the pulse results.'
   * 
   * @param queryObject
   *            - query object
   * @return
   *    the pulse results
   */
  public List<PulseResults> getAllPulseResults(Query queryObject) {
    queryObject.fields().exclude("pulseScore");
    queryObject.with(new Sort(Sort.Direction.DESC, "endDate"));
    return mongoTemplate.find(queryObject, PulseResults.class);
  }

  @Override
  public PulseResults findPulseResultById(String pulseResultId) {
    Assert.hasText(pulseResultId, "Pulse result id required !!!");
    return mongoTemplate.findById(pulseResultId, PulseResults.class);
  }

  @Override
  public List<PulseAssessment> getAllPulseAssessments(String pulseRequestId, List<String> memberList) {
    return mongoTemplate.find(
        query(where("pulseRequestId").is(pulseRequestId).andOperator(
            where("memberId").in(memberList))), PulseAssessment.class);
  }
  
  @Override
  public List<PulseAssessment> getAllPulseAssessments(String pulseRequestId) {
    return mongoTemplate.find(query(where("pulseRequestId").is(pulseRequestId)),
        PulseAssessment.class);
  }

  @Override
  public void savePulseQuestionSet(PulseQuestionSet pulseQuestionSet) {
    mongoTemplate.save(pulseQuestionSet);
  }

  @Override
  public PulseRequest findPulseRequestById(String pulseRequestId) {
    return mongoTemplate.findById(pulseRequestId, PulseRequest.class);
  }

  @Override
  public void savePulseAssessment(PulseAssessment pulseAssessment) {
    mongoTemplate.save(pulseAssessment);
  }

  @Override
  public List<PulseRequest> findPulseRequestsByEndDate(LocalDate endDate) {
    return mongoTemplate.find(query(where("endDate").lte(endDate)), PulseRequest.class);
  }

  @Override
  public void savePulseResult(PulseResults pulseResult) {
    mongoTemplate.save(pulseResult);
  }

  @Override
  public void removePulseRequest(PulseRequest pulseRequest) {
    mongoTemplate.remove(pulseRequest);
  }

  @Override
  public PulseAssessment getPulseAssessment(String pulseRequestId, String memberId) {
    Assert.hasText(pulseRequestId, "Pulse request id is required.");
    Assert.hasText(memberId, "Member id is required.");
    return mongoTemplate.findOne(
        query(where("pulseRequestId").is(pulseRequestId)
            .andOperator(where("memberId").is(memberId))), PulseAssessment.class);
  }

  @Override
  public List<PulseQuestionSet> findPulseQuestionSetsFor(String companyId) {
    Assert.hasText(companyId, "Company id is required.");
    return mongoTemplate.find(
        query(new Criteria().orOperator(where(Constants.ENTITY_IS_FOR_ALL).is(true),
            where(Constants.ENTITY_COMPANY_ID).in(companyId))), PulseQuestionSet.class);
  }

  @Override
  public List<PulseQuestionSet> getAllPulseQuestionSets() {
    return mongoTemplate.findAll(PulseQuestionSet.class);
  }

  @Override
  public List<PulseRequest> findAllPulseRequests() {
    return mongoTemplate.findAll(PulseRequest.class);
  }

  @Override
  public void delete(PulseResults pulseResult) {
    mongoTemplate.remove(pulseResult);
  }

  @Override
  public void delete(PulseAssessment pulseAssessment) {
    mongoTemplate.remove(pulseAssessment);
  }
 
  @Override
  public List<PulseAssessment> getAllPulseAssessmentsForUser(String userId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_MEMBER_ID).is(userId)),
        PulseAssessment.class);
  }
}
