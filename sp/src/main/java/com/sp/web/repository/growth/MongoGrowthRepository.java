package com.sp.web.repository.growth;

import com.sp.web.model.GrowthFeedbackQuestions;
import com.sp.web.model.GrowthRequest;
import com.sp.web.model.GrowthRequestArchived;
import com.sp.web.model.RequestStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <code>MongoGrowthRepository</code> contains mongo operation for growth.
 * 
 * @author pradeep
 *
 */
@Repository
public class MongoGrowthRepository implements GrowthRepository {
  
  /** Mongo Template for accessing data from mongo. */
  @Autowired
  private MongoTemplate mongoTemplate;
  
  /**
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#createGrowthRequest(com.sp.web.model.GrowthRequest)
   */
  @Override
  public GrowthRequest createGrowthRequest(GrowthRequest growthRequest) {
    
    /* create the grwoth request */
    mongoTemplate.save(growthRequest);
    return growthRequest;
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#findGrowthRequest(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public List<GrowthRequest> findGrowthRequest(String memberEmail, String userEmail) {
    return mongoTemplate.find(
        Query.query(Criteria.where("memberEmail").is(memberEmail).and("requestedByEmail")
            .is(userEmail)), GrowthRequest.class);
    
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#archiveGrowthRequest(java.lang.String)
   */
  @Override
  public void archiveGrowthRequest(String growthRequestID) {
    
    GrowthRequest growthRequest = mongoTemplate.findOne(
        Query.query(Criteria.where("id").is(growthRequestID)), GrowthRequest.class);
    
    if (growthRequest.getRequestStatus() != RequestStatus.COMPLETED
        && growthRequest.getRequestStatus() != RequestStatus.DECLINED) {
      growthRequest.setRequestStatus(RequestStatus.DEACTIVE);
    }
    archiveGrowthRequest(growthRequest);
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#archiveGrowthRequest(java.lang.String)
   */
  @Override
  public void archiveGrowthRequest(GrowthRequest growthRequest) {
    
    /* Create a new Archived Growth request */
    GrowthRequestArchived growthRequestArchived = new GrowthRequestArchived(growthRequest);
    
    mongoTemplate.save(growthRequestArchived);
    /* remove the growth Request for the archived user */
    mongoTemplate.remove(growthRequest);
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#getArchivedGrowthRequest(java.lang.String)
   */
  @Override
  public List<GrowthRequestArchived> getArchivedGrowthRequest(String email) {
    return mongoTemplate.find(Query.query(Criteria.where("requestedByEmail").is(email)),
        GrowthRequestArchived.class);
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#findArchivedGrowthRequest(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public GrowthRequestArchived findArchivedGrowthRequest(String archivedId, String requestedByEmail) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("id").is(archivedId).and("requestedByEmail")
            .is(requestedByEmail)), GrowthRequestArchived.class);
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#allRecievedGrowthRequest(java.lang.String)
   */
  @Override
  public List<GrowthRequest> allRecievedGrowthRequest(String email) {
    
    /* fetcht the growth request where logged in user is in the member email */
    return mongoTemplate.find(
        Query.query(Criteria.where("memberEmail").is(email)
            .andOperator(Criteria.where("requestStatus").ne(RequestStatus.DELETED))),
        GrowthRequest.class);
  }
  
  /**
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#findGrowthRequest(java.lang.String)
   */
  @Override
  public GrowthRequest findGrowthRequest(String growthRequestId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("id").is(growthRequestId)),
        GrowthRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#removeGrowthRequest(java.lang.String)
   */
  @Override
  public void removeGrowthRequest(String growthRequestId) {
    mongoTemplate
        .remove(Query.query(Criteria.where("id").is(growthRequestId)), GrowthRequest.class);
    
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#updateGrowthRequest(com.sp.web.model.GrowthRequest)
   */
  @Override
  public void updateGrowthRequest(GrowthRequest growthRequest) {
    mongoTemplate.save(growthRequest);
  }
  
  /**
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#getAllGrowthFeedbackQuestions()
   */
  @Override
  public List<GrowthFeedbackQuestions> getAllGrowthFeedbackQuestions() {
    return mongoTemplate.findAll(GrowthFeedbackQuestions.class);
  }
  
  /**
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#getAllGrowthFeedbackQuestions()
   */
  @Override
  public GrowthFeedbackQuestions getGrowthFeedbackQuestion(String questionId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("id").is(questionId)),
        GrowthFeedbackQuestions.class);
  }
  
  @Override
  public List<GrowthFeedbackQuestions> getGrowthFeedbackQuestionForGoals(List<String> goal) {
    goal = Optional.ofNullable(goal).orElse(Collections.emptyList());
    
    if (goal == null) {
      return new ArrayList<GrowthFeedbackQuestions>();
    }
    return mongoTemplate.find(Query.query(Criteria.where("questionId").in(goal)),
        GrowthFeedbackQuestions.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#getAllGrowthRequests(java.lang.String)
   */
  @Override
  public List<GrowthRequest> getAllGrowthRequests(String email) {
    /* fetcht the growth request where logged in user is in the member email */
    return mongoTemplate.find(Query.query(Criteria.where("requestedByEmail").is(email)),
        GrowthRequest.class);
  }
  
  /**
   * <code>findGrowthRequestByUser</code> method returns the growth request requested by the current
   * user. It is used when the user is deleted from the system and to find out all the growth
   * request associated to it.
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#findGrowthRequestByUser(java.lang.String)
   */
  @Override
  public List<GrowthRequest> findGrowthRequestByUser(String email) {
    return mongoTemplate.find(Query.query(Criteria.where("requestedByEmail").is(email)),
        GrowthRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.growth.GrowthRepository#removeArchivedGrowthRequest(com.sp.web.model.
   *      GrowthRequestArchived)
   */
  @Override
  public void removeArchivedGrowthRequest(GrowthRequestArchived growthRequestArchived) {
    mongoTemplate.remove(growthRequestArchived);
  }
  
  /**
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.growth.GrowthRepository#getPendingGrowthRequests(java.util.List)
   */
  @Override
  public int getPendingGrowthRequests(List<String> userEmails) {
    Criteria criteria1 = Criteria.where("requestStatus").is(RequestStatus.NOT_INITIATED);
    Criteria criteria2 = Criteria.where("requestStatus").is(RequestStatus.ACTIVE);
    return mongoTemplate.find(
        Query.query(new Criteria().orOperator(criteria1, criteria2).and("requestedByEmail")
            .in(userEmails)), GrowthRequest.class).size();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.growth.GrowthRepository#getAllGrowthRequestsByCompany(java.lang.String)
   */
  @Override
  public List<GrowthRequest> getAllGrowthRequestsByCompany(String companyId) {
    List<GrowthRequest> requests = new ArrayList<GrowthRequest>();
    requests.addAll(mongoTemplate.find(Query.query(Criteria.where("companyId").is(companyId)), GrowthRequest.class));
    requests.addAll(mongoTemplate.find(Query.query(Criteria.where("companyId").is(companyId)), GrowthRequestArchived.class));
    return requests;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.growth.GrowthRepository#getAllGrowthRequestArchivedsByCompany(java.lang
   * .String)
   */
  @Override
  public List<GrowthRequestArchived> getAllGrowthRequestArchivedsByCompany(String companyId) {
    return mongoTemplate.find(Query.query(Criteria.where("companyId").is(companyId)), GrowthRequestArchived.class);
  }
  
  @Override
  public List<GrowthRequest> getAllRequests() {
    List<GrowthRequest> requests = new ArrayList<GrowthRequest>();
    requests.addAll(mongoTemplate.findAll(GrowthRequest.class));
    requests.addAll(mongoTemplate.findAll(GrowthRequestArchived.class));
    return requests;
  }
  
}