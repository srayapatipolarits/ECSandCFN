package com.sp.web.repository.feed;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation for the dashboard message repository interface.
 */
@Repository
public class MongoDashboardMessageRepository extends GenericMongoRepositoryImpl<DashboardMessage>
    implements DashboardMessageRepository {

  @Override
  public DashboardMessage findBySrcId(String srcId, String companyId) {
    return mongoTemplate.findOne(
        query(where(Constants.ENTITY_SRC_ID).is(srcId).and(Constants.ENTITY_COMPANY_ID)
            .is(companyId)), DashboardMessage.class);
  }

  @Override
  public List<DashboardMessage> findByOwnerId(String ownerId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_OWNER_ID).is(ownerId)),
        DashboardMessage.class);
  }
  
}
