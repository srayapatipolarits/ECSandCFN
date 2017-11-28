package com.sp.web.dto;

import com.sp.web.dto.competency.BaseCompetencyProfileDTO;
import com.sp.web.dto.goal.ActionPlanSummaryDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * PracticeCompOrgViewDTO contains the member view data for competency, practice area and
 * organization plan.
 * 
 * @author pradeepruhil
 *
 */
public class PracticeCompOrgViewDTO {
  
  private List<BaseGoalDto> goals;
  
  private boolean autoLearning;
  
  private BaseCompetencyProfileDTO competencyProfile;
  
  private List<ActionPlanSummaryDTO> actionPlans;
  
  public List<BaseGoalDto> getGoals() {
    return goals;
  }
  
  public void setGoals(List<BaseGoalDto> goals) {
    this.goals = goals;
  }
  
  public boolean isAutoLearning() {
    return autoLearning;
  }
  
  public void setAutoLearning(boolean autoLearning) {
    this.autoLearning = autoLearning;
  }
  
  public BaseCompetencyProfileDTO getCompetencyProfile() {
    return competencyProfile;
  }
  
  public void setCompetencyProfile(BaseCompetencyProfileDTO competencyProfile) {
    this.competencyProfile = competencyProfile;
  }
  
  public List<ActionPlanSummaryDTO> getActionPlans() {
    return actionPlans;
  }
  
  public void setActionPlans(List<ActionPlanSummaryDTO> actionPlans) {
    this.actionPlans = actionPlans;
  }

  /**
   * Get or create a new action plan list.
   * 
   * @return
   *        - action plan list
   */
  public List<ActionPlanSummaryDTO> getOrCreateActionPlans() {
    if (actionPlans == null) {
      actionPlans = new ArrayList<ActionPlanSummaryDTO>();
    }
    return actionPlans;
  }
  
}
