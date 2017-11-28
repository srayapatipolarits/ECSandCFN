package com.sp.web.model;

/**
 * @author pradeepruhil
 *
 */
public class BookMarkTracking extends ArticleTrackingBean {

  /**
   * 
   */
  private static final long serialVersionUID = -233243607946893250L;

  private boolean isBookMarked;
  
  private String companyId;
  

  /**
   * @return the isBookMarked
   */
  public boolean isBookMarked() {
    return isBookMarked;
  }

  /**
   * @param isBookMarked
   *          the isBookMarked to set
   */
  public void setBookMarked(boolean isBookMarked) {
    this.isBookMarked = isBookMarked;
  }

  
  /**
   * @param companyId the companyId to set
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