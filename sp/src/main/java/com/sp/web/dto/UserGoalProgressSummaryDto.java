package com.sp.web.dto;

import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalSource;
import com.sp.web.model.goal.GoalSourceType;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         This is the user goals progress DTO, but stores the goals summary. This DTO object does
 *         not store the article listing.
 */
public class UserGoalProgressSummaryDto {
  
  private GoalDto goal;
  
  private boolean mandatory;
  
  private int totalArticles;
  
  private int articlesCompleted;
  
  private boolean selected;
  
  private Map<GoalSourceType, List<GoalSource>> sourceList;
  
  private int allGoalWeight;
  private int prismWeight;
  private int prismLensWeight;
  private boolean isGoalInQueue;
  
  private List<DevelopmentStrategy> activeDevelopmentStrategyList;
  
  /**
   * Constructor.
   * 
   * @param goal
   *          - goal
   */
  public UserGoalProgressSummaryDto(SPGoal goal) {
    this.goal = new GoalDto(goal);
  }
  
  /**
   * Constructor.
   * 
   * @param ugpDao
   *          - user goals progress dao
   */
  public UserGoalProgressSummaryDto(UserGoalProgressDao ugpDao) {
    this(ugpDao.getGoal());
    BeanUtils.copyProperties(ugpDao, this);
    final List<UserArticleProgressDao> articleList = ugpDao.getArticleList();
    articlesCompleted = (int) articleList.stream()
        .filter(a -> a.getArticleStatus() == ArticleStatus.COMPLETED).count();
    totalArticles = articleList.size();
    int goalsMandatoryArticlesCount = ugpDao.getGoal().getMandatoryArticles().size();
    if (totalArticles < goalsMandatoryArticlesCount) {
      totalArticles = goalsMandatoryArticlesCount;
    }
    this.sourceList = ugpDao.getSourceList().stream()
        .collect(Collectors.groupingBy(GoalSource::getGoalSourceType));
  }
  
  public GoalDto getGoal() {
    return goal;
  }
  
  public void setGoal(GoalDto goal) {
    this.goal = goal;
  }
  
  public boolean isMandatory() {
    return mandatory;
  }
  
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }
  
  public int getTotalArticles() {
    return totalArticles;
  }
  
  public void setTotalArticles(int totalArticles) {
    this.totalArticles = totalArticles;
  }
  
  public int getArticlesCompleted() {
    return articlesCompleted;
  }
  
  public void setArticlesCompleted(int articlesCompleted) {
    this.articlesCompleted = articlesCompleted;
  }
  
  protected void incrementArticlesCompleted() {
    articlesCompleted++;
  }
  
  @Override
  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof UserGoalProgressSummaryDto)) {
      return false;
    } else {
      UserGoalProgressSummaryDto object = (UserGoalProgressSummaryDto) obj;
      return ((this.getGoal().getId() != null) ? (this.getGoal().getId().equals(object.getGoal()
          .getId())) : false);
    }
  }
  
  @Override
  public int hashCode() {
    int code = 42;
    code += (this.getGoal().getId() != null ? this.getGoal().getId().hashCode() : 0);
    return code;
  }
  
  public GoalCategory getCategory() {
    return getGoal().getCategory();
  }
  
  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
  
  /**
   * @param sourceList
   *          the sourceList to set
   */
  public void setSourceList(Map<GoalSourceType, List<GoalSource>> sourceList) {
    this.sourceList = sourceList;
  }
  
  /**
   * @return the sourceList
   */
  public Map<GoalSourceType, List<GoalSource>> getSourceList() {
    return sourceList;
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
  
  public boolean isGoalInQueue() {
    return isGoalInQueue;
  }
  
  public void setGoalInQueue(boolean isGoalInQueue) {
    this.isGoalInQueue = isGoalInQueue;
  }
  
  public void setActiveDevelopmentStrategyList(
      List<DevelopmentStrategy> activeDevelopmentStrategyList) {
    this.activeDevelopmentStrategyList = activeDevelopmentStrategyList;
  }
  
  public List<DevelopmentStrategy> getActiveDevelopmentStrategyList() {
    return activeDevelopmentStrategyList;
  }
  
}
