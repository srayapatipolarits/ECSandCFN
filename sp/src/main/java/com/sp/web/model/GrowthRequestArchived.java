package com.sp.web.model;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * GrowthRequestArchvied contains the archived request
 * 
 * @author pradeep
 *
 */
public class GrowthRequestArchived extends GrowthRequest {

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 2209934745033881206L;

  /** Archived on date. */
  private LocalDateTime archivedOn;

  /**
   * GrowthRequestArchived.
   */
  public GrowthRequestArchived() {
  }

  /**
   * <code>growthRequest to be archived</code>
   * 
   * @param growthRequest
   *          which is archived.
   */
  public GrowthRequestArchived(GrowthRequest growthRequest) {
    BeanUtils.copyProperties(growthRequest, this);
    this.setFeedbackIntervals(growthRequest.getFeedbackIntervals());
    this.setFeedbacks(growthRequest.getFeedbacks());
    this.setPendingFeedbacks(growthRequest.getPendingFeedbacks());
    this.setStartDate(growthRequest.getStartDate());
    this.setEndDate(growthRequest.getEndDate());
    this.setGoals(growthRequest.getGoals());
    this.archivedOn = LocalDateTime.now();
    this.setCompanyId(growthRequest.getCompanyId());

  }

  /**
   * @param archivedOn
   *          the archivedOn to set.
   */
  public void setArchivedOn(LocalDateTime archivedOn) {
    this.archivedOn = archivedOn;
  }

  /**
   * @return the archivedOn.
   */
  public LocalDateTime getArchivedOn() {
    return archivedOn;
  }

}
