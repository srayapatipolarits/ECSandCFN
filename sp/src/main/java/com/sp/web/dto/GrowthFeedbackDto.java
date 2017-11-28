/**
 * 
 */
package com.sp.web.dto;

import com.sp.web.model.GrowthFeedbackResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * @author pradeep
 *
 */
public class GrowthFeedbackDto {

  private String createdOn;

  private LocalDate createdOnLocalDate;

  private String message;

  private boolean isReminder;

  List<GrowthFeedbackResponse> responseFeedbacks;

  /**
   * 
   */
  public GrowthFeedbackDto(String createdOn, LocalDate createdOnLocalDate) {
    this.createdOn = createdOn;
    this.createdOnLocalDate = createdOnLocalDate;
  }

  /**
   * 
   */
  public GrowthFeedbackDto() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @return the createdOn
   */
  public String getCreatedOn() {
    return createdOn;
  }

  /**
   * @param createdOn
   *          the createdOn to set
   */
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * @return the responseFeedbacks
   */
  public List<GrowthFeedbackResponse> getResponseFeedbacks() {
    return responseFeedbacks;
  }

  /**
   * @param responseFeedbacks
   *          the responseFeedbacks to set
   */
  public void setResponseFeedbacks(List<GrowthFeedbackResponse> responseFeedbacks) {
    this.responseFeedbacks = responseFeedbacks;
  }

  /**
   * @param createdOnLocalDate
   *          the createdOnLocalDate to set
   */
  public void setCreatedOnLocalDate(LocalDate createdOnLocalDate) {
    this.createdOnLocalDate = createdOnLocalDate;
  }

  /**
   * @return the createdOnLocalDate
   */
  public LocalDate getCreatedOnLocalDate() {
    return createdOnLocalDate;
  }

  /**
   * @param message
   *          the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param isReminder
   *          the isReminder to set
   */
  public void setReminder(boolean isReminder) {
    this.isReminder = isReminder;
  }

  /**
   * @return the isReminder
   */
  public boolean isReminder() {
    return isReminder;
  }
}
