package com.sp.web.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Prasanna Venkatesh
 *
 */
public class ReadabilityDTO implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private double currentScore;
  
  private LocalDate calculatedOn;

  public double getCurrentScore() {
    return currentScore;
  }

  public void setCurrentScore(double currentScore) {
    this.currentScore = currentScore;
  }

  public LocalDate getCalculatedOn() {
    return calculatedOn;
  }

  public void setCalculatedOn(LocalDate calculatedOn) {
    this.calculatedOn = calculatedOn;
  }
  
  
  
}
