package com.sp.web.model.competency;

import com.sp.web.Constants;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * @author Dax Abraham
 * 
 *         The model class to store the competency evaluation details.
 */
public class UserCompetencyEvaluationDetails implements Serializable {
  
  private static final long serialVersionUID = 4285983003224553402L;
  private String id;
  private String userId;
  private String reviewerId;
  private String competencyId;
  private String evaluationId;
  private LocalDateTime createdOn;
  private double[] scoreArray;
  private double totalScore;
  private EvaluationType evaluationType;
  
  /**
   * Default constructor.
   */
  public UserCompetencyEvaluationDetails() {
    super();
  }
  
  /**
   * Constructor for score array.
   * 
   * @param scoreArray
   *          - score array
   * @param userId
   *          - user id
   * @param reviewerId
   *          - reviewer id
   * @param competencyProfileId
   *          - competency profile id
   * @param competnecyEvaluationId
   *          - competency evaluation id         
   * @param type
   *          - evaluation type         
   */
  public UserCompetencyEvaluationDetails(double[] scoreArray, String userId, String reviewerId,
      String competencyProfileId, String competnecyEvaluationId, EvaluationType type) {
    // validating if the score array is present and has at least one element
    Assert.notNull(scoreArray, "Score not found.");
    Assert.isTrue(scoreArray.length > 0, "Score not found.");
    this.scoreArray = scoreArray;
    // getting the total score
    OptionalDouble average = Arrays.stream(scoreArray).average();
    totalScore = BigDecimal.valueOf(average.orElse(0d))
        .setScale(Constants.PERCENT_PRECISION, Constants.ROUNDING_MODE).doubleValue();
    // setting the created on
    this.createdOn = LocalDateTime.now();
    // set the user id
    this.userId = userId;
    this.reviewerId = reviewerId;
    this.competencyId = competencyProfileId;
    this.evaluationId = competnecyEvaluationId;
    this.evaluationType = type;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public double[] getScoreArray() {
    return scoreArray;
  }
  
  public void setScoreArray(double[] scoreArray) {
    this.scoreArray = scoreArray;
  }
  
  public double getTotalScore() {
    return totalScore;
  }
  
  public void setTotalScore(double totalScore) {
    this.totalScore = totalScore;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getReviewerId() {
    return reviewerId;
  }
  
  public void setReviewerId(String reviewerId) {
    this.reviewerId = reviewerId;
  }
  
  public String getCompetencyId() {
    return competencyId;
  }
  
  public void setCompetencyId(String competencyId) {
    this.competencyId = competencyId;
  }

  public String getEvaluationId() {
    return evaluationId;
  }

  public void setEvaluationId(String evaluationId) {
    this.evaluationId = evaluationId;
  }

  public EvaluationType getEvaluationType() {
    return evaluationType;
  }

  public void setEvaluationType(EvaluationType evaluationType) {
    this.evaluationType = evaluationType;
  }
  
}
