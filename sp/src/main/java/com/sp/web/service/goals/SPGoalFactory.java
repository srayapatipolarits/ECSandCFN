package com.sp.web.service.goals;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dto.UserGoalProgressSummaryDto;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.ArticleTrackingBean;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalSource;
import com.sp.web.model.goal.GoalSourceType;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserArticleProgress;
import com.sp.web.model.goal.UserGoal;
import com.sp.web.model.goal.UserGoalProgress;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.goal.UserGoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.translation.TranslationFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * <code>SPGoalFactory</code> method will return the SPGoals returned the sp goals associated with a
 * personality type.
 * 
 * @author pradeep
 */
@Component
public class SPGoalFactory {
  
  private static final Logger log = Logger.getLogger(SPGoalFactory.class);
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private TrainingLibraryArticleRepository libraryArticleRepository;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private UserGoalsRepository userGoalsRepository;
  
  @Autowired
  private SPGoalFactoryCache factoryCache;
  
  @Autowired
  TrainingLibraryArticleRepository articleRepository;
  
  @Autowired
  TrackingRepository trackingRepository;
  
  @Autowired
  private GoalsAlgorithm algorithm;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  @Autowired
  private TranslationFactory translationFactory;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  /**
   * Constructor.
   */
  public SPGoalFactory() {
  }
  
  /**
   * Get the reference to the user goals.
   * 
   * @param userGoalId
   *          - goal id
   * @param locale
   * @return UserGoalDao.
   */
  @Cacheable(value = "userGoals", key = "#userGoalId+#locale")
  public UserGoalDao getUserGoal(String userGoalId, String locale) {
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    return new UserGoalDao(userGoal, articlesFactory, this, algorithm, userGoalsRepository, locale);
  }
  
  /**
   * <code>getOwnUserGoals</code> method will fetch the userOwn goals.
   * 
   * @param user
   *          logged in user
   * @return the user goal
   */
  public UserGoalDao addGoalsForUser(User user) {
    String userGoalId = user.getUserGoalId();
    if (userGoalId == null) {
      boolean isFeedbackUser = user instanceof FeedbackUser ? Boolean.TRUE : Boolean.FALSE;
      List<UserGoalProgress> userGoalsProgress = getUserGoalsProgress(user, isFeedbackUser);
      
      List<UserGoalProgress> sortedUgp = algorithm.sortedUserGoalProgressList(userGoalsProgress);
      
      /* Stop assigning goals to user */
      /* assign the order fo the first six */
      /*
       * int order = 0; for (UserGoalProgress userGoalProgress : sortedUgp) { if (order < 6) {
       * userGoalProgress.setOrderIndex(order + 1); userGoalProgress.setSelected(true);
       * userGoalProgress.setMandatory(true); } else { break; } order = order + 1; }
       */
      UserGoal userGoal = new UserGoal(sortedUgp);
      userGoalsRepository.save(userGoal);
      userGoalId = userGoal.getId();
      user.setUserGoalId(userGoalId);
      userRepository.updateGenericUser(user);
    }
    return getUserGoal(userGoalId, user.getUserLocale());
  }
  
  /**
   * <code>addFeedbackUserGoal</code> will add the feedback user goal to the user.
   * 
   * @param user
   *          - the user
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void addFeedbackUserGoal(FeedbackUser feedbackUser, User user) {
    
    String userGoalId = user.getUserGoalId();
    Assert.notNull(userGoalId, "User goals not set.");
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    
    UserGoal feedbackUserGoal = userGoalsRepository.findById(feedbackUser.getUserGoalId());
    List<UserGoalProgress> feedbackUserGoalProgresses = feedbackUserGoal.getGoalProgress();
    
    /*
     * creating a goal source which will be helpfull in during the weighing process.
     */
    GoalSource goalSource = new GoalSource();
    goalSource.setGoalSourceType(GoalSourceType.PrismLens);
    goalSource.setValue(feedbackUser.getFirstName() + " " + feedbackUser.getLastName());
    
    feedbackUserGoalProgresses.stream().forEach(
        feedbackUsGp -> {
          String goalId = feedbackUsGp.getGoalId();
          SPGoal validatedGoal = validateGoal(goalId);
          /** Get from the all goals and add the lens source to that list. */
          List<UserGoalProgress> goalProgressList = userGoal.getGoalProgress();
          boolean doAddArticles = false;
          Optional<UserGoalProgress> existingGoalsProgress = goalProgressList.stream()
              .filter(ugp -> ugp.getGoalId().equals(goalId)).findFirst();
          if (existingGoalsProgress.isPresent()) {
            
            UserGoalProgress existingUserGoalProgress = existingGoalsProgress.get();
            existingUserGoalProgress.getSourceList().add(goalSource);
            
            /* in case of feedback user goals add weight of only 1 */
            existingUserGoalProgress.addToAllWeight(1);
            existingUserGoalProgress.addToPrismLensWeight(1);
            if (existingUserGoalProgress.getDevelopmentStrategyLists().isEmpty()) {
              existingUserGoalProgress
                  .setDevelopmentStrategyLists(getDevelopmentStrategy(validatedGoal));
            }
            doAddArticles = true;
          } else {
            UserGoalProgress userGoalProgress = new UserGoalProgress(goalId, false, false);
            userGoalProgress.getSourceList().add(goalSource);
            userGoalProgress.addToAllWeight(1);
            userGoalProgress.addToPrismLensWeight(1);
            goalProgressList.add(userGoalProgress);
            userGoalProgress.setDevelopmentStrategyLists(getDevelopmentStrategy(validatedGoal));
            // goalProgressList.add(new UserGoalProgress(goalId, isMandatory, false));
            doAddArticles = true;
          }
          if (doAddArticles) {
            final List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
            validatedGoal.getMandatoryArticles().stream().map(UserArticleProgress::new)
                .filter(uap -> !articleProgress.contains(uap))
                .forEach(userGoal::addArticleProgress);
          }
          userGoalsRepository.save(userGoal);
          ;
          
        });
    // validating if the goal exists
    
  }
  
  /**
   * Add all the mandatory articles for the user.
   * 
   * @param user
   *          - logged in user
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void enableMandatoryGoals(User user) {
    // get the user goal id
    String userGoalId = user.getUserGoalId();
    if (userGoalId == null) {
      addGoalsForUser(user);
      userGoalId = user.getUserGoalId();
    }
    
    // adding the goals from company for the user
    
    if (!(user instanceof FeedbackUser)) {
      List<SPGoal> goalsFromCategory = null;
      final String companyId = user.getCompanyId();
      if (companyId != null) {
        List<SPGoal> goalsForCategory = factoryCache.getGoalsForCategory(GoalCategory.Business);
        goalsFromCategory = goalsForCategory
            .stream()
            .filter(
                g -> g.getStatus() == GoalStatus.ACTIVE
                    && (g.isAllAccounts() || g.getAccounts().contains(companyId)))
            .collect(Collectors.toList());
      } else {
        goalsFromCategory = factoryCache.getGoalsForCategory(GoalCategory.Individual).stream()
            .filter(g -> g.getStatus() == GoalStatus.ACTIVE).collect(Collectors.toList());
      }
      // adding the goal to the user
      goalsFromCategory.stream().forEach(
          goal -> addGoalToUser(user, goal.getId(), goal.isMandatory()));
      
    }
    
    // get the user goal
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    
    // loop through all the goals and check if they are mandatory
    // add the same to the user goals as selected
    for (UserGoalProgress ugp : userGoal.getGoalProgress()) {
      if (ugp.isMandatory() && ugp.isSelected()) {
        selectGoalAndAddArticles(userGoal, ugp);
      }
    }
    // update the user goal in the database
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Adding a new goal to the user.
   * 
   * @param user
   *          - user
   * @param goalId
   *          - goal id
   */
  @Deprecated
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void addGoalToUser(User user, String goalId) {
    doAddGoalToUser(user, goalId, false);
  }
  
  /**
   * Adds the goal for the user.
   * 
   * @param user
   *          - user
   * @param goalId
   *          - goal id
   * @param isMandatory
   *          - is mandatory
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  @Deprecated
  public void addGoalToUser(User user, String goalId, boolean isMandatory) {
    doAddGoalToUser(user, goalId, isMandatory);
  }
  
  /**
   * Adds the goal for the user.
   * 
   * @param user
   *          - user
   * @param goalId
   *          - goal id
   * @param isMandatory
   *          - is mandatory
   */
  @Deprecated
  private void doAddGoalToUser(User user, String goalId, boolean isMandatory) {
    final String userGoalId = user.getUserGoalId();
    Assert.notNull(userGoalId, "User goals not set.");
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    // validating if the goal exists
    SPGoal validatedGoal = validateGoal(goalId);
    List<UserGoalProgress> goalProgressList = userGoal.getGoalProgress();
    // boolean doAddArticles = false;
    Optional<UserGoalProgress> existingGoalsProgress = goalProgressList.stream()
        .filter(ugp -> ugp.getGoalId().equals(goalId)).findFirst();
    if (existingGoalsProgress.isPresent()) {
      UserGoalProgress existingUserGoalProgress = existingGoalsProgress.get();
      if (!existingUserGoalProgress.isSelected()) {
        existingUserGoalProgress.setSelected(true);
        // doAddArticles = true;
      }
    } else {
      final UserGoalProgress userGoalProgress = new UserGoalProgress(goalId, isMandatory, true);
      // adding the development strategies to the goal progress
      
      userGoalProgress.setDevelopmentStrategyLists(getDevelopmentStrategy(validatedGoal));
      // goalProgressList.add(new UserGoalProgress(goalId, isMandatory, false));
      goalProgressList.add(userGoalProgress);
      // doAddArticles = true;
    }
    // Commeting below, as now all the articles will be part of user goal progress instead of
    // mandatory articles.
    // if (doAddArticles) {
    // final List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
    // validatedGoal.getMandatoryArticles().stream().map(UserArticleProgress::new)
    // .filter(uap -> !articleProgress.contains(uap)).forEach(userGoal::addArticleProgress);
    // }
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Convert the given string to integer.
   * 
   * @param id
   *          - id
   * @return the integer representation for id
   */
  private Integer getInt(String id) {
    return (id != null) ? Integer.valueOf(id) : null;
  }
  
  /**
   * Adds the given article to the user goals only when they are completed.
   * 
   * @param user
   *          - the logged in user.
   * @param articleId
   *          - article id
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void addArticleToUserGoals(User user, String articleId) {
    // validate the article
    Article article = articlesFactory.getArticle(articleId);
    if (article == null) {
      throw new InvalidRequestException("Article not found to add :" + articleId);
    }
    
    // get the user goals
    final String userGoalId = user.getUserGoalId();
    Assert.notNull(userGoalId, "User goals not set.");
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    
    // check if article is already present int the article progress list
    List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
    Optional<UserArticleProgress> findFirst = articleProgress.stream()
        .filter(ap -> ap.getArticleId().equals(articleId)).findFirst();
    
    // if article not present add it to the article progress
    if (!findFirst.isPresent()) {
      UserArticleProgress uap = new UserArticleProgress(articleId, false);
      uap.setArticleStatus(ArticleStatus.COMPLETED);
      
      articleProgress.add(uap);
      // update the user goals
      userGoalsRepository.save(userGoal);
    }
    
    /* Update the badge for the article */
    Set<String> goals = article.getGoals();
    if (!goals.isEmpty()) {
      String goalId = goals.stream().findFirst().get();
      badgeFactory.updateBadgeProgress(user, goalId, BadgeType.Erti);
    }
    
  }
  
  /**
   * Method to remove the goal from the user goal list.
   * 
   * @param user
   *          - user
   * @param goalId
   *          - goal id
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void removeGoalFromUser(User user, String goalId) {
    final String userGoalId = user.getUserGoalId();
    Assert.notNull(userGoalId, "User goals not set.");
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    // validating if the goal exists
    validateGoal(goalId);
    List<UserGoalProgress> goalProgressList = userGoal.getGoalProgress();
    Optional<UserGoalProgress> existingGoalsProgress = goalProgressList.stream()
        .filter(ugp -> ugp.getGoalId().equals(goalId)).findFirst();
    
    if (existingGoalsProgress.isPresent()) {
      UserGoalProgress userGoalProgress = existingGoalsProgress.get();
      goalProgressList.remove(userGoalProgress);
      SPGoal goal = getGoal(userGoalProgress.getGoalId());
      final List<String> mandatoryArticles = goal.getMandatoryArticles();
      userGoal.getArticleProgress().removeIf(
          ap -> mandatoryArticles.contains(ap.getArticleId())
              && ap.getArticleStatus() != ArticleStatus.COMPLETED);
      // if (userGoalProgress.isMandatory()) {
      // throw new InvalidRequestException("Cannot remove mandatory goal.");
      // } else {
      // }
      userGoalsRepository.save(userGoal);
    } else {
      throw new InvalidRequestException("Goal not found in user list.");
    }
  }
  
  /**
   * Update the the goals selections for the user.
   * 
   * @param user
   *          -user
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void updateGoalSelection(User user, List<String> goalIdList) {
    
    final String userGoalId = user.getUserGoalId();
    Assert.notNull(userGoalId, "User goals not set.");
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    // final List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
    // removing all the articles that have not been completed
    // articleProgress.removeIf(ap -> ap.getArticleStatus() != ArticleStatus.COMPLETED);
    for (UserGoalProgress ugp : userGoal.getGoalProgress()) {
      final String goalId = ugp.getGoalId();
      final int indexOf = goalIdList.indexOf(goalId);
      if (indexOf > -1) {
        ugp.setSelected(true);
        ugp.setOrderIndex(indexOf + 1);
        publicChannelHelper.addUser(user, goalId);
        badgeFactory.addToBadgeProgress(user, goalId, BadgeType.Erti, null);
      } else {
        ugp.setOrderIndex(0);
        ugp.setSelected(false);
        publicChannelHelper.removeUser(user, goalId);
        badgeFactory.deleteBadgeProgress(goalId, user);
      }
      
    }
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Method to remove the goal from the user goal list.
   * 
   * @param user
   *          - user
   * @param goalId
   *          - goal id
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void selectGoalForUser(User user, String goalId) {
    final String userGoalId = user.getUserGoalId();
    Assert.notNull(userGoalId, "User goals not set.");
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    // validating if the goal exists
    List<UserGoalProgress> goalProgressList = userGoal.getGoalProgress();
    Optional<UserGoalProgress> existingGoalsProgress = goalProgressList.stream()
        .filter(ugp -> ugp.getGoalId().equals(goalId)).findFirst();
    
    if (existingGoalsProgress.isPresent()) {
      selectGoalAndAddArticles(userGoal, existingGoalsProgress.get());
      userGoalsRepository.save(userGoal);
    } else {
      throw new InvalidRequestException("Goal not found in user list.");
    }
  }
  
  private void selectGoalAndAddArticles(UserGoal userGoal, UserGoalProgress userGoalProgress) {
    SPGoal validatedGoal = validateGoal(userGoalProgress.getGoalId());
    final List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
    userGoalProgress.setSelected(true);
    validatedGoal.getMandatoryArticles().stream().map(UserArticleProgress::new)
        .filter(uap -> !articleProgress.contains(uap)).forEach(userGoal::addArticleProgress);
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id else invalid request exception
   */
  public SPGoal getGoal(String goalId) {
    return factoryCache.getGoal(goalId, Constants.DEFAULT_LOCALE);
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id else invalid request exception
   */
  public SPGoal getGoal(String goalId, String locale) {
    return factoryCache.getGoal(goalId, locale);
  }
  
  /**
   * Gets the goal from the db.
   * 
   * @param goalId
   *          - goal id
   * @return the goal
   */
  public SPGoal getGoalFromDB(String goalId) {
    return goalsRepository.findById(goalId);
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id else invalid request exception
   */
  public SPGoal updateGoal(SPGoal spGoal) {
    return factoryCache.updateGoal(spGoal);
  }
  
  /**
   * Remove the given goal from the DB.
   * 
   * @param spGoal
   *          - goal to remove
   */
  public void removeGoal(SPGoal spGoal) {
    factoryCache.removeGoal(spGoal);
  }
  
  /**
   * Validate if the goal is present.
   * 
   * @param goalId
   *          - goal id
   */
  private SPGoal validateGoal(String goalId) {
    return Optional.ofNullable(getGoal(goalId)).orElseThrow(
        () -> new InvalidRequestException("Goal not found :" + goalId));
  }
  
  /**
   * <code>getuserGoals</code> method will retrieve the user goals and add them to the user.
   * 
   * @param user
   *          user goal
   * @return the spGoal
   */
  public List<UserGoalProgress> getUserGoalsProgress(User user, boolean isFeedbackUser) {
    
    if (user.getAnalysis() == null) {
      throw new InvalidRequestException("User analysis not found.");
    }
    
    // add the primary personality profile goals
    final List<UserGoalProgress> goals = new ArrayList<UserGoalProgress>();
    final HashMap<String, UserGoalProgress> goalProgressMap = new HashMap<String, UserGoalProgress>();
    
    PersonalityType primaryPersonalityType = user.getAnalysis().getPersonality()
        .get(RangeType.Primary).getPersonalityType();
    addUgpToGoals(user, isFeedbackUser, goals, goalProgressMap, primaryPersonalityType,
        GoalSourceType.PrismPrimary);
    
    // sill the underpressure goals for the feedback user and add the secondary personality profile
    // goals in case of primary user.
    if (!isFeedbackUser) {
      PersonalityType personalityType = user.getAnalysis().getPersonality()
          .get(RangeType.UnderPressure).getPersonalityType();
      addUgpToGoals(user, isFeedbackUser, goals, goalProgressMap, personalityType,
          GoalSourceType.PrismUnderPressure);
    }
    
    return goals;
  }
  
  /**
   * <code>addUGPTOGoal</code> method will add the ugp to the goals.
   * 
   * @param user
   *          to which ugp is to added for.
   * @param isFeedbackUser
   *          whether user is a feedback user of not.
   * @param goals
   *          user goal progress.
   * @param goalProgressMap
   *          goal progress map.
   * @param personalityType
   *          Personality Type.
   */
  private void addUgpToGoals(User user, boolean isFeedbackUser, final List<UserGoalProgress> goals,
      final HashMap<String, UserGoalProgress> goalProgressMap, PersonalityType personalityType,
      GoalSourceType goalSourceType) {
    if (log.isDebugEnabled()) {
      log.debug("Processing personality :" + personalityType);
    }
    List<SPGoal> practiceAreas = getGoals(factoryCache.getPersonalityPracticeArea(personalityType,
        user.getUserLocale()));
    /*
     * as the practice area are sorted against hightes to lowerst, to taking the size and will
     * assign that to the first goal and then decrease by 1.
     */
    
    int maxPressureWeight = practiceAreas.size();
    for (SPGoal g : practiceAreas) {
      if (!goalProgressMap.containsKey(g.getId())) {
        UserGoalProgress ugp = new UserGoalProgress(g.getId());
        goalProgressMap.put(g.getId(), ugp);
        GoalSource goalSource = new GoalSource();
        if (isFeedbackUser) {
          goalSource.setGoalSourceType(GoalSourceType.PrismLens);
          goalSource.setValue(user.getFirstName() + " " + user.getLastName());
        } else {
          goalSource.setGoalSourceType(GoalSourceType.Prism);
          goalSource.setValue(goalSourceType.toString());
          ugp.addToAllWeight(maxPressureWeight);
          ugp.addToPrismWeight(maxPressureWeight);
          
        }
        ugp.getSourceList().add(goalSource);
        ugp.setDevelopmentStrategyLists(getDevelopmentStrategy(g));
        goals.add(ugp);
      } else {
        
        /*
         * In case of under pressure add the goal source which will be helpfull in putting the
         * corresponding weighing list.
         */
        if (!isFeedbackUser) {
          UserGoalProgress userGoalProgress = goalProgressMap.get(g.getId());
          /* Add the source for under pressure as well */
          GoalSource goalSource = new GoalSource();
          goalSource.setGoalSourceType(GoalSourceType.Prism);
          goalSource.setValue(goalSourceType.toString());
          userGoalProgress.addToAllWeight(maxPressureWeight);
          userGoalProgress.getSourceList().add(goalSource);
          userGoalProgress.addToPrismWeight(maxPressureWeight);
        }
        
      }
      maxPressureWeight = maxPressureWeight - 1;
    }
  }
  
  private List<SPGoal> getGoals(PersonalityPracticeArea personalityPracticeArea) {
    if (log.isDebugEnabled()) {
      log.debug("Processing personality :" + personalityPracticeArea);
    }
    return personalityPracticeArea.getGoalIds().stream().map(this::getGoal)
        .collect(Collectors.toList());
  }
  
  /**
   * getActiveDevelopmentStrategy method will return the active development strategy for the goal
   * which will be first initialized to the user when user completes assessment or give prism lens.
   * 
   * @param spGoal
   *          spGpaol
   * @return the list of active development strategy.
   */
  public List<Integer> getDevelopmentStrategy(SPGoal spGoal) {
    List<DevelopmentStrategy> developmentStrategyList = spGoal.getDevelopmentStrategyList();
    List<Integer> collect = developmentStrategyList.stream().filter(ds -> ds.isActive() == true)
        .map(ds -> getInt(ds.getId())).collect(Collectors.toList());
    return collect;
  }
  
  /**
   * Update the article for the given user.
   * 
   * @param user
   *          - user
   * @param articleId
   *          - article id
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void updateArticle(User user, String articleId) {
    if (user.getUserGoalId() == null) {
      throw new InvalidRequestException("User goals not set.");
    }
    updateArticle(user.getUserGoalId(), articleId);
  }
  
  /**
   * Update the article for the user as completed.
   * 
   * @param userGoalId
   *          - user goal id
   * @param articleId
   *          - article id
   */
  public void updateArticle(String userGoalId, String articleId) {
    UserGoal userGoal = userGoalsRepository.findById(userGoalId);
    // UserArticleProgress articleProgress = userGoal.getArticleProgress().stream()
    // .filter(ap -> ap.getArticleId().equals(articleId)).findFirst()
    // .orElseThrow(() -> new InvalidRequestException("Article not found in user list."));
    UserArticleProgress uap = new UserArticleProgress(articleId, false);
    uap.setArticleStatus(ArticleStatus.COMPLETED);
    userGoal.getArticleProgress().add(uap);
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Remove all the duplicate articles.
   */
  public void removeDuplicateArticles() {
    List<Article> allArticles = articleRepository.getAllArticles();
    List<String> articleNames = new ArrayList<String>();
    List<Article> articlesToRemove = new ArrayList<Article>();
    
    // get the list of articles to remove
    for (Article article : allArticles) {
      final String articleLinkLabel = article.getArticleLinkLabel();
      if (articleNames.contains(articleLinkLabel)) {
        articlesToRemove.add(article);
        articleRepository.remove(article);
      } else {
        articleNames.add(articleLinkLabel);
      }
    }
    
    if (log.isDebugEnabled()) {
      log.debug("Removing articles count:" + articlesToRemove.size() + ": Articles:"
          + articlesToRemove);
    }
    
    // removing the articles from the goals
    List<SPGoal> allGoals = goalsRepository.findAllGoals();
    Map<String, SPGoal> goalsMap = allGoals.stream().collect(
        Collectors.toMap(SPGoal::getId, g -> g));
    for (Article article : articlesToRemove) {
      for (String goalId : article.getGoals()) {
        SPGoal spGoal = goalsMap.get(goalId);
        if (spGoal != null) {
          spGoal.getMandatoryArticles().remove(article.getId());
        }
      }
    }
    
    List<String> articlesToRemoveIds = articlesToRemove.stream().map(Article::getId)
        .collect(Collectors.toList());
    
    // update the goals
    goalsRepository.updateGoals(allGoals);
    
    // removing the articles from the article progress
    List<UserGoal> allUserGoals = userGoalsRepository.getAllUserGoals();
    for (UserGoal ug : allUserGoals) {
      final List<UserArticleProgress> articleProgress = ug.getArticleProgress();
      boolean isUpdated = false;
      for (UserArticleProgress uap : articleProgress) {
        if (articlesToRemoveIds.contains(uap.getArticleId())) {
          articleProgress.remove(uap);
          isUpdated = true;
        }
      }
      // update in db
      if (isUpdated) {
        userGoalsRepository.save(ug);
      }
    }
    
    // update bookmarked articles
    List<BookMarkTracking> allBookMarkTrackingBean = trackingRepository
        .findAllBookMarkTrackingBean();
    allBookMarkTrackingBean.stream()
        .filter(bmt -> articlesToRemoveIds.contains(bmt.getArticleId()))
        .forEach(trackingRepository::remove);
    
    // update the tracking bean
    List<ArticleTrackingBean> allArticleTrackingBeans = trackingRepository
        .findAllArticleTrackingBean();
    allArticleTrackingBeans.stream()
        .filter(atb -> articlesToRemoveIds.contains(atb.getArticleId()))
        .forEach(trackingRepository::remove);
  }
  
  public Map<String, String> getAllThemesForCompany(String companyId, String locale) {
    return factoryCache.getGoalsForCompany(companyId, locale);
  }
  
  public Map<String, String> getAllThemesForIndividual(String locale) {
    return factoryCache.getGoalsForIndividual(locale);
  }
  
  public List<SPGoal> getAllGoals() {
    return factoryCache.getAllGoals();
  }
  
  /**
   * Get the development strategy for the given goal id and development id.
   * 
   * @param goalId
   *          - goal id
   * @param developmentId
   *          - development id
   * @return the development strategy for the given goal id and development strategy id or null if
   *         not found.
   */
  public DevelopmentStrategy getDevelopmentStrategyById(String goalId, int developmentId) {
    return getDevelopmentStrategyById(goalId, developmentId, Constants.DEFAULT_LOCALE);
  }
  
  /**
   * Get the development strategy for the given goal id and development id.
   * 
   * @param goalId
   *          - goal id
   * @param developmentId
   *          - development id
   * @return the development strategy for the given goal id and development strategy id or null if
   *         not found.
   */
  public DevelopmentStrategy getDevelopmentStrategyById(String goalId, int developmentId,
      String locale) {
    SPGoal goal = factoryCache.getGoal(goalId, locale);
    List<DevelopmentStrategy> listDevelopmentStrategy = goal.getDevelopmentStrategyList();
    if (listDevelopmentStrategy != null && developmentId < listDevelopmentStrategy.size()) {
      return listDevelopmentStrategy.get(developmentId);
    }
    return null;
  }
  
  /**
   * Helper method to get the development strategy when the development strategy id is a string.
   * 
   * @param goalId
   *          - practice area id
   * @param devStrategyId
   *          - development strategy id
   * @return the development strategy
   */
  public DevelopmentStrategy getDevelopmentStrategyById(String goalId, String devStrategyId,
      String locale) {
    return getDevelopmentStrategyById(goalId, Integer.parseInt(devStrategyId), locale);
  }
  
  /**
   * Get the list of practice areas.
   * 
   * @return the list of practice areas
   */
  public List<SPGoal> getAllPratcieAreas() {
    return factoryCache.getGoalsForCategory(GoalCategory.GrowthAreas);
  }
  
  /**
   * Get all the personality practice areas.
   * 
   * @return - the list of personality practice areas
   */
  public List<PersonalityPracticeArea> getAllPersonalityPracticeAreas() {
    return goalsRepository.findAllPersonalityPracticeAreas();
  }
  
  public PersonalityPracticeArea getPersonalityPracticeArea(PersonalityType personalityType) {
    return factoryCache.getPersonalityPracticeArea(personalityType, Constants.DEFAULT_LOCALE);
  }
  
  public void updatePersonalityPracticeArea(PersonalityPracticeArea personalityPracticeArea) {
    factoryCache.updatePersonalityPracticeArea(personalityPracticeArea);
  }
  
  /**
   * Update the updateDevelopmentsStragtegy for the user will update the active development stragegy
   * for the user.
   * 
   * @param user
   *          - user logged in whose ds is to be updated.
   * @param activeDs
   *          - article id.
   * @param goalId
   *          GOal id for which user ds has to be updated.
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void updateDevelopmentsStragtegy(User user, List<String> activeDs, String goalId) {
    UserGoal userGoal = userGoalsRepository.findById(user.getUserGoalId());
    UserGoalProgress userGoalProgress = userGoal.getGoalProgress().stream()
        .filter(gp -> gp.getGoalId().equals(goalId)).findFirst()
        .orElseThrow(() -> new InvalidRequestException("Goal id not found in user list."));
    List<Integer> collect = activeDs.stream().map(ac -> Integer.valueOf(ac))
        .collect(Collectors.toList());
    userGoalProgress.setDevelopmentStrategyLists(collect);
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Update the active/selected goal which user clicks on the Individual PA Page.
   * 
   * @param user
   *          - user
   * @param goalId
   *          - goal id
   */
  public void updateActiveGoalSelection(User user, String goalId) {
    UserGoal userGoal = userGoalsRepository.findById(user.getUserGoalId());
    userGoal.setActiveGoalId(goalId);
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Get the blueprint for the given blueprint id.
   * 
   * @param user
   *          - the user
   * @return the blueprint
   */
  public Blueprint getBlueprint(User user) {
    Assert.notNull(user, "User is required.");
    String blueprintId = user.getBlueprintId();
    if (blueprintId == null) {
      return null;
    }
    return factoryCache.getBlueprint(blueprintId);
  }
  
  /**
   * Get the blueprint else throw an error.
   * 
   * @param user
   *          - user
   * @return the blueprint
   */
  public Blueprint getValidBlueprint(User user) {
    Blueprint blueprint = getBlueprint(user);
    Assert.notNull(blueprint, MessagesHelper.getMessage("service.growl.message4"));
    return blueprint;
  }
  
  /**
   * Update the given blueprint object in DB and cache.
   * 
   * @param blueprint
   *          - blueprint to update
   */
  public void updateBlueprint(Blueprint blueprint) {
    factoryCache.updateBlueprint(blueprint);
  }
  
  /**
   * Delete the given blueprint.
   * 
   * @param blueprint
   *          - blueprint
   */
  public void removeBlueprint(Blueprint blueprint) {
    factoryCache.removeBlueprint(blueprint);
  }
  
  /**
   * bookMarkArticle method will update the book mark to the user.
   * 
   * @param isBookMarked
   *          - flag for book mark
   * @param articleId
   *          - article id
   * @param user
   *          - user
   */
  @CacheEvict(value = "userGoals", key = "#user.userGoalId+#user.userLocale")
  public void bookMarkArticle(boolean isBookMarked, String articleId, User user) {
    
    UserGoal userGoal = userGoalsRepository.findById(user.getUserGoalId());
    Set<String> bookMarkedArticles = userGoal.getBookMarkedArticles();
    if (!bookMarkedArticles.contains(articleId)) {
      // bookmark the article
      bookMarkedArticles.add(articleId);
    } else {
      bookMarkedArticles.remove(articleId);
    }
    userGoalsRepository.save(userGoal);
  }
  
  /**
   * Get all the goals for the given goals list.
   * 
   * @param goalIdList
   *          - the goal id list
   * @return the goal id list
   */
  public List<SPGoal> findAllGoalsById(List<String> goalIdList) {
    // Important : must not get from cache as this is for all other
    // cases like competency etc.
    return goalsRepository.findAllGoalsById(goalIdList);
  }
  
  /**
   * getUserForGoals method will return all the userIds who are having the goalId as selected.
   * 
   * @param goalId
   *          - practice area id.
   * @param companyId
   *          - id of the company.
   * @return the users.
   */
  public List<String> getUsersForGoals(String goalId, String companyId) {
    
    List<User> users = userRepository.findUsers(Constants.ENTITY_COMPANY_ID, companyId);
    List<String> usersIds = users.stream().map(User::getUserGoalId).filter(ugid -> ugid != null)
        .collect(Collectors.toList());
    List<UserGoal> usersForGoals = userGoalsRepository.getUsersForGoals(goalId, usersIds);
    
    List<String> validUsers = new ArrayList<>();
    for (UserGoal userGoal : usersForGoals) {
      
      for (User user : users) {
        if (userGoal.getId().equalsIgnoreCase(user.getUserGoalId())) {
          validUsers.add(user.getId());
          break;
        }
      }
    }
    
    return validUsers;
  }
  
  /**
   * getAllGoalsSummary method will cache the goals for the userfor the passed locale.
   * 
   * @param locale
   *          for which all goals are to be retreived.
   * @return
   */
  @Cacheable(value = "goals", key = "#locale")
  public List<UserGoalProgressSummaryDto> getAllGoalsSummary(String locale) {
    
    List<SPGoal> goalsForCategory = factoryCache.getGoalsForCategory(GoalCategory.GrowthAreas);
    if (locale.equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
      return goalsForCategory.stream().filter(goal -> (goal.getStatus() == GoalStatus.ACTIVE))
          .map(gl -> {
            return new UserGoalProgressSummaryDto(gl);
          }).collect(Collectors.toList());
    } else {
      return goalsForCategory.stream().filter(goal -> (goal.getStatus() == GoalStatus.ACTIVE))
          .map(gl -> {
            
            SPGoal translatedGoal = factoryCache.getGoal(gl.getId(), locale);
            return new UserGoalProgressSummaryDto(translatedGoal);
          }).collect(Collectors.toList());
    }
    
  }
}
