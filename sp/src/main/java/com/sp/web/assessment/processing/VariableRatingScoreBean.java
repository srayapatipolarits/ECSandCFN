package com.sp.web.assessment.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 *
 *         This class stores the values for the variable rating scores.
 */
public class VariableRatingScoreBean extends ScoreBean {
  
  private Map<Integer, ScoreBean> optionsScore;
  
  /**
   * Default constructor.
   */
  public VariableRatingScoreBean() {
    super();
    this.optionsScore = new HashMap<Integer, ScoreBean>();
  }
  
  public Map<Integer, ScoreBean> getOptionsScore() {
    return optionsScore;
  }
  
  public void setOptionsScore(Map<Integer, ScoreBean> optionsScore) {
    this.optionsScore = optionsScore;
  }
  
  /**
   * Increment the score at the given index.
   * 
   * @param index
   *          - index to update
   * @param score
   *          - score to add
   */
  public void increment(int index, double score) {
    ScoreBean scoreBean = optionsScore.computeIfAbsent(index, v -> new ScoreBean());
    scoreBean.increment(score);
  }
  
  /**
   * Get the value for the given index in the options scores.
   * 
   * @param index
   *          -index
   * @return return the value or zero
   */
  public double getScore(int index) {
    return Optional.ofNullable(optionsScore.get(index)).map(ScoreBean::getScore).orElse(0d);
  }

  @Override
  public String toString() {
    return "[optionsScore=" + optionsScore + "]";
  }
}
