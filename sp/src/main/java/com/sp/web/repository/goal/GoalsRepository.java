package com.sp.web.repository.goal;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;

import java.util.List;
import java.util.Set;

/**
 * <code>Goal</code> interface will load the goal for the personality type.
 * 
 * @author pradeep
 *
 */
public interface GoalsRepository {
  
  /**
   * <code>getGoals</code> method will return the goals from the personality type passed
   * 
   * @param personalityType
   *          personality type for which goals are to be retrieved.
   * @return the SPGoals
   */
  List<SPGoal> getGoals(PersonalityType personalityType);
  
  SPGoal findById(String goalId);
  
  List<SPGoal> findAllGoalsById(List<String> goalId);
  
  List<SPGoal> findAllGoalsById(Set<String> goalId);
  
  SPGoal findGoalByName(String name);
  
  SPGoal findGoalByName(String name, GoalCategory category);
  
  List<SPGoal> findAllGoalsByNames(List<String> name);
  
  void updateGoals(List<SPGoal> spGoals);
  
  SPGoal updateGoal(SPGoal spGoal);
  
  List<SPGoal> findAllGoals();
  
  List<SPGoal> findAllGoalsByCategory(GoalCategory... category);
  
  PersonalityPracticeArea findPersonalityPracticeArea(String personalityType);
  
  /**
   * Save the given personality practice area.
   * 
   * @param personalityPracticeArea
   *          - the personality practice area to save update
   */
  void updatePersonalityPracticeArea(PersonalityPracticeArea personalityPracticeArea);
  
  /**
   * Get all the personality practice areas.
   * 
   * @return the list of personality practice areas
   */
  List<PersonalityPracticeArea> findAllPersonalityPracticeAreas();
  
  /**
   * Method to remove the given goal.
   * 
   * @param spGoal
   *          - goal to remove
   */
  void removeGoal(SPGoal spGoal);
  
  /**
   * Get the blueprint for the given blueprint id.
   * 
   * @param blueprintId
   *          - blueprint id
   * @return the blueprint for the given blue print id
   */
  Blueprint getBlueprint(String blueprintId);
  
  /**
   * Update the blueprint in the database.
   * 
   * @param blueprint
   *          - blueprint to update
   */
  void updateBlueprint(Blueprint blueprint);
}
