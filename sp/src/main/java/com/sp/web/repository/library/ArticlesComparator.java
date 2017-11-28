package com.sp.web.repository.library;

import com.sp.web.model.article.Article;

import java.util.Comparator;

/**
 * @author Dax Abraham
 * 
 *         The comparator for articles sorting.
 */
public class ArticlesComparator implements Comparator<Article> {
  
  @Override
  public int compare(Article o1, Article o2) {
    if (o1 == null) {
      return -1;
    }
    
    if (o2 == null) {
      return 1;
    }
    if (o1.getArticleLinkLabel() == null) {
      return -1;
    }
    
    return o1.getArticleLinkLabel().compareTo(o2.getArticleLinkLabel());
  }
  
}
