package com.sp.web.model;

/**
 * @author pradeep
 * 
 *         The tracking bean entity.
 */
public class ArticleTrackingBean extends TrackingBean {

  private static final long serialVersionUID = 4299483294740637948L;

  private String id;

  private TrackingType trackingType = TrackingType.ARTICLES;

  private String articleId;

  /**
   * Constructor for article id and user id.
   * 
   * @param articleId
   *          - article id
   * @param userId
   *          - user id
   */
  public ArticleTrackingBean(String articleId, String userId) {
    super(userId, true);
    this.articleId = articleId;
  }
  
  /**
   * Default constructor.
   */
  public ArticleTrackingBean() { 
    super();
  }

  public TrackingType getTrackingType() {
    return trackingType;
  }

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public String getArticleId() {
    return articleId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}