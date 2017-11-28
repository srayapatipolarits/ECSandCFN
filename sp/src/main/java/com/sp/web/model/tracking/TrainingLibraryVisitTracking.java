package com.sp.web.model.tracking;

import com.sp.web.model.TrackingBean;
import com.sp.web.model.User;

/**
 * @author pradeepruhil
 *
 */
public class TrainingLibraryVisitTracking extends TrackingBean {
  
  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 3767237577287679052L;
  
  private String companyId;
  
  public TrainingLibraryVisitTracking() {
  }
  
  /**
   * Constructor.
   */
  public TrainingLibraryVisitTracking(User user, boolean updateAccessTime) {
    super(user.getId(), updateAccessTime);
    super.setAccessCount(1);
    
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
  
}
