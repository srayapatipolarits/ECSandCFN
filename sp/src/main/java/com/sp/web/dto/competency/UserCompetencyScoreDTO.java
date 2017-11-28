package com.sp.web.dto.competency;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to store the user and score information.
 */
public class UserCompetencyScoreDTO {
  
  private BaseUserDTO reviewer;
  private double score;
  
  /**
   * Constructor from base competency evaluation score.
   * 
   * @param evaluation
   *          - evaluation
   */
  public UserCompetencyScoreDTO(BaseCompetencyEvaluationScore evaluation) {
    this.score = evaluation.getScore();
  }
  
  /**
   * Constructor from user evaluation score.
   * 
   * @param evaluation
   *          - evaluation
   */
  public UserCompetencyScoreDTO(UserCompetencyEvaluationScore evaluation) {
    this((BaseCompetencyEvaluationScore) evaluation);
    this.reviewer = evaluation.getReviewer();
  }
  
  public BaseUserDTO getReviewer() {
    return reviewer;
  }
  
  public void setReviewer(BaseUserDTO reviewer) {
    this.reviewer = reviewer;
  }
  
  public double getScore() {
    return score;
  }
  
  public void setScore(double score) {
    this.score = score;
  }
  
}
