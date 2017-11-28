package com.sp.web.dto.competency;

import com.sp.web.model.User;
import com.sp.web.model.competency.UserCompetencyEvaluation;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class to store the competency evaluation and competency profile for a given user.
 */
public class CompetencyEvaluationViewByUserDTO extends CompetencyEvaluationUserDTO {
  
  private static final long serialVersionUID = 6116981514776431472L;
  private String competencyEvaluationId;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param userEvaluation
   *          - user evaluation
   * @param competencyEvaluationId
   *          - competency profile
   */
  public CompetencyEvaluationViewByUserDTO(User user, UserCompetencyEvaluation userEvaluation,
      String competencyEvaluationId) {
    super(user, userEvaluation);
    this.competencyEvaluationId = competencyEvaluationId;
  }
  
  public String getCompetencyEvaluationId() {
    return competencyEvaluationId;
  }

  public void setCompetencyEvaluationId(String competencyEvaluationId) {
    this.competencyEvaluationId = competencyEvaluationId;
  }
  
}
