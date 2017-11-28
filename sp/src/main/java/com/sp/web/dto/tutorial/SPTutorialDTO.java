package com.sp.web.dto.tutorial;

import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.dto.user.UserMarkerDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class for the SP Tutorial details data.
 */
public class SPTutorialDTO extends SPTutorialListingDTO {
  
  private List<TutorialStepDTO> steps;
  private LocalDateTime updatedOn;
  private UserMarkerDTO updatedBy;
  
  /**
   * Constructor.
   * 
   * @param tutorialDao
   *            - tutorial dao
   */
  public SPTutorialDTO(SPTutorialDao tutorialDao) {
    super(tutorialDao);
    steps = tutorialDao.getSteps().stream().map(TutorialStepDTO::new).collect(Collectors.toList());
  }

  public List<TutorialStepDTO> getSteps() {
    return steps;
  }
  
  public void setSteps(List<TutorialStepDTO> steps) {
    this.steps = steps;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
}
