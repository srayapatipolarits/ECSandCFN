package com.sp.web.dto;

import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPGoal;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to send the practice area details along with the goals category.
 */
@Deprecated
public class SPNoteFeedbackPracticeAreaDTO extends PracticeAreaDTO {

  private static final long serialVersionUID = -7174146614546652779L;
  
  private GoalCategory category;

  public SPNoteFeedbackPracticeAreaDTO(SPGoal spGoal) {
    super(spGoal);
  }

  public GoalCategory getCategory() {
    return category;
  }

  public void setCategory(GoalCategory category) {
    this.category = category;
  }

}
