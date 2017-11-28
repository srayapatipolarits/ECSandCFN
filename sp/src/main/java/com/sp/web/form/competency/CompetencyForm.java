package com.sp.web.form.competency;

import com.sp.web.model.SPRating;
import com.sp.web.model.SPRatingScore;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 * 
 *         The form class for competency.
 */
public class CompetencyForm {
  
  protected String id;
  private String name;
  private String description;
  private Map<String, String> introVideo;
  private SPRating rating;
  private List<DSActionCategory> devStrategyActionCategoryList;
  private List<String> mandatoryArticles;
  private GoalStatus status;
  
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
  
  public Map<String, String> getIntroVideo() {
    return introVideo;
  }
  
  public void setIntroVideo(Map<String, String> introVideo) {
    this.introVideo = introVideo;
  }
  
  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }
  
  public SPRating getRating() {
    return rating;
  }
  
  public void setRating(SPRating rating) {
    this.rating = rating;
  }
  
  /**
   * Validate the form data.
   * 
   * @param ratingConfiguration
   *              - rating configuration for the profile 
   */
  public void validate(RatingConfiguration ratingConfiguration) {
    Assert.hasText(name, "Competency name is required.");
    validateRating(ratingConfiguration);
    Assert.notEmpty(devStrategyActionCategoryList, "At least one category required.");
    Assert.isTrue(devStrategyActionCategoryList.size() == 1, "Only one category supported.");
    devStrategyActionCategoryList.forEach(this::validate);
  }
  
  /**
   * @param actionCategory
   *          - validate the action category.
   */
  private void validate(DSActionCategory actionCategory) {
    Assert.hasLength(actionCategory.getTitle(), "Category name required.");
    final List<DSAction> actionList = actionCategory.getActionList();
    Assert.notEmpty(actionList, "At least one strategy required.");
    actionList.forEach(this::validate);
  }
  
  /**
   * @param action
   *          - validate the action.
   */
  private void validate(DSAction action) {
    Assert.hasText(action.getTitle(), "Strategy title is required.");
  }
  
  /**
   * Validate the rating model.
   * 
   * @param ratingConfiguration
   *            - rating configuration 
   */
  private void validateRating(RatingConfiguration ratingConfiguration) {
    Assert.notNull(rating, "Rating not found.");
    final List<SPRatingScore> ratingList = rating.getRatingList();
    Assert.notEmpty(ratingList, "Rating not found.");
    final int ratingConfigurationSize = ratingConfiguration.getSize();
    Assert.isTrue(ratingConfigurationSize == ratingList.size(), "Rating must be at least :"
        + ratingConfigurationSize);
    for (SPRatingScore ratingScore : ratingList) {
      if (!StringUtils.isEmpty(ratingScore.getTitle())) {
        Assert.hasText(ratingScore.getDescription(), "Rating description required.");
      }
    }
  }
  
  /**
   * Add or update the competency data.
   * 
   * @param competency
   *          - competency to update
   * @param uidGenerator
   *          - uid generator
   */
  public void addUpdate(SPGoal competency, Supplier<String> uidGenerator) {
    BeanUtils.copyProperties(this, competency);
    competency.getDevStrategyActionCategoryList()
        .forEach(category -> category.addUID(uidGenerator));
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }

  public List<String> getMandatoryArticles() {
    return mandatoryArticles;
  }

  public void setMandatoryArticles(List<String> mandatoryArticles) {
    this.mandatoryArticles = mandatoryArticles;
  }
}
