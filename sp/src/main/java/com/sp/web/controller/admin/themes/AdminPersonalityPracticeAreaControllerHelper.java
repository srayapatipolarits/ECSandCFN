package com.sp.web.controller.admin.themes;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.dto.goal.PersonalityPracticeAreaDTO;
import com.sp.web.form.admin.personality.PersonalitySwotForm;
import com.sp.web.model.User;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The helper class for the practice area admin controller.
 */
@Component
public class AdminPersonalityPracticeAreaControllerHelper {
  
  @Autowired
  SPGoalFactory goalFactory;
  
  /**
   * Get the list of practice areas.
   * 
   * @param user
   *          - logged in user
   * @return the list of practice areas
   */
  public SPResponse getAll(User user) {
    final SPResponse resp = new SPResponse();
    List<PersonalityPracticeArea> allPersonalityPratcieAreas = goalFactory
        .getAllPersonalityPracticeAreas();
    return resp.add(
        Constants.PARAM_PRACTICE_AREAS,
        allPersonalityPratcieAreas.stream()
            .map(ppa -> new PersonalityPracticeAreaDTO(ppa, goalFactory))
            .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to Update the personality practice area.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to update
   * @return the status of the update request
   */
  @SuppressWarnings("unchecked")
  public SPResponse update(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // personality practice area id
    PersonalityType personalityType = (PersonalityType) params[0];
    
    // list of goal id's
    List<String> goalIds = (List<String>) params[1];
    
    // get the personality practice area for the given personality
    PersonalityPracticeArea personalityPracticeArea = goalFactory
        .getPersonalityPracticeArea(personalityType);
    
    // checking if the personality practice area was found
    Assert.notNull(personalityPracticeArea, "Personality practice area not found for :"
        + personalityType);
    
    // check if the goal id's to set are not null
    Assert.notEmpty(goalIds, "Goal id's required.");
    
    // validate if the goal id's are valid
    for (String goalId : goalIds) {
      SPGoal goal = goalFactory.getGoal(goalId);
      Assert.notNull(goal, "Goal not found for id :" + goalId);
    }
    
    // set the goal id's
    personalityPracticeArea.setGoalIds(goalIds);
    
    // update the personality practice area
    goalFactory.updatePersonalityPracticeArea(personalityPracticeArea);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to Update the Swot personalityMapping.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to update
   * @return the status of the update request
   */
  public SPResponse updateSwot(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // personality practice area id
    PersonalitySwotForm swotForm = (PersonalitySwotForm) params[0];
    
    // get the personality practice area for the given personality
    PersonalityPracticeArea personalityPracticeArea = goalFactory
        .getPersonalityPracticeArea(swotForm.getPersonalityType());
    
    // checking if the personality practice area was found
    Assert.notNull(personalityPracticeArea,
        "Personality practice area not found for :" + swotForm.getPersonalityType());
    
    // check if the valid swot details id's to set are not null
    Assert.notEmpty(swotForm.getSwotProfileMap(), "Swot forms required.");
    
    personalityPracticeArea.getSwotProfileMap().putAll(swotForm.getSwotProfileMap());
    
    // update the personality practice area
    goalFactory.updatePersonalityPracticeArea(personalityPracticeArea);
    
    return resp.isSuccess();
  }
  
}
