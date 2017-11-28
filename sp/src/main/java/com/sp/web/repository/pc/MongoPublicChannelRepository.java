package com.sp.web.repository.pc;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoPublicChannelRepository is the repository implementation for the PublicChannel from mongo.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoPublicChannelRepository extends GenericMongoRepositoryImpl<PublicChannel>
    implements PublicChannelRepository {
  
  /**
   * @see com.sp.web.repository.pc.PublicChannelRepository#findByPcRefId(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public PublicChannel findByPcRefId(String pcfRefId, String companyId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where(Constants.ENTITY_PC_REF_ID).is(pcfRefId)
            .and(Constants.ENTITY_COMPANY_ID).is(companyId)), PublicChannel.class);
  }

  @Override
  public List<PublicChannel> findByPcRefId(String pcfRefId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_PC_REF_ID).is(pcfRefId)),
        PublicChannel.class);
  }
  
  /**
   * @see com.sp.web.repository.pc.PublicChannelRepository#findByParentRefId(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public List<PublicChannel> findByParentRefId(String parentRefId, String companyId) {
    return mongoTemplate.find(
        Query.query(Criteria.where(Constants.ENTITY_PARENT_REF_ID).is(parentRefId)
            .and(Constants.ENTITY_COMPANY_ID).is(companyId)), PublicChannel.class);
  }

  @Override
  public List<PublicChannel> findByParentRefId(String parentRefId) {
    return mongoTemplate.find(
        Query.query(Criteria.where(Constants.ENTITY_PARENT_REF_ID).is(parentRefId)),
        PublicChannel.class);
  }
  
  /**
   * @see com.sp.web.repository.pc.PublicChannelRepository#findAllByCompanyId(java.lang.String)
   */
  @Override
  public List<PublicChannel> findAllByCompanyId(String companyId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)), PublicChannel.class);
  }
}
