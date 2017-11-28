package com.sp.web.model.competency;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The entity class to store competency evaluation for a company.
 */
public class CompanyCompetencyEvaluation implements Serializable {
  
  private static final long serialVersionUID = 3702027709431257861L;
  private String currentEvaluationId;
  private String lastCompetencyEvaluationId;
  
  public String getCurrentEvaluationId() {
    return currentEvaluationId;
  }
  
  public void setCurrentEvaluationId(String currentEvaluationId) {
    this.currentEvaluationId = currentEvaluationId;
  }
  
  public String getLastCompetencyEvaluationId() {
    return lastCompetencyEvaluationId;
  }
  
  public void setLastCompetencyEvaluationId(String lastCompetencyEvaluationId) {
    this.lastCompetencyEvaluationId = lastCompetencyEvaluationId;
  }
  
}
