package com.sp.web.model.goal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * 
 *         The entity for user goal progress.
 */
public class UserGoalProgress {
  
  private String goalId;
  
  private boolean mandatory;
  
  private boolean selected;
  
  private Set<String> feedbackGivenByNames;
  
  private boolean removedByUser;
  
  private List<GoalSource> sourceList;
  
  private List<Integer> developmentStrategyLists;
  
  private int allGoalWeight;
  
  private int prismWeight;
  
  private int prismLensWeight;
  
  private boolean isGoalInQueue;
  
  /* TODO updatd the order index. Update the default to 999. */
  private int orderIndex = 999;
  
  /**
   * Default Constructor.
   */
  public UserGoalProgress() {
  }
  
  /**
   * Constructor for creating a new user goal progress.
   * 
   * @param goalId
   *          - goalId
   */
  public UserGoalProgress(String goalId) {
    this(goalId, true);
  }
  
  /**
   * Constructor.
   * 
   * @param goalId
   *          - goal id
   * @param mandatory
   *          - mandatory flag
   */
  public UserGoalProgress(String goalId, boolean mandatory) {
    this.goalId = goalId;
    this.mandatory = mandatory;
  }
  
  /**
   * Constructor.
   * 
   * @param goalId
   *          - goal id
   * @param mandatory
   *          - mandatory
   * @param selected
   *          - selected
   */
  public UserGoalProgress(String goalId, boolean mandatory, boolean selected) {
    this(goalId, mandatory);
    this.selected = selected;
  }
  
  @Override
  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof UserGoalProgress)) {
      return false;
    } else {
      UserGoalProgress object = (UserGoalProgress) obj;
      return ((this.getGoalId() != null) ? (this.getGoalId().equals(object.getGoalId())) : false);
    }
  }
  
  /**
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int code = 42;
    code += (this.goalId != null ? this.goalId.hashCode() : 0);
    return code;
  }
  
  public void setGoalId(String goalId) {
    this.goalId = goalId;
  }
  
  public String getGoalId() {
    return goalId;
  }
  
  public void setFeedbackGivenByNames(Set<String> feedbackGivenByNames) {
    this.feedbackGivenByNames = feedbackGivenByNames;
  }
  
  /**
   * @return - the list of feedback users.
   */
  public Set<String> getFeedbackGivenByNames() {
    if (feedbackGivenByNames == null) {
      feedbackGivenByNames = new HashSet<String>();
    }
    return feedbackGivenByNames;
  }
  
  public void setRemovedByUser(boolean removedByUser) {
    this.removedByUser = removedByUser;
  }
  
  public boolean isRemovedByUser() {
    return removedByUser;
  }
  
  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
  
  public boolean isMandatory() {
    return mandatory;
  }
  
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }
  
  public void setSourceList(List<GoalSource> sourceList) {
    this.sourceList = sourceList;
  }
  
  /**
   * @return
   *    - the source list.
   */
  public List<GoalSource> getSourceList() {
    if (sourceList == null) {
      sourceList = new ArrayList<GoalSource>();
    }
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
  
  public void addToAllWeight(int weight) {
    this.allGoalWeight = allGoalWeight + weight;
  }
  
  public void addToPrismWeight(int weight) {
    this.prismWeight = prismWeight + weight;
  }
  
  public void addToPrismLensWeight(int weight) {
    this.prismLensWeight = prismLensWeight + weight;
  }
  
  public void setGoalInQueue(boolean isGoalInQueue) {
    this.isGoalInQueue = isGoalInQueue;
  }
  
  /**
   * In case mandatory is true for old, then setting to default true so that it can be shown in the
   * older list.
   * 
   * @return
   *    true if goal is mandatory else false
   */
  public boolean isGoalInQueue() {
    if (mandatory) {
      this.isGoalInQueue = Boolean.TRUE;
    }
    return isGoalInQueue;
  }
  
  public void setOrderIndex(int orderIndex) {
    this.orderIndex = orderIndex;
  }
  
  public int getOrderIndex() {
    return orderIndex;
  }
  
  public void setDevelopmentStrategyLists(List<Integer> developmentStrategyLists) {
    this.developmentStrategyLists = developmentStrategyLists;
  }

  /**
   * Get the development strategy list.
   * 
   * @return
   *      - the development strategy list
   */
  public List<Integer> getDevelopmentStrategyLists() {
    if (developmentStrategyLists == null) {
      developmentStrategyLists = new ArrayList<Integer>();
    }
    return developmentStrategyLists;
  }

  /**
   * Check if the given goal has the given goal source.
   * 
   * @param type
   *          - source type
   * @return
   *    true if source found else false
   */
  public boolean hasSourceType(GoalSourceType type) {
    if (sourceList != null) {
      for (GoalSource goalSource : sourceList) {
        if (goalSource.getGoalSourceType() == GoalSourceType.PrismLens) {
          return true;
        }
      }
    }
    return false;
  }
}
