package com.sp.web.model.pulse;

import com.sp.web.assessment.processing.ScoreBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The model class to store the pulse score.
 */
public class PulseScore {

  private double score;
  private ScoreBean totalScore;
  private List<List<PulseScoreBean>> summationList;

  public PulseScore() {
    totalScore = new ScoreBean();
    summationList = new ArrayList<List<PulseScoreBean>>(); 
  }
  
  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public ScoreBean getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(ScoreBean totalScore) {
    this.totalScore = totalScore;
  }

  public List<List<PulseScoreBean>> getSummationList() {
    return summationList;
  }

  public void setSummationList(List<List<PulseScoreBean>> summationList) {
    this.summationList = summationList;
  }
}
