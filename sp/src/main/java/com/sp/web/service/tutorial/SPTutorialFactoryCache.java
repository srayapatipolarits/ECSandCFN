package com.sp.web.service.tutorial;

import com.sp.web.Constants;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.model.tutorial.TutorialActivityData;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.repository.tutorial.SPTutorialRepository;
import com.sp.web.repository.tutorial.UserTutorialActivityRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.translation.TranslationFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The cache class for the tutorial factory.
 */
@Component
public class SPTutorialFactoryCache {
  
  @Autowired
  private SPTutorialRepository tutorialRepository;
  
  @Autowired
  private UserTutorialActivityRepository userActivityRepository;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private TranslationFactory tranlsationFactory;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  public List<SPTutorial> getAll() {
    return tutorialRepository.findAll();
  }
  
  /**
   * Get the tutorial for the given tutorial id.
   * 
   * @param id
   *          - tutorial id
   * @param locale
   *          - locale
   * @return the tutorial
   */
  @Cacheable(value = "sPTutorial", key = "#id+#locale")
  public SPTutorialDao get(String id, String locale) {
    SPTutorial findById = null;
    if (locale.equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
      findById = tutorialRepository.findById(id);
    } else {
      findById = tranlsationFactory.getTranslation(id, locale, SPTutorial.class,
          tutorialRepository::findById);
    }
    Assert.notNull(findById, "Tutorial not found.");
    return new SPTutorialDao(findById, locale, goalsFactory);
  }
  
  /**
   * Create a new tutorial.
   * 
   * @param tutorial
   *          - tutorial
   * @param locale
   *          - locale
   * @return the newly created tutorial instance
   */
  @CacheEvict(value = "sPTutorial", key = "#tutorial.id+#locale")
  public SPTutorialDao createUpdate(SPTutorialDao tutorial, String locale) {
    List<String> stepIds = tutorial.getStepIds();
    if (stepIds == null) {
      stepIds = new ArrayList<String>();
      tutorial.setStepIds(stepIds);
    }
    int actionCount = 0;
    String stepId = null;
    final Map<String, Set<String>> stepActionIds = new HashMap<String, Set<String>>();
    for (SPGoal step : tutorial.getSteps()) {
      step.updateUids(tutorial::getNextUID);
      step.updateActionIds();
      stepId = step.getId();
      goalsFactory.updateGoal(step);
      if (stepId == null) {
        stepIds.add(step.getId());
      }
      actionCount += step.getActionCount();
      stepActionIds.put(step.getId(), step.getActionIds());
    }
    tutorial.setActionCount(actionCount);
    tutorialRepository.save(tutorial);
    List<UserTutorialActivity> userActivityList = userActivityRepository.findAll();
    userActivityList.forEach(ua -> updateUserActivityList(ua, stepActionIds, tutorial));
    return tutorial;
  }
  
  /**
   * Update the user activity data with the new information.
   * 
   * @param userTutorialActivity
   *          - user tutorial activity to update
   * @param stepActionIds
   *          - updated step action ids
   * @param tutorial
   *          .get - tutorial id
   */
  private void updateUserActivityList(UserTutorialActivity userTutorialActivity,
      Map<String, Set<String>> stepActionIds, SPTutorialDao tutorial) {
    TutorialActivityData userActivityData = userTutorialActivity.getUserActivityData(tutorial
        .getId());
    if (userActivityData != null) {
      final Map<String, Set<String>> completionMap = userActivityData.getCompletionMap();
      ArrayList<String> idsToRemove = new ArrayList<String>();
      boolean doUpdate = false;
      for (String key : new ArrayList<>(completionMap.keySet())) {
        Set<String> actionIds = stepActionIds.get(key);
        Set<String> completedActionIds = completionMap.get(key);
        if (actionIds != null) {
          completedActionIds.stream().filter(id -> !actionIds.contains(id))
              .forEach(idsToRemove::add);
          if (!idsToRemove.isEmpty()) {
            completedActionIds.removeAll(idsToRemove);
            userActivityData.reduceCount(idsToRemove.size());
            doUpdate = true;
            idsToRemove.clear();
          }
        } else {
          completionMap.remove(key);
          userActivityData.reduceCount(completedActionIds.size());
          doUpdate = true;
        }
      }
      if (doUpdate) {
        userActivityRepository.save(userTutorialActivity);
        doUpdate = false;
      }
      badgeFactory.updateBadgeProgress(userTutorialActivity.getUserId(), tutorial.getId(),
          BadgeType.Tutorial, tutorial);
    }
  }
  
  /**
   * Delete the given tutorial.
   * 
   * @param id
   *          - id
   * @param locale
   *          - locale
   */
  @CacheEvict(value = "sPTutorial", key = "#id+#locale")
  public void delete(String id, String locale) {
    SPTutorial findById = validateGet(id);
    if (findById != null) {
      tutorialRepository.delete(findById);
    }
  }
  
  private SPTutorial validateGet(String id) {
    SPTutorial findById = tutorialRepository.findById(id);
    Assert.notNull(findById, "Tutorial not found.");
    return findById;
  }
  
  /**
   * Get the user user tutorial activity for the given user.
   * 
   * @param userId
   *          - user id
   * @return the user tutorial activity
   */
  public UserTutorialActivity getUserActivity(String userId) {
    UserTutorialActivity userTutorialActivity = userActivityRepository.findByUserId(userId);
    if (userTutorialActivity == null) {
      userTutorialActivity = new UserTutorialActivity();
      userTutorialActivity.setUserId(userId);
      userActivityRepository.save(userTutorialActivity);
    }
    return userTutorialActivity;
  }
  
  /**
   * Update the user activity record.
   * 
   * @param userTutorialActivity
   *          - user tutorial activity
   */
  public void save(UserTutorialActivity userTutorialActivity) {
    userActivityRepository.save(userTutorialActivity);
  }
  
  /**
   * Remove the given steps.
   * 
   * @param stepIdsToRemove
   *          - step ids to remove
   */
  public void removeSteps(List<String> stepIdsToRemove) {
    for (String stepId : stepIdsToRemove) {
      SPGoal step = goalsFactory.getGoal(stepId, Constants.DEFAULT_LOCALE);
      if (step != null) {
        goalsFactory.removeGoal(step);
      }
    }
  }
}
