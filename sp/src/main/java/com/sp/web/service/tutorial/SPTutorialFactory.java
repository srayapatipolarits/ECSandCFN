package com.sp.web.service.tutorial;

import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.model.User;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.service.badge.BadgeFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 * 
 *         The tutorial factory class.
 */
@Component
public class SPTutorialFactory {

  @Autowired
  private SPTutorialFactoryCache factoryCache;
  private String defaultTutorialId;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  @Inject
  public SPTutorialFactory(Environment environment) {
    defaultTutorialId = environment.getProperty("sp.tutorial.default");
  }


  /**
   * Get the user tutorial activity record.
   * 
   * @param user
   *          - user
   * @return
   *    the user tutorial activity
   */
  public UserTutorialActivity getUserTutorialActivity(User user) {
    String userId = user.getId();
    UserTutorialActivity userTutorialActivity = factoryCache.getUserActivity(userId);
    if (userTutorialActivity == null) {
      userTutorialActivity = new UserTutorialActivity();
      userTutorialActivity.setUserId(userId);
      if (!StringUtils.isBlank(defaultTutorialId)) {
        userTutorialActivity.addTutorial(defaultTutorialId);
        badgeFactory.addToBadgeProgress(user, defaultTutorialId, BadgeType.Tutorial);
      }
      factoryCache.save(userTutorialActivity);
    }
    return userTutorialActivity; 
  }

  /**
   * Get the tutorial.
   * 
   * @param tutorialId
   *            - tutorial id
   * @param locale
   *            - locale
   * @return
   *    the tutorial
   */
  public SPTutorialDao getTutorail(String tutorialId, String locale) {
    return factoryCache.get(tutorialId, locale);
  }

  /**
   * Update the user activity in the database.
   * 
   * @param userActivity
   *          - user activity
   */
  public void save(UserTutorialActivity userActivity) {
    factoryCache.save(userActivity);
  }
  
}
