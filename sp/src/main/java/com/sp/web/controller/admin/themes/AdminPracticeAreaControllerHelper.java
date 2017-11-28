package com.sp.web.controller.admin.themes;

import com.sp.web.Constants;
import com.sp.web.dto.PracticeAreaDetailsDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.goal.PracticeAreaForm;
import com.sp.web.model.User;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.pc.PublicChannelHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The helper class for the practice area admin controller.
 */
@Component
public class AdminPracticeAreaControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(AdminPracticeAreaControllerHelper.class);
  
  @Autowired
  SPGoalFactory goalFactory;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  @Autowired
  private DevelopmentFeedbackFactory feedbackFactory;
  
  @Autowired
  private BadgeFactory badgeFactory;
  
  /**
   * Get the list of practice areas.
   * 
   * @param user
   *          - logged in user
   * @return the list of practice areas
   */
  public SPResponse getAll(User user) {
    final SPResponse resp = new SPResponse();
    List<SPGoal> allPratcieAreas = goalFactory.getAllPratcieAreas();
    return resp.add(Constants.PARAM_PRACTICE_AREAS,
        allPratcieAreas.stream().map(PracticeAreaDetailsDTO::new).collect(Collectors.toList()));
  }
  
  /**
   * This method helps update the status for the given practice area.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - parameters
   * @return the status for the update request
   */
  public SPResponse activateDeactivate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the practice area id
    String practiceAreaId = (String) params[0];
    
    // get the status to set
    GoalStatus goalStatusToSet = (GoalStatus) params[1];
    
    SPGoal goal = getGoal(practiceAreaId);
    
    // check if status not same
    if (goal.getStatus() == goalStatusToSet) {
      throw new InvalidRequestException("Practice area already set to :" + goalStatusToSet);
    }
    
    // updating the status
    goal.setStatus(goalStatusToSet);
    
    // updating the goal
    goalFactory.updateGoal(goal);
    
    if (goalStatusToSet == GoalStatus.INACTIVE) {
      // all the practice feedbacks associated with the practice area
      final String refId = goal.getId();
      publicChannelHelper.deletePublicChannel(refId);
      
//       delete all the feedback by parent reference id
//      feedbackFactory.deleteByDevFeedRefId(refId);
      
      // delte the badge progress for the goal */
      badgeFactory.deleteBadgeProgress(goal.getId());
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Get the goal for the given practice area id.
   * 
   * @param practiceAreaId
   *          - practice area id
   * @return the goal or throws an exception
   */
  private SPGoal getGoal(String practiceAreaId) {
    // get the practice area to update
    SPGoal goal = Optional.ofNullable(goalFactory.getGoal(practiceAreaId)).orElseThrow(
        () -> new InvalidRequestException("Practice area not found with id :" + practiceAreaId));
    return goal;
  }
  
  /**
   * Helper method to create the practice area.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the parameters for the create
   * @return the status for the create request
   */
  public SPResponse create(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the practice are form
    PracticeAreaForm form = (PracticeAreaForm) params[0];
    
    // get the practice area from the form
    SPGoal practiceArea = form.createNewPracticeArea();
    
    updateGoal(practiceArea);
    
    return resp.isSuccess();
  }
  
  /**
   * Validate the given practice area and update it in the db.
   * 
   * @param practiceArea
   *          - practice area to update
   */
  private void updateGoal(SPGoal practiceArea) {
    // validating the form
    validate(practiceArea);
    
    // save the goal
    try {
      goalFactory.updateGoal(practiceArea);
    } catch (Exception e) {
      LOG.warn("Error craeating the practice area.", e);
      throw new InvalidRequestException("Error creating the Practice Area.");
    }
  }
  
  /**
   * The helper method to update the practice area.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to update
   * @return the status of the update request
   */
  public SPResponse update(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the practice are form
    PracticeAreaForm form = (PracticeAreaForm) params[0];
    
    // get the practice area id to update
    String practiceAreaId = (String) params[1];
    
    // get the goal to update
    SPGoal goal = getGoal(practiceAreaId);
    
    // update the goal with the new information provided
    form.updateGoal(goal);
    
    // update the goal
    updateGoal(goal);
    
    // do articles reload for updating themes
    articlesFactory.load();
    
    return resp.isSuccess();
  }
  
  /**
   * Method to validate the practice area.
   * 
   * @param practiceArea
   *          - practice area to validate
   */
  private void validate(SPGoal practiceArea) {
    // check name
    Assert.hasText(practiceArea.getName(), "Practice area name required.");
    
    // check description
    Assert.hasText(practiceArea.getDescription(), "Practice area description required.");
    
    // check development strategy list
    List<DevelopmentStrategy> developmentStrategyList = practiceArea.getDevelopmentStrategyList();
    Assert.notNull(developmentStrategyList, "Practice area development strategy required.");
    Assert.isTrue(developmentStrategyList.size() >= 3,
        "Practice area development strategy, minimum 3 required.");
    
    // loop over all the development strategies and validate description
    for (DevelopmentStrategy ds : developmentStrategyList) {
      Assert.hasText(ds.getId(), "Development strategy id required.");
      Assert.hasText(ds.getDsDescription(), "Development strategy description required.");
    }
  }
}
