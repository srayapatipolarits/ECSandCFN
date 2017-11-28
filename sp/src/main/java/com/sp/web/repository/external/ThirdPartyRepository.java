package com.sp.web.repository.external;

import com.sp.web.model.ThirdPartyUser;
import com.sp.web.repository.generic.GenericMongoRepository;

public interface ThirdPartyRepository extends GenericMongoRepository<ThirdPartyUser> {
  
  /**
   * Remove the user by the id.
   * 
   * @param spUserId
   *          is the surepeopel user id of the user.
   */
  void removeByUserlId(String spUserId);
  
  /**
   * findByExtenralId method will find the third party user from the extenral uid .
   * 
   * @param uid
   *          external uid.
   * @return the third party user.
   */
  ThirdPartyUser findByExternalUid(String uid);
  
  /**
   * findBySpUserId method will find the third party user from the extenral uid .
   * 
   * @param spUserId
   *          is the surePeople userid of the user
   * @return the third party user.
   */
  ThirdPartyUser findBySpUserId(String spUserId);
  
}
