package com.sp.web.model.lndfeedback;

import com.sp.web.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The model class to store all the users development feedback responses.
 */
public class UserDevelopmentFeedbackResponse {
  
  private String id;
  private String userId;
  private Map<String, List<DevelopmentFeedbackResponse>> responseMap;
  private List<String> keyOrder;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public Map<String, List<DevelopmentFeedbackResponse>> getResponseMap() {
    return responseMap;
  }
  
  public void setResponseMap(Map<String, List<DevelopmentFeedbackResponse>> responseMap) {
    this.responseMap = responseMap;
  }
  
  public List<String> getKeyOrder() {
    return keyOrder;
  }
  
  public void setKeyOrder(List<String> keyOrder) {
    this.keyOrder = keyOrder;
  }

  /**
   * Add the given feedback to the users development feedback list.
   * 
   * @param feedback
   *        - development feedback
   * @param user
   *        - user for who the feedback was provided
   */
  public void add(DevelopmentFeedback feedback, User user) {
    final String devFeedRefId = feedback.getDevFeedRefId();
    List<DevelopmentFeedbackResponse> feedbackList = responseMap.get(devFeedRefId);
    if (feedbackList == null) {
      feedbackList = new ArrayList<DevelopmentFeedbackResponse>();
      responseMap.put(devFeedRefId, feedbackList);
    }
    feedbackList.add(new DevelopmentFeedbackResponse(feedback, user));
    keyOrder.remove(devFeedRefId);
    keyOrder.add(0, devFeedRefId);
  }

  public List<DevelopmentFeedbackResponse> getResponse(String key) {
    return responseMap.get(key);
  }
  
}
