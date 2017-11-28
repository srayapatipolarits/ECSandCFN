package com.sp.web.service.goals;

import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.goal.GoalSource;
import com.sp.web.model.goal.GoalSourceType;
import com.sp.web.model.goal.UserGoalProgress;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <code>GoalsAlgorith</code> will arrange the goals on the basis of below algorithm and reutrn the
 * the goal progress list. 0 - Primary Personality, Under Pressure and PRISM Lens. 1- Primary
 * Personality and Under Pressure. 2 - Primary Personality and Prism Lens. 3 -. Under Pressure and
 * Prism Lens 4 - Primary Personality only 5 - Under Pressure only 6 - Lens only 7 - Others in case
 * user manually selects from the all goals list
 * 
 * @author pradeepruhil
 *
 */
@Component
public class GoalsAlgorithm {
  
  private GoalsUgpComporator goalsComparator = new GoalsUgpComporator();
  
  private GoalsOrderComporator orderComparator = new GoalsOrderComporator();
  
  private GoalsUgpDaoComporator daoComporator = new GoalsUgpDaoComporator();
  
  /*
   * Creating blank list for weighting having list at Index 0 -
   * 
   * 0 - Primary Personality, Under Pressure and PRISM Lens
   * 
   * 1- Primary Personality and Under Pressure
   * 
   * 2 - Primary Personality and Prism Lens
   * 
   * 3 -. Under Pressure and Prism Lens
   * 
   * 4 - Primary Personality only
   * 
   * 5 - Under Pressure only
   * 
   * 6 - Lens only
   * 
   * 7 - Others in case user manually selects from the all goals list
   */
  /**
   * <code>sortedUserGoalProgressList</code> method will sort the goal progress list.
   * 
   * @param goalProgresses
   *          User Goal porgress list.
   * @param goalsComparator
   *          goalsComparator.
   * @return
   */
  public List<UserGoalProgress> sortedUserGoalProgressList(List<UserGoalProgress> goalProgresses) {
    List<List<UserGoalProgress>> weightedList = createWeightedList();
    for (UserGoalProgress userGoalProgress : goalProgresses) {
      
      int index = -1;
      if (userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismPrimary))
          && userGoalProgress.getSourceList().contains(
              new GoalSource(GoalSourceType.PrismUnderPressure))
          && userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 0;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.PrismPrimary))
          && userGoalProgress.getSourceList().contains(
              new GoalSource(GoalSourceType.PrismUnderPressure))) {
        index = 1;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.PrismPrimary))
          && userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 2;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.PrismUnderPressure))
          && userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 3;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.PrismPrimary))) {
        index = 4;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.PrismUnderPressure))) {
        index = 5;
      } else if (userGoalProgress.getSourceList()
          .contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 6;
      } else {
        index = 7;
      }
      
      switch (index) {
      case 0:
        weightedList.get(0).add(userGoalProgress);
        break;
      case 1:
        weightedList.get(1).add(userGoalProgress);
        break;
      case 2:
        weightedList.get(2).add(userGoalProgress);
        break;
      case 3:
        weightedList.get(3).add(userGoalProgress);
        break;
      case 4:
        weightedList.get(4).add(userGoalProgress);
        break;
      case 5:
        weightedList.get(5).add(userGoalProgress);
        break;
      case 6:
        weightedList.get(6).add(userGoalProgress);
        break;
      default:
        weightedList.get(7).add(userGoalProgress);
        break;
      }
      
    }
    List<UserGoalProgress> finalSortedList = new ArrayList<>();
    weightedList.stream().forEach(ugpList -> {
      ugpList.sort(goalsComparator);
      finalSortedList.addAll(ugpList);
    });
    return finalSortedList;
    
  }
  
  /**
   * <code>sortedUserGoalProgressList</code> method will sort the goal progress list.
   * 
   * @param values
   *          User Goal porgress list.
   * @param goalsComparator
   *          goalsComparator.
   * @return
   */
  public List<UserGoalProgressDao> sortedUserGoalProgressDaoList(
      Collection<UserGoalProgressDao> values) {
    List<List<UserGoalProgressDao>> weightedList = createDaoWeightedList();
    for (UserGoalProgressDao userGoalProgress : values) {
      
      int index = -1;
      if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismPrimary.toString()))
          && userGoalProgress.getSourceList().contains(
              new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismUnderPressure.toString()))
          && userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 0;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismPrimary.toString()))
          && userGoalProgress.getSourceList().contains(
              new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismUnderPressure.toString()))) {
        index = 1;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismPrimary.toString()))
          && userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 2;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismUnderPressure.toString()))
          && userGoalProgress.getSourceList().contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 3;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismPrimary.toString()))) {
        index = 4;
      } else if (userGoalProgress.getSourceList().contains(
          new GoalSource(GoalSourceType.Prism, GoalSourceType.PrismUnderPressure.toString()))) {
        index = 5;
      } else if (userGoalProgress.getSourceList()
          .contains(new GoalSource(GoalSourceType.PrismLens))) {
        index = 6;
      } else {
        index = 7;
      }
      
      switch (index) {
      case 0:
        weightedList.get(0).add(userGoalProgress);
        break;
      case 1:
        weightedList.get(1).add(userGoalProgress);
        break;
      case 2:
        weightedList.get(2).add(userGoalProgress);
        break;
      case 3:
        weightedList.get(3).add(userGoalProgress);
        break;
      case 4:
        weightedList.get(4).add(userGoalProgress);
        break;
      case 5:
        weightedList.get(5).add(userGoalProgress);
        break;
      case 6:
        weightedList.get(6).add(userGoalProgress);
        break;
      default:
        weightedList.get(7).add(userGoalProgress);
        break;
      }
      
    }
    List<UserGoalProgressDao> finalSortedList = new ArrayList<>();
    weightedList.stream().forEach(ugpList -> {
      ugpList.sort(daoComporator);
      finalSortedList.addAll(ugpList);
    });
    return finalSortedList;
    
  }
  
  private List<List<UserGoalProgress>> createWeightedList() {
    List<List<UserGoalProgress>> weightedList = new ArrayList<>();
    
    for (int i = 0; i < 8; i++) {
      weightedList.add(new ArrayList<UserGoalProgress>());
    }
    return weightedList;
  }
  
  private List<List<UserGoalProgressDao>> createDaoWeightedList() {
    List<List<UserGoalProgressDao>> weightedList = new ArrayList<>();
    
    for (int i = 0; i < 8; i++) {
      weightedList.add(new ArrayList<UserGoalProgressDao>());
    }
    return weightedList;
  }
  
  public void sortUserGoalProgressDaoByOrder(List<UserGoalProgressDao> goalProgressDaos) {
    goalProgressDaos.sort(orderComparator);
  }
}
