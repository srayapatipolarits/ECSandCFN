/**
 * 
 */
package com.sp.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 *
 */
public class GoalCategoryDto implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -683131350891158305L;

  private String categoryName;

  List<GoalDto> goals;

  /**
   * @param categoryName
   *          the categoryName to set
   */
  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  /**
   * @return the categoryName
   */
  public String getCategoryName() {
    return categoryName;
  }

  /**
   * @param goals
   *          the goals to set
   */
  public void setGoals(List<GoalDto> goals) {
    this.goals = goals;
  }

  /**
   * @return the goals
   */
  public List<GoalDto> getGoals() {
    if(goals == null){
      goals = new ArrayList<>();
    }
    return goals;
  }

}
