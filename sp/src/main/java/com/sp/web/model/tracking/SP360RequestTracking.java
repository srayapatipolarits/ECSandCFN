package com.sp.web.model.tracking;

import com.sp.web.model.TrackingBean;
import com.sp.web.model.TrackingType;

/**
 * @author pradeepruhil
 *
 */
public class SP360RequestTracking extends TrackingBean {
  
  private static final long serialVersionUID = -233243607946893250L;
  
  private TrackingType trackingType;
  
  private String companyId;
  
  public SP360RequestTracking(String userId, boolean updateAccessTime, TrackingType requests) {
    super(userId, updateAccessTime);
    super.setAccessCount(1);
    this.trackingType = requests;
  }
  
  /**
   * Constructor
   */
  public SP360RequestTracking() {
  }
  
  public void setTrackingType(TrackingType trackingType) {
    this.trackingType = trackingType;
  }
  
  public TrackingType getTrackingType() {
    return trackingType;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
}
