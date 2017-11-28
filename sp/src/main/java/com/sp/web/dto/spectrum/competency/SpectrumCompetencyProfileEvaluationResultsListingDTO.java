package com.sp.web.dto.spectrum.competency;

import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the listing for the spectrum competency profiles.
 */
public class SpectrumCompetencyProfileEvaluationResultsListingDTO implements Serializable {
  
  private static final long serialVersionUID = -7817475739867543930L;
  private String id;
  private String name;

  /**
   * Constructor.
   * 
   * @param evaluationResults
   *              - evaluation results
   */
  public SpectrumCompetencyProfileEvaluationResultsListingDTO(
      SpectrumCompetencyProfileEvaluationResults evaluationResults) {
    this.id = evaluationResults.getCompetencyProfileId();
    this.name = evaluationResults.getName();
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
}
