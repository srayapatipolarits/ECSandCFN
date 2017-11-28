package com.sp.web.dto.competency;

import com.sp.web.Constants;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The competency evaluation DTO object.
 */
public class BaseCompetencyEvaluationDTO implements Serializable {
  
  private static final long serialVersionUID = -6829222955895588652L;
  private String id;
  private String startDate;
  private String endDate;
  private List<EvaluationType> requiredEvaluationList;
  
  /**
   * Constructor from competency evaluation.
   * 
   * @param competencyEvaluation
   *                - competency evaluation
   */
  public BaseCompetencyEvaluationDTO(CompetencyEvaluation competencyEvaluation) {
    BeanUtils.copyProperties(competencyEvaluation, this);
    this.startDate = MessagesHelper.formatDate(competencyEvaluation.getStartDate(), Constants.DATE_FORMAT_COMPETENCY);
    if (competencyEvaluation.isCompleted()) {
      this.endDate = MessagesHelper.formatDate(competencyEvaluation.getEndedOn(), Constants.DATE_FORMAT_COMPETENCY);
    } else {
      this.endDate = MessagesHelper.formatDate(competencyEvaluation.getEndDate(), Constants.DATE_FORMAT_COMPETENCY);
    }
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getStartDate() {
    return startDate;
  }
  
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }
  
  public String getEndDate() {
    return endDate;
  }
  
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public List<EvaluationType> getRequiredEvaluationList() {
    return requiredEvaluationList;
  }

  public void setRequiredEvaluationList(List<EvaluationType> requiredEvaluationList) {
    this.requiredEvaluationList = requiredEvaluationList;
  }
  
}
