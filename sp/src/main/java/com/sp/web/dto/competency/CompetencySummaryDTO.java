package com.sp.web.dto.competency;

import com.sp.web.model.SPRating;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to store the competency summary information.
 */
public class CompetencySummaryDTO implements Serializable {
  
  private static final long serialVersionUID = -9034593134276911980L;
  private String name;
  private String description;
  private SPRating rating;
  
  
  /**
   * Default Constructor.
   */
  public CompetencySummaryDTO() {
    super();
  }

  /**
   * Constructor from competency.
   * 
   * @param competency
   *            - competency
   */
  public CompetencySummaryDTO(SPGoal competency) {
    BeanUtils.copyProperties(competency, this);
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public SPRating getRating() {
    return rating;
  }
  
  public void setRating(SPRating rating) {
    this.rating = rating;
  }
}
