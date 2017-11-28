package com.sp.web.form.tutorial;

import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.model.goal.SPGoal;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class for SPTutorial.
 */
public class SPTutorialForm implements GenericForm<SPTutorialDao> {
  
  private String id;
  private String name;
  private String description;
  private String imgUrl;
  private List<TutorialStepForm> steps;
  private List<String> stepIdsToRemove;
  
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
  
  public List<TutorialStepForm> getSteps() {
    return steps;
  }
  
  public void setSteps(List<TutorialStepForm> steps) {
    this.steps = steps;
  }
  
  public List<String> getStepIdsToRemove() {
    return stepIdsToRemove;
  }

  public void setStepIdsToRemove(List<String> stepIdsToRemove) {
    this.stepIdsToRemove = stepIdsToRemove;
  }

  @Override
  public void validate() {
    Assert.notNull(name, "Name required.");
    Assert.notNull(description, "Description required.");
    Assert.notEmpty(steps, "At least one step required.");
  }

  @Override
  public void validateUpdate() {
    validateId();
    validate();
  }

  private void validateId() {
    Assert.hasText(id, "ID is required.");
  }

  @Override
  public void validateGet() {
    validateId();    
  }

  @Override
  public SPTutorialDao create(User user) {
    SPTutorialDao tutorial = new SPTutorialDao();
    tutorial.setCreatedOn(LocalDateTime.now());
    tutorial.setActive(true);
    updateBaseInfo(user, tutorial);
    final List<SPGoal> stepsToCreate = new ArrayList<SPGoal>();
    tutorial.setSteps(stepsToCreate);
    steps.forEach(step -> step.addNew(stepsToCreate));
    return tutorial;
  }

  private void updateBaseInfo(User user, SPTutorialDao tutorial) {
    tutorial.setName(name);
    tutorial.setDescription(description);
    tutorial.setImgUrl(imgUrl);
    tutorial.setUpdatedOn(LocalDateTime.now());
    tutorial.setUpdatedBy(new UserMarkerDTO(user));
  }

  @Override
  public void update(User user, SPTutorialDao tutorialToUpdate) {
    updateBaseInfo(user, tutorialToUpdate);
    final List<SPGoal> existingSteps = tutorialToUpdate.getSteps();
    final Map<String, SPGoal> stepMap = existingSteps.stream()
        .collect(Collectors.toMap(SPGoal::getId, Function.identity()));
    List<String> stepIds = new ArrayList<String>();
    for (TutorialStepForm step : steps) {
      final String stepId = step.getId();
      if (stepId != null) {
        SPGoal stepToUpdate = stepMap.get(stepId);
        Assert.notNull(stepToUpdate, "Step not found :" + stepId);
        step.update(stepToUpdate);
        stepIds.add(stepId);
      } else {
        step.addNew(existingSteps);
      }
    }
    final List<String> existingStepIds = tutorialToUpdate.getStepIds();
    if (existingStepIds != null) {
      existingStepIds.removeAll(stepIds);
      if (!existingStepIds.isEmpty()) {
        stepIdsToRemove = existingStepIds;
        existingStepIds.forEach(stepMap::remove);
        tutorialToUpdate.setSteps(new ArrayList<>(stepMap.values()));
      }
    }
    tutorialToUpdate.setStepIds(stepIds);
  }

  public boolean hasStepsToRemove() {
    return !CollectionUtils.isEmpty(stepIdsToRemove);
  }
}
