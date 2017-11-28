package com.sp.web.repository;

import com.sp.web.Constants;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.log.LogActionType;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation of the feedback repository.
 */
@Repository
public class MongoFeedbackRepository implements FeedbackRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;
  
  @Autowired
  @Qualifier("activityLog")
  LogGateway logGateway;
  
  @Override
  public FeedbackUser addFeedbackUser(FeedbackUser fbUser) {
    mongoTemplate.save(fbUser);
    return fbUser;
  }
  
  @Override
  public FeedbackUser findByIdValidated(String feedbackUserId) {
    Assert.notNull(feedbackUserId, "The reference id must not be null !!!");
    return mongoTemplate.findById(feedbackUserId, FeedbackUser.class);
  }
  
  @Override
  public FeedbackUser updateFeedbackUser(FeedbackUser fbUser) {
    mongoTemplate.save(fbUser);
    return fbUser;
  }
  
  @Override
  public List<FeedbackRequest> getAllFeedbackRequest() {
    return mongoTemplate.findAll(FeedbackRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#getAllFeedbackRequest(java.lang.String)
   */
  @Override
  public List<FeedbackRequest> getAllFeedbackRequest(String requestedById) {
    Assert.notNull(requestedById, "The requestedById id must not be null !!!");
    return mongoTemplate.find(Query.query(Criteria.where("requestedById").is(requestedById)),
        FeedbackRequest.class);
    
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#createFeedbackRequest(com.sp.web.model.FeedbackRequest)
   */
  @Override
  public void createFeedbackRequest(FeedbackRequest feedbackRequest) {
    mongoTemplate.save(feedbackRequest);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#findFeedbackRequest(java.lang.String)
   */
  @Override
  public FeedbackRequest findFeedbackRequest(String feedbackRequestId) {
    Assert.notNull(feedbackRequestId, "The feedbackRequest id must not be null !!!");
    return mongoTemplate.findById(feedbackRequestId, FeedbackRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#findAllFeebdackRequest(java.lang.String)
   */
  @Override
  public List<FeedbackRequest> findAllFeebdackRequest(String feedbackUserId) {
    return mongoTemplate.find(Query.query(Criteria.where("requestedById").is(feedbackUserId)),
        FeedbackRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#getAllFeedbackUsers(java.lang.String)
   */
  @Override
  public List<FeedbackUser> getAllFeedbackUsers(String feedbackUserId) {
    return mongoTemplate.find(Query.query(Criteria.where("feedbackUserId").is(feedbackUserId)),
        FeedbackUser.class);
  }
  
  @Override
  public FeedbackArchiveRequest archiveFeedbackRequest(FeedbackRequest feedbackRequest, User user) {
    
    /* get the feedback user */
    FeedbackUser feedbackUser = findByIdValidated(feedbackRequest.getFeedbackUserId());
    
    /* Create a new archive feedback rqeuest and archive feedback user */
    FeedbackUserArchive feedbackUserArchive = new FeedbackUserArchive(feedbackUser);
    /* Create new FeedbackArchive Request */
    FeedbackArchiveRequest feedbackArchiveRequest = new FeedbackArchiveRequest(feedbackRequest);
    
    if (feedbackUser.getUserStatus() == UserStatus.VALID) {
      feedbackArchiveRequest.setRequestStatus(RequestStatus.COMPLETED);
      logGateway.logActivity(new LogRequest(LogActionType.FeedbackArchived, user, feedbackUser));
    } else {
      feedbackArchiveRequest.setRequestStatus(RequestStatus.DEACTIVE);
      logGateway.logActivity(new LogRequest(LogActionType.FeedbackDeactive, user, feedbackUser));
    }
    mongoTemplate.save(feedbackUserArchive);
    feedbackArchiveRequest.setFeedbackUserId(feedbackUserArchive.getId());
    feedbackArchiveRequest.setFeedbackUserMemberId(feedbackUser.getMemberId());
    mongoTemplate.save(feedbackArchiveRequest);
    
    /* Remove the feedback user */
    mongoTemplate.remove(feedbackUser);
    mongoTemplate.remove(feedbackRequest);
    return feedbackArchiveRequest;
    
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#getAllArchivedFeedbackRequests(java.lang.String)
   */
  @Override
  public List<? extends FeedbackRequest> getAllArchivedFeedbackRequests(String requestedById) {
    Assert.hasLength(requestedById, "Requestd BY Id cannot be null");
    return mongoTemplate.find(Query.query(Criteria.where("requestedById").is(requestedById)),
        FeedbackArchiveRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#updateFeedbackReqest(com.sp.web.model.FeedbackRequest)
   */
  @Override
  public void updateFeedbackReqest(FeedbackRequest feedbackRequest) {
    mongoTemplate.save(feedbackRequest);
  }
  
  @Override
  public FeedbackRequest findFeedbackRequestByFeedbackUserId(String feedbackUserId) {
    Assert.notNull(feedbackUserId, "The feedbackUserId must not be null !!!");
    return mongoTemplate.findOne(Query.query(Criteria.where("feedbackUserId").is(feedbackUserId)),
        FeedbackRequest.class);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#getAllGoals(com.sp.web.model.User)
   */
  @Override
  public List<FeedbackUser> getAllFeedbackUser(User user) {
    List<FeedbackUser> feedbackUser = mongoTemplate.find(
        Query.query(Criteria.where("feedbackFor").is(user.getId())
            .andOperator(Criteria.where("userStatus").is("VALID"))), FeedbackUser.class);
    return feedbackUser;
    
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#getAllFeedbackUserArchive(com.sp.web.model.User)
   */
  @Override
  public List<FeedbackUserArchive> getAllFeedbackUserArchive(User user) {
    List<FeedbackUserArchive> feedbackUser = mongoTemplate.find(
        Query.query(Criteria.where("feedbackFor").is(user.getId())
            .andOperator(Criteria.where("userStatus").is("VALID"))), FeedbackUserArchive.class);
    return feedbackUser;
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.FeedbackRepository#findFeedbackUserByFor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public FeedbackUser findFeedbackUserByFor(String email, String id) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("email").is(email)
            .andOperator(Criteria.where("feedbackFor").is(id))), FeedbackUser.class);
  }
  
  @Override
  public void remove(FeedbackUser feedbackUser) {
    mongoTemplate.remove(feedbackUser);
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#getPendingFeedbackRequests(java.util.List)
   */
  @Override
  public int getPendingFeedbackRequests(List<String> reqestedByIds) {
    List<FeedbackRequest> feedbackRequest = mongoTemplate.find(
        Query.query(Criteria.where("requestedById").in(reqestedByIds)), FeedbackRequest.class);
    
    List<String> frs = feedbackRequest.stream().map(fr -> fr.getFeedbackUserId())
        .collect(Collectors.toList());
    
    int size = mongoTemplate.find(
        Query.query(Criteria.where("_id").in(frs).and("userStatus").ne(UserStatus.VALID)),
        FeedbackUser.class).size();
    return size;
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#removeFeedbackRequest(com.sp.web.model.FeedbackRequest)
   */
  @Override
  public void removeFeedbackRequest(FeedbackRequest feedbackRequest) {
    mongoTemplate.remove(feedbackRequest);
  }
  
  @Override
  public void removeFeedbacArchivedRequest(FeedbackArchiveRequest feedbackArchiveRequest) {
    mongoTemplate.remove(feedbackArchiveRequest);
    
  }
  
  /**
   * @see com.sp.web.repository.FeedbackRepository#removeFeedbackUserArchive(com.sp.web.model.FeedbackUserArchive)
   */
  @Override
  public void removeFeedbackUserArchive(FeedbackUserArchive feedbackUserArchive) {
    mongoTemplate.remove(feedbackUserArchive);
  }
  
  @Override
  public FeedbackUser findFeedbackUser(String memberId, String feedbackForId,
      FeatureType featureType) {
    return mongoTemplate.findOne(
        Query.query(Criteria
            .where("memberId")
            .is(memberId)
            .andOperator(Criteria.where("feedbackFor").is(feedbackForId),
                Criteria.where("featureType").is(featureType))), FeedbackUser.class);
  }
  
  @Override
  public List<FeedbackUser> findFeedbackUsers(String feedbackFor, FeatureType featureType) {
    return mongoTemplate.find(
        Query.query(Criteria.where("feedbackFor").is(feedbackFor)
            .andOperator(Criteria.where("featureType").is(featureType))), FeedbackUser.class);
  }

  @Override
  public FeedbackUser findFeedbackUserByEmail(String email, String userFor,
      FeatureType featureType) {
    return mongoTemplate.findOne(
        Query.query(Criteria
            .where(Constants.ENTITY_EMAIL)
            .is(email)
            .andOperator(Criteria.where("feedbackFor").is(userFor),
                Criteria.where("featureType").is(featureType))), FeedbackUser.class);
  }
}
