package com.sp.web.repository.partneraccount;

import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.repository.generic.GenericMongoRepository;

/**
 * PartnerAccountRepository hold the all the database interaction with regards to Partner accounts.
 * 
 * @author pradeepruhil
 *
 */
public interface PartnerAccountRepository extends GenericMongoRepository<PartnerAccount> {
  
  /**
   * findByPartnerId method will return the Partner account on partner id.
   * 
   * @param partnerId
   *          partner id for which partner account is to be retrieved.
   * @return the partner account.
   */
  PartnerAccount findByPartnerId(String partnerId);
  
  /**
   * findByCompanyId method will return the PartnerAccount on the companyid passed.
   * 
   * @param companyId
   *          is the company id.
   * @return the partner account.
   */
  PartnerAccount findByComanyId(String companyId);
  
}
