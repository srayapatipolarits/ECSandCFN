package com.sp.web.dao.goal;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.goal.GoalSourceType;
import com.sp.web.model.goal.UserArticleProgress;
import com.sp.web.model.goal.UserGoal;
import com.sp.web.model.goal.UserGoalProgress;
import com.sp.web.repository.goal.UserGoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.goals.GoalsAlgorithm;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The dao for user goals that saves all the user goals.
 */
public class UserGoalDao implements Serializable {
  
  private static final long serialVersionUID = -9082768674295856296L;
  
  private static final Logger log = Logger.getLogger(UserGoalDao.class);
  
  private List<UserGoalProgressDao> selectedGoalsProgressList;
  private Map<String, UserGoalProgressDao> goalsProgressMap;
  private Map<String, UserArticleProgressDao> articleProgressMap;
  private boolean containsPrismLensGoal;
  private String activeGoalId;
  
  private Set<String> bookMarkedArticles;
  
  public UserGoalDao(UserGoal userGoal, ArticlesFactory articlesFactory,
      SPGoalFactory spGoalsFactory, GoalsAlgorithm goalsAlgorithm,
      UserGoalsRepository userGoalsRepository, String locale) {
    init(userGoal, articlesFactory, spGoalsFactory, goalsAlgorithm, userGoalsRepository, locale);
  }
  
  /**
   * Initialize all the helper data structures for user goal.
   * 
   * @param userGoal
   *          - user goal
   * @param goalsComparator
   *          - goals comparator
   * @param userGoalsRepository
   *          - user goals repository
   * @param locale
   *          - user locale
   */
  private void init(UserGoal userGoal, ArticlesFactory articlesFactory,
      SPGoalFactory spGoalsFactory, GoalsAlgorithm goalsAlgorithm,
      UserGoalsRepository userGoalsRepository, String locale) {
    
    // the map to store the goals progress
    goalsProgressMap = new HashMap<String, UserGoalProgressDao>();
    
    articleProgressMap = new HashMap<String, UserArticleProgressDao>();
    // create the goals progress list
    selectedGoalsProgressList = new ArrayList<UserGoalProgressDao>();
    
    for (UserGoalProgress userGoalProgress : userGoal.getGoalProgress()) {
      if (!containsPrismLensGoal) {
        containsPrismLensGoal = userGoalProgress.hasSourceType(GoalSourceType.PrismLens);
      }
      
      UserGoalProgressDao progressDao = new UserGoalProgressDao(userGoalProgress, spGoalsFactory,
          locale);
      
      if (userGoalProgress.isSelected()) {
        selectedGoalsProgressList.add(progressDao);
      }
      goalsProgressMap.put(userGoalProgress.getGoalId(), progressDao);
    }
    
    // sort the goals
    if (!selectedGoalsProgressList.isEmpty()) {
      goalsAlgorithm.sortUserGoalProgressDaoByOrder(selectedGoalsProgressList);
    }
    
    /** Article Progress contains the top 10 articles for the user. */
    
    /* UserGoal article progress contains the list of completed articles only */
    for (UserArticleProgress articlesProgress : userGoal.getArticleProgress()) {
      final UserArticleProgressDao userArticlesProgress = new UserArticleProgressDao(
          articlesProgress);
      articleProgressMap.put(articlesProgress.getArticleId(), userArticlesProgress);
    }
    
    this.bookMarkedArticles = userGoal.getBookMarkedArticles();
    
    // TODO remove this from individual call for the details.
    // for (UserGoalProgressDao selectedUGP : selectedGoalsProgressList) {
    // List<ArticleDao> artilces = articlesFactory.getArtilces(Constants.THEME, selectedUGP
    // .getGoal().getId(), locale);
    // updateArticles(artilces, locale, selectedUGP);
    // }
    
  }
  
  public void updateArticles(List<ArticleDao> artilces, String locale,
      UserGoalProgressDao selectedUGP, ArticlesFactory articlesFactory) {
    
    updateArticles(articleProgressMap, artilces, selectedUGP, articlesFactory);
  }
  
  /**
   * UpdateArticles method will update the articles to the user goal detail. It will add all the
   * articles with not started articles first and then completed articles at the end.
   * 
   * @param completedArticlesMap
   *          completed articles map.
   * @param artilces
   *          list of articles.
   * @param goalProgressDao
   *          goals progress dao
   */
  public void updateArticles(Map<String, UserArticleProgressDao> completedArticlesMap,
      List<ArticleDao> artilces, UserGoalProgressDao goalProgressDao,
      ArticlesFactory articlesFactory) {
    
    List<UserArticleProgressDao> mandatoryArticles = new ArrayList<>();
    List<UserArticleProgressDao> nonMandatoryArticles = new ArrayList<>();
    List<UserArticleProgressDao> completedArticles = new ArrayList<>();
    // Map<String, UserArticleProgressDao> completedArticles = new HashMap<>();
    
    final List<String> goalMandatoryArticles = goalProgressDao.getGoal().getMandatoryArticles();
    for (ArticleDao a : artilces) {
      final String articleId = a.getArticle().getId();
      final UserArticleProgressDao completedArticleDao = completedArticlesMap.get(articleId);
      if (completedArticleDao == null) {
        UserArticleProgressDao articleProgress = new UserArticleProgressDao(articleId);
        if (goalMandatoryArticles.contains(articleId)) {
          mandatoryArticles.add(articleProgress);
        } else {
          nonMandatoryArticles.add(articleProgress);
        }
      }
      completedArticles.add(completedArticleDao);
    }
    /*
     * if mandataory articles size is greater then 10, then add the first ten and ignore rest of the
     * articles and incase less then 10, then include non mandatory articles till 10.
     */
    List<UserArticleProgressDao> totalUserArticlesProgress = new ArrayList<>();
    totalUserArticlesProgress.addAll(mandatoryArticles);
    totalUserArticlesProgress.addAll(nonMandatoryArticles);
    totalUserArticlesProgress.addAll(completedArticles);
    
    totalUserArticlesProgress.stream().limit(10).forEach(aop -> {
      addArticleProgressDao(aop, goalProgressDao, articlesFactory);
    });
  }
  
  /**
   * @param articleProgressToRemove
   * @param aop
   * @param goalProgressDao
   * @param articlesFactory
   */
  private void addArticleProgressDao(UserArticleProgressDao uap,
      UserGoalProgressDao goalProgressDao, ArticlesFactory articlesFactory) {
    try {
      
      Article article = articlesFactory.getArticle(uap.getArticleId());
      if (bookMarkedArticles.contains(article.getId())) {
        uap.setArticleBookmarked(true);
      }
      if (uap.getArticleStatus() == null) {
        uap.setArticleStatus(ArticleStatus.NOT_STARTED);
      }
      
      goalProgressDao.add(uap);
      log.info("Adding user article " + article.getArticleLinkLabel() + ", Goal "
          + goalProgressDao.getGoal().getName());
    } catch (Exception e) {
      log.warn("Erorr adding article :" + uap.getArticleId() + ": ignoring.", e);
    }
  }
  
  public List<UserGoalProgressDao> getSelectedGoalsProgressList() {
    return selectedGoalsProgressList;
  }
  
  public void setSelectedGoalsProgressList(List<UserGoalProgressDao> selectedGoalsProgressList) {
    this.selectedGoalsProgressList = selectedGoalsProgressList;
  }
  
  public Map<String, UserGoalProgressDao> getGoalsProgressMap() {
    return goalsProgressMap;
  }
  
  public void setGoalsProgressMap(HashMap<String, UserGoalProgressDao> goalsProgressMap) {
    this.goalsProgressMap = goalsProgressMap;
  }
  
  public Map<String, UserArticleProgressDao> getArticleProgressMap() {
    if (articleProgressMap == null) {
      articleProgressMap = new HashMap<>();
    }
    return articleProgressMap;
  }
  
  public void setArticleProgressMap(HashMap<String, UserArticleProgressDao> articleProgressMap) {
    this.articleProgressMap = articleProgressMap;
  }
  
  public UserArticleProgressDao getArticleProgress(String articleId) {
    return getArticleProgressMap().get(articleId);
  }
  
  public UserGoalProgressDao getUserGoalProgress(String goalId) {
    return goalsProgressMap.get(goalId);
  }
  
  /**
   * @param containsPrismLensGoal
   *          the containsPrismLensGoal to set
   */
  public void setContainsPrismLensGoal(boolean containsPrismLensGoal) {
    this.containsPrismLensGoal = containsPrismLensGoal;
  }
  
  /**
   * @return the containsPrismLensGoal
   */
  public boolean isContainsPrismLensGoal() {
    return containsPrismLensGoal;
  }
  
  public String getActiveGoalId() {
    return activeGoalId;
  }
  
  public void setActiveGoalId(String activeGoalId) {
    this.activeGoalId = activeGoalId;
  }
  
  /**
   * @param bookMarkedArticles
   *          the bookMarkedArticles to set
   */
  public void setBookMarkedArticles(Set<String> bookMarkedArticles) {
    this.bookMarkedArticles = bookMarkedArticles;
  }
  
  /**
   * @return the bookMarkedArticles
   */
  public Set<String> getBookMarkedArticles() {
    if (bookMarkedArticles == null) {
      bookMarkedArticles = new HashSet<>();
    }
    return bookMarkedArticles;
    
  }
}
