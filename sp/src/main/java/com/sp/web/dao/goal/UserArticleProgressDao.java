package com.sp.web.dao.goal;

import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.goal.UserArticleProgress;
import com.sp.web.repository.library.ArticlesFactory;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 *
 *         The user articles progress dao.
 */
public class UserArticleProgressDao implements Serializable {
  
  private static final long serialVersionUID = 4990985132566563630L;
  private String articleId;
  private ArticleStatus articleStatus;
  private boolean articleBookmarked;
  private boolean isArticleRecommendedByUser = false;
  private boolean mandatoryArticle;
  
  public UserArticleProgressDao(UserArticleProgress userArticlesProgress) {
    BeanUtils.copyProperties(userArticlesProgress, this);
  }
  
  public UserArticleProgressDao(String articleId) {
    this.articleId = articleId;
  }
  
  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }
  
  public String getArticleId() {
    return articleId;
  }
  
  public boolean isArticleBookmarked() {
    return articleBookmarked;
  }
  
  public void setArticleBookmarked(boolean articleBookmarked) {
    this.articleBookmarked = articleBookmarked;
  }
  
  public ArticleStatus getArticleStatus() {
    return articleStatus;
  }
  
  public void setArticleStatus(ArticleStatus articleStatus) {
    this.articleStatus = articleStatus;
  }
  
  public void setArticleRecommendedByUser(boolean isArticleRecommendedByUser) {
    this.isArticleRecommendedByUser = isArticleRecommendedByUser;
  }
  
  public boolean isArticleRecommendedByUser() {
    return isArticleRecommendedByUser;
  }
  
  /**
   * @param mandatoryArticle
   *          the mandatoryArticle to set
   */
  public void setMandatoryArticle(boolean mandatoryArticle) {
    this.mandatoryArticle = mandatoryArticle;
  }
  
  /**
   * @return the mandatoryArticle
   */
  public boolean isMandatoryArticle() {
    return mandatoryArticle;
  }
}
