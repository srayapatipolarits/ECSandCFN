package com.sp.web.dto.library;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.utils.MessagesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author pradeep
 *
 *         The article DTO.
 */
public class ArticleDto extends BaseArticleDto {

  private static final long serialVersionUID = 8376230199455370256L;

  private String articleTypeFormatted;
  
  private List<BaseGoalDto> goals;
  
  /** The article status for a particular user. */
  private ArticleStatus userArticleStatus;
  
  private boolean articleForUser = false;
  

  private boolean canAddToUserGoals;
  
  private Set<String> goalIds;
  
  private boolean isArticleRecommendedByUser = false;
  
  private boolean articleBookmarked;
  
  private boolean mandatoryArticle;
  
  /**
   * Constructor from article.
   * 
   * @param article
   *          - article
   */
  public ArticleDto(Article article) {
    super(article);
    this.articleTypeFormatted = MessagesHelper.getMessage("traininglibrary.article.type."
        + article.getArticleType().toString());
  }

  /**
   * Constructor from article.
   * 
   * @param articleDao
   *          - article
   */
  public ArticleDto(ArticleDao articleDao) {
    this(articleDao.getArticle());
  }
  
  /**
   * Constructor.
   * 
   * @param article
   *            - article
   * @param userArticlesMap
   *            - user article map
   */
  public ArticleDto(Article article, Map<String, UserArticleProgressDao> userArticlesMap) {
    this(article);
    if (userArticlesMap != null) {
      Optional.ofNullable(userArticlesMap.get(article.getId())).ifPresent(uap -> {
          userArticleStatus = uap.getArticleStatus();
        });
    }
    articleForUser = true;
    this.goalIds = article.getGoals();
  }

  /**
   * Constructor.
   * 
   * @param article
   *          - article
   * @param articlesProgressMap
   *          - article progress map
   */
  public ArticleDto(ArticleDao article, Map<String, UserArticleProgressDao> articlesProgressMap) {
    this(article.getArticle(), articlesProgressMap);
    goals = article.getGoalList();
  }

  
  public String getArticleTypeFormatted() {
    return articleTypeFormatted;
  }

  public void setArticleTypeFormatted(String articleTypeFormatted) {
    this.articleTypeFormatted = articleTypeFormatted;
  }

  public ArticleStatus getUserArticleStatus() {
    return userArticleStatus;
  }

  public void setUserArticleStatus(ArticleStatus userArticleStatus) {
    this.userArticleStatus = userArticleStatus;
  }

  public boolean isArticleForUser() {
    return articleForUser;
  }

  public void setArticleForUser(boolean articleForUser) {
    this.articleForUser = articleForUser;
  }

  public List<BaseGoalDto> getGoals() {
    return Optional.ofNullable(goals).orElseGet(() -> {
        goals = new ArrayList<BaseGoalDto>();
        return goals;
      });
  }

  public void setGoals(List<BaseGoalDto> goals) {
    this.goals = goals;
  }
  
  public boolean isCanAddToUserGoals() {
    return canAddToUserGoals;
  }

  public void setCanAddToUserGoals(boolean canAddToUserGoals) {
    this.canAddToUserGoals = canAddToUserGoals;
  }

  public Set<String> getGoalIds() {
    return goalIds;
  }

  public void setGoalIds(Set<String> goalIds) {
    this.goalIds = goalIds;
  }

  public boolean isArticleRecommendedByUser() {
    return isArticleRecommendedByUser;
  }

  public void setArticleRecommendedByUser(boolean isArticleRecommendedByUser) {
    this.isArticleRecommendedByUser = isArticleRecommendedByUser;
  }
  
  public boolean isArticleBookmarked() {
    return articleBookmarked;
  }

  public void setArticleBookmarked(boolean articleBookmarked) {
    this.articleBookmarked = articleBookmarked;
  }

  public void setMandatoryArticle(boolean mandatoryArticle) {
    this.mandatoryArticle = mandatoryArticle;
  }
  
  public boolean isMandatoryArticle() {
    return mandatoryArticle;
  }

}
