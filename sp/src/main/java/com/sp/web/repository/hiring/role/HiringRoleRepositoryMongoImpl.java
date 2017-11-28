package com.sp.web.repository.hiring.role;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HiringRoleRepositoryMongoImpl extends GenericMongoRepositoryImpl<HiringRole> implements
    HiringRoleRepository {
  
  @Override
  public List<HiringRole> findByPortraitId(String portraitId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_PORTRAIT_ID).is(portraitId)),
        HiringRole.class);
  }
  
  @Override
  public List<HiringRole> findByPortraitId(String portraitId, String companyId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_PORTRAIT_ID).is(portraitId).andOperator(
            where(Constants.ENTITY_COMPANY_ID).is(companyId))), HiringRole.class);
  }
  
  @Override
  public HiringRole findByNameIgnoreCase(String companyId, String name) {
    /*
     * regex, this regex, fails for "AA, ,AAA so groups with all these names and search by a will
     * show group exist
     */
    List<HiringRole> hiringRoles = mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID)
        .is(companyId).andOperator(where(Constants.ENTITY_NAME).regex(name, "i"))),
        HiringRole.class);
    
    if (hiringRoles.isEmpty()) {
      return null;
    }
    
    /* check further for the regex */
    for (HiringRole role : hiringRoles) {
      if (role.getName().equalsIgnoreCase(name)) {
        return role;
      }
    }
    return null;
  }
}
