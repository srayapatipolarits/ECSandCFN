/**
 * 
 */
package com.sp.web.model.tracking;

import com.sp.web.model.TrackingBean;
import com.sp.web.model.TrackingType;
import com.sp.web.model.User;

/**
 * @author pradeepruhil
 *
 */
public class ArticleUsefullArticleTracking extends TrackingBean {
  
  private static final long serialVersionUID = -233243607946893250L;
  
  private String companyId;
  
  private TrackingType trackingType;
  
  private boolean isUseFull;
  
  /**
   * Constructor.
   */
  public ArticleUsefullArticleTracking() {
    
  }
  
  public ArticleUsefullArticleTracking(User user, boolean updateAccessTime) {
    super(user.getId(), updateAccessTime);
    super.setAccessCount(1);
    this.trackingType = TrackingType.ARTICLES;
    this.companyId = user.getCompanyId();
  }
  
  /**
   * @param companyId
   *          the companyId to set
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  /**
   * @return the companyId
   */
  public String getCompanyId() {
    return companyId;
  }
  
  /**
   * @param trackingType
   *          the trackingType to set
   */
  public void setTrackingType(TrackingType trackingType) {
    this.trackingType = trackingType;
  }
  
  /**
   * @return the trackingType
   */
  public TrackingType getTrackingType() {
    return trackingType;
  }
  
  /**
   * @param isUseFull
   *          the isUseFull to set
   */
  public void setUseFull(boolean isUseFull) {
    this.isUseFull = isUseFull;
  }
  
  /**
   * @return the isUseFull
   */
  public boolean isUseFull() {
    return isUseFull;
  }
}
