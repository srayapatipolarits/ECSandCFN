package com.sp.web.repository.badge;

import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * <code>BadgeRepositoRY</code> interface provides method to fetch user badges from the database.
 * 
 * @author pradeepruhil
 *
 */
public interface BadgeRepository extends GenericMongoRepository<UserBadgeActivity> {
  
  /**
   * UserBadgeActivity method will return the user badge for the passed userId.
   * 
   * @param userId
   *          of the user.
   * @return the UserBadgeActivity
   */
  public UserBadgeActivity getUserBadges(String userId);
  
  /**
   * getUserBadgesByCompanyId will return all the badges associated with the user.
   * 
   * @param companyId
   *          for which user badges are to be returned.
   * @return the user badge activity.
   */
  public List<UserBadgeActivity> getUserBadgesByCompany(String companyId);
  
}
