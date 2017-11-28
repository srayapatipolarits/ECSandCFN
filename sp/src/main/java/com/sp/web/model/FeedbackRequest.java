package com.sp.web.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <code>FeedbackURequest</code> is the bean class for feedback request.
 * 
 * @author pradeep
 *
 */
public class FeedbackRequest implements Serializable {

  /**
   * serial versio id.
   */
  private static final long serialVersionUID = 1846566851420181567L;

  private String id;

  /** FeedbackUser id who has givene the feedback. */
  private String feedbackUserId;

  /** RequestdById who has sent the request. */
  private String requestedById;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate startDate;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate endDate;

  private RequestStatus requestStatus;

  private RequestType requestType;

  private boolean externalUserVerifedDetails;

  private String tokenUrl;

  /**
   * return the id.
   * 
   * @return the id
   */
  public String getId() {
    return id;
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
   * the feedback user id.
   * 
   * @return the feedbackUserId
   */
  public String getFeedbackUserId() {
    return feedbackUserId;
  }

  /**
   * set the feedback user id.
   * 
   * @param feedbackUserId
   *          the feedbackUserId to set
   */
  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }

  /**
   * return the requested by id.
   * 
   * @return the requestedById
   */
  public String getRequestedById() {
    return requestedById;
  }

  /**
   * set the requested by id.
   * 
   * @param requestedById
   *          the requestedById to set
   */
  public void setRequestedById(String requestedById) {
    this.requestedById = requestedById;
  }

  /**
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
   * return the end date.
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
   * Return the request status.
   * 
   * @return the requestStatus
   */
  public RequestStatus getRequestStatus() {
    return requestStatus;
  }

  /**
   * Set the request status.
   * 
   * @param requestStatus
   *          the requestStatus to set
   */
  public void setRequestStatus(RequestStatus requestStatus) {
    this.requestStatus = requestStatus;
  }

  /**
   * @param requestType
   *          the requestType to set
   */
  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  /**
   * @return the requestType
   */
  public RequestType getRequestType() {
    return requestType;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FeedbackRequest [id=" + id + ", feedbackUserId=" + feedbackUserId + ", requestedById="
        + requestedById + ", startDate=" + startDate + ", endDate=" + endDate + ", requestStatus="
        + requestStatus + "]";
  }

  /**
   * @return
   */
  public boolean isExternalUserVerifedDetails() {
    return externalUserVerifedDetails;
  }

  /**
   * @param externalUserVerifedDetails
   *          the externalUserVerifedDetails to set
   */
  public void setExternalUserVerifedDetails(boolean externalUserVerifedDetails) {
    this.externalUserVerifedDetails = externalUserVerifedDetails;
  }

  /**
   * @param tokenUrl
   *          the tokenUrl to set
   */
  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }

  /**
   * @return the tokenUrl
   */
  public String getTokenUrl() {
    return tokenUrl;
  }

}
