package com.sp.web.repository.goal;

import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The repository interface to maintain and managing Action Plans and User Action Plans.
 */
public interface ActionPlanRepository {

  /**
   * Method to return all the action plans.
   * 
   * @return
   *      - the action plans list
   */
  List<ActionPlan> findAllActionPlans();

  /**
   * Get the action plan for the given action plan id.
   * 
   * @param actionPlanId
   *            - action plan id
   * @return
   *      the action plan 
   */
  ActionPlan getActionPlan(String actionPlanId);

  /**
   * Method to update the given action plan.
   * 
   * @param actionPlan
   *            - action plan to update
   */
  void updateActionPlan(ActionPlan actionPlan);
  
  /**
   * The method to store or update the user action plan to the repository.
   * 
   * @param userActionPlan
   *          - user action plan
   */
  void updateUserActionPlan(UserActionPlan userActionPlan);

  /**
   * Method to get the user action plan for the user action plan id.
   * 
   * @param userActionPlanId
   *            - user action plan id
   * @return
   *    the user action plan
   */
  UserActionPlan userActionPlanFindById(String userActionPlanId);

  /**
   * Delete the given user action plan.
   * 
   * @param userActionPlan
   *            - user action plan
   */
  void deleteUserActionPlan(UserActionPlan userActionPlan);
  
  /**
   * The list of valid action plans for the company.
   * 
   * @param companyId
   *            - company id
   * @return
   *    the list of action plans 
   */
  List<ActionPlan> findByCompanyId(String companyId);
  
  /**
   * The list of valid action plans for the company. This will return both active and inactive ones
   * 
   * @param companyId
   *            - company id
   * @return
   *    the list of action plans 
   */
  List<ActionPlan> findAllByCompanyId(String companyId);

  /**
   * Repository method to get the action plan with the given name.
   * 
   * @param name
   *          - action plan name
   * @param createdByCompanyId
   *          - company Id 
   * @return
   *      the action plan
   */
  ActionPlan findByName(String name, String createdByCompanyId);

  /**
   * Delete the action Plan.
   * @param actionPlan to be deleted.
   */
  void deleteActionPlan(ActionPlan actionPlan);

  /**
   * @return
   *    - get all the global action plans.
   */
  List<ActionPlan> findAllGlobalActionPlans();

  /**
   * Get all the action plans with the given step types.
   * 
   * @param stepTypes
   *            - step type
   * @return
   *    list of action plans
   */
  List<ActionPlan> findAllByStepType(StepType[] stepTypes);
  
}
