package com.sp.web.model.competency;

import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the competency evaluation for a particular competency profile.
 */
public class CompetencyEvaluationByProfile implements Serializable {
  
  private static final long serialVersionUID = -8802917360347711078L;
  private CompetencyProfileSummaryDTO competencyProfile;
  private Map<String, UserEvaluationResult> userEvaluationMap;
  
  /**
   * Default constructor.
   */
  public CompetencyEvaluationByProfile() { /* default constructor */
  }
  
  /**
   * Constructor from competency profile.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param competencyFactory
   *          - competency factory
   */
  public CompetencyEvaluationByProfile(String competencyProfileId,
      CompetencyFactory competencyFactory) {
    this(competencyFactory.getCompetencyProfile(competencyProfileId));
  }
  
  /**
   * Creating the competency evaluation by profile using the given competency profile.
   * 
   * @param competencyProfile
   *          - the competency profile
   */
  public CompetencyEvaluationByProfile(CompetencyProfileDao competencyProfile) {
    this.competencyProfile = new CompetencyProfileSummaryDTO(competencyProfile);
    this.userEvaluationMap = new HashMap<String, UserEvaluationResult>();
  }
  
  public Map<String, UserEvaluationResult> getUserEvaluationMap() {
    return userEvaluationMap;
  }
  
  public void setUserEvaluationMap(Map<String, UserEvaluationResult> userEvaluationMap) {
    this.userEvaluationMap = userEvaluationMap;
  }
  
  public CompetencyProfileSummaryDTO getCompetencyProfile() {
    return competencyProfile;
  }
  
  public void setCompetencyProfile(CompetencyProfileSummaryDTO competencyProfile) {
    this.competencyProfile = competencyProfile;
  }
  
  /**
   * Update the user evaluation result.
   * 
   * @param type
   *          - evaluation type
   * @param userId
   *          - user
   * @param scoreArray
   *          - score array
   * @param competencyFactory
   *          - competency factory
   * @param reviewer
   *          - reviewer
   * @param competencyEvaluaitonId
   *          - competency evaluation id
   */
  public void updateEvaluationResult(EvaluationType type, String userId, double[] scoreArray,
      CompetencyFactory competencyFactory, User reviewer, String competencyEvaluaitonId) {
    // validate the competency and score
    validateCompetencyAndScore(scoreArray);
    UserEvaluationResult evaluationResult = userEvaluationMap.get(userId);
    if (evaluationResult == null) {
      if (type == EvaluationType.Self) {
        evaluationResult = new UserEvaluationResult(reviewer);
        userEvaluationMap.put(userId, evaluationResult);
      } else {
        throw new InvalidRequestException("Evaluation not initiated.");
      }
    }
    evaluationResult.update(type, competencyFactory.createCompetencyEvaluationDetails(scoreArray,
        userId, reviewer.getId(), competencyProfile.getId(), competencyEvaluaitonId, type));
  }
  
  /**
   * Validate if the competency evaluation score and profile match.
   * 
   * @param competencyEvaluationByProfile
   *          - competency evaluation by profile
   * @param scoreArray
   *          - score array
   */
  private void validateCompetencyAndScore(double[] scoreArray) {
    Assert.isTrue(competencyProfile.getCompetencyList().size() == scoreArray.length,
        "Score mismatch from competency profile.");
  }
  
  /**
   * Find or create a user evaluation result.
   * 
   * @param userFor
   *          - user for
   * @return user evaluation result
   */
  public UserEvaluationResult findOrCreateUserEvaluationResult(User userFor) {
    UserEvaluationResult userEvaluationResult = userEvaluationMap.get(userFor.getId());
    if (userEvaluationResult == null) {
      userEvaluationResult = new UserEvaluationResult(userFor);
      userEvaluationMap.put(userFor.getId(), userEvaluationResult);
    }
    return userEvaluationResult;
  }
  
  /**
   * Add a new user competency evaluation result for the given user.
   * 
   * @param member
   *          - user
   * @return 
   *      the newly created user evaluation result 
   */
  public UserEvaluationResult addUserCompetencyResult(User member) {
    final UserEvaluationResult userEvaluationResult = new UserEvaluationResult(member);
    userEvaluationMap.put(member.getId(), userEvaluationResult);
    return userEvaluationResult;
  }
  
  /**
   * Get the user evaluation result for the given user id.
   * 
   * @param userId
   *          - user id
   * @return the user evaluation result
   */
  public UserEvaluationResult findUserEvaluationResult(String userId) {
    return userEvaluationMap.get(userId);
  }
  
}
