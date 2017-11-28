package com.sp.web.form.lndfeedback;

import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Development Form for the creating the feedback request.
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentForm implements GenericForm<DevelopmentFeedback> {
  
  private String id;
  
  private String devFeedRefId;
  
  private String response;
  
  private String comment;
  
  private String feedParentRefId;
  
  private String spFeature;
  
  private List<String> feedbackUsers;
  
  private boolean decline;
  
  /**
   * @see com.sp.web.form.generic.GenericForm#validate()
   */
  @Override
  public void validate() {
    Assert.hasText(devFeedRefId, "Development Feedback is blank.");
    Assert.hasText(comment, "Development feedback comment is blank");
    Assert.notNull(spFeature, "SPFeature is not present.");
    Assert.notEmpty(feedbackUsers, "No Feedback Users present.");
  }
  
  /**
   * @see com.sp.web.form.generic.GenericForm#validateUpdate()
   */
  @Override
  public void validateUpdate() {
    Assert.hasText(id, "Development Feedback id  blank.");
    Assert.hasText(devFeedRefId, "Development Feedback is blank.");
    if (!decline) {
      Assert.hasText(response, "Development feedback response is blank");
    }
    Assert.notNull(spFeature, "SPFeature is not present.");
  }
  
  @Override
  public void validateGet() {
    
  }
  
  /**
   * @see com.sp.web.form.generic.GenericForm#create(com.sp.web.model.User)
   */
  @Override
  public DevelopmentFeedback create(User user) {
    DevelopmentFeedback developmentFeedback = new DevelopmentFeedback();
    BeanUtils.copyProperties(this, developmentFeedback);
    developmentFeedback.setUserId(user.getId());
    developmentFeedback.setCreatedOn(LocalDateTime.now());
    developmentFeedback.setRequestStatus(RequestStatus.NOT_INITIATED);
    developmentFeedback.setUpdatedOn(LocalDateTime.now());
    developmentFeedback.setCompanyId(user.getCompanyId());
    return developmentFeedback;
  }
  
  /**
   * @see com.sp.web.form.generic.GenericForm#update(com.sp.web.model.User, java.lang.Object)
   */
  @Override
  public void update(User user, DevelopmentFeedback instanceToUpdate) {
    instanceToUpdate.setRepliedOn(LocalDateTime.now());
    instanceToUpdate.setResponse(response);
    if (decline) {
      instanceToUpdate.setRequestStatus(RequestStatus.DECLINED);
    } else {
      instanceToUpdate.setRequestStatus(RequestStatus.COMPLETED);
    }
    instanceToUpdate.setUpdatedOn(LocalDateTime.now());
  }
  
  public String getDevFeedRefId() {
    return devFeedRefId;
  }
  
  public void setDevFeedRefId(String devFeedRefId) {
    this.devFeedRefId = devFeedRefId;
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
  
  public String getFeedParentRefId() {
    return feedParentRefId;
  }
  
  public void setFeedParentRefId(String feedParentRefId) {
    this.feedParentRefId = feedParentRefId;
  }
  
  public void setSpFeature(String spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getSpFeature() {
    return spFeature;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
  public void setFeedbackUsers(List<String> feedbackUsers) {
    this.feedbackUsers = feedbackUsers;
  }
  
  public List<String> getFeedbackUsers() {
    return feedbackUsers;
  }
  
  public void setDecline(boolean decline) {
    this.decline = decline;
  }
  
  public boolean isDecline() {
    return decline;
  }
}
