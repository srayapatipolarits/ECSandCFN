package com.sp.web.model.spectrum.competency;

import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity to store the competency evaluation results by competency profile for spectrum.
 */
public class SpectrumCompetencyProfileEvaluationResults implements Serializable {
  
  private static final long serialVersionUID = 5274086791963666714L;
  private String id;
  private String competencyProfileId;
  private String name;
  private String companyId;
  private List<SpectrumCompetencyEvaluationSummary> evaluationResults;
  
  /**
   * Default Constructor.
   */
  public SpectrumCompetencyProfileEvaluationResults() {
  }
  
  /**
   * Constructor.
   * 
   * @param competencyProfile
   *          - competency profile
   * @param companyId
   *          - company id
   */
  public SpectrumCompetencyProfileEvaluationResults(CompetencyProfileSummaryDTO competencyProfile,
      String companyId) {
    this.competencyProfileId = competencyProfile.getId();
    this.name = competencyProfile.getName();
    this.companyId = companyId;
    evaluationResults = new ArrayList<SpectrumCompetencyEvaluationSummary>();
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getCompetencyProfileId() {
    return competencyProfileId;
  }
  
  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public List<SpectrumCompetencyEvaluationSummary> getEvaluationResults() {
    return evaluationResults;
  }
  
  public void setEvaluationResults(List<SpectrumCompetencyEvaluationSummary> evaluationResults) {
    this.evaluationResults = evaluationResults;
  }
  
  public SpectrumCompetencyProfileEvaluationResults add(
      SpectrumCompetencyEvaluationSummary evaluationSummary) {
    evaluationResults.add(evaluationSummary);
    return this;
  }
  
  /**
   * Get the evaluation result object for the given competency evaluation id.
   * 
   * @param competencyEvaluationId
   *          - competency evaluation id
   * @return the optional spectrum competency evaluation summary object
   */
  public Optional<SpectrumCompetencyEvaluationSummary> getEvaluationResult(
      String competencyEvaluationId) {
    return evaluationResults.stream()
        .filter(e -> e.getCompetencyEvaluationId().equals(competencyEvaluationId)).findFirst();
  }

  /**
   * Remove the evaluation from the spectrum summary if found.
   * 
   * @param competencyEvaluationId
   *              - competency evaluation id to remove
   * @return
   *    true if removed else false
   */
  public boolean removeEvaluation(String competencyEvaluationId) {
    return getEvaluationResult(competencyEvaluationId).map(evaluationResults::remove).orElse(false);
  }

  /**
   * Check if there are any evaluations for the given profile.
   * 
   * @return
   *    true if evaluations exists else false
   */
  public boolean hasEvaluations() {
    return !evaluationResults.isEmpty();
  }
}
