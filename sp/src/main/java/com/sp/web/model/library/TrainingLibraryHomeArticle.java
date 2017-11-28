package com.sp.web.model.library;

import com.sp.web.Constants;
import com.sp.web.model.article.Article;

/**
 * @author Dax Abraham
 *
 *         The entity bean to store the list of articles for the training library home page.
 */
public class TrainingLibraryHomeArticle {
  
  private String id;
  private String articleId;
  private String shortDescription;
  private ArticleLocation articleLocation;
  private int articlePosition;
  private LibraryHome articleHome;
  private String companyId;
  private String articleLinkLabel;
  private String imageUrl;
  
  
  /**
   * Default Constructor.
   */
  public TrainingLibraryHomeArticle() {
  }
  
  /**
   * Constructor.
   * 
   * @param article
   *          - article
   * @param shortDescription
   *          - short description
   * @param position
   *          - position
   * @param companyId
   *          - company id 
   * @param articleLocaltion
   *          - article location
   */
  public TrainingLibraryHomeArticle(Article article, String shortDescription, int position,
      String companyId, ArticleLocation articleLocaltion, String articleLinkLabel) {
    this.articleId = article.getId();
    this.shortDescription = shortDescription;
    this.articleLocation = articleLocaltion;
    this.articlePosition = position + Constants.getPositionOffset(articleLocation);
    this.companyId = companyId;
    this.articleLinkLabel = article.getArticleLinkLabel();
    this.imageUrl = article.getImageUrl();
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getArticleId() {
    return articleId;
  }
  
  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }
  
  public ArticleLocation getArticleLocation() {
    return articleLocation;
  }

  public void setArticleLocation(ArticleLocation articleLocation) {
    this.articleLocation = articleLocation;
  }

  public int getArticlePosition() {
    return articlePosition;
  }

  public void setArticlePosition(int articlePosition) {
    this.articlePosition = articlePosition;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }
  
  public void setArticleHome(LibraryHome articleHome) {
    this.articleHome = articleHome;
  }
  
  public LibraryHome getArticleHome() {
    return articleHome;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }

  public String getArticleLinkLabel() {
    return articleLinkLabel;
  }

  public void setArticleLinkLabel(String articleLinkLabel) {
    this.articleLinkLabel = articleLinkLabel;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
}
