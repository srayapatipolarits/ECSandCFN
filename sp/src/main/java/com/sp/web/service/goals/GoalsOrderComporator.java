package com.sp.web.service.goals;

import com.sp.web.dao.goal.UserGoalProgressDao;

import java.util.Comparator;

/**
 * @author Dax Abraham
 *
 *         The goals comparator.
 */
public class GoalsOrderComporator implements Comparator<UserGoalProgressDao> {
  
  @Override
  public int compare(UserGoalProgressDao o1, UserGoalProgressDao o2) {
    if (o1 == null) {
      return -1;
    }
    
    if (o2 == null) {
      return 1;
    }
    if (o1.getOrderIndex() > o2.getOrderIndex()) {
      return 1;
    } else if (o1.getOrderIndex() == o2.getOrderIndex()) {
      return 0;
    } else {
      return -1;
    }
    
  }
  
}
