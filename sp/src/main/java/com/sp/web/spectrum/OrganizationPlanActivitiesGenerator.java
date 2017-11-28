package com.sp.web.spectrum;

import com.sp.web.Constants;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.spectrum.OrgPlanActivity;
import com.sp.web.model.spectrum.OrgPlanGroupActivity;
import com.sp.web.model.spectrum.OrgPlanSteps;
import com.sp.web.model.spectrum.OrgPlanUserActivity;
import com.sp.web.model.spectrum.OrganizationPlanActivities;
import com.sp.web.model.spectrum.SpectrumFilter;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ProfileGenerator class will generate the profile balance data for the spectrum.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class OrganizationPlanActivitiesGenerator {
  
  private static final Logger LOG = Logger.getLogger(OrganizationPlanActivitiesGenerator.class);
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  GroupRepository groupRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * getOrganizationPlanActivities method will calculate the org plan for a company and will return
   * the result.
   * 
   * @param users
   *          list of the users
   * @return the Proifle Balance.
   */
  @Cacheable(value = "organizationPlanActivities", key = "#companyId")
  public OrganizationPlanActivities getOrganizationPlanActivities(String companyId, List<User> users) {
    List<User> validUsers = users.stream().filter(usr -> usr.getUserStatus() == UserStatus.VALID)
        .collect(Collectors.toList());
    
    List<ActionPlanDao> companiesActionPlans = actionPlanFactory
        .getAllActionPlansForCompany(companyId).stream().filter(act -> act.isActive())
        .map(actPlan -> actionPlanFactory.getActionPlan(actPlan.getId()))
        .collect(Collectors.toList());
    
    OrganizationPlanActivities organizationPlanActivities = new OrganizationPlanActivities();
    organizationPlanActivities.setTotalOrgPlan(companiesActionPlans.size());
    organizationPlanActivities.setCompanyId(companyId);
    if (companiesActionPlans.size() == 0) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("No action plans are present for the company ");
      }
      return organizationPlanActivities;
    }
    
    /* creating a map of user action agains the selected Action plan */
    
    Map<String, String> orgPlans = new HashMap<>();
    Map<String, OrgPlanActivity> orgPlanActivitesMap = new HashMap<>();
    companiesActionPlans.stream().forEach(
        actPlan -> {
          CompanyActionPlanSettings companyActionPlanSettings = actionPlanFactory
              .getCompanyActionPlanSettings(actPlan.getId(), companyId);
          if (companyActionPlanSettings != null) {
            OrgPlanActivity orgPlanActivity = orgPlanActivitesMap.get(actPlan.getId());
            if (orgPlanActivity == null) {
              orgPlanActivity = new OrgPlanActivity();
              orgPlanActivitesMap.put(actPlan.getId(), orgPlanActivity);
            }
            int totalKpi = 0;
            for (SPGoal spGoal : actPlan.getPracticeAreaList()) {
              if (spGoal.getStatus() != GoalStatus.ACTIVE) {
                continue;
              }
              OrgPlanSteps orgPlanStep = new OrgPlanSteps();
              orgPlanStep.setStepName(spGoal.getName());
              orgPlanActivity.getOrgPlanSteps().add(orgPlanStep);
              final List<String> actionIds = spGoal.findActionIds();
              orgPlanStep.setUids(actionIds);
              totalKpi += actionIds.size();
            }
            orgPlanActivity.setTotalKpi(totalKpi);
            orgPlanActivity.setPlanId(actPlan.getId());
            orgPlanActivity.setPlanName(actPlan.getName());
            orgPlanActivity.setOnHold(companyActionPlanSettings.isOnHold());
            
            organizationPlanActivities.getOrgPlanActivities().add(orgPlanActivity);
            orgPlans.put(actPlan.getId(), actPlan.getName());
          }
          
        });
    
    /* Create a map against groupid */
    Map<String, Map<String, OrgPlanGroupActivity>> groupsActivity = new HashMap<String, Map<String, OrgPlanGroupActivity>>();
    
    /*
     * In case an org plan belongs to all companies then we have to loop through all members, so
     * doing it once for all the org plan to increase performance.
     */
    for (User usr : validUsers) {
      for (Map.Entry<String, OrgPlanActivity> orgPlanEntry : orgPlanActivitesMap.entrySet()) {
        
        Map<String, OrgPlanGroupActivity> map = groupsActivity.get(orgPlanEntry.getKey());
        if (map == null) {
          map = new HashMap<String, OrgPlanGroupActivity>();
          groupsActivity.put(orgPlanEntry.getKey(), map);
        }
        
        if (StringUtils.isNotBlank(usr.getUserActionPlanId())) {
          UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(usr);
          if (userActionPlan != null
              && StringUtils.isNotBlank(userActionPlan.getSelectedActionPlan())) {
            ActionPlanProgress actionPlanProgress = userActionPlan
                .getActionPlanProgress(orgPlanEntry.getKey());
            Set<String> completdActions = new HashSet<>();
            if (actionPlanProgress != null) {
              OrgPlanUserActivity orgPlanUserActivity = new OrgPlanUserActivity(usr);
              if (actionPlanProgress.getCompletedActionsForStep() != null) {
                actionPlanProgress.getCompletedActionsForStep().values().stream().forEach(com -> {
                  completdActions.addAll(com);
                });
              }
              int completedCount = actionPlanProgress.getCompletedCount();
              orgPlanUserActivity.setCompletedLKpi(completedCount);
              orgPlanUserActivity.setCompletedUserSteps(completdActions);
              orgPlanEntry.getValue().addCompletdKpi(completedCount);
              
              for (GroupAssociation ga : usr.getGroupAssociationList()) {
                String groupId = ga.getGroupId();
                OrgPlanGroupActivity orgPlanGroupActivity = map.get(groupId);
                if (orgPlanGroupActivity == null) {
                  UserGroup userGroup = groupRepository.findById(groupId);
                  orgPlanGroupActivity = new OrgPlanGroupActivity();
                  orgPlanGroupActivity.setGroupName(userGroup.getName());
                  map.put(groupId, orgPlanGroupActivity);
                }
                
                orgPlanGroupActivity.setTotalMembers(orgPlanGroupActivity.getTotalMembers() + 1);
                
                orgPlanGroupActivity.addCompletedKpi(completdActions != null ? completdActions
                    .size() : 0);
                if (completdActions != null) {
                  orgPlanGroupActivity.getCompletedGroupSteps().addAll(completdActions);
                }
                
                /* Add the group infor to the user */
                orgPlanUserActivity.getGroups().add(ga.getName());
              }
              orgPlanEntry.getValue().getUserActivities().add(orgPlanUserActivity);
              
            }
            
          }
          
        }
        
      }
    }
    
    /* Adding the group data */
    for (Map.Entry<String, OrgPlanActivity> orgPlanEntry : orgPlanActivitesMap.entrySet()) {
      String key = orgPlanEntry.getKey();
      OrgPlanActivity value = orgPlanEntry.getValue();
      Map<String, OrgPlanGroupActivity> map = groupsActivity.get(key);
      value.getOrgPlanGroupActivities().addAll(map.values());
    }
    /* calculate the percentage */
    orgPlanActivitesMap.forEach((actKye, orgPlanAct) -> {
      // int totalMembers = orgPlanAct.getUserActivities().size();
      //
      // orgPlanAct.setCompletedKpiPercent(calculatePercentage(
      // totalMembers * orgPlanAct.getTotalKpi(), orgPlanAct.getCompletdKpi()));
      //
      // } else {
        LOG.info("OrgPlan Activity " + orgPlanAct.getUserActivities().size() + ", actionPlan"
            + orgPlanAct.getPlanId() + orgPlanAct.getPlanName());
        
        orgPlanAct.setCompletedKpiPercent(calculatePercentage(orgPlanAct.getUserActivities().size()
            * orgPlanAct.getTotalKpi(), orgPlanAct.getCompletdKpi()));
        // }
        
      });
    SpectrumFilter spectrumFilter = new SpectrumFilter();
    spectrumFilter.getFilters().put("organizationPlan", orgPlans);
    organizationPlanActivities.setSpectrumFilter(spectrumFilter);
    return organizationPlanActivities;
  }
  
  /**
   * populateGroupActivity will populate orggroup plan acitvity with the group id passed.
   * 
   * @param actionPlan
   *          for which orgplan group activity is to populated.
   * @param planActivity
   *          for the org plan.
   * @param grpList
   *          for the group.
   * @param groupId
   *          of the group.
   * @return the org plan group activity.
   */
  private OrgPlanGroupActivity populateGroupActivity(ActionPlan actionPlan,
      OrgPlanActivity planActivity, boolean isExcuteGroupLead, String groupId, String companyId) {
    UserGroup userGroup = groupRepository.findById(groupId);
    if (userGroup != null) {
      return populateGroupActivity(actionPlan, planActivity, isExcuteGroupLead, userGroup,
          companyId);
    } else {
      return null;
    }
    
  }
  
  /**
   * populateGroupActivity will populate the org plan activity with group passed.
   * 
   * @param actionPlan
   *          for which org group activity is to be created.
   * @param planActivity
   *          is the activity plan.
   * @param isExcuteGroupLead
   *          whether group lead is included or excluded.
   * @param userGroup
   *          is the user group.
   * @return the org plan group activity.
   */
  private OrgPlanGroupActivity populateGroupActivity(ActionPlan actionPlan,
      OrgPlanActivity planActivity, boolean isExcuteGroupLead, UserGroup userGroup, String companyId) {
    List<User> groupMembers = groupRepository.getMembers(companyId, userGroup.getName()).stream()
        .filter(usr -> usr.getUserStatus() == UserStatus.VALID).collect(Collectors.toList());
    
    OrgPlanGroupActivity groupActivity = new OrgPlanGroupActivity();
    groupActivity.setGroupName(userGroup.getName());
    
    // groupActivity.setTotalMembers(groupMembers.size());
    
    groupMembers.stream().forEach(
        usr -> {
          UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(usr);
          ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlan
              .getId());
          if (userActionPlan != null && actionPlanProgress != null) {
            
            groupActivity.setTotalMembers(groupActivity.getTotalMembers() + 1);
            
            Set<String> completdActions = new HashSet<>();
            actionPlanProgress.getCompletedActionsForStep().values().stream().forEach(com -> {
              completdActions.addAll(com);
            });
            ;
            
            groupActivity.addCompletedKpi(completdActions != null ? completdActions.size() : 0);
            if (completdActions != null) {
              groupActivity.getCompletedGroupSteps().addAll(completdActions);
            }
          }
          
          LOG.info("OrgPlanActivity" + planActivity);
        });
    return groupActivity;
  }
  
  @CacheEvict(value = "organizationPlanActivities", allEntries = true)
  public void reset() {
    
  }
  
  private BigDecimal calculatePercentage(int baseValue, int perc) {
    if (baseValue == 0) {
      return new BigDecimal(0);
    }
    
    return new BigDecimal(((perc * 100) / baseValue)).setScale(Constants.PULSE_PRECISION,
        Constants.ROUNDING_MODE);
  }
  
  private void addOrgGroupPlanData(ActionPlan actionPlan, OrgPlanActivity planActivity,
      CompanyActionPlanSettings actionPlanSettings) {
    
    if (actionPlanSettings.isAllMembers()) {
      List<UserGroup> allGroups = groupRepository.findAllGroups(actionPlanSettings.getCompanyId());
      allGroups.stream().forEach(
          ugroup -> {
            
            OrgPlanGroupActivity groupActivity = populateGroupActivity(actionPlan, planActivity,
                false, ugroup, actionPlanSettings.getCompanyId());
            
            planActivity.getOrgPlanGroupActivities().add(groupActivity);
            
          });
    } else {
      List<String> memberIds = actionPlanSettings.getMemberIds();
      
      memberIds.stream().forEach(
          uid -> {
            User user = userFactory.getUser(uid);
            user.getGroupAssociationList()
                .stream()
                .forEach(
                    grpList -> {
                      String groupId = grpList.getGroupId();
                      
                      OrgPlanGroupActivity groupActivity = populateGroupActivity(actionPlan,
                          planActivity, false, groupId, actionPlanSettings.getCompanyId());
                      if (groupActivity != null) {
                        planActivity.getOrgPlanGroupActivities().add(groupActivity);
                      }
                      
                    });
          });
      
    }
    
  }
  
}