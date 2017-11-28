package com.sp.web.dto.competency;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to store the competency name and score.
 */
public class CompetencyScoreDTO implements Serializable {
  
  private static final long serialVersionUID = 169878290036946939L;
  private String name;
  private double score;
  
  
  /**
   * Default Constructor.
   */
  public CompetencyScoreDTO() { }

  /**
   * Constructor from competency and score.
   * 
   * @param competency
   *            - competency
   * @param score
   *            - score
   */
  public CompetencyScoreDTO(CompetencySummaryDTO competency, double score) {
    this.name = competency.getName();
    this.score = score;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public double getScore() {
    return score;
  }
  
  public void setScore(double score) {
    this.score = score;
  }
}
