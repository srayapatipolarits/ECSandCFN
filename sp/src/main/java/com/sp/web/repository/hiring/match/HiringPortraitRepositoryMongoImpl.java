package com.sp.web.repository.hiring.match;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The mongo implementation of the hiring portrait match repository.
 */
@Repository
public class HiringPortraitRepositoryMongoImpl extends GenericMongoRepositoryImpl<HiringPortrait>
    implements HiringPortraitRepository {
  
  @Override
  public HiringPortrait findByNameIgnoreCase(String name) {
    /*
     * regex, this regex, fails for "AA, ,AAA so groups with all these names and search by a will
     * show group exist
     */
    List<HiringPortrait> hiringPortraits = mongoTemplate.find(query(where(Constants.ENTITY_NAME)
        .regex(name, "i")), HiringPortrait.class);
    
    if (hiringPortraits.isEmpty()) {
      return null;
    }
    
    /* check further for the regex */
    for (HiringPortrait portrait : hiringPortraits) {
      if (portrait.getName().equalsIgnoreCase(name)) {
        return portrait;
      }
    }
    return null;
  }
  
  @Override
  public List<HiringPortrait> findAllByCompanyId(String companyId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_IDS).in(companyId)),
        HiringPortrait.class);
  }
  
}
