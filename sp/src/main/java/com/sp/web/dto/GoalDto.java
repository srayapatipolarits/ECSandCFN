package com.sp.web.dto;

import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pradeep
 *
 *         The goal DTO.
 */
public class GoalDto extends BaseGoalDto {
  
  private static final long serialVersionUID = -4395681734967964280L;
  
  private GoalCategory category;
  
  private boolean alreadyInvited;
  
  private GoalStatus status;
  
  private boolean isMandatory;
  
  private List<CompanyDTO> companyDTO;
  
  private boolean allAccounts;
  
  private List<DevelopmentStrategy> developmentStrategyList;
  
  /**
   * Constructor.
   * 
   * @param spGoal
   *          - goal
   */
  public GoalDto(SPGoal spGoal) {
    super(spGoal);
    this.developmentStrategyList = getDevelopmentStrategyList().stream()
        .filter(DevelopmentStrategy::isActive).collect(Collectors.toList());
  }
  
  public GoalCategory getCategory() {
    return category;
  }
  
  public void setCategory(GoalCategory category) {
    this.category = category;
  }
  
  public void setAlreadyInvited(boolean alreadyInvited) {
    this.alreadyInvited = alreadyInvited;
  }
  
  public boolean isAlreadyInvited() {
    return alreadyInvited;
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public boolean isMandatory() {
    return isMandatory;
  }
  
  public void setMandatory(boolean isMandatory) {
    this.isMandatory = isMandatory;
  }
  
  public void setAllAccounts(boolean allAccounts) {
    this.allAccounts = allAccounts;
  }
  
  public boolean isAllAccounts() {
    return allAccounts;
  }
  
  public void setCompanyDTO(List<CompanyDTO> companyDTO) {
    this.companyDTO = companyDTO;
  }
  
  public List<CompanyDTO> getCompanyDTO() {
    return companyDTO;
  }
  
  public void setDevelopmentStrategyList(List<DevelopmentStrategy> developmentStrategyList) {
    this.developmentStrategyList = developmentStrategyList;
  }
  
  public List<DevelopmentStrategy> getDevelopmentStrategyList() {
    return developmentStrategyList;
  }
  
  @Override
  public String toString() {
    return "GoalDto [description=" + getDescription() + ", category=" + category + ", alreadyInvited="
        + alreadyInvited + ", status=" + status + ", isMandatory=" + isMandatory + "]";
  }
}
