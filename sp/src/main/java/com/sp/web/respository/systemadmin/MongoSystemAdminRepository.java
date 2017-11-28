/**
 * 
 */
package com.sp.web.respository.systemadmin;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.audit.AuditLogBean;
import com.sp.web.model.library.TipOfTheDay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pradeepruhil
 *
 */
@Repository
public class MongoSystemAdminRepository implements SystemAdminRepository {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  private Class<? extends User> getCollectionType(UserType userType) {
    
    switch (userType) {
    case Feedback:
      return FeedbackUser.class;
    case HiringCandidate:
      return HiringUser.class;
      
    case Member:
      return User.class;
    default:
      return User.class;
    }
  }
  
  /**
   * 
   * @see com.sp.web.respository.systemadmin.SystemAdminRepository#updateUserProperty(com.sp.web.model.UserType,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void updateUserProperty(UserType userType, Map<String, String> map, String id) {
    Class user = getCollectionType(userType);
    Update updateObj = new Update();
    map.entrySet().stream().forEach(enset -> {
      updateObj.set(enset.getKey(), enset.getValue());
    });
    
    mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), updateObj, user);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.respository.systemadmin.SystemAdminRepository#updateUserPersonality(com.sp.web.model
   * .UserType, com.sp.web.assessment.personality.PersonalityType, java.lang.String)
   */
  @Override
  public void updateUserPersonality(UserType userType, PersonalityType personalityType, String id) {
    
    Class<User> user = (Class<User>) getCollectionType(userType);
    User findById = mongoTemplate.findById(id, user);
    HashMap<RangeType, PersonalityBeanResponse> personality = findById.getAnalysis()
        .getPersonality();
    PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
    personalityBeanResponse.setPersonalityType(personalityType);
    mongoTemplate.save(findById);
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.respository.systemadmin.SystemAdminRepository#getUserFromType(java.util.Map,
   * com.sp.web.model.UserType)
   */
  public User getUserFromType(Map<String, String> feedbackUser, UserType type) {
    
    Class<FeedbackUser> user = (Class<FeedbackUser>) getCollectionType(type);
    Query query = new Query();
    Set<String> keySet = feedbackUser.keySet();
    for (String key : keySet) {
      
      query.addCriteria(Criteria.where(key).is(feedbackUser.get(key)));
    }
    
    FeedbackUser find = mongoTemplate.findOne(query, user);
    return find;
  }
  
  /**
   * @see com.sp.web.respository.systemadmin.SystemAdminRepository#addAuditLogs(com.sp.web.model.audit.AuditLogBean)
   */
  @Override
  public void addAuditLogs(AuditLogBean auditLog) {
    mongoTemplate.save(auditLog);
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.respository.systemadmin.SystemAdminRepository#findAuditLogs(java.time.LocalDateTime)
   */
  @Override
  public List<AuditLogBean> findAuditLogs() {
    
    return mongoTemplate.findAll(AuditLogBean.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.respository.systemadmin.SystemAdminRepository#removeAuditLogs(java.util.List)
   */
  @Override
  public void removeAuditLogs(List<AuditLogBean> auditLogs) {
    if (!CollectionUtils.isEmpty(auditLogs)) {
      auditLogs.stream().forEach(al -> {
        mongoTemplate.remove(al);
      });
    }
    
  }

  @Override
  public List<TipOfTheDay> getAllTipsOfTheDay() {
    return mongoTemplate.findAll(TipOfTheDay.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.respository.systemadmin.SystemAdminRepository#findAuditLogs(java.lang.String)
   */
  @Override
  public List<AuditLogBean> findAuditLogs(String email) {
    final MongoConverter converter = mongoTemplate.getConverter();
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(AuditLogBean.class));
    final Query query = query(where(Constants.ENTITY_EMAIL).is(email));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_CREATED_ON, -1);
    List<DBObject> objectList = collection.find(query.getQueryObject()).sort(orderBy).toArray();
    return objectList.stream().map(o -> {
      return converter.read(AuditLogBean.class, o);
    }).collect(Collectors.toList());
  }
  
  @Override
  public List<AuditLogBean> findAuditLogs(String email,LocalDate startDate, LocalDate endDate) {
    Instant instantStart = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    Date start = Date.from(instantStart);
    Instant instantEnd = endDate.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant();
    Date end = Date.from(instantEnd);
    
    final MongoConverter converter = mongoTemplate.getConverter();
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(AuditLogBean.class));
    final Query query = query(where(Constants.ENTITY_EMAIL).is(email).and(Constants.ENTITY_CREATED_ON).gt(start).lt(end));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_CREATED_ON, -1);
    List<DBObject> objectList = collection.find(query.getQueryObject()).sort(orderBy).toArray();
    return objectList.stream().map(o -> {
      return converter.read(AuditLogBean.class, o);
    }).collect(Collectors.toList());
  }
  
  @Override
  public List<AuditLogBean> findAuditLogs(LocalDate startDate, LocalDate endDate) {
    Instant instantStart = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    Date start = Date.from(instantStart);
    Instant instantEnd = endDate.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant();
    Date end = Date.from(instantEnd);
    final MongoConverter converter = mongoTemplate.getConverter();
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(AuditLogBean.class));
    final Query query = query(where(Constants.ENTITY_CREATED_ON).gte(start).lte(end));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_CREATED_ON, -1);
    List<DBObject> objectList = collection.find(query.getQueryObject()).sort(orderBy).toArray();
    return objectList.stream().map(o -> {
      return converter.read(AuditLogBean.class, o);
    }).collect(Collectors.toList());
  }
}