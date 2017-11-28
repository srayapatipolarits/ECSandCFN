package com.sp.web.repository.hiring.group;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The repository class for hiring group.
 */
@Repository
public class HiringGroupRepositoryMongoImpl extends GenericMongoRepositoryImpl<HiringGroup>
    implements HiringGroupRepository {
  
  @Override
  public List<HiringGroup> findGroupsForUser(String companyId, String userId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_USER_IDS).in(userId))), HiringGroup.class);
  }
  
  @Override
  public HiringGroup findByNameIgnoreCase(String companyId, String name) {
    /*
     * regex, this regex, fails for "AA, ,AAA so groups with all these names and search by a will
     * show group exist
     */
    List<HiringGroup> hiringGroups = mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID)
        .is(companyId).andOperator(where(Constants.ENTITY_NAME).regex(name, "i"))),
        HiringGroup.class);
    
    if (hiringGroups.isEmpty()) {
      return null;
    }
    
    /* check further for the regex */
    for (HiringGroup group : hiringGroups) {
      if (group.getName().equalsIgnoreCase(name)) {
        return group;
      }
    }
    return null;
  }
}
