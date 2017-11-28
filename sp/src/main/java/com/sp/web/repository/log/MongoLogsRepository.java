package com.sp.web.repository.log;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.model.log.ActivityLogMessage;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.LogMessage;
import com.sp.web.model.log.NotificationLogMessage;
import com.sp.web.model.log.UserNotificationsSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation of the logs repository.
 */
@Repository
public class MongoLogsRepository implements LogsRepository {

  @Autowired
  MongoTemplate mongoTemplate;
  
  @Override
  public List<NotificationLogMessage> getNotificationLogs(String memberId, int limitCount) {
    final MongoConverter converter = mongoTemplate.getConverter();
    return getLogMessages(NotificationLogMessage.class, memberId, limitCount).stream().map(o -> {
        return converter.read(NotificationLogMessage.class, o);
      }).collect(Collectors.toList());
  }

  private List<DBObject> getLogMessages(Class<? extends LogMessage> collectionsClass,
      String memberId, int limitCount) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(collectionsClass));
    final Query query = query(where(Constants.ENTITY_MEMBER_ID).is(memberId));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_CREATED_ON, -1);
    List<DBObject> objectList = (limitCount != -1) ? collection.find(query.getQueryObject())
        .limit(limitCount).sort(orderBy).toArray() : collection.find(query.getQueryObject())
        .sort(orderBy).toArray();
    return objectList;
  }

  private List<DBObject> getCompanyLogMessages(Class<? extends LogMessage> collectionsClass,
      String companyId, int limitCount) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(collectionsClass));
    final Query query = query(where(Constants.ENTITY_COMPANY_ID).is(companyId));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_CREATED_ON, -1);
    List<DBObject> objectList = (limitCount != -1) ? collection.find(query.getQueryObject())
        .limit(limitCount).sort(orderBy).toArray() : collection.find(query.getQueryObject())
        .sort(orderBy).toArray();
    return objectList;
  }
  
  @Override
  public List<ActivityLogMessage> getActivityLogs(String memberId, int limitCount) {
    final MongoConverter converter = mongoTemplate.getConverter();
    return getLogMessages(ActivityLogMessage.class, memberId, limitCount).stream().map(o -> {
        return converter.read(ActivityLogMessage.class, o);
      }).collect(Collectors.toList());
  }

  /**
   * @see com.sp.web.repository.log.LogsRepository#removeActivityLogs(java.lang.String)
   */
  @Override
  public void removeActivityLogs(String memberId) {
    mongoTemplate.remove(Query.query(Criteria.where("memberId").is(memberId)),ActivityLogMessage.class);
  }

  /**
   * @see com.sp.web.repository.log.LogsRepository#removeNotificationLogs(java.lang.String)
   */
  @Override
  public void removeNotificationLogs(String memberId) {
    mongoTemplate.remove(Query.query(Criteria.where("memberId").is(memberId)),NotificationLogMessage.class);    
  }

  @Override
  public void removeNotificationLogs(String userId, LogActionType logActionType) {
    mongoTemplate.remove(
        query(where("memberId").is(userId).andOperator(where("logActionType").is(logActionType))),
        NotificationLogMessage.class);
  }
  
  @Override
  public List<ActivityLogMessage> getCompanyActivityLogs(String companyId,
      int limitCount) {
    final MongoConverter converter = mongoTemplate.getConverter();
    return getCompanyLogMessages(ActivityLogMessage.class, companyId, limitCount).stream().map(o -> {
        return converter.read(ActivityLogMessage.class, o);
      }).collect(Collectors.toList());
  }

  @Override
  public UserNotificationsSummary getUserNotificationsSummary(String userId) {
    UserNotificationsSummary userNotificationSummary = mongoTemplate.findOne(
        query(where(Constants.ENTITY_USER_ID).is(userId)), UserNotificationsSummary.class);
    if (userNotificationSummary == null) {
      userNotificationSummary = new UserNotificationsSummary(userId);
      mongoTemplate.save(userNotificationSummary);
    }
    return userNotificationSummary;
  }

  @Override
  public void save(UserNotificationsSummary userNotificationsSummary) {
    mongoTemplate.save(userNotificationsSummary);
  }

  @Override
  public void save(NotificationLogMessage notificationLog) {
    mongoTemplate.save(notificationLog);
  }
  
  @Override
  public NotificationLogMessage notificationsLogFindById(String notificationId) {
    return mongoTemplate.findById(notificationId, NotificationLogMessage.class);
  }
}
