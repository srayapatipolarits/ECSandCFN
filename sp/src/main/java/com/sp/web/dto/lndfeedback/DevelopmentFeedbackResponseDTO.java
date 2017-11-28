package com.sp.web.dto.lndfeedback;

import com.sp.web.model.SPFeature;
import com.sp.web.model.lndfeedback.DevelopmentFeedbackResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the Development Feedback Responses.
 */
public class DevelopmentFeedbackResponseDTO {

  private String parentTitle;
  private String title;
  private SPFeature spFeature;
  private List<BaseDevelopmentFeedbackResponseDTO> feedbackResponseList;
  
  /**
   * Constructor.
   * 
   * @param developmentFeedbackResponse
   *                - development feedback response
   */
  public DevelopmentFeedbackResponseDTO(DevelopmentFeedbackResponse developmentFeedbackResponse) {
    spFeature = developmentFeedbackResponse.getSpFeature();
    feedbackResponseList = new ArrayList<BaseDevelopmentFeedbackResponseDTO>();
  }

  public SPFeature getSpFeature() {
    return spFeature;
  }

  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }

  public List<BaseDevelopmentFeedbackResponseDTO> getFeedbackResponseList() {
    return feedbackResponseList;
  }

  public void setFeedbackResponseList(List<BaseDevelopmentFeedbackResponseDTO> feedbackResponseList) {
    this.feedbackResponseList = feedbackResponseList;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getParentTitle() {
    return parentTitle;
  }

  public void setParentTitle(String parentTitle) {
    this.parentTitle = parentTitle;
  }
  
}
