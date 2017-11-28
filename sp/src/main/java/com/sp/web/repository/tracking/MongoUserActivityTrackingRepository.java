package com.sp.web.repository.tracking;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.model.usertracking.TopPracticeTracking;
import com.sp.web.model.usertracking.UserActivityTracking;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MongoUserActivityTrackingRepository extends
    GenericMongoRepositoryImpl<UserActivityTracking> implements UserActivityTrackingRepository {
  
  @Override
  public List<UserActivityTracking> findUserActivityTracking(String companyId, LocalDate startDate,
      LocalDate endDate) {
    
    Instant instantStart = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    Date start = Date.from(instantStart);
    Instant instantEnd = endDate.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant();
    Date end = Date.from(instantEnd);
    
    final MongoConverter converter = mongoTemplate.getConverter();
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(UserActivityTracking.class));
    final Query query = query(where(Constants.ENTITY_COMPANY_ID).is(companyId).and("date")
        .gt(start).lte(end));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_CREATED_ON, -1);
    List<DBObject> objectList = collection.find(query.getQueryObject()).sort(orderBy).toArray();
    return objectList.stream().map(o -> {
      return converter.read(UserActivityTracking.class, o);
    }).collect(Collectors.toList());
  }
  
  @Override
  public UserActivityTracking findUserActivityTrackingByIdDate(String userId, LocalDate date) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("userId").is(userId).and("date").is(date)),
        UserActivityTracking.class);
  }
  
  @Override
  public TopPracticeTracking findTopPracticeArea(String companyId, LocalDate date) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("companyId").is(companyId).and("date").is(date)),
        TopPracticeTracking.class);
  }
  
  @Override
  public void saveTopPracticeArea(TopPracticeTracking findTopPracticeArea) {
    mongoTemplate.save(findTopPracticeArea);
  }
  
  @Override
  public List<TopPracticeTracking> findTopPracticeAreaFromDate(String companyId, LocalDate startDate) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("date").gte(startDate)),
        TopPracticeTracking.class);
    
  }
  
  @Override
  public void deleteActivityTracking(String userId) {
    List<UserActivityTracking> find = mongoTemplate.find(
        Query.query(Criteria.where("userId").is(userId)), UserActivityTracking.class);
    find.stream().forEach(ua -> archiveRepository.archive(ua));
  }
  
}
