package com.sp.web.dao.goal;

import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalSource;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserGoalProgress;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The DAO to store the user goals progress.
 * 
 * @author Dax Abraham
 */
public class UserGoalProgressDao implements Serializable {
  
  /**
   * Defualt serial version id.
   */
  private static final long serialVersionUID = 378374593898052728L;
  private boolean mandatory;
  private boolean selected;
  private List<GoalSource> sourceList;
  private SPGoal goal;
  private List<UserArticleProgressDao> articleList;
  private List<DevelopmentStrategy> activeDevelopmentStrategyList;
  private int allGoalWeight;
  private int prismWeight;
  private int prismLensWeight;
  private boolean isGoalInQueue;
  private int orderIndex;
  
  /**
   * <code>UserGoalProgressDao</code> constructor intializing the goal progress.
   * 
   * @param userGoalProgress
   *            - user goal progress
   * @param goalFactory
   *            - goals factory
   * @param locale 
   */
  public UserGoalProgressDao(UserGoalProgress userGoalProgress, SPGoalFactory goalFactory, String locale) {
    BeanUtils.copyProperties(userGoalProgress, this);
    this.sourceList = userGoalProgress.getSourceList();
    init(userGoalProgress, goalFactory,locale);
    this.activeDevelopmentStrategyList = userGoalProgress.getDevelopmentStrategyLists().stream()
        .map(index -> goalFactory.getDevelopmentStrategyById(userGoalProgress.getGoalId(), index, locale))
        .collect(Collectors.toList());
  }
  
  /**
   * Initialize.
   * 
   * @param userGoalProgress
   *          - user goal progress
   * @param goalFactory
   *          - goal factory
   * @param locale 
   */
  private void init(UserGoalProgress userGoalProgress, SPGoalFactory goalFactory, String locale) {
    this.goal = goalFactory.getGoal(userGoalProgress.getGoalId(), locale);
  }
  
  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
  
  public List<GoalSource> getSourceList() {
    return sourceList;
  }
  
  public void setSourceList(List<GoalSource> sourceList) {
    this.sourceList = sourceList;
  }
  
  public SPGoal getGoal() {
    return goal;
  }
  
  public void setGoal(SPGoal goal) {
    this.goal = goal;
  }
  
  public void add(UserArticleProgressDao userArticlesProgress) {
    getArticleList().add(userArticlesProgress);
  }
  
  public List<UserArticleProgressDao> getArticleList() {
    return Optional.ofNullable(articleList).orElseGet(() -> {
      articleList = new ArrayList<UserArticleProgressDao>();
      return articleList;
    });
  }
  
  public void setArticleList(List<UserArticleProgressDao> articleList) {
    this.articleList = articleList;
  }
  
  public boolean isMandatory() {
    return mandatory;
  }
  
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }
  
  public void setActiveDevelopmentStrategyList(
      List<DevelopmentStrategy> activeDevelopmentStrategyList) {
    this.activeDevelopmentStrategyList = activeDevelopmentStrategyList;
  }
  
  public List<DevelopmentStrategy> getActiveDevelopmentStrategyList() {
    return activeDevelopmentStrategyList;
  }
  
  public int getAllGoalWeight() {
    return allGoalWeight;
  }
  
  public void setAllGoalWeight(int allGoalWeight) {
    this.allGoalWeight = allGoalWeight;
  }
  
  public int getPrismWeight() {
    return prismWeight;
  }
  
  public void setPrismWeight(int prismWeight) {
    this.prismWeight = prismWeight;
  }
  
  public int getPrismLensWeight() {
    return prismLensWeight;
  }
  
  public void setPrismLensWeight(int prismLensWeight) {
    this.prismLensWeight = prismLensWeight;
  }
  
  public void setGoalInQueue(boolean isGoalInQueue) {
    this.isGoalInQueue = isGoalInQueue;
  }
  
  public boolean isGoalInQueue() {
    return isGoalInQueue;
  }
  
  public void setOrderIndex(int orderIndex) {
    this.orderIndex = orderIndex;
  }
  
  public int getOrderIndex() {
    return orderIndex;
  }
  
}
