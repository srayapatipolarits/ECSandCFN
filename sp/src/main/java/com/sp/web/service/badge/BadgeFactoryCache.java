package com.sp.web.service.badge;

import com.sp.web.model.User;
import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.repository.badge.BadgeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * BadgeFactoryCache is the factory cache for badges.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class BadgeFactoryCache {
  
  @Autowired
  private BadgeRepository badgeRepository;
  
  @Cacheable(value = "badge", key = "#userId")
  public UserBadgeActivity getUserBadge(String userId ) {
    return badgeRepository.getUserBadges(userId);
  }
  
  /**
   * <code>updateUserBadgeActivity</code> method will update the user badge activity in the
   * repository and cache.
   * 
   * @param userBadgeActivity
   *          userBadgeActivity to be updated.
   * @param user
   *          is the logged in user.
   */
  @CacheEvict(value = "badge", key = "#userBadgeActivity.userId")
  public void updateUserBadgeActivity(UserBadgeActivity userBadgeActivity) {
    badgeRepository.save(userBadgeActivity);
  }
  
  public UserBadgeActivity getUserBadgeNoCache(User user) {
    return badgeRepository.getUserBadges(user.getId());
  }
  
  public void updateUserBadgeActivityNoCache(UserBadgeActivity userBadgeActivity) {
    badgeRepository.save(userBadgeActivity);
  }

  public void resetUserBadge(UserBadgeActivity userBadge) {
    badgeRepository.delete(userBadge);
    
  }
  
}
