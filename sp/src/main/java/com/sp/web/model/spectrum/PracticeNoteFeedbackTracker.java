/**
 * 
 */
package com.sp.web.model.spectrum;

import java.io.Serializable;

/**
 * @author pradeepruhil
 *
 */
public class PracticeNoteFeedbackTracker implements Serializable {
  
  /**
   * default serial version id.
   */
  private static final long serialVersionUID = -6284212576065251849L;
  
  private int requestSent;
  
  private int completed;
  
  private int pending;
  
  private int totalNotes;
  
  private int declined;
  
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
  
  public void setTotalNotes(int totalNotes) {
    this.totalNotes = totalNotes;
  }
  
  public int getTotalNotes() {
    return totalNotes;
  }
  
  public void setDeclined(int declined) {
    this.declined = declined;
  }
  
  public int getDeclined() {
    return declined;
  }
}
