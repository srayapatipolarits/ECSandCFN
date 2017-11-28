package com.sp.web.model.goal;

import com.sp.web.model.article.ArticleStatus;

import org.springframework.util.Assert;

/**
 * @author pradeep
 * 
 *         The entity to store the user article progress.
 */
public class UserArticleProgress {

  private String articleId;

  private ArticleStatus articleStatus;

  private boolean isMandatory;

  /**
   * Default Constructor.
   */
  public UserArticleProgress() { }
  
  /**
   * Constructor to create a primary article for the user.
   * 
   * @param articleId
   *          - article id
   */
  public UserArticleProgress(String articleId) {
    this(articleId, true);
  }

  /**
   * Constructor to create a user article progress.
   * 
   * @param articleId
   *          - article id
   * @param isMandatory
   *          - is mandatory flag
   */
  public UserArticleProgress(String articleId, boolean isMandatory) {
    Assert.hasText(articleId, "Article id is required.");
    this.articleId = articleId;
    this.isMandatory = isMandatory;
    this.articleStatus = ArticleStatus.NOT_STARTED;
  }

  public String getArticleId() {
    return articleId;
  }

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public void setMandatory(boolean isMandatory) {
    this.isMandatory = isMandatory;
  }

  public boolean isMandatory() {
    return isMandatory;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof UserArticleProgress)) {
      return false;
    } else {
      return (articleId.equalsIgnoreCase(((UserArticleProgress) obj).getArticleId()));
    }
  }

  /**
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int code = 42;
    code += (this.articleId != null ? this.articleId.hashCode() : 0);
    return code;
  }

  public void setArticleStatus(ArticleStatus articleStatus) {
    this.articleStatus = articleStatus;
  }

  public ArticleStatus getArticleStatus() {
    return articleStatus;
  }
}
