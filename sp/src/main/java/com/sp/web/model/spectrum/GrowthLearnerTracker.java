/**
 * 
 */
package com.sp.web.model.spectrum;

import java.io.Serializable;

/**
 * @author pradeepruhil
 *
 */
public class GrowthLearnerTracker implements Serializable {
  
  /**
   * default serial version id.
   */
  private static final long serialVersionUID = -6284212576065251849L;
  
  private int requestSent;
  
  private int completed;
  
  private int pending;
  
  private int declined;
  
  private int deactivated;
  
  public int getRequestSent() {
    return requestSent;
  }
  
  public void setRequestSent(int requestSent) {
    this.requestSent = requestSent;
  }
  
  public int getCompleted() {
    return completed;
  }
  
  public void setCompleted(int completed) {
    this.completed = completed;
  }
  
  public int getPending() {
    return pending;
  }
  
  public void setPending(int pending) {
    this.pending = pending;
  }
  
  public int getDeclined() {
    return declined;
  }
  
  public void setDeclined(int declined) {
    this.declined = declined;
  }
  
  public void setDeactivated(int deactivated) {
    this.deactivated = deactivated;
  }
  
  public int getDeactivated() {
    return deactivated;
  }
  
}
