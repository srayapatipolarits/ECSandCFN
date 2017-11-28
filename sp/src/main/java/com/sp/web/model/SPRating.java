package com.sp.web.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the rating information.
 */
public class SPRating implements Serializable {
  
  private static final long serialVersionUID = -5114514852139594480L;
  private int defaultIndex;
  private List<SPRatingScore> ratingList;
  
  public int getDefaultIndex() {
    return defaultIndex;
  }
  
  public void setDefaultIndex(int defaultIndex) {
    this.defaultIndex = defaultIndex;
  }
  
  public List<SPRatingScore> getRatingList() {
    return ratingList;
  }
  
  public void setRatingList(List<SPRatingScore> ratingList) {
    this.ratingList = ratingList;
  }
  
}
