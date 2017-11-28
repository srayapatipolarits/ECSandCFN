/**
 * 
 */
package com.sp.web.model.library;

import com.sp.web.model.article.Article;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author pradeep
 *
 */
@Document(collection = "articles")
public class NewsCredArticle extends Article {

  /**
   * Default serial version id for.
   */
  private static final long serialVersionUID = 8483164163763843609L;
  private String newsCredUrl;

  public String getNewsCredUrl() {
    return newsCredUrl;
  }

  public void setNewsCredUrl(String newsCredUrl) {
    this.newsCredUrl = newsCredUrl;
  }
}
