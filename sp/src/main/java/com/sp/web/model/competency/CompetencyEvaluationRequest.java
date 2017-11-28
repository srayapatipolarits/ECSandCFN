package com.sp.web.model.competency;

import com.sp.web.model.FeedbackUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The model class to store all the competency evaluation requests.
 */
public class CompetencyEvaluationRequest implements Serializable {
  
  private static final long serialVersionUID = 6205545485472085790L;
  private String id;
  private String feedbackUserId;
  private String companyId;
  private List<UserEvaluationRequest> requestsList;
  
  /**
   * Default constructor.
   */
  public CompetencyEvaluationRequest() {
    super();
  }
  
  /**
   * Constructor from feedback user.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  public CompetencyEvaluationRequest(FeedbackUser feedbackUser) {
    this.feedbackUserId = feedbackUser.getId();
    this.companyId = feedbackUser.getCompanyId();
    requestsList = new ArrayList<UserEvaluationRequest>();
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getFeedbackUserId() {
    return feedbackUserId;
  }
  
  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public List<UserEvaluationRequest> getRequestsList() {
    return requestsList;
  }
  
  public void setRequestsList(List<UserEvaluationRequest> requestsList) {
    this.requestsList = requestsList;
  }
  
  /**
   * Add a new evaluation request.
   * 
   * @param competencyProfileId
   *          - competency profile
   * @param userId
   *          - user id
   * @param type
   *          - type
   */
  public void add(String competencyProfileId, String userId, EvaluationType type) {
    UserEvaluationRequest evaluationRequest = new UserEvaluationRequest(competencyProfileId,
        userId, type);
    requestsList.add(evaluationRequest);
  }
  
  /**
   * Remove the given user evaluation request.
   * 
   * @param userEvaluationRequest
   *          - user evaluation request to remove
   */
  public void removeRequest(UserEvaluationRequest userEvaluationRequest) {
    if (requestsList != null) {
      requestsList.remove(userEvaluationRequest);
    }
  }
  
}
