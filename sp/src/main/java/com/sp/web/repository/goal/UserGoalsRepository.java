package com.sp.web.repository.goal;

import com.sp.web.model.goal.UserGoal;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The repository interface for user goals.
 */
public interface UserGoalsRepository {

  UserGoal findById(String goalId);

  void save(UserGoal userGoal);

  List<UserGoal> getAllUserGoals();
  
  void remove(String goalId);
  
  /**
   * GetUserForGoals method will return all the users associated with practice area passed for the
   * given company.
   * 
   * @param goalId of the user.
   * @param users of the company.
   * @return the list of users who has goalId in their active list.
   */
  List<UserGoal> getUsersForGoals(String goalId, List<String> users);

}
