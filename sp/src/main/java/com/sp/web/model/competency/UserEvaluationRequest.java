package com.sp.web.model.competency;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The model class to store the user evaluation request.
 */
public class UserEvaluationRequest implements Serializable {
  
  private static final long serialVersionUID = -3257187855689776056L;
  private String competencyProfileId;
  private String userId;
  private EvaluationType type;
  
  /**
   * Default constructor. 
   */
  public UserEvaluationRequest() {
    super();
  }

  /**
   * Constructor from competency profile, user id and type.
   * 
   * @param competencyProfileId
   *              - competency profile id
   * @param userId
   *              - user id
   * @param type
   *              - type
   */
  public UserEvaluationRequest(String competencyProfileId, String userId, EvaluationType type) {
    this.competencyProfileId = competencyProfileId;
    this.userId = userId;
    this.type = type;
  }

  public String getCompetencyProfileId() {
    return competencyProfileId;
  }
  
  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public EvaluationType getType() {
    return type;
  }
  
  public void setType(EvaluationType type) {
    this.type = type;
  }
  
}
