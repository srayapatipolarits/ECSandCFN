package com.sp.web.form.competency;

import com.sp.web.dao.CompanyDao;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.EvaluationType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The form class for initiating a competency evaluation.
 */
public class CompetencyEvaluationForm {
  
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endDate;
  private List<EvaluationType> requiredEvaluationList;
  private Set<String> userIds;

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public List<EvaluationType> getRequiredEvaluationList() {
    return requiredEvaluationList;
  }

  public void setRequiredEvaluationList(List<EvaluationType> requiredEvaluationList) {
    this.requiredEvaluationList = requiredEvaluationList;
  }
  
  public Set<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<String> userIds) {
    this.userIds = userIds;
  }
  
  /**
   * Create a new competency evaluation instance from the form data.
   * @param company 
   * 
   * @return
   *    new competency evaluation instance
   */
  public CompetencyEvaluation createCompetencyEvaluation(CompanyDao company) {
    CompetencyEvaluation competencyEvaluation = CompetencyEvaluation.newInstance(company.getId(),
        requiredEvaluationList);
    competencyEvaluation.setEndDate(endDate);
    return competencyEvaluation;
  }

  /**
   * Validate the form data.
   */
  public void validate() {
    Assert.notNull(endDate, "End date required.");
    Assert.isTrue(!endDate.isBefore(LocalDateTime.now()), "End date cannot be before today.");
    Assert.notEmpty(requiredEvaluationList, "At least one evaluation type is required.");
    Assert.notEmpty(userIds, "At least one user required.");
  }
}
