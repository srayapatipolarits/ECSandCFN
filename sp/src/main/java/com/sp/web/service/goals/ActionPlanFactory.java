package com.sp.web.service.goals;

import com.sp.web.Constants;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.Operation;
import com.sp.web.model.User;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.repository.goal.ActionPlanRepository;
import com.sp.web.repository.goal.CompanyActionPlanSettingsRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The factory class to provide functions to maintain action plans for the user.
 */
@Component
public class ActionPlanFactory {
  
  private static final Logger log = Logger.getLogger(ActionPlanFactory.class);
  
  @Autowired
  private ActionPlanRepository actionPlanRepository;
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private CompanyActionPlanSettingsRepository capsRepo;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  private String surepeopleIntroProgram;
  private HashSet<String> readOnlyProgramIds = new HashSet<String>();
  
  @Autowired
  private BadgeFactory badgeFactory;
  
  /**
   * Constructor to setup configuration values.
   * 
   * @param env
   *          - environment
   */
  @Inject
  public ActionPlanFactory(Environment env) {
    final String spIntroProgram = env.getProperty("surepeopleIntro");
    if (!StringUtils.isBlank(spIntroProgram)) {
      setSurepeopleIntroProgram(spIntroProgram);
      readOnlyProgramIds.add(spIntroProgram);
    }
    String readOnlyPrograms = env.getProperty("readOnlyPrograms");
    if (!StringUtils.isBlank(readOnlyPrograms)) {
      String[] split = readOnlyPrograms.split(",");
      for (String readOnlyProgramId : split) {
        readOnlyProgramIds.add(readOnlyProgramId);
      }
    }
  }
  
  /**
   * Get all the action plans.
   * 
   * @return all the action plans
   */
  public List<ActionPlan> getAllActionPlans() {
    return actionPlanRepository.findAllActionPlans();
  }
  
  /**
   * Get the action plan for the given action plan id.
   * 
   * @param actionPlanId
   *          - action plan id
   * @return the action plan
   */
  @Cacheable(value = "actionPlan")
  public ActionPlanDao getActionPlan(String actionPlanId) {
    final ActionPlan actionPlan = actionPlanRepository.getActionPlan(actionPlanId);
    
    if (actionPlan == null) {
      return null;
    }
    
    // create the dao
    ActionPlanDao actionPlanDao = new ActionPlanDao(actionPlan);
    final List<String> practiceAreaIdList = actionPlan.getPracticeAreaIdList();
    if (!CollectionUtils.isEmpty(practiceAreaIdList)) {
      // update the practice areas
      actionPlanDao.setPracticeAreaList(practiceAreaIdList.stream().map(goalsFactory::getGoal)
          .filter(g -> g != null).collect(Collectors.toList()));
    } else {
      actionPlanDao.setPracticeAreaList(new ArrayList<SPGoal>());
    }
    actionPlanDao.setReadOnly(readOnlyProgramIds.contains(actionPlanId));
    return actionPlanDao;
  }
  
  /**
   * Update the action plan.
   * 
   * @param actionPlan
   *          - action plan to update
   */
  @Caching(evict = { @CacheEvict(value = "actionPlan", key = "#actionPlan.id"),
      @CacheEvict(value = "actionPlan", key = "'active' + #actionPlan.companyId"),
      @CacheEvict(value = "actionPlan", key = "'all' + #actionPlan.companyId") })
  public void updateActionPlan(ActionPlan actionPlan) {
    try {
      actionPlanRepository.updateActionPlan(actionPlan);
    } catch (DuplicateKeyException e) {
      log.info("Error updating action plan.", e);
      throw new InvalidRequestException("Duplicate action plan.");
    }
  }
  
  /**
   * Get the user's action plan.
   * 
   * @param user
   *          - the user
   * @return the action plan for the user
   */
  public UserActionPlan getUserActionPlan(User user) {
    String userActionPlanId = user.getUserActionPlanId();
    UserActionPlan userActionPlan = null;
    if (userActionPlanId == null) {
      userActionPlan = new UserActionPlan();
      userActionPlan.setActionPlanProgressMap(new HashMap<String, ActionPlanProgress>());
      actionPlanRepository.updateUserActionPlan(userActionPlan);
      user.setUserActionPlanId(userActionPlan.getId());
      userFactory.updateUser(user);
    } else {
      userActionPlan = actionPlanRepository.userActionPlanFindById(userActionPlanId);
    }
    return userActionPlan;
  }
  
  /**
   * Delete the given user action plan.
   * 
   * @param userActionPlan
   *          - user action plan
   */
  public void deleteUserActionPlan(UserActionPlan userActionPlan) {
    actionPlanRepository.deleteUserActionPlan(userActionPlan);
  }
  
  /**
   * Update the given user action plan.
   * 
   * @param userActionPlan
   *          - the user action plan
   */
  public void updateUserActionPlan(UserActionPlan userActionPlan) {
    actionPlanRepository.updateUserActionPlan(userActionPlan);
  }
  
  /**
   * Get the action plan for the given company id.
   * 
   * @param companyId
   *          - company id
   * @return the list of action plans
   */
  // @Cacheable(value = "actionPlan", key = "'active' + #companyId")
  public List<ActionPlan> getCompanyActionPlans(String companyId) {
    return actionPlanRepository.findByCompanyId(companyId);
  }
  
  /**
   * Get the all the action plan for the given company id.
   * 
   * @param companyId
   *          - company id
   * @return the list of action plans
   */
  // @Cacheable(value = "actionPlan", key = "'all' + #companyId")
  public List<ActionPlan> getAllActionPlansForCompany(String companyId) {
    return actionPlanRepository.findAllByCompanyId(companyId);
  }
  
  /**
   * The action plan for the given action plan id but not from cache.
   * 
   * @param actionPlanId
   *          - the action plan id
   * @return the action plan
   */
  public ActionPlan getActionPlanNoCache(String actionPlanId) {
    return actionPlanRepository.getActionPlan(actionPlanId);
  }
  
  /**
   * Get the action plan with the given name.
   * 
   * @param name
   *          - action plan name
   * @param createdByCompanyId
   *          - company Id
   * @return - the action plan
   */
  public ActionPlan getActionPlanByName(String name, String createdByCompanyId) {
    return actionPlanRepository.findByName(name, createdByCompanyId);
  }
  
  /**
   * Delete the given action plan.
   * 
   * @param actionPlan
   *          - action plan
   */
  @Caching(evict = { @CacheEvict(value = "actionPlan", key = "#actionPlan.id"),
      // @CacheEvict(value = "actionPlan", key = "'active' + #actionPlan.companyId"),
      // @CacheEvict(value = "actionPlan", key = "'all' + #actionPlan.companyId"),
      @CacheEvict(value = "actionPlan", key = "#actionPlan.companyId + #actionPlan.id") })
  public void deletedActionPlan(ActionPlan actionPlan) {
    actionPlanRepository.deleteActionPlan(actionPlan);
  }
  
  /**
   * Get the action plan settings for the given company and action plan id.
   * 
   * @param apId
   *          - action plan id
   * @param companyId
   *          - company id
   * @return the company action plan settings
   */
  @CachePut(value = "actionPlan", key = "#companyId + #apId")
  public CompanyActionPlanSettings getCompanyActionPlanSettings(String apId, String companyId) {
    return capsRepo.findByCompanyIdAndActionPlanId(apId, companyId);
  }
  
  /**
   * Create a new company action plan settings.
   * 
   * @param companyId
   *          - company id
   * @param actionPlan
   *          - action plan
   * @return the newly created company action plan settings
   */
  @CachePut(value = "actionPlan", key = "#companyId + #actionPlan.id")
  public synchronized CompanyActionPlanSettings createCompanyActionPlanSettings(String companyId,
      ActionPlan actionPlan) {
    CompanyActionPlanSettings capSettings = capsRepo.findByCompanyIdAndActionPlanId(
        actionPlan.getId(), companyId);
    if (capSettings == null) {
      capSettings = new CompanyActionPlanSettings(companyId, actionPlan);
      List<String> companyIds = actionPlan.getCompanyIds();
      if (actionPlan.getCompanyIds() == null) {
        companyIds = new ArrayList<String>();
      }
      if (!companyIds.contains(companyId)) {
        companyIds.add(companyId);
      }

      capsRepo.save(capSettings);
    }
    return capSettings;
  }
  
  /**
   * Update the company action plan settings in the DB.
   * 
   * @param capSettings
   *          - company action plan settings
   */
  @CachePut(value = "actionPlan", key = "#capSettings.companyId + #capSettings.actionPlanId")
  public void updateCompanyActionPlanSettings(CompanyActionPlanSettings capSettings) {
    capsRepo.save(capSettings);
  }
  
  /**
   * Remove the company action plan settings.
   * 
   * @param capSettings
   *          - company action plan settings
   */
  @CacheEvict(value = "actionPlan", key = "#capSettings.companyId + #capSettings.actionPlanId")
  public void removeCompanyActionPlanSettings(CompanyActionPlanSettings capSettings) {
    capsRepo.delete(capSettings);
  }
  
  /**
   * Create a new action plan in the data base.
   * 
   * @param actionPlan
   *          - action plan
   */
  public void createActionPlan(ActionPlan actionPlan) {
    actionPlanRepository.updateActionPlan(actionPlan);
  }
  
  /**
   * Updates the user's action plan and other settings.
   * 
   * @param userActionPlan
   *          - user action plan
   * @param actionPlanDao
   *          - action plan Dao
   * @param op 
   *          - operation
   */
  public void addUpdateActionPlanProgress(User user, UserActionPlan userActionPlan,
      ActionPlanDao actionPlanDao, Operation op) {
    updateActionPlanProgress(user, userActionPlan, actionPlanDao, true, op);
  }
  
  /**
   * Update the action plan progress for the given user.
   * 
   * @param user
   *          - user
   * @param userActionPlan
   *          - user action plan progress
   * @param actionPlanDao
   *          - action plan dao
   * @param op
   *          - operation  
   */
  public void updateActionPlanProgress(User user, UserActionPlan userActionPlan,
      ActionPlanDao actionPlanDao, boolean doReset, Operation op) {
    
    final String actionPlanId = actionPlanDao.getId();
    ActionPlanProgress actionPlanProgress = userActionPlan
        .getOrCreateActionPlanProgress(actionPlanId);
    HashSet<String> prevActiveSteps = new HashSet<String>(actionPlanProgress.getActiveSteps());
    if (doReset) {
      actionPlanProgress.reset();
      // todoFactory.removeAllTasksWithParentId(user, actionPlanId);
    }
    
    List<SPGoal> practiceAreaList = actionPlanDao.getPracticeAreaList();
    LocalDate taskDueDate = actionPlanProgress.taskStartDate();
    LocalDate today;
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
    ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests
        .getParentTodoTaskRequest(actionPlanId);
    if (parentTodoTaskRequest == null) {
      parentTodoTaskRequest = new ParentTodoTaskRequests(TodoType.OrgPlan, actionPlanId);
      userTodoRequests.add(parentTodoTaskRequest);
    }
    switch (actionPlanDao.getStepType()) {
    case All:
      for (SPGoal pa : practiceAreaList) {
        if (pa.isActive()) {
          Set<String> completedActions = actionPlanProgress.addPracticeArea(pa);
          final String stepId = pa.getId();
          if (pa.hasRemainingActions(completedActions)) {
            if (!parentTodoTaskRequest.hasRequest(stepId)) {
              parentTodoTaskRequest.add(TodoRequest.newInstanceFromParentRefId(TodoType.OrgPlan,
                  actionPlanId, stepId, null));
            }
          } else {
            parentTodoTaskRequest.remove(stepId);
          }
          if (!prevActiveSteps.remove(stepId)) {
            publicChannelHelper.addUser(user, stepId);
          }
        }
      }
      break;
    
    case AllWithDuration:
      for (SPGoal pa : practiceAreaList) {
        if (pa.isActive()) {
          final String stepId = pa.getId();
          Set<String> completedActions = actionPlanProgress.addPracticeArea(pa);
          taskDueDate = taskDueDate.plusDays(pa.getDurationDays());
          if (pa.hasRemainingActions(completedActions)) {
            parentTodoTaskRequest.add(TodoRequest.newInstanceFromParentRefId(TodoType.OrgPlan,
                actionPlanId, stepId, LocalDateTime.of(taskDueDate, LocalTime.MIDNIGHT)));
          } else {
            parentTodoTaskRequest.remove(stepId);
          }
          if (!prevActiveSteps.remove(stepId)) {
            publicChannelHelper.addUser(user, stepId);
          }
        }
      }
      break;
    
    case TimeBased:
      today = LocalDate.now();
      for (SPGoal pa : practiceAreaList) {
        if (pa.isActive()) {
          final String stepId = pa.getId();
          final Set<String> completedActions = actionPlanProgress.addPracticeArea(pa);
          taskDueDate = taskDueDate.plusDays(pa.getDurationDays());
          if (pa.hasRemainingActions(completedActions)) {
            parentTodoTaskRequest.add(TodoRequest.newInstanceFromParentRefId(TodoType.OrgPlan,
                actionPlanId, stepId, LocalDateTime.of(taskDueDate, LocalTime.MIDNIGHT)));
          } else {
            parentTodoTaskRequest.remove(stepId);
          }
          
          if (!prevActiveSteps.remove(stepId)) {
            publicChannelHelper.addUser(user, stepId);
          }
          
          if (taskDueDate.isAfter(today)) {
            break;
          }
        }
      }
      break;
    
    case TimeBasedWithCompletion:
      today = LocalDate.now();
      for (SPGoal pa : practiceAreaList) {
        if (pa.isActive()) {
          final String stepId = pa.getId();
          final Set<String> completedActions = actionPlanProgress.addPracticeArea(pa);
          taskDueDate = taskDueDate.plusDays(pa.getDurationDays());
          final boolean hasRemainingActions = pa.hasRemainingActions(completedActions);
          if (hasRemainingActions) {
            parentTodoTaskRequest.add(TodoRequest.newInstanceFromParentRefId(TodoType.OrgPlan,
                actionPlanId, stepId, LocalDateTime.of(taskDueDate, LocalTime.MIDNIGHT)));
          } else {
            parentTodoTaskRequest.remove(stepId);
          }
          if (!prevActiveSteps.remove(stepId)) {
            publicChannelHelper.addUser(user, stepId);
          }
          if (taskDueDate.isAfter(today) && hasRemainingActions) {
            break;
          }
        }
      }
      break;
    
    default:
      break;
    }
    
    // updating the action plan in db
    updateUserActionPlan(userActionPlan);

    if (!prevActiveSteps.isEmpty()) {
      for (String stepId : prevActiveSteps) {
        publicChannelHelper.removeUser(user, stepId);
        parentTodoTaskRequest.remove(stepId);
      }
    }
    if (parentTodoTaskRequest.isEmpty()) {
      userTodoRequests.removeByParent(actionPlanId);
      todoFactory.sendSse(user, Operation.DELETE, parentTodoTaskRequest, false);
    } else {
      todoFactory.sendSse(user, op,
          todoFactory.processWithParent(user, parentTodoTaskRequest), false);
    }
    
    todoFactory.updateUserTodoRequests(userTodoRequests);
    badgeFactory.updateBadgeProgress(user, actionPlanId, BadgeType.OrgPlan, actionPlanDao);
  }
  
  /**
   * Send the SSE event for the given operation to the users.
   * 
   * @param companyId
   *          - company id
   * @param memberIds
   *          - member id's
   * @param op
   *          - operation
   * @param actionPlanId
   *          - action plan id
   */
  public void sendSSE(String companyId, List<String> memberIds, Operation op, String actionPlanId) {
    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.PARAM_OPERATION, op);
    payload.put(Constants.PARAM_ACTION_PLAN, actionPlanId);
    MessageEventRequest messageEventRequest = MessageEventRequest.newEvent(
        ActionType.OrganisationPlan, memberIds, payload, companyId);
    eventGateway.sendEvent(messageEventRequest);
  }
  
  /**
   * Get all the global action plans.
   * 
   * @return the list of global action plans
   */
  public List<ActionPlan> getAllGlobalPrograms() {
    return actionPlanRepository.findAllGlobalActionPlans();
  }
  
  /**
   * Get the list of action plans with the given types.
   * 
   * @param stepTypes
   *          - step types
   * @return list of action plans
   */
  public List<ActionPlan> getActionPlanByStepType(StepType... stepTypes) {
    return actionPlanRepository.findAllByStepType(stepTypes);
  }
  
  public String getSurepeopleIntroProgram() {
    return surepeopleIntroProgram;
  }
  
  public void setSurepeopleIntroProgram(String surepeopleIntroProgram) {
    this.surepeopleIntroProgram = surepeopleIntroProgram;
  }
  
  public HashSet<String> getReadOnlyProgramIds() {
    return readOnlyProgramIds;
  }
  
}
