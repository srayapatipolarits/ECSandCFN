package com.sp.web.model;

import java.time.LocalDate;

/**
 * @author pradeep
 * 
 *         The Prism Lens feedback archive.
 */
public class FeedbackArchiveRequest extends FeedbackRequest {

  private static final long serialVersionUID = 8383119779109758687L;
  private LocalDate archivedOn;
  private String feedbackUserMemberId;

  public FeedbackArchiveRequest() {
  }

  public FeedbackArchiveRequest(FeedbackRequest feedbackRequest) {
    org.springframework.beans.BeanUtils.copyProperties(feedbackRequest, this);
    this.archivedOn = LocalDate.now();
  }

  public void setArchivedOn(LocalDate archivedOn) {
    this.archivedOn = archivedOn;
  }

  public LocalDate getArchivedOn() {
    return archivedOn;
  }

  @Override
  public String toString() {
    return "FeedbackArchiveRequest [archivedOn=" + archivedOn + ", getArchivedOn()=" + getArchivedOn() + ", getId()="
        + getId() + ", getFeedbackUserId()=" + getFeedbackUserId() + ", getRequestedById()=" + getRequestedById()
        + ", getStartDate()=" + getStartDate() + ", getEndDate()=" + getEndDate() + ", getRequestStatus()="
        + getRequestStatus() + ", getRequestType()=" + getRequestType() + ", toString()=" + super.toString()
        + ", isExternalUserVerifedDetails()=" + isExternalUserVerifedDetails() + ", getClass()=" + getClass()
        + ", hashCode()=" + hashCode() + "]";
  }

  public String getFeedbackUserMemberId() {
    return feedbackUserMemberId;
  }

  public void setFeedbackUserMemberId(String feedbackUserMemberId) {
    this.feedbackUserMemberId = feedbackUserMemberId;
  }

}
