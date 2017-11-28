package com.sp.web.dto;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.repository.library.ArticlesFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pradeep
 *
 *         The user article progress DTO.
 */
public class UserArticleProgressDetailDTO implements Serializable {
  
  private static final long serialVersionUID = -4435507550454318769L;
  
  private Set<String> filters;
  
  private List<UserArticleProgressDto> userArticlesProgress;
  
  private int totalArticles;
  
  private int articlesCompleted;
  
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
  
  public static long getSerialversionuid() {
    return serialVersionUID;
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
   * @param articlesFactory 
   */
  public void updateArticles(Map<String, UserArticleProgressDao> completedArticlesMap,
      List<ArticleDao> artilces, Set<String> bookMarkArticles, User user,
      List<String> mandatoryArticles, ArticlesFactory articlesFactory) {
    filters = new HashSet<String>();
    List<UserArticleProgressDto> mandatoryArticlesDto = new ArrayList<>();
    List<UserArticleProgressDto> nonMandatoryArticles = new ArrayList<>();
    List<UserArticleProgressDto> completedArticles = new ArrayList<>();
    artilces.stream().forEach(
        a -> {
          Article article = articlesFactory.getArticle(a.getArticle().getId());
          if (!completedArticlesMap.containsKey(a.getArticle().getId())) {
            UserArticleProgressDto uap = new UserArticleProgressDto(a);
            if (bookMarkArticles.contains(a.getArticle().getId())) {
              uap.setArticleBookmarked(true);
            }
            
            uap.setUserArticleStatus(ArticleStatus.NOT_STARTED);
            filters.add(uap.getArticleTypeFormatted());
            if (article.getArticleReccomendations().contains(user.getEmail())) {
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
            UserArticleProgressDto uap = new UserArticleProgressDto(userArticleProgressDao, a.getArticle());
            if (bookMarkArticles.contains(article.getId())) {
              uap.setArticleBookmarked(true);
            }
            uap.setUserArticleStatus(userArticleProgressDao.getArticleStatus());
            incrementArticlesCompleted();
            filters.add(uap.getArticleTypeFormatted());
            if (article.getArticleReccomendations()
                .contains(user.getEmail())) {
              uap.setArticleRecommendedByUser(true);
            }
            if (mandatoryArticles.contains(article.getId())) {
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
  
  public void setTotalArticles(int totalArticles) {
    this.totalArticles = totalArticles;
  }
  
  public int getTotalArticles() {
    return totalArticles;
  }
  
  public void setArticlesCompleted(int articlesCompleted) {
    this.articlesCompleted = articlesCompleted;
  }
  
  protected void incrementArticlesCompleted() {
    articlesCompleted++;
  }
  
  public int getArticlesCompleted() {
    return articlesCompleted;
  }
  
}
