/**
 * 
 */
package com.sp.web.form;

import com.sp.web.model.GrowthFeedbackResponse;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @author pradeep
 *
 */
public class GrowthFeedbackForm {

  @NotNull
  private List<GrowthFeedbackResponse> feedbackResponses;

  @NotNull
  private String growthRequestId;

  @NotNull
  private String createdOn;

  /**
   * @param feedbackResponses
   *          the feedbackResponses to set
   */
  public void setFeedbackResponses(List<GrowthFeedbackResponse> feedbackResponses) {
    this.feedbackResponses = feedbackResponses;
  }

  /**
   * @return the feedbackResponses
   */
  public List<GrowthFeedbackResponse> getFeedbackResponses() {
    return feedbackResponses;
  }

  /**
   * @param createdOn
   *          the createdOn to set
   */
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * @param growthRequestId
   *          the growthRequestId to set
   */
  public void setGrowthRequestId(String growthRequestId) {
    this.growthRequestId = growthRequestId;
  }

  /**
   * @return the createdOn
   */
  public String getCreatedOn() {
    return createdOn;
  }

  /**
   * @return the growthRequestId
   */
  public String getGrowthRequestId() {
    return growthRequestId;
  }
}
