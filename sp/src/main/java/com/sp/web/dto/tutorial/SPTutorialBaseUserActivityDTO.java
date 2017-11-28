package com.sp.web.dto.tutorial;

import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.model.tutorial.TutorialActivityData;

import org.springframework.beans.BeanUtils;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class to list out the basic SP Tutorial details along with user activity details.
 */
public class SPTutorialBaseUserActivityDTO {
  
  private String id;
  private String name;
  private String description;
  private String imgUrl;
  private int actionCount;
  private int completedCount;
  
  /**
   * Constructor.
   * 
   * @param tutorialDao
   *            - tutorial dao
   * @param userActivity
   *            - user activity
   */
  public SPTutorialBaseUserActivityDTO(SPTutorialDao tutorialDao,
      TutorialActivityData userActivity) {
    BeanUtils.copyProperties(tutorialDao, this, "steps");
    this.completedCount = userActivity.getCount();
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
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
  
  public String getImgUrl() {
    return imgUrl;
  }
  
  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }
  
  public int getCompletedCount() {
    return completedCount;
  }
  
  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }

  public int getActionCount() {
    return actionCount;
  }

  public void setActionCount(int actionCount) {
    this.actionCount = actionCount;
  }
  
}
