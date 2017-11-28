package com.sp.web.model.pulse;

import com.sp.web.assessment.processing.ScoreBean;

/**
 * @author Dax Abraham
 * 
 *         The pulse score bean additionally stores the percentage responded.
 */
public class PulseScoreBean extends ScoreBean {

  private double percentResponded;

  public double getPercentResponded() {
    return percentResponded;
  }

  public void setPercentResponded(double percentResponded) {
    this.percentResponded = percentResponded;
  }
}
