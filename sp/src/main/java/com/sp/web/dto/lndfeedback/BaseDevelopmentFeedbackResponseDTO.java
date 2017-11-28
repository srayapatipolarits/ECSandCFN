package com.sp.web.dto.lndfeedback;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.lndfeedback.DevelopmentFeedbackResponse;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO for the basic development feedback response list.
 */
public class BaseDevelopmentFeedbackResponseDTO {
  
  private UserMarkerDTO user;
  private RequestStatus requestStatus;
  private LocalDateTime repliedOn;
  private String response;
  private String comment;
  
  /**
   * Constructor.
   * 
   * @param feedbackResposne
   *            - feedback response
   */
  public BaseDevelopmentFeedbackResponseDTO(DevelopmentFeedbackResponse feedbackResposne) {
    BeanUtils.copyProperties(feedbackResposne, this);
  }
  
  public UserMarkerDTO getUser() {
    return user;
  }
  
  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
  
  public RequestStatus getRequestStatus() {
    return requestStatus;
  }
  
  public void setRequestStatus(RequestStatus requestStatus) {
    this.requestStatus = requestStatus;
  }
  
  public LocalDateTime getRepliedOn() {
    return repliedOn;
  }
  
  public void setRepliedOn(LocalDateTime repliedOn) {
    this.repliedOn = repliedOn;
  }
  
  public String getResponse() {
    return response;
  }
  
  public void setResponse(String response) {
    this.response = response;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
}
