package com.sp.web.repository.partneraccount;

import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * MongoPartnerAccountRepository class provides partner account functionality.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoPartnerAccountRepository extends GenericMongoRepositoryImpl<PartnerAccount>
    implements PartnerAccountRepository {
  
  /**
   * @see PartnerAccountRepository#findByPartnerId(String).
   * @param partnerId
   *          is the partner id.
   */
  @Override
  public PartnerAccount findByPartnerId(String partnerId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("partnerId").is(partnerId)),
        PartnerAccount.class);
  }
  
  /**
   * findByCompanyId return the partner account on the basis of company id.
   */
  @Override
  public PartnerAccount findByComanyId(String companyId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("companyId").is(companyId)),
        PartnerAccount.class);
  }
  
}
