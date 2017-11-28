package com.sp.web.repository.tracking;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the activity tracking repository interface.
 */
@Repository
public class MongoActivityTrackingRepository extends GenericMongoRepositoryImpl<ActivityTracking>
    implements ActivityTrackingRepository {

  @Override
  public List<ActivityTracking> findFeedAfter(String companyId, LocalDateTime dateToCheck) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).and(Constants.ENTITY_CREATED_ON)
            .gte(dateToCheck).and(Constants.ENTITY_POSTED_TO_DASHBOARD).is(false)),
        ActivityTracking.class);
  }
  
}
