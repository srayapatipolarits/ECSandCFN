package com.sp.web.dto.lndfeedback;

import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.user.UserFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * DevelopmentFeedbackDTO contains the detail of the feedback.
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedbackDTO extends BaseDevelopmentFeedbackDTO {
  
  private SPFeature spFeature;
  
  private Map<String, Object> dataMap;
  
  private SPGoal devFeedback;
  
  /**
   * Constructor for the feedbackDTO
   * 
   * @param devFeedback
   *          is the feedback detail.
   * @param user
   *          is the user who has requested the feedback.
   */
  public DevelopmentFeedbackDTO(DevelopmentFeedback devFeedback, User user, UserFactory userFactory) {
    super(devFeedback, user, userFactory);
    this.spFeature = SPFeature.valueOf(devFeedback.getSpFeature());
  }
  
  public void setDataMap(Map<String, Object> dataMap) {
    this.dataMap = dataMap;
  }
  
  public Map<String, Object> getDataMap() {
    if (dataMap == null) {
      dataMap = new HashMap<String, Object>();
    }
    return dataMap;
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  public void setDevFeedback(SPGoal devFeedback) {
    this.devFeedback = devFeedback;
  }
  
  public SPGoal getDevFeedback() {
    return devFeedback;
  }
  
  @Override
  public String toString() {
    return "DevelopmentFeedbackDTO [spFeature=" + spFeature + ", dataMap=" + dataMap
        + ", devFeedback=" + devFeedback + ", getId()=" + getId() + ", getDevFeedRefId()="
        + getDevFeedRefId() + ", getRequestStatus()=" + getRequestStatus() + ", getCreatedOn()="
        + getCreatedOn() + ", getRepliedOn()=" + getRepliedOn() + ", getResponse()="
        + getResponse() + ", getComment()=" + getComment() + ", getFeedParentRefId()="
        + getFeedParentRefId() + ", getFeedbackUserDto()=" + getFeedbackUserDto() + ", getUser()="
        + getUser() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
        + super.toString() + "]";
  }
  
}
