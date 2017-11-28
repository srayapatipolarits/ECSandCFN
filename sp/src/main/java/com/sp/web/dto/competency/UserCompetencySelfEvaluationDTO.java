package com.sp.web.dto.competency;

import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the user competency evaluation details.
 */
public class UserCompetencySelfEvaluationDTO implements Serializable {
  
  private static final long serialVersionUID = 5767510290906926977L;
  private LocalDateTime startDate;
  private CompetencyProfileSummaryDTO competencyProfile;
  private double score;
  private List<CompetencyScoreDTO> competencyScores;
  
  /**
   * Constructor.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   * @param competencyProfile
   *          - competency profile
   */
  public UserCompetencySelfEvaluationDTO(CompetencyEvaluation competencyEvaluation,
      CompetencyProfileSummaryDTO competencyProfile) {
    this.startDate = competencyEvaluation.getStartDate();
    this.competencyProfile = competencyProfile;
  }
  
  public LocalDateTime getStartDate() {
    return startDate;
  }
  
  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }
  
  public double getScore() {
    return score;
  }
  
  public void setScore(double score) {
    this.score = score;
  }
  
  public List<CompetencyScoreDTO> getCompetencyScores() {
    return competencyScores;
  }
  
  public void setCompetencyScores(List<CompetencyScoreDTO> competencyScores) {
    this.competencyScores = competencyScores;
  }
  
  public CompetencyProfileSummaryDTO getCompetencyProfile() {
    return competencyProfile;
  }

  public void setCompetencyProfile(CompetencyProfileSummaryDTO competencyProfile) {
    this.competencyProfile = competencyProfile;
  }
  
  /**
   * Update the scores with the given user competency evaluation.
   * 
   * @param evaluation
   *            - user evaluation details
   * @param competencyProfile
   *            - competency profile
   */
  public void add(UserCompetencyEvaluationDetails evaluation,
      CompetencyProfileSummaryDTO competencyProfile) {
    this.score = evaluation.getTotalScore();
    final List<CompetencySummaryDTO> competencyList = competencyProfile.getCompetencyList();
    competencyScores = new ArrayList<CompetencyScoreDTO>(competencyList.size());
    int index = 0;
    final double[] scoreArray = evaluation.getScoreArray();
    for (CompetencySummaryDTO competency : competencyList) {
      competencyScores.add(new CompetencyScoreDTO(competency, scoreArray[index++]));
    }
  }
}
