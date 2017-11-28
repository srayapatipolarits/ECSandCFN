package com.sp.web.model.competency;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The model to store the competency evaluation score summary.
 */
public class BaseCompetencyEvaluationScore implements Serializable {
  
  private static final long serialVersionUID = 6142280111495853220L;
  private double score;
  private double[] scoreArray;
  @JsonIgnore
  private String competencyEvaluationScoreDetailsId;
  
  /**
   * Default constructor.
   */
  public BaseCompetencyEvaluationScore() {
    super();
  }
  
  /**
   * Constructor from competency evaluation details.
   * 
   * @param competencyEvaluationDetails
   *          - competency evaluation details
   */
  public BaseCompetencyEvaluationScore(UserCompetencyEvaluationDetails competencyEvaluationDetails) {
    updateFrom(competencyEvaluationDetails);
  }
  
  /**
   * Update the data from the given competency evaluation details.
   * 
   * @param competencyEvaluationDetails
   *          - competency evaluation details
   */
  public void updateFrom(UserCompetencyEvaluationDetails competencyEvaluationDetails) {
    this.competencyEvaluationScoreDetailsId = competencyEvaluationDetails.getId();
    this.score = competencyEvaluationDetails.getTotalScore();
    this.scoreArray = competencyEvaluationDetails.getScoreArray();
  }
  
  public String getCompetencyEvaluationScoreDetailsId() {
    return competencyEvaluationScoreDetailsId;
  }
  
  public void setCompetencyEvaluationScoreDetailsId(String competencyEvaluationScoreDetailsId) {
    this.competencyEvaluationScoreDetailsId = competencyEvaluationScoreDetailsId;
  }
  
  public double getScore() {
    return score;
  }
  
  public void setScore(double score) {
    this.score = score;
  }

  public double[] getScoreArray() {
    return scoreArray;
  }

  public void setScoreArray(double[] scoreArray) {
    this.scoreArray = scoreArray;
  }
  
  public boolean isCompleted() {
    return (getScore() > 0);
  }
  
}
