package com.sp.web.dto.library;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleType;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         This article stores the basic articles dto information.
 */
public class BaseArticleDto implements Serializable{
  
  private static final long serialVersionUID = 5218929726149598194L;

   private String id;
   
  /** Article Link label. */
  private String articleLinkLabel;
  
  private String articleLinkUrl;
  
  /** author of the article. */
  private List<String> author;
  
  private String videoUrl;
  
  private String imageUrl;
  
  /** Article type (video/audio/text). */
  private ArticleType articleType;
  
  /** article recommendation count. */
  private int recommendationCount;
  
  private String articleSource;
  
  public BaseArticleDto(Article article) {
    BeanUtils.copyProperties(article, this);
  }

  public BaseArticleDto(ArticleDao article) {
    BeanUtils.copyProperties(article.getArticle(), this);
  }
  
  public String getArticleLinkLabel() {
    return articleLinkLabel;
  }
  
  public void setArticleLinkLabel(String articleLinkLabel) {
    this.articleLinkLabel = articleLinkLabel;
  }
  
  public String getArticleLinkUrl() {
    return articleLinkUrl;
  }
  
  public void setArticleLinkUrl(String articleLinkUrl) {
    this.articleLinkUrl = articleLinkUrl;
  }
  
  public List<String> getAuthor() {
    return author;
  }
  
  public void setAuthor(List<String> author) {
    this.author = author;
  }
  
  public ArticleType getArticleType() {
    return articleType;
  }
  
  public void setArticleType(ArticleType articleType) {
    this.articleType = articleType;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public int getRecommendationCount() {
    return recommendationCount;
  }

  public void setRecommendationCount(int recommendationCount) {
    this.recommendationCount = recommendationCount;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
  public void setArticleSourceTypes(String articleSourceTypes) {
    this.articleSource = articleSourceTypes;
  }

  public String getArticleSourceTypes() {
    return articleSource;
  }

  public String getArticleSource() {
    return articleSource;
  }

  public void setArticleSource(String articleSource) {
    this.articleSource = articleSource;
  }

  
}
