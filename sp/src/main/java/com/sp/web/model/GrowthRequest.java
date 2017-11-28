package com.sp.web.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.form.GrowthInviteForm;
import com.sp.web.utils.DateTimeUtil;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Growth Request is the request to the growth team members for the feedback.
 * 
 * @author pradeep
 */
public class GrowthRequest {

  private String id;

  private String memberEmail;

  private String requestedByEmail;

  private RequestType requestType;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate startDate;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate endDate;

  private List<String> goals;

  private RequestStatus requestStatus;

  private List<GrowthFeedback> feedbacks;

  private List<LocalDate> feedbackIntervals;

  private List<LocalDate> pendingFeedbacks;

  private boolean externalUserVerifedDetails;

  private String tokenUrl;

  private String declineComment;
  
  /* Adding comment to be used in the email */
  private String comment;

  private String companyId;
  /**
   * Constructor.
   */
  public GrowthRequest() {
  }

  /**
   * Constructor initializing the growth request with the form data submittd
   * 
   * @param growthInviteForm
   *          growthInviteForm
   */
  public GrowthRequest(GrowthInviteForm growthInviteForm) {
    BeanUtils.copyProperties(growthInviteForm, this);
    this.requestType = RequestType.valueOf(growthInviteForm.getRequestType());
    this.startDate = DateTimeUtil.getLocalDate(growthInviteForm.getStartDate());
    this.endDate = DateTimeUtil.getLocalDate(growthInviteForm.getEndDate());
    this.goals = growthInviteForm.getGoal();
    this.comment = growthInviteForm.getComment();

  }

  /**
   * @param goals
   *          the goals to set
   */
  public void setGoals(List<String> goals) {
    this.goals = goals;
  }

  /**
   * @return the goals
   */
  public List<String> getGoals() {
    return goals;
  }

  /**
   * set the id.
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * the id.
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param memberEmail
   *          the memberEmail to set
   */
  public void setMemberEmail(String memberEmail) {
    this.memberEmail = memberEmail;
  }

  /**
   * @return the memberEmail
   */
  public String getMemberEmail() {
    return memberEmail;
  }

  /**
   * the requested by email.
   * 
   * @return the requestedByEmail
   */
  public String getRequestedByEmail() {
    return requestedByEmail;
  }

  /**
   * set ther equested by email.
   * 
   * @param requestedByEmail
   *          the requestedByEmail to set
   */
  public void setRequestedByEmail(String requestedByEmail) {
    this.requestedByEmail = requestedByEmail;
  }

  /**
   * the requested type.
   * 
   * @return the requestType
   */
  public RequestType getRequestType() {
    return requestType;
  }

  /**
   * set the requestype.
   * 
   * @param requestType
   *          the requestType to set
   */
  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  /**
   * the state date.
   * 
   * @return the startDate
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * set the start date.
   * 
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * the end date.
   * 
   * @return the endDate
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * set the end date.
   * 
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   * the request status.
   * 
   * @return the requestStatus
   */
  public RequestStatus getRequestStatus() {
    return requestStatus;
  }

  /**
   * set the request status.
   * 
   * @param requestStatus
   *          the requestStatus to set
   */
  public void setRequestStatus(RequestStatus requestStatus) {
    this.requestStatus = requestStatus;
  }

  /**
   * return the list of growth feedbacks.
   * 
   * @return the feedbacks
   */
  public List<GrowthFeedback> getFeedbacks() {
    if (feedbacks == null) {
      feedbacks = new ArrayList<GrowthFeedback>();
    }
    return feedbacks;
  }

  /**
   * the feedbacks.
   * 
   * @param feedbacks
   *          the feedbacks to set
   */
  public void setFeedbacks(List<GrowthFeedback> feedbacks) {
    this.feedbacks = feedbacks;
  }

  /**
   * the feedback internals.
   * 
   * @return the feedbackIntervals
   */
  public List<LocalDate> getFeedbackIntervals() {
    if (feedbackIntervals == null) {
      feedbackIntervals = new ArrayList<LocalDate>();
    }
    return feedbackIntervals;
  }

  /**
   * set the feedback internals.
   * 
   * @param feedbackIntervals
   *          the feedbackIntervals to set
   */
  public void setFeedbackIntervals(List<LocalDate> feedbackIntervals) {
    this.feedbackIntervals = feedbackIntervals;
  }

  /**
   * returns pending feebacks.
   * 
   * @return the pendingFeedbacks
   */
  public List<LocalDate> getPendingFeedbacks() {
    if (pendingFeedbacks == null) {
      pendingFeedbacks = new ArrayList<LocalDate>();
    }
    return pendingFeedbacks;
  }

  /**
   * set the pending feedbacks.
   * 
   * @param pendingFeedbacks
   *          the pendingFeedbacks to set
   */
  public void setPendingFeedbacks(List<LocalDate> pendingFeedbacks) {
    this.pendingFeedbacks = pendingFeedbacks;
  }

  public void setExternalUserVerifedDetails(boolean externalUserVerifedDetails) {
    this.externalUserVerifedDetails = externalUserVerifedDetails;
  }

  public boolean isExternalUserVerifedDetails() {
    return externalUserVerifedDetails;
  }

  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }

  public String getTokenUrl() {
    return tokenUrl;
  }

  public void setDeclineComment(String declineComment) {
    this.declineComment = declineComment;
  }

  public String getDeclineComment() {
    return declineComment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getComment() {
    return comment;
  }
  
  @Override
  public String toString() {
    return id + ":" + requestType + ":" + memberEmail;
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
