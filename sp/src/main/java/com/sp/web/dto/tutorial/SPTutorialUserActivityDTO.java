package com.sp.web.dto.tutorial;

import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.model.tutorial.TutorialActivityData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO for the SP Tutorial details along with user activity.
 */
public class SPTutorialUserActivityDTO extends SPTutorialBaseUserActivityDTO {

  private List<StepUserActivityDTO> steps;

  /**
   * Constructor.
   * 
   * @param tutorialDao
   *          - tutorial
   * @param userActivity
   *          - user activity
   */
  public SPTutorialUserActivityDTO(SPTutorialDao tutorialDao, TutorialActivityData userActivity) {
    super(tutorialDao, userActivity);
    steps = tutorialDao.getSteps().stream().map(s -> new StepUserActivityDTO(s, userActivity))
        .collect(Collectors.toList());
  }
  
  public List<StepUserActivityDTO> getSteps() {
    return steps;
  }
  
  public void setSteps(List<StepUserActivityDTO> steps) {
    this.steps = steps;
  }
}
