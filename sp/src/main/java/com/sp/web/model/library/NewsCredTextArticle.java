package com.sp.web.model.library;

import com.sp.web.model.article.ArticleType;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * @author pradeep
 *
 *         The News Cred Text Article entity.
 */
@Document(collection = "articles")
public class NewsCredTextArticle extends NewsCredArticle {

  private static final long serialVersionUID = -8632168611256481820L;

  private Set<NewsCredImage> images;

  /**
   * Constructor.
   * 
   * @param articleType
   *          - article type
   */
  public NewsCredTextArticle(ArticleType articleType) {
    this.setArticleType(articleType);
  }

  /**
   * Default Constructor.
   */
  public NewsCredTextArticle() {
  }

  /**
   * Get the the article images.
   * 
   * @return
   *        the images
   */
  public Set<NewsCredImage> getImages() {
    if (images == null) {
      images = new HashSet<NewsCredImage>();
    }
    return images;
  }

  public void setImages(Set<NewsCredImage> images) {
    this.images = images;
  }
}
