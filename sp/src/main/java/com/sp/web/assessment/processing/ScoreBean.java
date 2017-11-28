package com.sp.web.assessment.processing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author daxabraham
 * 
 *         The enveloping class for the scoring bean.
 */
public class ScoreBean {

  private double score = 0d;

  public ScoreBean() { }
  
  public ScoreBean(double score) {
    this.score = score;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  /**
   * Increment the score.
   * 
   * @param dblScore
   *          - the score to increment by
   */
  public void increment(double dblScore) {
    this.score += dblScore;
  }

  /**
   * Decrease the score.
   * 
   * @param dblScore
   *          - value to decrease the score by
   */
  public void decrease(double dblScore) {
    this.score -= dblScore;
  }

  /**
   * Add the score bean.
   * 
   * @param scoreBean
   *          - score to add
   * @return - added score
   */
  public double add(ScoreBean scoreBean) {
    return score + scoreBean.getScore();
  }

  public int getIntScore() {
    return BigDecimal.valueOf(score).setScale(0, RoundingMode.HALF_UP).intValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return score + "";
  }
}
