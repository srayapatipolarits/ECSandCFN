package com.sp.web.model.readability;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 * Readability DB Entity Bean
 * 
 * @author Prasanna Venkatesh
 *
 */

public class ReadabilityScore implements Serializable{


  private static final long serialVersionUID = 1L;
  
  private String id;
  
  private double currentScore;
  
  private LocalDate calculatedOn;
  
  private ArrayList<ReadabilityScore> scoreHistory;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public ArrayList<ReadabilityScore> getScoreHistory() {
    if (scoreHistory == null) {
      scoreHistory = new ArrayList<ReadabilityScore>();
    }
    return scoreHistory;
  }

  public void setScoreHistory(ArrayList<ReadabilityScore> scoreHistory) {
    this.scoreHistory = scoreHistory;
  }

  
}
