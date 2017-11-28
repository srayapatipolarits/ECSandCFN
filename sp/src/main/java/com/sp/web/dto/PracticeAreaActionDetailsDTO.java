package com.sp.web.dto;

import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.KeyOutcomes;
import com.sp.web.model.goal.SPGoal;

import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The DTO class to store the practice area details.
 */
public class PracticeAreaActionDetailsDTO extends PracticeAreaDTO {
  
  private static final long serialVersionUID = 3795936740042247881L;

  private GoalStatus status;
  private Map<String, String> introVideo;
  private KeyOutcomes keyOutcomes;
  private int durationDays;
  private List<DSActionCategory> devStrategyActionCategoryList;
  
  /**
   * Default constructor.
   * 
   * @param spGoal
   *          - the goal to send
   */
  public PracticeAreaActionDetailsDTO(SPGoal spGoal) {
    super(spGoal);
  }

  public GoalStatus getStatus() {
    return status;
  }

  public void setStatus(GoalStatus status) {
    this.status = status;
  }

  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }

  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }

  public Map<String, String> getIntroVideo() {
    return introVideo;
  }

  public void setIntroVideo(Map<String, String> introVideo) {
    this.introVideo = introVideo;
  }

  public KeyOutcomes getKeyOutcomes() {
    return keyOutcomes;
  }

  public void setKeyOutcomes(KeyOutcomes keyOutcomes) {
    this.keyOutcomes = keyOutcomes;
  }

  public int getDurationDays() {
    return durationDays;
  }

  public void setDurationDays(int durationDays) {
    this.durationDays = durationDays;
  }
  
}
