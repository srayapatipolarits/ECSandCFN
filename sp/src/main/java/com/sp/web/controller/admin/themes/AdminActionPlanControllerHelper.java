package com.sp.web.controller.admin.themes;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.dto.goal.ActionPlanDTO;
import com.sp.web.dto.goal.ActionPlanEditDTO;
import com.sp.web.dto.goal.ActionPlanListingDTO;
import com.sp.web.dto.goal.ActionPlanSummaryDTO;
import com.sp.web.dto.goal.ActionPlanUserListDTO;
import com.sp.web.dto.goal.ActionPlanWithUserDTO;
import com.sp.web.dto.goal.BaseActionPlanDTO;
import com.sp.web.dto.goal.CompanyActionPlanListingDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.Operation;
import com.sp.web.form.goal.ActionPlanForm;
import com.sp.web.form.goal.ManageActionPlanForm;
import com.sp.web.form.goal.PracticeAreaActionForm;
import com.sp.web.model.Company;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.GroupPermissions;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * ThemeControllerHeper is the helper class for the themes.
 * 
 * @author Dax Abraham
 *
 */
@Component
public class AdminActionPlanControllerHelper {
  
  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(AdminActionPlanControllerHelper.class);
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  GroupRepository groupRepository;
  
  @Autowired
  SPNoteFeedbackRepository spNoteFeedbackRepository;
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationsProcessor;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private DevelopmentFeedbackFactory feedbackFactory;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  /**
   * This is the helper method to get all the action plans configured in the system.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get all call
   */
  public SPResponse getAll(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    LocalDateTime now = LocalDateTime.now();
    log.info("Get All start time " + now);
    // get the type of plans
    final ActionPlanType type = (ActionPlanType) params[0];
    
    // getting the list of all the action plans
    List<ActionPlan> allActionPlans = actionPlanFactory.getAllActionPlans();
    
    if (!allActionPlans.isEmpty()) {
      switch (type) {
      case SurePeople:
        // filtering only the SP plans and adding to list
        resp.add(Constants.PARAM_ACTION_PLAN, allActionPlans.stream().filter(this::addSPActionPlan)
            .map(ActionPlanListingDTO::new).collect(Collectors.toList()));
        break;
      
      case Company:
        List<Company> companyList = companyFactory
            .findCompaniesByFeature(SPFeature.OrganizationPlan);
        final Map<String, CompanyActionPlanListingDTO> companyActionPlanMap = companyList
            .stream()
            .map(CompanyActionPlanListingDTO::new)
            .collect(
                Collectors.toMap(CompanyActionPlanListingDTO::getCompanyId, Function.identity()));
        // allActionPlans = allActionPlans.stream().filter(this::addCompanyActionPlan)
        // .collect(Collectors.toList());
        if (!allActionPlans.isEmpty()) {
          allActionPlans.forEach(ap -> addToCompany(ap, companyActionPlanMap));
        }
        resp.add(Constants.PARAM_ACTION_PLAN, companyActionPlanMap.values());
        break;
      
      default:
        break;
      }
      
    }
    log.info("getAll method finish" + Duration.between(LocalDateTime.now(), now).getSeconds());
    return resp.isSuccess();
  }
  
  /**
   * This is the helper method to get all the action plans configured in the system for the company.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for request
   * @return the response to the get all call
   */
  public SPResponse getCompanyActionPlans(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    boolean accountDashboardRequest = (boolean) params[0];
    boolean includeUserId = (boolean) params[1];
    
    List<ActionPlan> allActionPlans = actionPlanFactory.getAllActionPlansForCompany(user
        .getCompanyId());
    
    // grouping all the action plans by the company
    List<? extends BaseActionPlanDTO> actionPlansForCompany = (includeUserId) ? allActionPlans
        .stream()
        .map(
            ap -> new ActionPlanUserListDTO(ap, getCompanyActionPlanSettings(user, ap),
                actionPlanFactory.getReadOnlyProgramIds())).collect(Collectors.toList())
        : allActionPlans.stream().map(BaseActionPlanDTO::new).collect(Collectors.toList());
    
    // if account dashboard request then add the user id's for the first organization plan
    if (accountDashboardRequest && !actionPlansForCompany.isEmpty()) {
      final ActionPlan actionPlan = allActionPlans.get(0);
      resp.add(
          Constants.PARAM_ACTION_PLAN,
          new ActionPlanWithUserDTO(actionPlan, userFactory, actionPlanFactory,
              getCompanyActionPlanSettings(user, actionPlan), actionPlanFactory
                  .getReadOnlyProgramIds()));
    }
    
    return resp.add(Constants.PARAM_ACTION_PLAN_LISTING, actionPlansForCompany);
  }
  
  /**
   * This is the helper method to get all the companies who have Organizational Plan feature.
   * 
   * @param user
   *          - logged in user
   * @return the list of Companies
   */
  public SPResponse getCompanies(User user) {
    final SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = companyFactory
        .findCompaniesByFeature(SPFeature.OrganizationPlan);
    
    resp.add("companies",
        findAllCompanies.stream().map(BaseCompanyDTO::new).collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * Helper method to get the details for the action plan.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the action plan
   */
  public SPResponse getDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String actionPlanId = (String) params[0];
    // get the account dashboard request flag
    boolean accountDashboard = (boolean) params[1];
    boolean isEdit = (boolean) params[2];
    
    // get the action plan
    ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
    Assert.notNull(actionPlan, "Action plan not found.");
    
    authorizeRequest(user, actionPlan);
    
    if (accountDashboard) {
      resp.add(
          Constants.PARAM_ACTION_PLAN,
          new ActionPlanWithUserDTO(actionPlan, userFactory, actionPlanFactory,
              getCompanyActionPlanSettings(user, actionPlan), actionPlanFactory
                  .getReadOnlyProgramIds()));
    } else {
      if (isEdit) {
        resp.add(Constants.PARAM_ACTION_PLAN, new ActionPlanEditDTO(actionPlan));
      } else {
        resp.add(Constants.PARAM_ACTION_PLAN, new ActionPlanDTO(actionPlan));
      }
    }
    
    // returning the result
    return resp;
  }
  
  /**
   * Helper to create the action plan.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params containing the form data
   * @return the response to the create request
   */
  public SPResponse create(User user, Object[] params) {
    ActionPlanForm actionPlanForm = (ActionPlanForm) params[0];
    if (log.isDebugEnabled()) {
      log.debug("The form data : " + actionPlanForm);
    }
    
    ActionPlan actionPlan = new ActionPlan();
    actionPlan.updateCreatedBy(user);
    
    // update and add the action plan
    // The actionPlanId should be sent back as a response for FE. Using that FE can redirect to
    // Update Page
    addUpdateActionPlan(actionPlanForm, actionPlan);
    
    // setting the edit allowed if the
    // user is account administrator
    if (!user.hasAnyRole(RoleType.SuperAdministrator, RoleType.SysOrgPlans)
        && user.hasRole(RoleType.AccountAdministrator)) {
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
      actionPlanDao.setEditAllowed(true);
      actionPlanFactory.updateActionPlan(actionPlanDao);
    }
    
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    
    spResponse.add("actionPlanId", actionPlan.getId());
    return spResponse;
  }
  
  /**
   * Helper to update the action plan.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params containing the form data
   * @return the response to the create request
   */
  public SPResponse update(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    ActionPlanForm actionPlanForm = (ActionPlanForm) params[0];
    if (log.isDebugEnabled()) {
      log.debug("The form data : " + actionPlanForm);
    }
    
    final String actionPlanId = actionPlanForm.getId();
    Assert.hasText(actionPlanId, "Action plan not found.");
    
    // get the action plan for the given action plan id
    ActionPlan actionPlan = actionPlanFactory.getActionPlanNoCache(actionPlanId);
    Assert.notNull(actionPlan, "Action plan not found.");
    
    // Authorizing the action plan update
    authorizeRequest(user, actionPlan, true);
    
    // update the edited by
    actionPlan.updateEditedBy(user);
    
    // update and add the action plan
    addUpdateActionPlan(actionPlanForm, actionPlan);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper to delete the action plan or action plan step. If action plan step is having the value,
   * then action value step is deleted and corresponding request feedbacks and note will be deleted.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params containing the form data
   * @return the response to the create request
   */
  public SPResponse delete(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String actionPlanId = (String) params[0];
    
    /* this is step id for the organization action plan */
    String practiceAreaId = (String) params[1];
    
    if (log.isDebugEnabled()) {
      log.debug("actionPlanId which is to be deleted : " + actionPlanId + ", Practice area Id"
          + practiceAreaId);
    }
    
    Assert.hasText(actionPlanId, "Action plan not found.");
    
    // get the action plan for the given action plan id
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorizing the request
    authorizeRequest(user, actionPlan, true);
    
    // update and add the action plan
    deleteActionPlanOrStep(actionPlan, practiceAreaId);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to enable admin access to create/edit action plans.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for update
   * @return the response to the update request
   */
  public SPResponse adminEnable(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String companyId = (String) params[0];
    
    /* this is step id for the organization action plan */
    boolean enableAdminAccess = (boolean) params[1];
    
    CompanyDao company = companyFactory.getCompany(companyId);
    Assert.isTrue(company.hasFeature(SPFeature.OrganizationPlan),
        "Company does not have organisation plan.");
    
    // setting the flag and updating database as well as all the users in session
    company.setActionPlanAdminEnabled(enableAdminAccess);
    companyFactory.updateCompanyDao(company);
    
    // should not be required as the organization plan has moved out of navigation
    // companyFactory.updateUsersInSession(company.getId(), ActionType.UpdateNavigation);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to assign the action plans to users.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the assign request
   */
  public SPResponse assignActionPlan(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    ManageActionPlanForm actionPlanForm = (ManageActionPlanForm) params[0];
    
    final List<User> userList = getMemberListToProcess(actionPlanForm);
    
    // iterate over the list of action plans and add the users
    actionPlanForm.getActionPlanIdList().forEach(
        apId -> addActionPlanForUser(user.getCompanyId(), getActionPlan(apId), userList));
    
    // Sending the summary response as it is required from
    // https://surepeople.mydonedone.com/issuetracker/projects/46273/issues/29
    
    Map<String, List<ActionPlanSummaryDTO>> actionPlanSummary = new HashMap<>();
    
    userList.stream()
        .forEach(
            usr -> {
              UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(usr);
              Map<String, ActionPlanProgress> completeActionsMap = userActionPlan
                  .getActionPlanProgressMap();
              actionPlanSummary.computeIfAbsent(usr.getId(),
                  k -> new ArrayList<ActionPlanSummaryDTO>());
              final List<ActionPlanSummaryDTO> actionPlans = actionPlanSummary.get(usr.getId());
              completeActionsMap.forEach((actId, actionPlanProgress) -> {
                ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actId);
                if (log.isDebugEnabled()) {
                  log.debug("Action Plan Dao is " + actionPlan + ", actId " + actId);
                }
                if (actionPlan != null) {
                  actionPlans.add(new ActionPlanSummaryDTO(actionPlan, actionPlanProgress
                      .getCompletedCount()));
                }
              });
            });
    
    resp.add("usrActSum", actionPlanSummary);
    return resp.isSuccess();
  }
  
  /**
   * Helper method to remove the action plan assignment for users.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse removeActionPlan(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    ManageActionPlanForm actionPlanForm = (ManageActionPlanForm) params[0];
    
    final List<User> userList = getMemberListToProcess(actionPlanForm);
    
    // iterate over the list of action plans and remove the users
    ArrayList<String> ignoredActionPlans = new ArrayList<String>();
    for (String actionPlanId : actionPlanForm.getActionPlanIdList()) {
      ActionPlanDao actionPlan = getActionPlan(actionPlanId);
      if (actionPlan.isActive()) {
        CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
            actionPlanId, user.getCompanyId());
        if (capSettings != null && !capSettings.isOnHold()) {
          removeActionPlanForUsers(actionPlan, userList, capSettings, false, false);
        } else {
          ignoredActionPlans.add(actionPlanId);
        }
      } else {
        ignoredActionPlans.add(actionPlanId);
      }
    }
    
    // adding the plans that were ignored
    resp.add(Constants.PARAM_ACTION_PLAN_ERROR, ignoredActionPlans);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to activate and deactivate action plans.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return activate or deactivate action plans
   */
  public SPResponse activateDeactivate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String actionPlanId = (String) params[0];
    final boolean onHold = (boolean) params[1];
    String companyId = (String) params[2];
    
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorize the action plan request
    authorizeRequest(user, actionPlan);
    
    // check if the plan is published
    Assert.isTrue(actionPlan.isActive(), "Plan not published.");
    
    if (!user.hasAnyRole(RoleType.SuperAdministrator, RoleType.SysOrgPlans)) {
      companyId = user.getCompanyId();
    }
    
    // get the company settings
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        actionPlanId, companyId);
    Assert.notNull(capSettings, "Unauthorised access.");
    
    // check if the flags are not the same
    if (capSettings.isOnHold() != onHold) {
      // check if it is the activate flow or the deactivate flow
      if (onHold) {
        ArrayList<String> savedMemberIds = new ArrayList<String>(capSettings.getMemberIds());
        // processing the deactivate flow
        // removing the company action plan for the users
        removeActionPlanForUsers(capSettings, actionPlan, false, true);
        // set the activate/deactivate flag for the company action plan
        capSettings.setOnHold(true);
        capSettings.setMemberIds(savedMemberIds);
      } else {
        // set the activate/deactivate flag for the company action plan
        capSettings.setOnHold(false);
        // activate the company action plan for the users
        addActionPlanToUsers(actionPlan, capSettings, true);
      }
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to publish the action plan.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the publish request
   */
  public SPResponse publish(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String actionPlanId = (String) params[0];
    final boolean allCompanies = (boolean) params[1];
    final boolean allMembers = (boolean) params[2];
    
    // get the action plan
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorize the action plan request
    authorizeRequest(user, actionPlan, true);
    
    // check if the plan is published
    Assert.isTrue(!actionPlan.isActive(), "Plan already published.");
    
    actionPlan.setActive(true);
    
    if (actionPlan.getType() == ActionPlanType.Company) {
      // creating a new company action plan
      CompanyActionPlanSettings capSettings = actionPlan.addCompany(
          actionPlan.getCreatedByCompanyId(), actionPlanFactory);
      if (allMembers) {
        capSettings.setAllMembers(allMembers);
        actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      }
    } else {
      actionPlan.setAllMembers(allMembers);
      actionPlan.setAllCompanies(allCompanies);
    }
    
    actionPlan.updatePublishedBy(user);
    actionPlanFactory.updateActionPlan(actionPlan);
    return resp.isSuccess();
  }
  
  /**
   * Helper method to modify the edit access for the action plans.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the edit access request
   */
  public SPResponse editAccess(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String actionPlanId = (String) params[0];
    final boolean enable = (boolean) params[1];
    
    // get the action plan
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorize the action plan request
    authorizeRequest(user, actionPlan);
    
    // check if the plan is published
    Assert.isTrue(actionPlan.getType() == ActionPlanType.Company, "Unauthorised access.");
    
    actionPlan.setEditAllowed(enable);
    actionPlanFactory.updateActionPlan(actionPlan);
    return resp.isSuccess();
  }
  
  /**
   * Helper method to update the action plan all companies access.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to modify all companies
   */
  public SPResponse modifyAllCompanies(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String actionPlanId = (String) params[0];
    final boolean enable = (boolean) params[1];
    
    // get the action plan
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorize the action plan request
    authorizeRequest(user, actionPlan);
    
    // check if the plan is published and is of type SurePeople
    Assert.isTrue(actionPlan.getType() == ActionPlanType.SurePeople, "Unauthorised access.");
    Assert.isTrue(actionPlan.isActive(), "Plan not published.");
    
    actionPlan.setAllCompanies(enable);
    actionPlanFactory.updateActionPlan(actionPlan);
    return resp.isSuccess();
  }
  
  /**
   * Helper method to update the action plan all members access.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to modify all companies
   */
  public SPResponse modifyAllMembers(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String actionPlanId = (String) params[0];
    final boolean enable = (boolean) params[1];
    final String companyId = (String) params[2];
    
    // get the action plan
    final ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorize the action plan request
    authorizeRequest(user, actionPlan);
    
    // check if the plan is published and is of type SurePeople
    Assert.isTrue(actionPlan.isActive(), "Plan not published.");
    
    // check if it is for global update
    if (StringUtils.isBlank(companyId)) {
      Assert.isTrue(user.hasAnyRole(RoleType.SuperAdministrator, RoleType.SysOrgPlans),
          "Unauthorized access.");
      
      Assert.isTrue(actionPlan.getType() != ActionPlanType.Company, "Company id required.");
      
      // if the plan was not all members previously
      // then we need to force the all members flag
      // for all the companies
      final boolean prevAllMembers = actionPlan.isAllMembers();
      actionPlan.setAllMembers(enable);
      if (!prevAllMembers) {
        // setting the all members flag for all the companies to true
        final List<String> companyIds = actionPlan.getCompanyIds();
        if (!CollectionUtils.isEmpty(companyIds)) {
          companyIds.forEach(c -> updateCompanyAllMembers(c, actionPlan));
        }
      }
      actionPlanFactory.updateActionPlan(actionPlan);
    } else {
      // validate the company
      companyFactory.getCompany(companyId);
      // get the company action plan settings
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);
      if (capSettings == null) {
        Assert.isTrue(
            actionPlan.getType() == ActionPlanType.SurePeople && actionPlan.isAllCompanies(),
            "Unauthorized access.");
        // adding a new company action plan settings
        capSettings = actionPlan.addCompany(companyId, actionPlanFactory);
        actionPlanFactory.updateActionPlan(actionPlan);
      }
      
      // check if the action plan is enforcing all members
      Assert.isTrue(!actionPlan.isAllMembers(), "Unauthorized access.");
      
      capSettings.setAllMembers(enable);
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
    }
    return resp.isSuccess();
  }
  
  /**
   * Helper method to add a company to the action plan.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add request
   */
  @SuppressWarnings("unchecked")
  public SPResponse addCompany(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final List<String> actionPlanIds = (List<String>) params[0];
    Assert.notEmpty(actionPlanIds, "At least one action plan required.");
    
    final String companyId = (String) params[1];
    
    // validating the company
    companyFactory.getCompany(companyId);
    
    List<ActionPlanListingDTO> addedActionPlans = new ArrayList<ActionPlanListingDTO>();
    for (String actionPlanId : actionPlanIds) {
      // get the action plan
      ActionPlanDao actionPlan = getActionPlan(actionPlanId);
      
      // authorize the action plan request
      authorizeRequest(user, actionPlan);
      
      // check if the plan is published and is of type SurePeople
      Assert.isTrue(actionPlan.isActive(), "Plan not published.");
      Assert.isTrue(actionPlan.getType() == ActionPlanType.SurePeople, "Not a global plan.");
      Assert.isTrue(!actionPlan.hasCompany(companyId), "Company already added.");
      
      CompanyActionPlanSettings capSettings = actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
      addedActionPlans.add(new ActionPlanListingDTO(actionPlan, capSettings));
    }
    return resp.add(Constants.PARAM_ACTION_PLAN_LISTING, addedActionPlans);
  }
  
  /**
   * Helper method to remove a company to the action plan.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse removeCompany(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String actionPlanId = (String) params[0];
    final String companyId = (String) params[1];
    
    // get the action plan
    ActionPlanDao actionPlan = getActionPlan(actionPlanId);
    
    // authorize the action plan request
    authorizeRequest(user, actionPlan);
    
    // validate the company
    companyFactory.getCompany(companyId);
    
    // check if the plan is published and is of type SurePeople
    Assert.isTrue(actionPlan.isActive(), "Plan not published.");
    Assert.isTrue(actionPlan.getType() == ActionPlanType.SurePeople, "Not a global plan.");
    Assert.isTrue(actionPlan.hasCompany(companyId), "Company not added.");
    
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        actionPlanId, companyId);
    
    // removing the company action plan and all the users
    removeActionPlanForUsers(capSettings, actionPlan, true, false);
    
    // removing the company from the action plan
    actionPlan.removeCompany(companyId);
    actionPlanFactory.updateActionPlan(actionPlan);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get SP programs for a company.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getGlobalPrograms(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String companyId = (String) params[0];
    
    // validate the company
    companyFactory.getCompany(companyId);
    
    // get all the global action plans
    List<ActionPlan> globalActionPlans = actionPlanFactory.getAllGlobalPrograms();
    
    // adding the action plans to the response
    return resp.add(
        Constants.PARAM_ACTION_PLAN_LIST,
        globalActionPlans.stream()
            .filter(ap -> ap.isActive() && !ap.isAllCompanies() && !ap.hasCompany(companyId))
            .map(BaseActionPlanDTO::new).collect(Collectors.toList()));
  }
  
  /**
   * Update the company action plan settings.
   * 
   * @param companyId
   *          - company id
   * @param actionPlan
   *          - action plan
   */
  private void updateCompanyAllMembers(String companyId, ActionPlanDao actionPlan) {
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        actionPlan.getId(), companyId);
    if (capSettings != null) {
      capSettings.setAllMembers(actionPlan.isAllMembers());
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
    }
  }
  
  /**
   * <code>authroizeRequest</code> method will validate whether the request belong to same company
   * in case request is coming from Company Account Page. Defaults to check edit not required.
   * 
   * @param user
   *          logged in user.
   * @param actionPlan
   *          for
   */
  private void authorizeRequest(User user, ActionPlan actionPlan) {
    authorizeRequest(user, actionPlan, false);
  }
  
  /**
   * <code>authroizeRequest</code> method will validate whether the request belong to same company
   * in case request is coming from Company Account Page.
   * 
   * @param user
   *          logged in user.
   * @param actionPlan
   *          for
   * @param checkEditAllowed
   *          - flag to check if edit is allowed
   */
  private void authorizeRequest(User user, ActionPlan actionPlan, boolean checkEditAllowed) {
    if (!(user.hasRole(RoleType.SuperAdministrator) || user.hasRole(RoleType.SysOrgPlans))) {
      Assert.isTrue(actionPlan.authorizeCompany(user.getCompanyId()), "Unauthorized access.");
      if (checkEditAllowed) {
        Assert.isTrue(actionPlan.isEditAllowed(), "Unauthorized access.");
        Assert.isTrue(actionPlan.getType() == ActionPlanType.Company,
            "Cannot modify a global plan.");
      }
    }
  }
  
  /**
   * Add the action plan to the company action plan as per applicable.
   * 
   * @param ap
   *          - action plan
   * @param companyActionPlanMap
   *          - company action plan map
   */
  private void addToCompany(ActionPlan ap,
      Map<String, CompanyActionPlanListingDTO> companyActionPlanMap) {
    
    if (ap.getType() == ActionPlanType.SurePeople) {
      if (ap.isAllCompanies()) {
        ActionPlanListingDTO globalActionPlanListing = new ActionPlanListingDTO(ap);
        for (CompanyActionPlanListingDTO cap : companyActionPlanMap.values()) {
          final String companyId = cap.getCompanyId();
          CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
              ap.getId(), companyId);
          if (capSettings != null) {
            cap.addActionPlan(ap, capSettings);
          } else {
            cap.addActionPlan(globalActionPlanListing);
          }
        }
      } else {
        final List<String> companyIds = ap.getCompanyIds();
        if (!CollectionUtils.isEmpty(companyIds)) {
          for (String companyId : companyIds) {
            addToCompanyListing(ap, companyActionPlanMap, companyId);
          }
        }
      }
    } else {
      final String companyId = ap.getCreatedByCompanyId();
      addToCompanyListing(ap, companyActionPlanMap, companyId);
    }
  }
  
  private void addToCompanyListing(ActionPlan ap,
      Map<String, CompanyActionPlanListingDTO> companyActionPlanMap, String companyId) {
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        ap.getId(), companyId);
    CompanyActionPlanListingDTO cap = companyActionPlanMap.get(companyId);
    if (cap != null) {
      cap.addActionPlan(ap, capSettings);
    }
  }
  
  /**
   * Check if the action plan is of SP Type.
   * 
   * @param ap
   *          - action plan
   * 
   * @return true if it is to be added else false
   */
  private boolean addSPActionPlan(ActionPlan ap) {
    return ap.getType() == ActionPlanType.SurePeople;
  }
  
  /**
   * Get the member list to process for both assign and removal of action plans.
   * 
   * @param actionPlanForm
   *          - action plan form
   * @return the list of members to process
   */
  private List<User> getMemberListToProcess(ManageActionPlanForm actionPlanForm) {
    // validate the action plan form
    actionPlanForm.validate();
    
    // get the list of users to update
    final List<User> userList = new ArrayList<User>();
    final List<String> userIdList = Optional.ofNullable(actionPlanForm.getUserIdList()).orElse(
        new ArrayList<String>());
    if (!userIdList.isEmpty()) {
      userList.addAll(userIdList.stream().map(userFactory::getUser).filter(u -> u != null)
          .collect(Collectors.toList()));
    }
    
    // iterate over all the groups and add the users to the user list
    final List<GroupPermissions> groupPermissionsList = actionPlanForm.getGroupPermissionsList();
    if (!CollectionUtils.isEmpty(groupPermissionsList)) {
      groupPermissionsList.forEach(gp -> addGroupUsers(gp, userList, userIdList));
    }
    return userList;
  }
  
  /**
   * Get the action plan for the given action plan id.
   * 
   * @param actionPlanId
   *          - action plan id
   * @return the action plan
   */
  private ActionPlanDao getActionPlan(String actionPlanId) {
    // get the action plan
    final ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
    // validate if action plan is present
    Assert.notNull(actionPlan, "Learning plan not found.");
    return actionPlan;
  }
  
  /**
   * Remove all the users of the action plan from the given action plan.
   * 
   * @param actionPlan
   *          - action plan
   */
  private void removeActionPlanForUsers(ActionPlanDao actionPlan, boolean isActionPlanDelete) {
    String apId = actionPlan.getId();
    if (!CollectionUtils.isEmpty(actionPlan.getCompanyIds())) {
      for (String companyId : actionPlan.getCompanyIds()) {
        CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
            apId, companyId);
        removeActionPlanForUsers(capSettings, actionPlan, isActionPlanDelete, false);
      }
    }
  }
  
  /**
   * Remove the members for the given action plan settings.
   * 
   * @param capSettings
   *          - cap settings
   * @param actionPlan
   *          - action plan
   * @param isActionPlanDelete
   *          - is the action plan being deleted
   */
  private void removeActionPlanForUsers(CompanyActionPlanSettings capSettings,
      ActionPlanDao actionPlan, boolean isActionPlanDelete, boolean onHold) {
    
    if (capSettings != null) {
      // check if the action plan is on hold then remove is not supported
      if (!isActionPlanDelete) {
        Assert.isTrue(!capSettings.isOnHold(), MessagesHelper.getMessage("orgplan.error.planhold"));
      }
      
      // if action plan settings is on
      // hold then there is no need to remove from the users
      if (!capSettings.isOnHold()) {
        removeActionPlanForUsers(actionPlan, getMembers(capSettings), capSettings,
            isActionPlanDelete, onHold);
      }
      
      if (isActionPlanDelete) {
        actionPlanFactory.removeCompanyActionPlanSettings(capSettings);
        /* delete the public channel associated with action plan. */
        final String companyId = capSettings.getCompanyId();
        final String actionPlanId = actionPlan.getId();
        publicChannelHelper.deletePublicChannelByParent(companyId, actionPlanId);
        feedbackFactory.deleteByParentRefId(actionPlanId, companyId);
        badgeFactory.deleteBadgeProgress(actionPlanId);
      } else {
        actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      }
    }
  }
  
  /**
   * Remove the action plan for the given users.
   * 
   * @param actionPlan
   *          - action plan
   * @param userList
   *          - user list
   * @param isStatusChange
   *          - flag to indicate if there is a status change
   * @param capSettings
   *          - company action plan settings
   * @param isActionPlanDelete
   *          - action plan delete flag
   * @param onHold
   *          - flag to indicate plan going on hold
   */
  private void removeActionPlanForUsers(ActionPlanDao actionPlan, List<User> userList,
      CompanyActionPlanSettings capSettings, boolean isActionPlanDelete, boolean onHold) {
    
    // prepare the params for adding the action plan for the users
    final String actionPlanId = actionPlan.getId();
    final String actionPlanName = actionPlan.getName();
    final Map<String, Object> params = new HashMap<String, Object>();
    params.put(Constants.PARAM_ACTION_PLAN_NAME, actionPlanName);
    params.put(Constants.PARAM_MESSAGE,
        MessagesHelper.getMessage("log.ActionPlan.ActionPlanRemove.message", actionPlanName));
    params.put(Constants.PARAM_NOTIFICATION_URL_PARAM, actionPlan.getId());
    
    final List<String> memberIds = capSettings.getMemberIds();
    ArrayList<String> sseMemebers = new ArrayList<String>();
    
    // add the action plan to the user also filtering users that are already present in the
    // organization plan
    for (User user : userList) {
      final String userId = user.getId();
      if (memberIds.contains(userId)) {
        
        /* set the locale user specific for the user */
        if (onHold) {
          params.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.ActionPlanOnHold", user.getLocale(), actionPlanName));
          params.put(
              Constants.PARAM_NOTIFICATION_MESSAGE,
              MessagesHelper.getMessage(LogActionType.ActionPlanOnHold.getMessageKey(),
                  user.getLocale(), actionPlanName));
          params.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.ActionPlanOnHold", user.getLocale(), actionPlanName));
        } else {
          params.put(
              Constants.PARAM_NOTIFICATION_MESSAGE,
              MessagesHelper.getMessage(LogActionType.ActionPlanRemove.getMessageKey(),
                  user.getLocale(), actionPlanName));
          params.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.ActionPlanRemove", user.getLocale(), actionPlanName));
        }
        removeActionPlanForUser(user, actionPlanId, memberIds, params, isActionPlanDelete, onHold);
        sseMemebers.add(userId);
      }
    }
    
    // send sse
    if (!sseMemebers.isEmpty()) {
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
    }
  }
  
  /**
   * Removes the given action plan from the user.
   * 
   * @param user
   *          - user
   * @param actionPlanId
   *          - action plan id
   * @param actionPlanName
   *          - action plan name
   * @param userIdList
   *          - user id list
   * @param params
   *          - params
   * @param onHold
   *          - action plan on hold
   * @param isPlanActive
   *          - flag to indicate if the plan is active
   */
  private void removeActionPlanForUser(User user, String actionPlanId, List<String> userIdList,
      Map<String, Object> params, boolean isActionPlanDelete, boolean onHold) {
    
    // get the user action plan
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    
    // removing the action plan
    userActionPlan.removeActionPlan(user, actionPlanId, isActionPlanDelete, todoFactory,
        publicChannelHelper);
    if (onHold) {
      badgeFactory.suspendBadgeProgress(actionPlanId, user);
    } else {
      badgeFactory.deleteBadgeProgress(actionPlanId, user);
    }
    
    // updating the action plan in db
    actionPlanFactory.updateUserActionPlan(userActionPlan);
    
    // removing the user id from the action plan
    userIdList.remove(user.getId());
    
    // sending the notification
    if (onHold) {
      notificationsProcessor.process(NotificationType.ActionPlanOnHold, user, user, params);
    } else {
      notificationsProcessor.process(NotificationType.ActionPlanRemove, user, user, params);
    }
    // userFactory.addMessage(user, SPFeature.OrganizationPlan,
    // (String) params.get(Constants.PARAM_MESSAGE));
  }
  
  /**
   * Add the action plan to the users present in the action plan.
   * 
   * @param actionPlan
   *          - action plan
   * @param capSettings
   *          - company action plan settings
   * @param planResume
   *          - flag to indicate if the plan has been resumed.
   */
  private void addActionPlanToUsers(ActionPlanDao actionPlan,
      CompanyActionPlanSettings capSettings, boolean planResume) {
    // getting the users from the cap settings
    final List<User> members = getMembers(capSettings);
    // resetting the member ids list
    capSettings.getMemberIds().clear();
    addActionPlanForUser(capSettings, actionPlan, members, planResume);
  }
  
  /**
   * Adding the action plan for the users in the company action plan settings.
   * 
   * @param companyId
   *          - company id
   * @param actionPlan
   *          - action plan
   * @param userList
   *          - user list
   */
  private void addActionPlanForUser(String companyId, ActionPlanDao actionPlan, List<User> userList) {
    CompanyActionPlanSettings capSettings = getCompanyActionPlan(companyId, actionPlan);
    if (capSettings == null) {
      Assert.isTrue(
          actionPlan.getType() == ActionPlanType.SurePeople && actionPlan.isAllCompanies(),
          "Unauthorised request.");
      capSettings = actionPlanFactory.createCompanyActionPlanSettings(companyId, actionPlan);
      actionPlanFactory.updateActionPlan(actionPlan);
    }
    addActionPlanForUser(capSettings, actionPlan, userList, false);
  }
  
  /**
   * Add the list of users to the given action plan.
   * 
   * @param actionPlan
   *          - action plan
   * @param userList
   *          - user list
   * @param planResume
   *          - flag to indicate if plan resumed
   */
  private void addActionPlanForUser(CompanyActionPlanSettings capSettings,
      ActionPlanDao actionPlan, List<User> userList, boolean planResume) {
    
    Assert.isTrue(actionPlan.isActive(), "Plan not active.");
    
    // check if the company action plan settings is not on hold
    Assert.isTrue(!capSettings.isOnHold(), MessagesHelper.getMessage("orgplan.error.planhold"));
    
    // prepare the params for adding the action plan for the users
    final String actionPlanName = actionPlan.getName();
    final Map<String, Object> params = new HashMap<String, Object>();
    params.put(Constants.PARAM_ACTION_PLAN_NAME, actionPlanName);
    params.put(Constants.PARAM_ACTION_PLAN_DESCRIPTION, actionPlan.getDescription());
    params.put(Constants.PARAM_NOTIFICATION_URL_PARAM, actionPlan.getId());
    
    final List<String> memberIds = capSettings.getMemberIds();
    ArrayList<String> sseMemberIds = new ArrayList<String>();
    for (User u : userList) {
      final String userId = u.getId();
      params.put(Constants.PARAM_MESSAGE, MessagesHelper.getMessage(
          "log.ActionPlan.ActionPlanAssign.message", u.getLocale(), actionPlanName));
      
      if (!memberIds.contains(userId)) {
        /* setting the subject with locale specific to the user */
        
        if (planResume) {
          params.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.ActionPlanResume", u.getLocale(), actionPlanName));
          params.put(
              Constants.PARAM_NOTIFICATION_MESSAGE,
              MessagesHelper.getMessage(LogActionType.ActionPlanResume.getMessageKey(),
                  u.getLocale(), actionPlanName));
          
          params.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.ActionPlanResume", u.getLocale(), actionPlanName));
        } else {
          params.put(
              Constants.PARAM_NOTIFICATION_MESSAGE,
              MessagesHelper.getMessage(LogActionType.ActionPlanAssign.getMessageKey(),
                  u.getLocale(), actionPlanName));
        }
        addActionPlan(u, actionPlan, memberIds, params, planResume);
        sseMemberIds.add(userId);
      }
    }
    
    if (!sseMemberIds.isEmpty()) {
      // updating the action plan with all the newly added users
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
    }
  }
  
  /**
   * Get the company action plan.
   * 
   * @param companyId
   *          - company id
   * @param actionPlan
   *          - action plan
   * @return company action plan
   */
  private CompanyActionPlanSettings getCompanyActionPlan(String companyId, ActionPlanDao actionPlan) {
    // getting the company action plan settings
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        actionPlan.getId(), companyId);
    
    // creating a new cap setting if not found
    if (capSettings == null) {
      capSettings = actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
    }
    return capSettings;
  }
  
  /**
   * Add the action plan to the user.
   * 
   * @param user
   *          - user
   * @param actionPlanId
   *          - action plan id
   * @param actionPlanName
   *          - action plan name
   * @param userIdList
   *          - user id list
   * @param params
   *          - params for notification
   * @param planResume
   *          - flag to indicate if the plan resumed
   */
  private void addActionPlan(User user, ActionPlanDao actionPlan, List<String> userIdList,
      Map<String, Object> params, boolean planResume) {
    // get the user action plan
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    // adding the action plan to the user
    actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlan, Operation.ADD);
    // adding the user id to the action plan
    userIdList.add(user.getId());
    
    /* add the badge progress to the user */
    badgeFactory.addToBadgeProgress(user, actionPlan.getId(), BadgeType.OrgPlan, actionPlan);
    
    // sending the notification
    if (planResume) {
      notificationsProcessor.process(NotificationType.ActionPlanResume, user, user, params);
    } else {
      notificationsProcessor.process(NotificationType.ActionPlanAssign, user, user, params);
    }
    // userFactory.addMessage(user, SPFeature.OrganizationPlan,
    // (String) params.get(Constants.PARAM_MESSAGE));
  }
  
  /**
   * Add the users for the given group.
   * 
   * @param gp
   *          - group
   * @param userList
   *          - user list
   * @param userIdList
   *          - user id list
   */
  private void addGroupUsers(GroupPermissions gp, List<User> userList, List<String> userIdList) {
    // get the user group
    UserGroup userGroup = groupRepository.findById(gp.getGroupId());
    Assert.notNull(userGroup, "Group not found.");
    
    // create a list of members to process also add group lead as per group permissions
    final List<String> memberList = new ArrayList<String>(userGroup.getMemberList());
    if (!gp.isExcludeGroupLead() && userGroup.groupLeadPresent()) {
      memberList.add(userGroup.getGroupLead());
    }
    
    // process the add for all the members
    memberList.stream().map(userFactory::getUserByEmail)
        .filter(u -> !userIdList.contains(u.getId()) && !memberList.contains(u))
        .forEach(userList::add);
  }
  
  /**
   * DeleteActionPlanOrStep method will delete the action plan with the corresponding practice area.
   * 
   * @param actionPlan
   *          to be deleted.
   * @param practiceAreaId
   *          practice area id which is the step id in case step is deleted.
   */
  private void deleteActionPlanOrStep(ActionPlanDao actionPlan, String practiceAreaId) {
    
    /* check if only step is to be deleted or all. */
    final String actionPlanId = actionPlan.getId();
    if (StringUtils.isEmpty(practiceAreaId)) {
      List<String> practiceAreaIdList = actionPlan.getPracticeAreaIdList();
      /* delete complete action plan with all feedback, notes. */
      if (!CollectionUtils.isEmpty(practiceAreaIdList)) {
        practiceAreaIdList.forEach(this::removeGoal);
      }
      
      // removing the action plan for the users
      removeActionPlanForUsers(actionPlan, true);
      
      // removing the action plan
      actionPlanFactory.deletedActionPlan(actionPlan);
      
      publicChannelHelper.deletePublicChannelByParent(actionPlanId);
      feedbackFactory.deleteByParentRefId(actionPlanId);
      
      // delete the badges in progress state
      badgeFactory.deleteBadgeProgress(actionPlanId);
      
    } else {
      boolean isDeleted = actionPlan.removePracticeArea(practiceAreaId, publicChannelHelper);
      if (isDeleted) {
        feedbackFactory.deleteByDevFeedRefId(practiceAreaId);
        SPGoal spGoal = goalsFactory.getGoal(practiceAreaId);
        goalsFactory.removeGoal(spGoal);
        if (spGoal.isActive()) {
          actionPlan.reduceActionCount(spGoal.getActionCount());
        }
        actionPlanFactory.updateActionPlan(actionPlan);
        actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
        updateUserActionPlans(actionPlan);
        publicChannelHelper.deletePublicChannel(practiceAreaId);
        feedbackFactory.deleteByDevFeedRefId(practiceAreaId);
      }
    }
  }
  
  /**
   * Remove the given goal id.
   * 
   * @param goalId
   *          - goal id
   */
  private void removeGoal(String goalId) {
    spNoteFeedbackRepository.deleteAllFeedback(goalId);
    spNoteFeedbackRepository.deleteAllNote(goalId);
    SPGoal spGoal = goalsFactory.getGoal(goalId);
    goalsFactory.removeGoal(spGoal);
  }
  
  /**
   * Add update the action plan from the data present in the action plan form.
   * 
   * @param actionPlanForm
   *          - action plan form
   * @param actionPlan
   *          - action plan to update
   */
  private void addUpdateActionPlan(ActionPlanForm actionPlanForm, ActionPlan actionPlan) {
    // validate the form data
    actionPlanForm.validate();
    
    // check if the action plan with the same name already exists
    ActionPlan actionPlanByName = actionPlanFactory.getActionPlanByName(actionPlanForm.getName(),
        actionPlanForm.getCreatedByCompanyId());
    
    // check if there exists an action plan with the same name and the action plan id's do not match
    final String actionPlanId = actionPlan.getId();
    if (actionPlanByName != null) {
      if (actionPlanId == null || !actionPlanId.equals(actionPlanForm.getId())) {
        throw new InvalidRequestException(
            MessagesHelper.getMessage("admin.organization.planName.exists"));
      }
    }
    
    // validate the company
    companyFactory.getCompany(actionPlanForm.getCreatedByCompanyId());
    
    // update the action plan data
    actionPlanForm.updateActionPlan(actionPlan);
    
    // check if action plan has id as this is required for the UID for each action
    if (actionPlanId == null) {
      // create the action plan so that it can get the id
      actionPlanFactory.createActionPlan(actionPlan);
    }
    
    // check if the practice area list is present
    final List<String> practiceAreaIdList = new ArrayList<String>();
    
    final List<SPGoal> newGoalsList = new ArrayList<SPGoal>();
    // getting the existing goals for the action plan
    final Map<String, SPGoal> existingGoalsMap = actionPlan.createOrGetPracticeAreaIdList()
        .stream().map(goalsFactory::getGoal).filter(g -> g != null)
        .collect(Collectors.toMap(SPGoal::getId, Function.identity()));
    
    try {
      // get the list of goals to create or update
      int actionCount = actionPlanForm
          .getPracticeAreaList()
          .stream()
          .mapToInt(
              paaf -> createUpdateGoal(paaf, practiceAreaIdList, newGoalsList,
                  actionPlan::getNextUID, existingGoalsMap)).sum();
      
      // setting the practice area list with the new order
      actionPlan.setPracticeAreaIdList(practiceAreaIdList);
      actionPlan.setActionCount(actionCount);
      
      // add or update the action plan
      actionPlanFactory.updateActionPlan(actionPlan);
      
      // if action plan is active then update the data
      // for all the companies
      if (actionPlan.isActive()) {
        updateUserActionPlans(actionPlanFactory.getActionPlan(actionPlanId));
      }
    } catch (Exception e) {
      // deleting any newly created goals
      newGoalsList.forEach(goalsFactory::removeGoal);
      throw e;
    }
  }
  
  /**
   * Update the users for the given action plan.
   * 
   * @param actionPlanDao
   *          - action plan
   */
  private void updateUserActionPlans(final ActionPlanDao actionPlanDao) {
    List<String> companyIds = actionPlanDao.getCompanyIds();
    if (!CollectionUtils.isEmpty(companyIds)) {
      companyIds.forEach(cid -> updateUserActionPlans(cid, actionPlanDao));
    }
  }
  
  /**
   * Update the user action plan settings for the given company.
   * 
   * @param companyId
   *          - company id
   * @param actionPlanDao
   *          - action plan dao
   */
  private void updateUserActionPlans(String companyId, ActionPlanDao actionPlanDao) {
    final String actionPlanId = actionPlanDao.getId();
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        actionPlanId, companyId);
    if (capSettings != null && !capSettings.isOnHold()) {
      final List<String> memberIds = capSettings.getMemberIds();
      if (!CollectionUtils.isEmpty(memberIds)) {
        ArrayList<String> userIdsToRemove = null;
        for (String userId : memberIds) {
          User user = userFactory.getUser(userId);
          if (user != null) {
            updateActionPlan(user, actionPlanDao);
          } else {
            if (userIdsToRemove == null) {
              userIdsToRemove = new ArrayList<String>();
            }
            userIdsToRemove.add(userId);
          }
        }
        // check if there were any wrong users then remove the same
        if (!CollectionUtils.isEmpty(userIdsToRemove)) {
          memberIds.removeAll(userIdsToRemove);
          actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
        }
        
        // sending update to refresh the action plan
        actionPlanFactory.sendSSE(companyId, memberIds, Operation.UPDATE, actionPlanId);
      }
    }
  }
  
  /**
   * This method updates the user action plan for the given user.
   * 
   * @param user
   *          - user
   * @param actionPlanDao
   *          - action plan dao
   */
  private void updateActionPlan(User user, ActionPlanDao actionPlanDao) {
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlanDao,
        Operation.UPDATE);
  }
  
  /**
   * Create or update the given goal and update the action plan.
   * 
   * @param actionForm
   *          - goal form data
   * @param practiceAreaList
   *          - action plan practice area list to update
   * @param newGoalsList
   *          - the newly added goals
   * @param existingGoalsMap
   *          - map of existing goals
   * @return the count of actions for the goal
   */
  private int createUpdateGoal(PracticeAreaActionForm actionForm, List<String> practiceAreaList,
      List<SPGoal> newGoalsList, Supplier<String> uidGenerator, Map<String, SPGoal> existingGoalsMap) {
    SPGoal spGoal = null;
    String goalId = actionForm.getId();
    if (StringUtils.isEmpty(goalId)) {
      spGoal = new SPGoal();
      spGoal.setCategory(GoalCategory.ActionPlan);
      newGoalsList.add(spGoal);
    } else {
      spGoal = existingGoalsMap.get(goalId);
      if (spGoal == null) {
        throw new InvalidRequestException("Goal not found.");
      }
    }
    
    // validating title of goal is not same
    final String goalName = actionForm.getName();
    Optional<SPGoal> findFirst = existingGoalsMap.values().stream()
        .filter(g -> goalName.equalsIgnoreCase(g.getName())).findFirst();
    if (findFirst.isPresent()) {
      SPGoal existingGoal = findFirst.get();
      if (!existingGoal.getId().equalsIgnoreCase(goalId)) {
        throw new InvalidRequestException(
            MessagesHelper.getMessage("admin.organization.stepName.exists"));
      }
    }
    
    actionForm.updateGoal(spGoal, uidGenerator);
    goalsFactory.updateGoal(spGoal);
    if (StringUtils.isEmpty(goalId)) {
      goalId = spGoal.getId();
      existingGoalsMap.put(goalId, spGoal);
    }
    practiceAreaList.add(goalId);
    
    /*
     * In case of inactive goal status, we have to remove the all the development feedback for the
     * user
     */
    if (spGoal.getStatus() == GoalStatus.INACTIVE) {
      feedbackFactory.deleteByDevFeedRefId(goalId);
    }
    return spGoal.getActionCount();
  }
  
  /**
   * Get the members for the given company action plan.
   * 
   * @param capSettings
   *          - company action plan settings
   * @return the member list
   */
  private List<User> getMembers(CompanyActionPlanSettings capSettings) {
    return capSettings.getMemberIds().stream().map(userFactory::getUser).filter(u -> u != null)
        .collect(Collectors.toList());
  }
  
  /**
   * Method to get the company action plan settings.
   * 
   * @param user
   *          - user
   * @param ap
   *          - action plan
   * @return the company action plan settings
   */
  private CompanyActionPlanSettings getCompanyActionPlanSettings(User user, ActionPlan ap) {
    return actionPlanFactory.getCompanyActionPlanSettings(ap.getId(), user.getCompanyId());
  }
}
