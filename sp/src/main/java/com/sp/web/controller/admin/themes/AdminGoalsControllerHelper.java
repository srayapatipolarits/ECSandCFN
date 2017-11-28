package com.sp.web.controller.admin.themes;

import com.sp.web.account.AccountRepository;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.GoalDto;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.goal.GoalForm;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ThemeControllerHeper is the helper class for the themes.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class AdminGoalsControllerHelper {
  
  /** Intializing the logger. */
  private static final Logger LOG = Logger.getLogger(AdminGoalsControllerHelper.class);
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private SPGoalFactory spGoalFactory;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private UserRepository userRepository;
  
  /**
   * <code>createNewGoal</code> method will createa a new goal for the user
   * 
   * @param user
   *          adminstrator user logged in.
   * @param param
   *          is the parameter contaning the goal form.
   * @return the new SPGoal.
   */
  public SPResponse createNewGoal(User user, Object[] param) {
    
    GoalForm goalForm = (GoalForm) param[0];
    
    SPGoal spGoal = goalForm.createSPGoal();
    
    /*
     * this attribute is added, as the Growth is failling because there are no question added for
     * the groals, so this goal will be skieed from the user goals so that growth request can be
     * successfully completed.
     */
    spGoal.setAdminGoal(true);
    /* save teh goals */
    try {
      spGoalFactory.updateGoal(spGoal);
      
      updateGoalAndUsers(user, goalForm, spGoal.getId(), true);
    } catch (Exception e) {
      LOG.error("Goal already exist, Please add different goal", e);
      throw new InvalidRequestException("Goal already exist, Please add different goal");
    }
    
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
    
  }
  
  /**
   * <code>getAllGoals</code> method will return all teh goals for the user.
   * 
   * @param user
   *          adminstrator user.
   * @return the list of goals
   */
  public SPResponse getAllGoals(User user) {
    
    List<SPGoal> goals = goalsRepository.findAllGoalsByCategory(GoalCategory.Business,
        GoalCategory.Individual);
    
    List<GoalDto> goalDto = goals.stream().map(GoalDto::new).collect(Collectors.toList());
    SPResponse response = new SPResponse();
    response.add("goals", goalDto);
    return response;
  }
  
  /**
   * <code>updateGoal</code> method will update a new goal for the user
   * 
   * @param user
   *          adminstrator user logged in.
   * @param param
   *          is the parameter contaning the goal form.
   * @return the new SPGoal.
   */
  public SPResponse updateGoal(User user, Object[] param) {
    
    GoalForm goalForm = (GoalForm) param[0];
    
    String goalId = (String) param[1];
    
    updateGoalAndUsers(user, goalForm, goalId, false);
    
    return new SPResponse().isSuccess();
  }
  
  private void updateGoalAndUsers(User user, GoalForm goalForm, String goalId, boolean isCreate) {
    SPGoal goal = spGoalFactory.getGoal(goalId);
    
    if (goal == null) {
      throw new InvalidRequestException("Goal not found to update.");
    }
    
    // setting the basic information from the form
    if (goalForm.getName() != null) {
      goal.setName(goalForm.getName());
    }
    
    if (goalForm.getDescription() != null) {
      goal.setDescription(goalForm.getDescription());
    }
    
    boolean statusChanged = false;
    
    if (goalForm.getIsMandatory() != null) {
      Boolean isMandatory = Boolean.valueOf(goalForm.getIsMandatory());
      if (isMandatory != goal.isMandatory()) {
        goal.setMandatory(isMandatory);
        statusChanged = true;
      }
    }
    
    if (goalForm.getStatus() != null) {
      if (goal.getStatus() != goalForm.getStatus()) {
        goal.setStatus(goalForm.getStatus());
        statusChanged = true;
      }
    }
    
    try {
      spGoalFactory.updateGoal(goal);
    } catch (Exception e) {
      LOG.error("Goal already exist, Please add different goal");
      throw new InvalidRequestException("Goal already exist, Please add different goal");
    }
    
    boolean isAllAccounts = Boolean.valueOf(goalForm.getAllAccounts());
    List<String> companyList = goalForm.getAccounts();
    if (companyList.size() > 0) {
      isAllAccounts = false;
    }
    
    if (goal.isAllAccounts() != isAllAccounts) {
      statusChanged = true;
    }
    
    // checking if goal form is business or individual
    if ((goalForm.getCategory() != null && goalForm.getCategory() != goal.getCategory())
        || !goalForm.getAccounts().containsAll(goal.getAccounts())
        || goalForm.getAccounts().size() != goal.getAccounts().size() || statusChanged || isCreate) {
      List<User> allMembers = userRepository.findAllMembers(false);
      List<User> individualUsers = new ArrayList<User>();
      List<User> businessUsers = new ArrayList<User>();
      List<User> prevBusinessUsers = new ArrayList<User>();
      boolean oldIsAllAccounts = goal.isAllAccounts();
      List<String> oldCompanyList = goal.getAccounts();
      for (User usr : allMembers) {
        // check for individual users
        if (usr.getAccountId() != null) {
          if (usr.getAnalysis() != null) {
            individualUsers.add(usr);
          }
        } else {
          // check for all accounts for both new and old one
          if (isAllAccounts) {
            if (usr.getAnalysis() != null) {
              businessUsers.add(usr);
            }
          }
          
          if (oldIsAllAccounts) {
            if (usr.getAnalysis() != null) {
              prevBusinessUsers.add(usr);
            }
          }
          
          String companyId = usr.getCompanyId();
          if (companyList.contains(companyId)) {
            if (!businessUsers.contains(usr)) {
              if (usr.getAnalysis() != null) {
                businessUsers.add(usr);
              }
            }
          }
          
          if (oldCompanyList.contains(companyId)) {
            if (!prevBusinessUsers.contains(usr)) {
              if (usr.getAnalysis() != null) {
                prevBusinessUsers.add(usr);
              }
            }
          }
        }
      }
      
      if (goalForm.getCategory() == GoalCategory.Business) {
        if (goal.getCategory() != GoalCategory.Business) {
          // need to switch the goal category from individual to business
          removeForAll(goalId, individualUsers);
          goal.setCategory(goalForm.getCategory());
          if (goal.getStatus() == GoalStatus.ACTIVE) {
            addGoalForUsers(goalId, businessUsers, goal);
          }
        } else {
          removeForAll(goalId, prevBusinessUsers);
          if (goal.getStatus() == GoalStatus.ACTIVE) {
            addGoalForUsers(goalId, businessUsers, goal);
          }
        }
        goal.setAccounts(goalForm.getAccounts());
        goal.setAllAccounts(isAllAccounts);
      } else {
        if (goal.getCategory() != GoalCategory.Individual) {
          goal.setCategory(goalForm.getCategory());
          goal.setAccounts(null);
          goal.setAllAccounts(isAllAccounts);
          removeForAll(goalId, prevBusinessUsers);
          if (goal.getStatus() == GoalStatus.ACTIVE) {
            addGoalForUsers(goalId, individualUsers, goal);
          }
        } else {
          if (statusChanged || isCreate) {
            removeForAll(goalId, individualUsers);
            if (goal.getStatus() == GoalStatus.ACTIVE) {
              addGoalForUsers(goalId, individualUsers, goal);
            }
          }
        }
      }
    }
    
    try {
      spGoalFactory.updateGoal(goal);
    } catch (Exception e) {
      LOG.error("Goal already exist, Please add different goal");
      throw new InvalidRequestException("Goal already exist, Please add different goal");
    }
  }
  
  /**
   * Adds the given goal to the user's goal list.
   * 
   * @param goalId
   *          - goal id
   * @param userList
   *          - user list
   * @param goal
   *          - goal
   */
  private void addGoalForUsers(String goalId, List<User> userList, SPGoal goal) {
    userList.stream().forEach(u -> {
      try {
        spGoalFactory.addGoalToUser(u, goalId, goal.isMandatory());
      } catch (Exception e) {
        LOG.info("Error adding goals for the user.", e);
      }
    });
  }
  
  /**
   * Remove the given goal from the users goals.
   * 
   * @param goalId
   *          - goal id
   * @param userList
   *          - user list
   */
  private void removeForAll(String goalId, List<User> userList) {
    userList.stream().forEach(u -> {
      try {
        spGoalFactory.removeGoalFromUser(u, goalId);
      } catch (Exception e) {
        LOG.info("Could not remove goal for user.", e);
      }
    });
  }
  
  /**
   * deactivateGoal method will deattivate the theme if the theme is not used in any company by the
   * user.
   * 
   * @param user
   *          system adminstrator user
   * @param param
   *          logged in user
   * @return the activated user.
   */
  public SPResponse deactivateGoal(User user, Object[] param) {
    
    String goalId = (String) param[0];
    
    /* find out all the companies where this theme is applied to */
    SPGoal goal = spGoalFactory.getGoal(goalId);
    if (goal == null) {
      throw new InvalidRequestException("No Goal found for requested goalId");
    }
    
    if (goal.getStatus() == GoalStatus.INACTIVE) {
      throw new SPException("Goal needs to be active first");
    }
    
    GoalForm goalForm = new GoalForm(goal);
    goalForm.setStatus(GoalStatus.INACTIVE);
    updateGoalAndUsers(user, goalForm, goalId, false);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * deactivateGoal method will deattivate the theme if the theme is not used in any company by the
   * user.
   * 
   * @param user
   *          adminstrator user.
   * @param param
   *          contains the goal id
   * @return the response.
   */
  public SPResponse activateGoal(User user, Object[] param) {
    
    String goalId = (String) param[0];
    
    /* find out all the companies where this theme is applied to */
    SPGoal goal = spGoalFactory.getGoal(goalId);
    if (goal == null) {
      throw new InvalidRequestException("No Goal found for requested goalId");
    }
    if (goal.getStatus() == GoalStatus.ACTIVE) {
      throw new SPException("Goal needs to deactivated first");
    }
    
    GoalForm goalForm = new GoalForm(goal);
    goalForm.setStatus(GoalStatus.ACTIVE);
    updateGoalAndUsers(user, goalForm, goalId, false);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * getGoalDetail method will return the goal details.
   * 
   * @param user
   *          system admin user
   * @param param
   *          logged in param
   * @return the sp response.
   */
  public SPResponse getGoalDetail(User user, Object[] param) {
    
    String goalId = (String) param[0];
    
    /* find out all the companies where this theme is applied to */
    SPGoal goal = spGoalFactory.getGoal(goalId);
    
    if (goal == null) {
      throw new InvalidRequestException("No Goal found for requested goalId");
    }
    List<String> accounts = goal.getAccounts();
    
    GoalDto dto = new GoalDto(goal);
    
    if (!CollectionUtils.isEmpty(accounts)) {
      
      List<Company> companies = accountRepository.findCompanyById(new HashSet<String>(accounts));
      
      List<CompanyDTO> companiesDTOs = companies.stream().map(CompanyDTO::new)
          .collect(Collectors.toList());
      dto.setCompanyDTO(companiesDTOs);
      
    }
    
    SPResponse response = new SPResponse();
    response.add("goal", dto);
    return response;
  }
  
}
