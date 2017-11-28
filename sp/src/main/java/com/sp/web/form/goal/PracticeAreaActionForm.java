package com.sp.web.form.goal;

import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;
import com.sp.web.model.goal.DSActionType;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.KeyOutcomes;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 * 
 *         The form to store the practice area action details.
 */
public class PracticeAreaActionForm {
  
  private String id;
  private String name;
  private String description;
  private GoalStatus status;
  private Map<String, String> introVideo;
  private KeyOutcomes keyOutcomes;
  private List<DSActionCategory> devStrategyActionCategoryList;
  private int durationDays;
  
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
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }
  
  public Map<String, String> getIntroVideo() {
    return introVideo;
  }
  
  public void setIntroVideo(Map<String, String> introVideo) {
    this.introVideo = introVideo;
  }
  
  public KeyOutcomes getKeyOutcomes() {
    return keyOutcomes;
  }
  
  public void setKeyOutcomes(KeyOutcomes keyOutcomes) {
    this.keyOutcomes = keyOutcomes;
  }
  
  /**
   * Validate the form data.
   * @param stepType 
   *          - step type
   */
  public void validate(StepType stepType) {
    Assert.hasText(name, "Practice area name required.");
    Assert.notNull(status, "Practice area status required.");
    if (status == GoalStatus.ACTIVE) {
      Assert.hasText(description, "Practice Area overview is required.");
      Assert.notEmpty(devStrategyActionCategoryList,
          "Practice Area at least one category required.");
      devStrategyActionCategoryList.forEach(this::validateActionCategory);
    }
    Assert.notNull(stepType, "Step type is required.");
    if (stepType != StepType.All) {
      Assert.isTrue(durationDays > 0, "Duration is reuquired.");
    }
  }
  
  /**
   * Validate the action category data.
   * 
   * @param actionCategory
   *          - action category to validate
   */
  private void validateActionCategory(DSActionCategory actionCategory) {
    Assert.notNull(actionCategory, "Action Category required.");
    final GoalStatus categoryStatus = actionCategory.getStatus();
    Assert.notNull(categoryStatus, "Category status required.");
    if (categoryStatus == GoalStatus.ACTIVE) {
      Assert.hasText(actionCategory.getTitle(), "Category title required.");
      final List<DSAction> actionList = actionCategory.getActionList();
      Assert.notEmpty(actionList, "At least one category action required.");
      actionList.forEach(this::validateActionCategoryAction);
    }
  }
  
  /**
   * Validate the given action data.
   * 
   * @param action
   *          - action to validate
   */
  private void validateActionCategoryAction(DSAction action) {
    Assert.hasText(action.getTitle(), "Action title is required.");
    final DSActionType type = action.getType();
    Assert.notNull(type, "Action type required.");
    
    /*
     * if (type == DSActionType.Single) {
     * Assert.isTrue(action.getPermission(DSActionConfig.Completion), "Completion is required."); }
     */
    
    if (action.isActive()) {
      final List<DSActionData> actionData = action.getActionData();
      // Assert.notEmpty(actionData, "At least one action data required.");
      if (actionData != null) {
        actionData.forEach(ad -> validateActionData(ad, type));
      }
    }
  }
  
  /**
   * Validate the action data values.
   * 
   * @param actionData
   *          - action data to validate
   * @param actionType
   *          - action type
   */
  private void validateActionData(DSActionData actionData, DSActionType actionType) {
    boolean validate = true;
    
    if (actionType == DSActionType.Group) {
      /* Action data type is optional in case of group, in case any 1 is present then do the validation. */
      if (actionData.getType() == null) {
        validate = false;
      }
    }
    if (validate) {
      Assert.notNull(actionData.getType(), "Action data type is required.");
      Assert.hasText(actionData.getLinkText(), "Action data Link text is required.");
      if (actionData.getType() != DSActionDataType.Schedule) {
        Assert.hasText(actionData.getUrl(), "Action data URL is required.");
      } 
    }
    
    
    /*
     * if (actionType == DSActionType.Group) {
     * Assert.isTrue(actionData.getPermission(DSActionConfig.Completion),
     * "Completion required for group type of action data."); }
     */
  }
  
  /**
   * Update the goal for the data in the form.
   * 
   * @param goal
   *          - goal to update
   * @param uidGenerator
   *          - the unique id generator
   */
  public void updateGoal(SPGoal goal, Supplier<String> uidGenerator) {
    BeanUtils.copyProperties(this, goal);
    // check all the actions and action data and set the uid
    devStrategyActionCategoryList.stream().forEach(
        actionCategory -> actionCategory.addUID(uidGenerator));
    goal.updateActionIds();
  }

  public int getDurationDays() {
    return durationDays;
  }

  public void setDurationDays(int durationDays) {
    this.durationDays = durationDays;
  }
}
