package com.sp.web.service.todo;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.todo.ActionPlanTodoDTO;
import com.sp.web.dto.todo.TodoDTO;
import com.sp.web.model.User;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.service.goals.ActionPlanFactory;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The display processor for action plans.
 */
@Component("actionPlanDisplayProcessor")
public class ActionPlanDisplayProcessor implements TodoWithParentDisplayProcessor {
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Override
  public TodoDTO process(User user, ParentTodoTaskRequests parentRequest,
      MutableBoolean updateUserTodoRequests) {
    final String actionPlanId = parentRequest.getParentRefId();
    ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
    if (actionPlan == null) {
      return null;
    }
    
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    final ActionPlanProgress actionPlanProgress = userActionPlan
        .getActionPlanProgress(actionPlanId);
    
    if (actionPlanProgress == null) {
      return null;
    }
    
    final Map<String, TodoRequest> todoTasks = parentRequest.getTodoTasks();
    if (!CollectionUtils.isEmpty(todoTasks)) {
      final ActionPlanTodoDTO actionPlanDto = new ActionPlanTodoDTO(actionPlan, actionPlanProgress);
      actionPlan
          .getPracticeAreaList()
          .stream()
          .filter(pa -> pa.isActive() && todoTasks.containsKey(pa.getId()))
          .forEach(
              pa -> actionPlanDto.add(pa, actionPlanProgress, todoTasks, updateUserTodoRequests));
      return actionPlanDto;
    }
    return null;
  }
  
}
