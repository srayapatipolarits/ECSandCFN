package com.sp.web.dto;

import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The DTO class to store the practice area details.
 */
public class PracticeAreaDetailsDTO extends PracticeAreaDTO {
  
  private static final long serialVersionUID = 7329981882333494040L;
  private GoalStatus status;
  private List<DevelopmentStrategy> developmentStrategyList;
  
  /**
   * Default constructor.
   * 
   * @param spGoal
   *          - the goal to send
   */
  public PracticeAreaDetailsDTO(SPGoal spGoal) {
    super(spGoal);
   // this.developmentStrategyList = spGoal.getDevelopmentStrategyList();
  }

  public GoalStatus getStatus() {
    return status;
  }

  public void setStatus(GoalStatus status) {
    this.status = status;
  }

  public List<DevelopmentStrategy> getDevelopmentStrategyList() {
    return developmentStrategyList;
  }

  public void setDevelopmentStrategyList(List<DevelopmentStrategy> developmentStrategyList) {
    this.developmentStrategyList = developmentStrategyList;
  }
  
}
