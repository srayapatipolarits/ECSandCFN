/**
 * 
 */
package com.sp.web.model;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

/**
 * @author pradeep
 *
 */
public class FeedbackUserArchive extends FeedbackUser {

  /**
   * 
   */
  private static final long serialVersionUID = -6297085390641609590L;

  private LocalDate archivedOn;

  /**
   * 
   */
  public FeedbackUserArchive() {
  }

  public FeedbackUserArchive(FeedbackUser feedbackUser) {
    BeanUtils.copyProperties(feedbackUser, this);
    this.archivedOn = LocalDate.now();
  }

  /**
   * @return the archivedOn
   */
  public LocalDate getArchivedOn() {
    return archivedOn;
  }

  /**
   * @param archivedOn
   *          the archivedOn to set
   */
  public void setArchivedOn(LocalDate archivedOn) {
    this.archivedOn = archivedOn;
  }

}
