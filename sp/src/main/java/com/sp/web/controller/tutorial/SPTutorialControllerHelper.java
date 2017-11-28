package com.sp.web.controller.tutorial;

import com.sp.web.Constants;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.dto.tutorial.SPTutorialBaseUserActivityDTO;
import com.sp.web.dto.tutorial.SPTutorialUserActivityDTO;
import com.sp.web.model.User;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.tutorial.TutorialActivityData;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.tutorial.SPTutorialFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * @author Dax Abraham
 * 
 *         The controller helper for SP Tutorial controller.
 */
@Component
public class SPTutorialControllerHelper {
  
  @Autowired
  private SPTutorialFactory tutorialFactory;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  /**
   * The helper method to get the users tutorial information.
   * 
   * @param user
   *          - user
   * @return the response to the get request
   */
  public SPResponse get(User user) {
    final SPResponse resp = new SPResponse();
    UserTutorialActivity userActivity = tutorialFactory.getUserTutorialActivity(user);
    TutorialActivityData selectedActivity = userActivity.getSelectedActivity();
    if (selectedActivity != null) {
      SPTutorialDao tutorialDao = tutorialFactory.getTutorail(selectedActivity.getTutorialId(),
          user.getUserLocale());
      resp.add(Constants.PARAM_TUTORIAL, new SPTutorialBaseUserActivityDTO(tutorialDao,
          selectedActivity));
    }
    return resp.isSuccess();
  }
  
  /**
   * Controller helper method to get the details for the given tutorial.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the get details request
   */
  public SPResponse getDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String tutorialId = (String) params[0];
    Assert.hasText(tutorialId, "Tutorial id is required.");
    
    UserTutorialActivity userActivity = tutorialFactory.getUserTutorialActivity(user);
    TutorialActivityData userActivityData = userActivity.getUserActivityData(tutorialId);
    Assert.notNull(userActivityData, "User not assigned tutorial.");
    SPTutorialDao tutorialDao = tutorialFactory.getTutorail(userActivityData.getTutorialId(),
        user.getUserLocale());
    resp.add(Constants.PARAM_TUTORIAL, new SPTutorialUserActivityDTO(tutorialDao,
        userActivityData));
    return resp;
  }

  /**
   * Controller helper method to mark the action in the tutorial step as completed.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the mark complete request
   */
  public SPResponse markComplete(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String tutorialId = (String) params[0];
    Assert.hasText(tutorialId, "Tutorial id is required.");

    final String stepId = (String) params[1];
    final String actionId = (String) params[2];
    
    UserTutorialActivity userActivity = tutorialFactory.getUserTutorialActivity(user);
    TutorialActivityData userActivityData = userActivity.getUserActivityData(tutorialId);
    Assert.notNull(userActivityData, "User not assigned tutorial.");
    
    if (!StringUtils.isBlank(stepId) && !StringUtils.isBlank(actionId)) {
      if (userActivityData.markComplete(stepId, actionId)) {
        tutorialFactory.save(userActivity);
        badgeFactory.updateBadgeProgress(user, tutorialId, BadgeType.Tutorial);
      }
    }
    return resp.isSuccess();
  }
  
}
