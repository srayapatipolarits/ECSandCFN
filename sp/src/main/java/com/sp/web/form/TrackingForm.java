package com.sp.web.form;

import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author pradeep
 * 
 *         The tracking form.
 */
public class TrackingForm implements Serializable {

  private static final long serialVersionUID = 215782564162635983L;

  String articleId;

  String trackingType;

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public String getArticleId() {
    return articleId;
  }

  public void setTrackingType(String trackingType) {
    this.trackingType = trackingType;
  }

  public String getTrackingType() {
    return trackingType;
  }

  /**
   * Helper method to create a new booking mark tracking bean from the 
   * information available in the tracking form.
   * 
   * @param user
   *          - logged in user
   * @return
   *    the newly created book mark tracking bean
   */
  public BookMarkTracking getBookMarkTrackingBean(User user) {
    BookMarkTracking bookMarkTrackingBean = new BookMarkTracking();
    bookMarkTrackingBean.setArticleId(articleId);
    bookMarkTrackingBean.setAccessTime(LocalDateTime.now());
    bookMarkTrackingBean.setUserId(user.getId());
    return bookMarkTrackingBean;
  }
}
