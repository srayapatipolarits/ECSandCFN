package com.sp.web.model.goal;

import java.io.Serializable;

/**
 * @author pradeep
 *
 *         The source for the goal.
 */
public class GoalSource implements Serializable {
  
  /**
   * Defaults erial version ID.
   */
  private static final long serialVersionUID = -5413072766739984481L;
  
  private GoalSourceType goalSourceType;
  
  private String value;
  
  /**
   * 
   */
  public GoalSource() {
  }
  
  public GoalSource(GoalSourceType goalSourceType) {
    this.goalSourceType = goalSourceType;
  }
  
  /**
   * GoalSOurce value.
   * 
   * @param prism
   *          goal source type.
   * @param value of the goal Source type.
   */
  public GoalSource(GoalSourceType prism, String value) {
    this.goalSourceType = prism;
    this.value = value;
    
  }
  
  public GoalSourceType getGoalSourceType() {
    return goalSourceType;
  }
  
  public void setGoalSourceType(GoalSourceType goalSourceType) {
    this.goalSourceType = goalSourceType;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((goalSourceType == null) ? 0 : goalSourceType.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GoalSource other = (GoalSource) obj;
    if (other.getGoalSourceType() == GoalSourceType.PrismLens
        && this.goalSourceType == GoalSourceType.PrismLens) {
      return true;
    }
    if (value == null) {
      return false;
    }
    if (!value.equalsIgnoreCase(other.getValue())) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "GoalSource [goalSourceType=" + goalSourceType + ", value=" + value + "]";
  }

  
}
