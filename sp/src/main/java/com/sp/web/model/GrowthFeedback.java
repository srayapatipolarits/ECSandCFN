package com.sp.web.model;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Growth Feedback contains the feedback data given by the user.
 * 
 * @author pradeep
 *
 */
public class GrowthFeedback implements Serializable {

  /**
   * Default serial vesrion id.
   */
  private static final long serialVersionUID = 4861596702914066825L;

  /** Feedback created on. */
  private LocalDate createdOn;

  List<GrowthFeedbackResponse> responseFeedbacks;

  /** check if feedback is give by the user or not */
  private boolean feedbackGive;

  /**
   * constructor for the growth feedback.
   * 
   * @param createdOn
   *          feebdack frequencey created on.
   */
  public GrowthFeedback(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * feedback on which it is given.
   * 
   * @return the createdOn
   */
  public LocalDate getCreatedOn() {
    return createdOn;
  }

  /**
   * set the date.
   * 
   * @param createdOn
   *          the createdOn to set
   */
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * list of response feedbacks.
   * 
   * @return the responseFeedbacks
   */
  public List<GrowthFeedbackResponse> getResponseFeedbacks() {
    if (responseFeedbacks == null) {
      responseFeedbacks = new ArrayList<GrowthFeedbackResponse>();
    }
    return responseFeedbacks;
  }

  /**
   * set the response feedbacks.
   * 
   * @param responseFeedbacks
   *          the responseFeedbacks to set
   */
  public void setResponseFeedbacks(List<GrowthFeedbackResponse> responseFeedbacks) {
    this.responseFeedbacks = responseFeedbacks;
  }

  /**
   * @return the feedbackGive
   */
  public boolean isFeedbackGive() {
    feedbackGive = CollectionUtils.isEmpty(getResponseFeedbacks()) ? Boolean.FALSE : Boolean.TRUE;
    return feedbackGive;
  }
}
