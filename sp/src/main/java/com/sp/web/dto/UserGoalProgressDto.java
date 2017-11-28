package com.sp.web.dto;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.goal.GoalSource;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.repository.library.ArticlesFactory;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * 
 *         The DTO for user goals progress.
 */
public class UserGoalProgressDto extends UserGoalProgressSummaryDto {
  
  private Set<String> filters;
  
  private List<UserArticleProgressDto> userArticlesProgress;
  
  private String activeGoalId;
  
  /**
   * Constructor.
   * 
   * @param userGoalsProgress
   *          - user goals progress
   */
  public UserGoalProgressDto(UserGoalProgressDao userGoalsProgress) {
    this(userGoalsProgress.getGoal());
    BeanUtils.copyProperties(userGoalsProgress, this);
    setSourceList(userGoalsProgress.getSourceList().stream()
        .collect(Collectors.groupingBy(GoalSource::getGoalSourceType)));
    // filters = new HashSet<String>();
    // userArticlesProgress = userGoalsProgress.getArticleList().stream().map(a -> {
    // UserArticleProgressDto uap = new UserArticleProgressDto(a);
    // if (a.getArticleStatus() == ArticleStatus.COMPLETED) {
    // incrementArticlesCompleted();
    // }
    // filters.add(uap.getArticleTypeFormatted());
    // return uap;
    // }).collect(Collectors.toList());
    // setTotalArticles(userArticlesProgress.size());
  }
  
  /**
   * Constructor.
   * 
   * @param goal
   *          - goal
   */
  public UserGoalProgressDto(SPGoal goal) {
    super(goal);
  }
  
  public Set<String> getFilters() {
    return filters;
  }
  
  public void setFilters(Set<String> filters) {
    this.filters = filters;
  }
  
  public List<UserArticleProgressDto> getUserArticlesProgress() {
    return userArticlesProgress;
  }
  
  public void setUserArticlesProgress(List<UserArticleProgressDto> userArticlesProgress) {
    this.userArticlesProgress = userArticlesProgress;
  }
  
  public String getActiveGoalId() {
    return activeGoalId;
  }
  
  public void setActiveGoalId(String activeGoalId) {
    this.activeGoalId = activeGoalId;
  }
  
  /**
   * UpdateArticles method will update the articles to the user goal detail. It will add all the
   * articles with not started articles first and then completed articles at the end.
   * 
   * @param completedArticlesMap
   *          completed articles map.
   * @param artilces
   *          list of articles.
   * @param bookMarkArticles
   *          articles which are book marked.
   * @param mandatoryArticles
   *          mandatory articles list
   */
  public void updateArticles(Map<String, UserArticleProgressDao> completedArticlesMap,
      List<ArticleDao> artilces, Set<String> bookMarkArticles, User user,
      List<String> mandatoryArticles) {
    filters = new HashSet<String>();
    List<UserArticleProgressDto> mandatoryArticlesDto = new ArrayList<>();
    List<UserArticleProgressDto> nonMandatoryArticles = new ArrayList<>();
    List<UserArticleProgressDto> completedArticles = new ArrayList<>();
    artilces.stream().forEach(
        a -> {
          if (!completedArticlesMap.containsKey(a.getArticle().getId())) {
            UserArticleProgressDto uap = new UserArticleProgressDto(a);
            if (bookMarkArticles.contains(a.getArticle().getId())) {
              uap.setArticleBookmarked(true);
            }
            
            uap.setUserArticleStatus(ArticleStatus.NOT_STARTED);
            filters.add(uap.getArticleTypeFormatted());
            if (a.getArticle().getArticleReccomendations().contains(user.getEmail())) {
              uap.setArticleRecommendedByUser(true);
            }
            if (mandatoryArticles.contains(a.getArticle().getId())) {
              uap.setMandatoryArticle(true);
              mandatoryArticlesDto.add(uap);
            } else {
              nonMandatoryArticles.add(uap);
            }
          } else {
            UserArticleProgressDao userArticleProgressDao = completedArticlesMap.get(a.getArticle()
                .getId());
            UserArticleProgressDto uap = new UserArticleProgressDto(userArticleProgressDao, a
                .getArticle());
            if (bookMarkArticles.contains(userArticleProgressDao.getArticleId())) {
              uap.setArticleBookmarked(true);
            }
            uap.setUserArticleStatus(userArticleProgressDao.getArticleStatus());
            incrementArticlesCompleted();
            filters.add(uap.getArticleTypeFormatted());
            if (a.getArticle().getArticleReccomendations().contains(user.getEmail())) {
              uap.setArticleRecommendedByUser(true);
            }
            if (mandatoryArticles.contains(a.getArticle().getId())) {
              uap.setMandatoryArticle(true);
            }
            completedArticles.add(uap);
          }
        });
    userArticlesProgress = new ArrayList<>();
    userArticlesProgress.addAll(mandatoryArticlesDto);
    userArticlesProgress.addAll(nonMandatoryArticles);
    userArticlesProgress.addAll(completedArticles);
    setTotalArticles(userArticlesProgress.size());
    
  }
  
  /**
   * UpdateArticles method will update the articles to the user goal detail. It will add all the
   * articles with not started articles first and then completed articles at the end.
   * 
   * @param mandatoryArticles
   *          - mandatory articles list
   * @param articlesFactory
   */
  public void updateArticles(List<UserArticleProgressDao> topArticleProgressDao, User user,
      List<String> mandatoryArticles, ArticlesFactory articlesFactory) {
    filters = new HashSet<String>();
    userArticlesProgress = new ArrayList<>();
    topArticleProgressDao.stream().forEach(a -> {
      Article article = articlesFactory.getArticle(a.getArticleId());
      UserArticleProgressDto uap = new UserArticleProgressDto(a, article);
      filters.add(uap.getArticleTypeFormatted());
      if (article.getArticleReccomendations().contains(user.getEmail())) {
        uap.setArticleRecommendedByUser(true);
      }
      if (mandatoryArticles.contains(article.getId())) {
        uap.setMandatoryArticle(true);
      }
      uap.setUserArticleStatus(a.getArticleStatus());
      userArticlesProgress.add(uap);
    });
    setTotalArticles(userArticlesProgress.size());
    
  }
  
}
