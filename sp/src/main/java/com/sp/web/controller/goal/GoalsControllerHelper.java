package com.sp.web.controller.goal;

import com.sp.web.Constants;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.GoalDto;
import com.sp.web.dto.PracticeAreaDTO;
import com.sp.web.dto.PracticeAreaProgressDTO;
import com.sp.web.dto.UserGoalProgressDto;
import com.sp.web.dto.UserGoalProgressSummaryDto;
import com.sp.web.dto.badge.BadgeProgressDTO;
import com.sp.web.dto.goal.DevelopmentStrategyDTO;
import com.sp.web.dto.goal.SPGoalDetailDTO;
import com.sp.web.exception.AssessmentNotTakenException;
import com.sp.web.exception.InvalidParameterException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.badge.UserBadge;
import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.model.badge.UserBadgeProgress;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.goals.GoalsAlgorithm;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pradeep The helper class for goals controller.
 */
@Component
public class GoalsControllerHelper {
  
  /** initializing the logger. */
  private static final Logger LOG = Logger.getLogger(GoalsControllerHelper.class);
  
  /** SPGoalFactory, will return the goals associated with the user profile. */
  @Autowired
  private SPGoalFactoryHelper spGoalFactoryHelper;
  
  @Autowired
  private GoalsRepository mongoGoalRepository;
  
  @Autowired
  private TrainingLibraryArticleRepository mongoTlArticle;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  private GoalsAlgorithm goalsAlgorithm;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  /**
   * <code>getUserGoal</code> method will fetch the user goals associated with his profile as well
   * as give to him by the user.
   * 
   * @param user
   *          logged in user
   * @return the user goals sp response.
   */
  public SPResponse getUserGoals(User user) {
    
    SPResponse response = new SPResponse();
    
    getUserGoals(user, response);
    return response;
  }
  
  /**
   * Get the user Practice Areas limited to 6 and the Detailed Information of the first Goal.
   * 
   * @param user
   *          - user
   * @param response
   *          - response
   */
  public void getUserGoals(User user, SPResponse response) {
    UserGoalDao userGoals = getGoals(user);
    List<PracticeAreaDTO> individualGoalsList = new ArrayList<PracticeAreaDTO>();
    int counter = 0;
    String activeGoalId = null;
    boolean goalDetailAvailable = false;
    
    UserBadgeActivity userBadge = badgeFactory.getUserBadge(user);
    for (UserGoalProgressDao goalDao : userGoals.getSelectedGoalsProgressList()) {
      // Maximum of 12 PA's allowed for a User
      if (counter >= Constants.MAXIMUM_PRACTICE_AREA_COUNT) {
        break;
      }
      
      SPGoal goal = goalDao.getGoal();
      if (goal.getCategory() == GoalCategory.GrowthAreas) {
        
        PracticeAreaProgressDTO areaProgressDTO = new PracticeAreaProgressDTO(goal);
        UserBadgeProgress userBadgeProgress = userBadge.getUserBadgeProgress(goal.getId());
        UserBadge completedUserBadge = userBadge.getCompletedBadges().get(goal.getId());
        if (userBadgeProgress == null && completedUserBadge == null) {
          
          /* not user badge progress is present for the user */
          userBadgeProgress = badgeFactory.addToBadgeProgress(user, goal.getId(), BadgeType.Erti,
              userBadge, goal);
        }
        /* check if still userBadgeProgress is present or not */
        if (userBadgeProgress != null) {
          areaProgressDTO.setCompletedCount(userBadgeProgress.getCompletedCount());
          areaProgressDTO.setTotalCount(userBadgeProgress.getTotalCount());
          areaProgressDTO.setLevel(userBadgeProgress.getLevel());
        } else {
          /* user has completed all the badgeprogress */
          completedUserBadge = userBadge.getCompletedBadges().get(goal.getId());
          areaProgressDTO.setCompletedCount(15);
          areaProgressDTO.setTotalCount(15);
          areaProgressDTO.setLevel(completedUserBadge.getLevel());
        }
        
        individualGoalsList.add(areaProgressDTO);
        // Active GoalId logic
        if (!goalDetailAvailable) {
          if (userGoals.getActiveGoalId() != null) {
            activeGoalId = userGoals.getActiveGoalId();
          } else {
            if (counter == 0) {
              activeGoalId = goal.getId();
            }
          }
          SPGoalDetailDTO goalDetailDTO = getGoalDetailInfo(user, activeGoalId, false);
          response.add("goalDetail", goalDetailDTO);
          goalDetailAvailable = true;
        }
        
        counter++;
      }
    }
    response.add("autoLearning", user.getProfileSettings().isAutoUpdateLearning());
    response.add("userGoal", individualGoalsList);
  }
  
  /**
   * Helper method to get the goals for the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - parameters
   * @return the response to the get user goals for
   */
  public SPResponse getUserGoalsFor(User user, Object[] params) {
    
    final SPResponse response = new SPResponse();
    
    // get the email to get the data for
    String memberEmail = (String) params[0];
    
    // validate if member email is passed
    Assert.hasText(memberEmail, "Member email required.");
    
    User member = userRepository.findByEmailValidated(memberEmail, user.getCompanyId());
    
    getUserGoals(member, response);
    return response;
  }
  
  /**
   * Get's all the goals for the user.
   * 
   * @param user
   *          - user
   * @return the response to the get goals
   */
  public SPResponse getAllGoals(User user) {
    
    final SPResponse response = new SPResponse();
    
    UserGoalDao userGoals = getGoals(user);
    Collection<UserGoalProgressDao> values = userGoals.getGoalsProgressMap().values();
    List<UserGoalProgressDao> sortedUserGoalProgressDaoList = goalsAlgorithm
        .sortedUserGoalProgressDaoList(values);
    /* filter the goals which are inactive */
    List<UserGoalProgressSummaryDto> userGoalsList = sortedUserGoalProgressDaoList
        .stream()
        .filter(
            spg -> spg.getGoal().getCategory() == GoalCategory.GrowthAreas
                && spg.getGoal().getStatus() == GoalStatus.ACTIVE)
        .map(UserGoalProgressSummaryDto::new).collect(Collectors.toList());
    
    List<UserGoalProgressSummaryDto> allGoals = goalFactory.getAllGoalsSummary(LocaleHelper
        .locale().toString());
    /* filter the goals which are inactive and add only those which are active. */
    allGoals.stream().forEach(g -> {
      if (!userGoalsList.contains(g)) {
        userGoalsList.add(g);
      }
    });
    List<UserGoalProgressDao> selectedGoalsProgressList = userGoals.getSelectedGoalsProgressList()
        .stream().filter(ugpDao -> ugpDao.getGoal().getCategory() == GoalCategory.GrowthAreas)
        .collect(Collectors.toList());
    
    response.add("userAllGoal", userGoalsList);
    response.add("userSelectedGoal", selectedGoalsProgressList);
    response.add("prismLensExist", userGoals.isContainsPrismLensGoal());
    return response;
  }
  
  /**
   * Gets the user goals object for the given user.
   * 
   * @param user
   *          - logged in user
   * @return the user goals object
   */
  public UserGoalDao getGoals(User user) {
    UserGoalDao userGoals = spGoalFactoryHelper.getUserGoal(user);
    if (userGoals == null) {
      LOG.debug("Current user goals are null, adding the current user goals");
      userGoals = spGoalFactoryHelper.addGoalsForUser(user);
    }
    return userGoals;
  }
  
  /**
   * <code>addToUserGoal</code> method will add the goal to user profile.
   * 
   * @param user
   *          for goal is to be added.
   * @param param
   *          contains the goal id
   * @return the response.
   */
  public SPResponse addToUserGoals(User user, Object[] param) {
    String goalId = (String) param[0];
    spGoalFactoryHelper.addGoalToUser(user, goalId);
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to add the given article to the user goals.
   * 
   * @param user
   *          - logged in user
   * @param param
   *          - the parameters
   * @return the response to the add request
   */
  public SPResponse addArticleToUserGoals(User user, Object[] param) {
    String articleId = (String) param[0];
    spGoalFactoryHelper.addArticleToUserGoals(user, articleId);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>removeUserGoals</code> method will remove the goal to user profile.
   * 
   * @param user
   *          for goal is to be added.
   * @param param
   *          contains the goal id
   * @return the response.
   */
  public SPResponse removeUserGoals(User user, Object[] param) {
    String goalId = (String) param[0];
    spGoalFactoryHelper.removeGoalFromUser(user, goalId);
    return new SPResponse().isSuccess();
  }
  
  /**
   * Select the user goals.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the user goals selected
   */
  @SuppressWarnings("unchecked")
  public SPResponse updateUserGoalsSelection(User user, Object[] param) {
    List<String> goalIdList = (List<String>) param[0];
    if (goalIdList.size() < Constants.MINIMUM_PRACTICE_AREA_COUNT) {
      throw new InvalidRequestException(MessagesHelper.getMessage("practiceArea.noGoalsSelected"));
    }
    if (goalIdList.size() > Constants.MAXIMUM_PRACTICE_AREA_COUNT) {
      throw new InvalidRequestException(MessagesHelper.getMessage("practiceArea.maxGoalsSelected"));
    }
    spGoalFactoryHelper.updateGoalSelection(user, goalIdList);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>getUserGoal</code> method will fetch the user goals associated with his profile as well
   * as give to him by the user.
   * 
   * @param user
   *          logged in user
   * @return the user goals sp response.
   * 
   *         public SPResponse getDashboardUserGoals(User user) {
   * 
   *         SPResponse response = new SPResponse();
   * 
   *         /* fetch the goals associated with logged in user
   */
  /* check if user has completd the assessment to fetch the goals */
  
  /*
   * if (user.getUserStatus() != UserStatus.VALID) {
   * LOG.info("User has not taken the assessment, Cannot retreive goals for the user"); throw new
   * AssessmentNotTakenException( MessagesHelper.getMessage("goal.error.assessmentNotTaken")); }
   * 
   * UserGoalDao userGoal = spGoalFactoryHelper.getUserGoal(user); List<UserGoalProgressDto>
   * userGoalList = new ArrayList<UserGoalProgressDto>(); if (userGoal != null) { final
   * List<UserGoalProgressDao> goalsProgressList = userGoal.getSelectedGoalsProgressList(); for (int
   * i = 0; i < goalsProgressList.size(); i++) { UserGoalProgressDao ugpDAO =
   * goalsProgressList.get(i); userGoalList.add(new UserGoalProgressDto(ugpDAO,
   * Constants.DEFAULT_DASHBOARD_GOALS_ARTICLE_COUNT)); } } response.add("userGoal", userGoalList);
   * return response; }
   */
  
  /**
   * <code>getGoalDetail</code> method will fetch the goal details with articles, training spot
   * light and articles.
   * 
   * @param user
   *          logged in user
   * @return the SPResponse
   */
  public SPResponse getGoalDetail(User user, Object[] param) {
    
    String goalId = (String) param[0];
    if (StringUtils.isBlank(goalId)) {
      throw new InvalidParameterException("Goal Id is blank or null");
    }
    
    String memberEmail = (String) param[1];
    if (StringUtils.isNotBlank(memberEmail)) {
      user = userRepository.findByEmail(memberEmail);
    }
    
    SPGoalDetailDTO goalDetailDTO = getGoalDetailInfo(user, goalId, false);
    
    // Update the active/selected goal which user clicks on the Individual PA Page
    try {
      goalFactory.updateActiveGoalSelection(user, goalId);
    } catch (Exception e) {
      // Any exception - catch it here. Dont let user flow affected because of this
      LOG.error("Unable to Save User selected/clicked PA as active to DB " + e);
    }
    
    SPResponse response = new SPResponse();
    response.add("goalDetail", goalDetailDTO);
    return response;
  }
  
  /**
   * <code>getGoalInfo</code> method will fetch the goal details with development strategies.
   * 
   * @param user
   *          logged in user
   * @return the SPResponse
   */
  public SPResponse getGoalInfo(User user, Object[] param) {
    
    String goalId = (String) param[0];
    if (StringUtils.isBlank(goalId)) {
      throw new InvalidParameterException("Goal Id is blank or null");
    }
    
    SPGoal goal = goalFactory.getGoal(goalId, user.getUserLocale());
    Assert.notNull(goal, "Goal not found. " + goalId);
    return new SPResponse().add(Constants.PARAM_GOAL, new GoalDto(goal));
  }
  
  /**
   * <code>getGoalDetail</code> method will fetch the goal details with articles, training spot
   * light and articles.
   * 
   * @param user
   *          logged in user
   * @return the SPResponse
   */
  public SPResponse getGoalArticles(User user, Object[] param) {
    
    String goalId = (String) param[0];
    if (StringUtils.isBlank(goalId)) {
      throw new InvalidParameterException("Goal Id is blank or null");
    }
    
    String memberEmail = (String) param[1];
    if (StringUtils.isNotBlank(memberEmail)) {
      user = userRepository.findByEmail(memberEmail);
    }
    
    SPGoalDetailDTO goalDetailDTO = getGoalDetailInfo(user, goalId, true);
    
    SPResponse response = new SPResponse();
    response.add("userArticles", goalDetailDTO.getUserGoalsDto().getUserArticlesProgress());
    return response;
  }
  
  /**
   * <code>manageDevleopmentStrategy</code> method will mange the devleopment strategy
   * 
   * @param user
   *          logged in user
   * @param param
   *          params containin the goal id.
   * @return
   */
  public SPResponse manageDevelopmentStragegy(User user, Object[] param) {
    
    String goalId = (String) param[0];
    @SuppressWarnings("unchecked")
    List<String> acticeDsIds = (List<String>) param[1];
    
    SPGoal goal = goalFactory.getGoal(goalId);
    if (goal == null) {
      throw new InvalidRequestException("Invalid Goal Id");
    }
    goalFactory.updateDevelopmentsStragtegy(user, acticeDsIds, goalId);
    SPResponse response = new SPResponse();
    goalFactory.updateGoal(goal);
    response.isSuccess();
    return response;
  }
  
  /**
   * getManagementDevelopmentStrategy will return the development strategy.
   * 
   * @param user
   * @param param
   * @return
   */
  public SPResponse getManageDevelopmentStrategy(User user, Object[] param) {
    SPResponse response = new SPResponse();
    String goalId = (String) param[0];
    SPGoal goal = goalFactory.getGoal(goalId);
    if (goal == null) {
      throw new InvalidRequestException("Invalid Goal Id");
    }
    List<DevelopmentStrategy> developmentStrategyList = goal.getDevelopmentStrategyList();
    Assert.notNull(user.getUserGoalId(), "User goals not set.");
    UserGoalDao userGoal = goalFactory.getUserGoal(user.getUserGoalId(), user.getUserLocale());
    UserGoalProgressDao userGoalProgress = userGoal.getUserGoalProgress(goalId);
    
    List<DevelopmentStrategyDTO> devStrategyList = new ArrayList<>();
    developmentStrategyList.stream().forEach(ds -> {
      if (userGoalProgress.getActiveDevelopmentStrategyList().contains(ds)) {
        DevelopmentStrategyDTO developmentStrategyDTO = new DevelopmentStrategyDTO(ds);
        developmentStrategyDTO.setSelectedByUser(true);
        
        devStrategyList.add(developmentStrategyDTO);
      } else {
        if (ds.isActive()) {
          DevelopmentStrategyDTO developmentStrategyDTO = new DevelopmentStrategyDTO(ds);
          devStrategyList.add(developmentStrategyDTO);
        }
      }
    });
    response.add("mds", devStrategyList);
    return response;
  }
  
  /**
   * Private method to get Detailed Goal Information.
   * 
   * @param : userGoalDao
   * @param : String goalId
   * @return : SPGoalDetailDTO
   */
  private SPGoalDetailDTO getGoalDetailInfo(User user, String goalId, boolean isAllArticlesInfo) {
    UserGoalDao userGoalDao = getGoals(user);
    UserGoalProgressDao userGoalProgressDao = userGoalDao.getGoalsProgressMap().get(goalId);
    if (userGoalProgressDao == null) {
      throw new InvalidRequestException("Goal id is not part of active goals");
    }
    userGoalDao.setActiveGoalId(goalId);
    SPGoal goal = userGoalProgressDao.getGoal();
    
    List<ArticleDao> artilces = articlesFactory.getArtilces(Constants.THEME, goalId,
        user.getUserLocale());
    
    UserGoalProgressDto userGoalProgressDto = new UserGoalProgressDto(userGoalProgressDao);
    List<String> mandatoryArticles = goal.getMandatoryArticles();
    if (isAllArticlesInfo) {
      userGoalProgressDto.updateArticles(userGoalDao.getArticleProgressMap(), artilces,
          userGoalDao.getBookMarkedArticles(), user, mandatoryArticles);
    } else {
      if (userGoalProgressDao.getArticleList().isEmpty()) {
        userGoalDao.updateArticles(artilces, user.getUserLocale(), userGoalProgressDao,
            articlesFactory);
      }
      userGoalProgressDto.updateArticles(userGoalProgressDao.getArticleList(), user,
          mandatoryArticles, articlesFactory);
    }
    
    userGoalProgressDto.setActiveGoalId(goalId);
    
    SPGoalDetailDTO goalDetailDTO = new SPGoalDetailDTO(goal);
    goalDetailDTO.setUserGoalsDto(userGoalProgressDto);
    // goalDetailDTO.setSpotLightDTOs(trainingLIstDTO);
    
    return goalDetailDTO;
  }
  
  /**
   * getBadgeProgress will send thee badge progress for the passed refid.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          is the param
   * @return the badge progress.
   */
  public SPResponse getBadgeProgress(User user, Object[] params) {
    
    String goalId = (String) params[0];
    
    Assert.hasText(goalId, "Invalid Id");
    UserBadgeActivity userBadge = badgeFactory.getUserBadge(user);
    UserBadgeProgress userBadgeProgress = userBadge.getUserBadgeProgress().get(goalId);
    BadgeProgressDTO badgeProgressDTO = null;
    if (userBadgeProgress == null) {
      
      /* check if user has completd all the badge progress for the refid */
      UserBadge completedBadge = userBadge.getCompletedBadges().get(goalId);
      if (completedBadge != null) {
        badgeProgressDTO = new BadgeProgressDTO();
        badgeProgressDTO.setBadgeType(completedBadge.getBadgeType());
        badgeProgressDTO.setCompletedCount(15);
        badgeProgressDTO.setTotalCount(15);
        badgeProgressDTO.setLevel(completedBadge.getLevel());
      }
    } else {
      badgeProgressDTO = new BadgeProgressDTO(userBadgeProgress);
    }
    
    ;
    SPResponse response = new SPResponse();
    response.add("badgeProgress", badgeProgressDTO);
    return response;
    
  }
}
