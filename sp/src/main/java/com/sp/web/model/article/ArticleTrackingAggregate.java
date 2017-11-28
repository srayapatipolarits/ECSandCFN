package com.sp.web.model.article;

/**
 * @author Dax Abraham
 *
 *         This entity stores the article tracking aggregates.
 */
public class ArticleTrackingAggregate {
  
  private String id;
  private int totalCount;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public int getTotalCount() {
    return totalCount;
  }
  
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
}
