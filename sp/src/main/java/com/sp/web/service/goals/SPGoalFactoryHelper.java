package com.sp.web.service.goals;

import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.UserGoalProgressSummaryDto;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.service.pc.PublicChannelHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         This class holds some helper methods for sp goals factory.
 */
@Component
public class SPGoalFactoryHelper {
  @Autowired
  SPGoalFactory goalsFactory;
  
  private Comparator<UserGoalProgressSummaryDto> summaryComparator;
  
  @Autowired
  private PublicChannelHelper channelHelper;
  
  /**
   * Constructor.
   */
  public SPGoalFactoryHelper() {
    // comparator to sort the user goals
    summaryComparator = (obj1, obj2) -> obj1.getGoal().getName()
        .compareTo(obj2.getGoal().getName());
  }
  
  /**
   * Helper method to get the user goals.
   * 
   * @param user
   *          - user
   * @return the user goals
   */
  public UserGoalDao getUserGoal(User user) {
    
    return (user.getUserGoalId() != null) ? goalsFactory.getUserGoal(user.getUserGoalId(), user.getUserLocale()) : null;
  }
  
  /**
   * This request validates if the goals have been set or not.
   * 
   * @param user
   *          - user
   * @return user goals
   */
  public UserGoalDao getUserGoalValidated(User user) {
    return Optional.ofNullable(getUserGoal(user)).orElseThrow(
        () -> new InvalidRequestException("User goals not set."));
  }
  
  /**
   * <code>getGoal</code> will return the goals from the cache if found.
   * 
   * @param goalIds
   *          list of goals id.
   * @return list of SP goals.
   */
  public List<SPGoal> getGoal(List<String> goalIds) {
    return goalIds.stream().map(goalsFactory::getGoal).collect(Collectors.toList());
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id
   */
  public SPGoal getGoal(String goalId) {
    return goalsFactory.getGoal(goalId);
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id
   */
  public SPGoal getGoal(String goalId, String locale) {
    return goalsFactory.getGoal(goalId, locale);
  }
  
  /**
   * Order the list to growths and strength areas.
   * 
   * @param userGoalsSummaryList
   *          the summary list
   * @return the map of strengths and growth areas
   */
  public Map<GoalCategory, List<UserGoalProgressSummaryDto>> mapUserGoalProgressToCategories(
      List<UserGoalProgressSummaryDto> userGoalsSummaryList) {
    Map<GoalCategory, List<UserGoalProgressSummaryDto>> responseMap = userGoalsSummaryList.stream()
        .collect(Collectors.groupingBy(UserGoalProgressSummaryDto::getCategory));
    // sort the categories
    responseMap.values().forEach(summaryList -> {
      summaryList.sort(summaryComparator);
    });
    return responseMap;
  }
  
  /**
   * Add the goal for the user.
   * 
   * @param user
   *          - user
   * @return the updated user goal
   */
  public UserGoalDao addGoalsForUser(User user) {
    return goalsFactory.addGoalsForUser(user);
  }
  
  @Deprecated
  public void addGoalToUser(User user, String goalId) {
    goalsFactory.addGoalToUser(user, goalId);
  }
  
  public void removeGoalFromUser(User user, String goalId) {
    goalsFactory.removeGoalFromUser(user, goalId);
  }
  
  /**
   * Update the user goal selections.
   * 
   * @param user
   *          - user
   * @param goalIdList
   *          - goal id list
   */
  public void updateGoalSelection(User user, List<String> goalIdList) {
    Assert.notNull(user.getUserGoalId(), "User goals not set.");
    UserGoalDao userGoalDao = goalsFactory.getUserGoal(user.getUserGoalId(),user.getUserLocale());
    /* check fo for the new Goals */
    
    Map<String, UserGoalProgressDao> goalsProgressMap = userGoalDao.getGoalsProgressMap();
    /* Add the new goals into the new UGP */
    goalIdList.stream().filter(gId -> !goalsProgressMap.containsKey(gId))
        .forEach(gids -> goalsFactory.addGoalToUser(user, gids, true));
    
    goalsFactory.updateGoalSelection(user, goalIdList);
  }
  
  public void selectGoalForUser(User user, String goalId) {
    goalsFactory.selectGoalForUser(user, goalId);
  }
  
  public void addFeedbackUserGoals(FeedbackUser feedbackUser, User user) {
    goalsFactory.addFeedbackUserGoal(feedbackUser, user);
  }
  
  /**
   * Adds the given article to the user goals.
   * 
   * @param user
   *          - the logged in user
   * 
   * @param articleId
   *          - article id
   */
  public void addArticleToUserGoals(User user, String articleId) {
    goalsFactory.addArticleToUserGoals(user, articleId);
  }
  
  /**
   * Get the user goal dao.
   * 
   * @param user
   *          - user
   * @return user goal dao
   */
  public UserGoalDao getUserGoalDaoForId(User user) {
    return goalsFactory.getUserGoal(user.getUserGoalId(), user.getUserLocale());
  }
}
