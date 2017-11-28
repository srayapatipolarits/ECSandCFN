package com.sp.web.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author pradeep
 *
 *         The user goals dto.
 */
public class UserGoalDto {

  private List<UserGoalProgressDto> goalPrgress;

  public UserGoalDto(List<UserGoalProgressDto> userGoalsList) {
    this.goalPrgress = userGoalsList;
  }

  public List<UserGoalProgressDto> getGoalPrgress() {
    return Optional.ofNullable(goalPrgress).orElseGet(() -> {
        goalPrgress = new ArrayList<UserGoalProgressDto>();
        return goalPrgress;
      });
  }

  public void setGoalPrgress(List<UserGoalProgressDto> goalPrgress) {
    this.goalPrgress = goalPrgress;
  }
}
