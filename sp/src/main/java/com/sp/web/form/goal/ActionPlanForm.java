package com.sp.web.form.goal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.StepType;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The form to capture the data and update the details for the action plan form.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionPlanForm {
  
  private String id;
  private String name;
  private String description;
  private String imageUrl;
  private List<PracticeAreaActionForm> practiceAreaList;
  private String createdByCompanyId;
  private ActionPlanType type;
  private StepType stepType;
  
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
  
  public List<PracticeAreaActionForm> getPracticeAreaList() {
    return practiceAreaList;
  }
  
  public void setPracticeAreaList(List<PracticeAreaActionForm> practiceAreaList) {
    this.practiceAreaList = practiceAreaList;
  }
  
  /**
   * Method to validate the form data.
   */
  public void validate() {
    Assert.hasText(name, "Name not found.");
    Assert.hasText(description, "Description not found.");
    Assert.hasText(createdByCompanyId, "Company id not found.");
    Assert.notEmpty(practiceAreaList, "Need at least one practice area.");
    Assert.notNull(type, "Plan type required.");
    // validate each of the practice area forms
    practiceAreaList.forEach(paf -> paf.validate(getStepType()));
  }

  /**
   * Update the action plan with the data in this form..
   * 
   * @param actionPlan
   *            - action plan to update
   */
  public void updateActionPlan(ActionPlan actionPlan) {
    BeanUtils.copyProperties(this, actionPlan, "stepType");
    // allow change to step type only
    // if the action plan is not active
    if (!actionPlan.isActive()) {
      actionPlan.setStepType(stepType);
    }
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ActionPlanType getType() {
    return type;
  }

  public void setType(ActionPlanType type) {
    this.type = type;
  }

  public StepType getStepType() {
    return stepType;
  }

  public void setStepType(StepType stepType) {
    this.stepType = stepType;
  }

  public String getCreatedByCompanyId() {
    return createdByCompanyId;
  }

  public void setCreatedByCompanyId(String createdByCompanyId) {
    this.createdByCompanyId = createdByCompanyId;
  }

}
