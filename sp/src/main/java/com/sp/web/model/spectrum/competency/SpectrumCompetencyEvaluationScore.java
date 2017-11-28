package com.sp.web.model.spectrum.competency;

import com.sp.web.Constants;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity to store the competency evaluation score.
 */
public class SpectrumCompetencyEvaluationScore implements Serializable {
  
  private static final long serialVersionUID = -7318446356651841853L;
  private int completedCount;
  private int count;
  private double score;
  private double[] competencyScores;
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  public double getScore() {
    return score;
  }
  
  public void setScore(double score) {
    this.score = score;
  }
  
  public double[] getCompetencyScores() {
    return competencyScores;
  }
  
  public void setCompetencyScores(double[] competencyScores) {
    this.competencyScores = competencyScores;
  }
  
  /**
   * Add the given user score to the evaluation score.
   * 
   * @param userCompetencyEvaluationDetails
   *          - user score to add
   */
  public void add(UserCompetencyEvaluationDetails userCompetencyEvaluationDetails) {
    count++;
    if (userCompetencyEvaluationDetails != null) {
      completedCount++;
      score += userCompetencyEvaluationDetails.getTotalScore();
      double[] scoreArray = userCompetencyEvaluationDetails.getScoreArray();
      if (getCompetencyScores() == null) {
        setCompetencyScores(new double[scoreArray.length]);
        Arrays.fill(getCompetencyScores(), 0d);
      }
      
      for (int i = 0; i < scoreArray.length; i++) {
        getCompetencyScores()[i] += scoreArray[i];
      }
    }
  }
  
  /**
   * Averaging the results.
   */
  public void doAverage() {
    score = doAverage(score);
    if (getCompetencyScores() != null) {
      for (int i = 0; i < getCompetencyScores().length; i++) {
        getCompetencyScores()[i] = doAverage(getCompetencyScores()[i]);
      }
    }
  }
  
  private double doAverage(double scoreToAverage) {
    return (completedCount == 0) ? 0 : BigDecimal.valueOf(scoreToAverage / completedCount)
        .setScale(Constants.PRECISION, Constants.ROUNDING_MODE).doubleValue();
  }
  
  public int getCompletedCount() {
    return completedCount;
  }
  
  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
}
