package com.sp.web.dto;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dto.library.ArticleDto;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;

import java.io.Serializable;

/**
 * @author pradeep
 *
 *         The user article progress DTO.
 */
public class UserArticleProgressDto extends ArticleDto implements Serializable {
  
  private static final long serialVersionUID = -4435507550454318769L;
  
  private boolean completed;
  
  /**
   * Constructor.
   * 
   * @param userArticlesProgressDao
   *          - user article progress
   */
  public UserArticleProgressDto(UserArticleProgressDao userArticlesProgressDao, Article article) {
    super(article);
    this.completed = (userArticlesProgressDao.getArticleStatus() == ArticleStatus.COMPLETED);
    setArticleBookmarked(userArticlesProgressDao.isArticleBookmarked());
    setMandatoryArticle(userArticlesProgressDao.isMandatoryArticle());
  }
  
  /**
   * Constructor.
   * 
   * @param userArticlesProgressDao
   *          - user article progress
   */
  public UserArticleProgressDto(ArticleDao articleDao) {
    super(articleDao.getArticle());
  }
  
  public boolean isCompleted() {
    return completed;
  }
}
