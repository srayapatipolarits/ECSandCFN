package com.sp.web.model.library;

import com.sp.web.model.article.ArticleType;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author pradeep
 *
 *         The Newscred video article.
 */
@Document(collection = "articles")
public class NewsCredVideoArticle extends NewsCredArticle {

  private String videoUrl;

  /**
   * Constructor.
   * 
   * @param articleType
   *            - article type
   */
  public NewsCredVideoArticle(ArticleType articleType) {
    this.setArticleType(articleType);
  }

  /**
   * Default Constructor.
   */
  public NewsCredVideoArticle() {
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }
}
