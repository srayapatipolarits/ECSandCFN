package com.sp.web.dto.goal;

import com.sp.web.dto.GoalDto;
import com.sp.web.dto.UserGoalProgressDto;
import com.sp.web.dto.library.TrainingSpotLightDTO;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.SPGoal;

import java.util.List;

/**
 * @author Pradeep Ruhil
 * 
 *         The DTO class for the practice area list.
 */
public class SPGoalDetailDTO extends GoalDto {
  
  private static final long serialVersionUID = 6791431878729416242L;
  
  private UserGoalProgressDto userGoalsDto;
  
  private List<TrainingSpotLightDTO> spotLightDTOs;
  
  private List<DevelopmentStrategy> developmentStrategyList;
  
  /**
   * Constructor.
   * 
   * @param spGoal
   *          - the sp goal
   */
  public SPGoalDetailDTO(SPGoal spGoal) {
    super(spGoal);
  }
    
  public UserGoalProgressDto getUserGoalsDto() {
    return userGoalsDto;
  }
  
  public void setUserGoalsDto(UserGoalProgressDto userGoalsDto) {
    this.userGoalsDto = userGoalsDto;
  }
  
  public void setSpotLightDTOs(List<TrainingSpotLightDTO> spotLightDTOs) {
    this.spotLightDTOs = spotLightDTOs;
  }
  
  public List<TrainingSpotLightDTO> getSpotLightDTOs() {
    return spotLightDTOs;
  }

  public List<DevelopmentStrategy> getDevelopmentStrategyList() {
    return developmentStrategyList;
  }

  public void setDevelopmentStrategyList(List<DevelopmentStrategy> developmentStrategyList) {
    this.developmentStrategyList = developmentStrategyList;
  }
}
