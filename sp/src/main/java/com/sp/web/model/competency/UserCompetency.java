package com.sp.web.model.competency;

import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The model class to store the users competency evaluation details.
 */
public class UserCompetency {
  
  private String id;
  private String userId;
  private String companyId;
  private Map<String, UserCompetencyEvaluation> evaluationsMap;
  private String lastCompetencyEvaluationId;
  private boolean showSelfEvaluations;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public Map<String, UserCompetencyEvaluation> getEvaluationsMap() {
    return evaluationsMap;
  }
  
  public void setEvaluationsMap(Map<String, UserCompetencyEvaluation> evaluationsMap) {
    this.evaluationsMap = evaluationsMap;
  }

  public String getLastCompetencyEvaluationId() {
    return lastCompetencyEvaluationId;
  }

  public void setLastCompetencyEvaluationId(String lastCompetencyEvaluationId) {
    this.lastCompetencyEvaluationId = lastCompetencyEvaluationId;
  }

  public UserCompetencyEvaluation getEvaluation(String evalautionId) {
    return evaluationsMap.get(evalautionId);
  }

  public UserCompetencyEvaluation addEvaluation(String competencyEvaluationId,
      String competencyProfileId) {
    return evaluationsMap.computeIfAbsent(competencyEvaluationId,
        k -> new UserCompetencyEvaluation(competencyProfileId));
  }

  public UserCompetencyEvaluation removeEvaluation(String competencyEvaluationId) {
    return evaluationsMap.remove(competencyEvaluationId);
  }

  public boolean isShowSelfEvaluations() {
    return showSelfEvaluations;
  }

  public void setShowSelfEvaluations(boolean showSelfEvaluations) {
    this.showSelfEvaluations = showSelfEvaluations;
  }

  /**
   * Update the show self evaluation flag if the self evaluation supported is set to true.
   * Only sets the flag if the flag was previously false else it does not update the status.
   * 
   * @param selfEvaluationSupported
   *            - self evaluation supported flag
   */
  public void updateShowSelfEvaluation(boolean selfEvaluationSupported) {
    if (!showSelfEvaluations && selfEvaluationSupported) {
      showSelfEvaluations = true;
    }
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
}
