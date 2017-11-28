package com.sp.web.dto;

import com.sp.web.model.FeedbackRequest;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

/**
 * @author pradeep
 * 
 *         The DTO for the feedback Request.
 */
public class FeedbackRequestDTO {

  private BaseUserDTO feedbackUser;
  
  private String startDate;

  private String lastDate;

  private String requestStatus;

  private String id;

  /**
   * Constructor.
   * 
   * @param fbArchvedRequest
   *            - feedback request
   */
  public FeedbackRequestDTO(FeedbackRequest fbArchvedRequest) {
    BeanUtils.copyProperties(fbArchvedRequest, this);
    this.requestStatus = MessagesHelper.getMessage("feedbackStatus."
        + fbArchvedRequest.getRequestStatus());
  }

  public BaseUserDTO getFeedbackUser() {
    return feedbackUser;
  }

  public void setFeedbackUser(BaseUserDTO feedbackUser) {
    this.feedbackUser = feedbackUser;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getLastDate() {
    return lastDate;
  }

  public void setLastDate(String lastDate) {
    this.lastDate = lastDate;
  }

  public String getRequestStatus() {
    return requestStatus;
  }

  public void setRequestStatus(String requestStatus) {
    this.requestStatus = requestStatus;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
