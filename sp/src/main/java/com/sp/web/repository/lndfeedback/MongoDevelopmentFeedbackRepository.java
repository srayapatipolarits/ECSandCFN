package com.sp.web.repository.lndfeedback;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.model.lndfeedback.DevelopmentFeedbackResponse;
import com.sp.web.model.lndfeedback.UserDevelopmentFeedbackResponse;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoDevelopmentFeedbackRepositoryImpl.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoDevelopmentFeedbackRepository extends
    GenericMongoRepositoryImpl<DevelopmentFeedback> implements DevelopmentFeedbackRepository {
  
  /**
   * @see com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository#findByUserId(java.lang.String)
   *      .
   */
  @Override
  public List<DevelopmentFeedback> findByUserId(String id) {
    Criteria criteria = where(Constants.ENTITY_USER_ID).is(id);
    return getSortedDevelopmentFeedbacks(criteria);
  }
  
  /**
   * Returns the sorted devleopment feedback which are recently updated.
   * 
   * @param criteria
   *          is the query criteria
   * @return the recently updated sorted way.
   */
  private List<DevelopmentFeedback> getSortedDevelopmentFeedbacks(Criteria criteria) {
    MongoConverter converter = mongoTemplate.getConverter();
    
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(DevelopmentFeedback.class));
    
    List<DBObject> dbObjectList = collection.find((query(criteria).getQueryObject()))
        .sort(new BasicDBObject("updatedOn", -1)).toArray();
    List<DevelopmentFeedback> developmentFeedbacks = dbObjectList.stream()
        .map(dbObject -> converter.read(DevelopmentFeedback.class, dbObject))
        .collect(Collectors.toList());
    
    return developmentFeedbacks;
  }
  
  /**
   * @see com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository#findAllByParentRefId(java.lang
   *      .String, java.lang.String, com.sp.web.model.SPFeature)
   */
  @Override
  public List<DevelopmentFeedback> findAllByParentRefId(String parentRefId, String companyId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).and("feedParentRefId")
            .is(parentRefId)), DevelopmentFeedback.class);
  }
  
  @Override
  public List<DevelopmentFeedback> findAllByParentRefId(String parentRefId) {
    return mongoTemplate.find(query(Criteria.where("feedParentRefId").is(parentRefId)),
        DevelopmentFeedback.class);
  }
  
  /**
   * @see com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository#findAllByDevFeedRefId(java.
   *      lang.String, java.lang.String)
   */
  @Override
  public List<DevelopmentFeedback> findAllByDevFeedRefId(String devFeedRefId, String companyId,
      String feature) {
    Criteria criteria = Criteria.where(Constants.ENTITY_COMPANY_ID).is(companyId).and("spFeature")
        .is(feature).and("devFeedRefId").is(devFeedRefId);
    return getSortedDevelopmentFeedbacks(criteria);
  }

  @Override
  public List<DevelopmentFeedback> findAllByDevFeedRefId(String devFeedRefId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_DEV_FEED_REF_ID).is(devFeedRefId)),
        DevelopmentFeedback.class);
  }
  
  /**
   * @see com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository#findDevFeedbackbyTokenId(java.lang.String)
   */
  @Override
  public DevelopmentFeedback findDevFeedbackbyTokenId(String tokenId) {
    return mongoTemplate.findOne(Query.query(Criteria.where(Constants.ENTITY_TOKENID).is(tokenId)),
        DevelopmentFeedback.class);
  }
  
  /**
   * @see com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository#findAllByDevFeedRefId(java.
   *      lang.String, java.lang.String)
   */
  @Override
  public void deleteAllByDevFeedRefId(String devFeedRefId, String companyId, String feature) {
    Criteria criteria = Criteria.where(Constants.ENTITY_COMPANY_ID).is(companyId).and("spFeature")
        .is(feature).and("devFeedRefId").is(devFeedRefId);
    mongoTemplate.remove(Query.query(criteria), DevelopmentFeedback.class);
  }
  
  /**
   * @see com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository#getAllFeedbackUserRequest(java.lang.String,
   *      com.sp.web.model.RequestStatus)
   */
  @Override
  public List<DevelopmentFeedback> getAllFeedbackUserRequest(String email,
      RequestStatus requestStatus) {
    return mongoTemplate.find(
        query(where("feedbackUserEmail").is(email).and("requestStatus").is(requestStatus)),
        DevelopmentFeedback.class);
  }

  @Override
  public List<DevelopmentFeedback> findByUserAndFeedRefId(User member, String devFeedRefId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_USER_ID).is(member.getId())
            .and(Constants.ENTITY_DEV_FEED_REF_ID).is(devFeedRefId)), DevelopmentFeedback.class);
  }

  @Override
  public List<DevelopmentFeedback> findByUserAndFeedParentRefId(User member, String feedParentRefId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_USER_ID).is(member.getId())
            .and(Constants.ENTITY_DEV_FEED_PARENT_REF_ID).is(feedParentRefId)),
        DevelopmentFeedback.class);
  }

  @Override
  public List<DevelopmentFeedback> findAllByDevFeedRefIdUserId(String devFeedRefId, String userId,
      String feature) {
    Criteria criteria = Criteria.where(Constants.ENTITY_USER_ID).is(userId).and("spFeature")
        .is(feature).and("devFeedRefId").is(devFeedRefId);
    return getSortedDevelopmentFeedbacks(criteria);
  }

  @Override
  public UserDevelopmentFeedbackResponse getDevelopmentFeedbackResponse(String userId) {
    UserDevelopmentFeedbackResponse findOne = mongoTemplate.findOne(
        query(where(Constants.ENTITY_USER_ID).is(userId)), UserDevelopmentFeedbackResponse.class);
    if (findOne == null) {
      findOne = new UserDevelopmentFeedbackResponse();
      findOne.setUserId(userId);
      findOne.setKeyOrder(new ArrayList<String>());
      findOne.setResponseMap(new HashMap<String, List<DevelopmentFeedbackResponse>>());
      mongoTemplate.save(findOne);
    }
    return findOne;
  }

  @Override
  public void update(UserDevelopmentFeedbackResponse feedbackResponse) {
    mongoTemplate.save(feedbackResponse);
  }  
}
