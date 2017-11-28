package com.sp.web.dto;

import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.KeyOutcomes;
import com.sp.web.model.goal.SPGoal;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DevelopmentFeedbackpractice area DTO is the development feedback.
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedbackPracticeAreaDTO extends PracticeAreaDTO {
  
  private static final long serialVersionUID = -4876573072600755565L;
  
  private GoalCategory category;
  
  private KeyOutcomes keyOutcomes;
  
  private List<DSActionCategory> devStrategyActionCategoryList;
  
  private List<DevelopmentStrategy> developmentStrategyList;
  
  private String programName;
  
  /**
   * Constructor.
   * 
   * @param spGoal
   *          spGoal.
   * 
   */
  public DevelopmentFeedbackPracticeAreaDTO(SPGoal spGoal) {
    super(spGoal);
    developmentStrategyList = developmentStrategyList.stream().filter(devSt -> devSt.isActive())
        .collect(Collectors.toList());
  }
  
  /**
   * Constructor.
   * 
   * @param goal
   *          - practice area
   * @param userGoalProgress
   *          - user goal progress
   */
  public DevelopmentFeedbackPracticeAreaDTO(SPGoal goal, UserGoalProgressDao userGoalProgress) {
    super(goal);
    developmentStrategyList = userGoalProgress.getActiveDevelopmentStrategyList();
  }
  
  public GoalCategory getCategory() {
    return category;
  }
  
  public void setCategory(GoalCategory category) {
    this.category = category;
  }
  
  public KeyOutcomes getKeyOutcomes() {
    return keyOutcomes;
  }
  
  public void setKeyOutcomes(KeyOutcomes keyOutcomes) {
    this.keyOutcomes = keyOutcomes;
  }
  
  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }
  
  public void setDevelopmentStrategyList(List<DevelopmentStrategy> developmentStrategyList) {
    this.developmentStrategyList = developmentStrategyList;
  }
  
  public List<DevelopmentStrategy> getDevelopmentStrategyList() {
    return developmentStrategyList;
  }
  
  public void setProgramName(String programName) {
    this.programName = programName;
  }
  
  public String getProgramName() {
    return programName;
  }
}
