package com.sp.web.form.blueprint;

import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionType;
import com.sp.web.model.goal.GoalStatus;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The blueprint form to create or update the blueprint.
 */
public class BlueprintForm {
  
  private BlueprintMissionStatementForm missionStatement;
  private List<DSActionCategory> devStrategyActionCategoryList;
  private List<String> deleteList;
  
  public BlueprintMissionStatementForm getMissionStatement() {
    return missionStatement;
  }
  
  public void setMissionStatement(BlueprintMissionStatementForm missionStatement) {
    this.missionStatement = missionStatement;
  }
  
  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }
  
  public List<String> getDeleteList() {
    return deleteList;
  }
  
  public void setDeleteList(List<String> deleteList) {
    this.deleteList = deleteList;
  }
  
  /**
   * Updates the blueprint from the given data.
   * 
   * @param blueprint
   *          - blueprint to update
   */
  public void updateBlueprint(Blueprint blueprint) {
    
    Assert.isTrue(blueprint.getStatus() == GoalStatus.EDIT, "Blueprint not in edit state.");
    
    // updating the mission statement
    if (missionStatement != null) {
      missionStatement.updateMissionStatement(blueprint);
    }
    
    // update the objectives
    if (!CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
      devStrategyActionCategoryList.forEach(objective -> updateObjective(objective, blueprint));
    }
    
    // delete any UID present in the delete list
    if (!CollectionUtils.isEmpty(deleteList)) {
      final List<DSActionCategory> existingObjectives = blueprint
          .getDevStrategyActionCategoryList();
      if (!CollectionUtils.isEmpty(existingObjectives)) {
        delete(existingObjectives);
      }
      // cleaning up
      if (existingObjectives.isEmpty()) {
        blueprint.setDevStrategyActionCategoryList(null);
      }

    }
  }
  
  /**
   * Delete the objective if it is present in the delete list.
   * 
   * @param existingObjectives
   *          - the existing objectives list
   */
  private void delete(List<DSActionCategory> existingObjectives) {
    Iterator<DSActionCategory> iter = existingObjectives.iterator();
    while (iter.hasNext()) {
      DSActionCategory objective = iter.next();
      if (deleteList.remove(objective.getUid())) {
        iter.remove();
      } else {
        objective.delete(deleteList);
      }
    }
  }
  
  /**
   * Method to update the objective.
   * 
   * @param objective
   *          - objective
   * @param blueprint
   *          - blueprint
   * 
   */
  private void updateObjective(DSActionCategory objective, Blueprint blueprint) {
    
    List<DSActionCategory> objectiveList = blueprint.getDevStrategyActionCategoryList();
    if (objectiveList == null) {
      objectiveList = new ArrayList<DSActionCategory>();
      blueprint.setDevStrategyActionCategoryList(objectiveList);
    }
    
    Optional<DSActionCategory> findFirst = objectiveList.stream()
        .filter(actionCategory -> actionCategory.equals(objective)).findFirst();
    
    DSActionCategory objectiveToUpdate = null;
    if (findFirst.isPresent()) {
      objectiveToUpdate = findFirst.get();
    } else {
      objectiveToUpdate = new DSActionCategory();
      objectiveToUpdate.addUID(blueprint::getNextUID);
      objectiveToUpdate.setStatus(GoalStatus.ACTIVE);
      objectiveList.add(objectiveToUpdate);
    }
    
    objectiveToUpdate.setTitle(objective.getTitle());
    
    // update the key initiatives
    List<DSAction> actionList = objective.getActionList();
    if (actionList != null) {
      for (DSAction initiative : actionList) {
        updateInitiative(initiative, objectiveToUpdate, blueprint);
      }
    }
  }
  
  /**
   * Update the key initiative.
   * 
   * @param initiative
   *          - initiative
   * @param objective
   *          - objective
   * @param blueprint
   *          - blueprint
   */
  private void updateInitiative(DSAction initiative, DSActionCategory objective, Blueprint blueprint) {
    List<DSAction> actionList = objective.getActionList();
    DSAction initiativeToUpdate = null;
    
    // add action list if it does not exists
    if (actionList != null) {
      // search for existing initiative
      Optional<DSAction> findFirst = actionList.stream()
          .filter(action -> action.equals(initiative)).findFirst();
      if (findFirst.isPresent()) {
        initiativeToUpdate = findFirst.get();
      }
    } else {
      // creating action list
      actionList = new ArrayList<DSAction>();
      objective.setActionList(actionList);
    }
    
    // check if initiative exists else create a new one
    if (initiativeToUpdate == null) {
      initiativeToUpdate = new DSAction();
      initiativeToUpdate.setActive(true);
      initiativeToUpdate.addUID(blueprint::getNextUID);
      initiativeToUpdate.setType(DSActionType.Group);
      Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
      permissions.put(DSActionConfig.Note, Boolean.TRUE);
      permissions.put(DSActionConfig.Feedback, Boolean.TRUE);
      initiativeToUpdate.setPermissions(permissions);
      actionList.add(initiativeToUpdate);
    }
    
    // setting the updated data for the initiative
    initiativeToUpdate.setTitle(initiative.getTitle());
    
    // check the success measures for the initiative
    List<DSActionData> actionData = initiative.getActionData();
    if (actionData != null) {
      for (DSActionData successMeasure : actionData) {
        updateSuccessMeasure(successMeasure, initiativeToUpdate, blueprint);
      }
    }
  }
  
  /**
   * Update the success measure.
   * 
   * @param successMeasure
   *          - success measure to update
   * @param initiative
   *          - initiative
   * @param blueprint
   *          - blueprint
   */
  private void updateSuccessMeasure(DSActionData successMeasure, DSAction initiative,
      Blueprint blueprint) {
    List<DSActionData> actionData = initiative.getActionData();
    DSActionData successMeasureToUpdate = null;
    
    if (actionData != null) {
      Optional<DSActionData> findFirst = actionData.stream()
          .filter(ad -> ad.equals(successMeasure)).findFirst();
      if (findFirst.isPresent()) {
        successMeasureToUpdate = findFirst.get();
      }
    } else {
      actionData = new ArrayList<DSActionData>();
      initiative.setActionData(actionData);
    }
    
    if (successMeasureToUpdate == null) {
      successMeasureToUpdate = new DSActionData();
      successMeasureToUpdate.addUID(blueprint::getNextUID);
      Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
      permissions.put(DSActionConfig.Completion, Boolean.TRUE);
      successMeasureToUpdate.setPermissions(permissions);
      actionData.add(successMeasureToUpdate);
    }
    
    successMeasureToUpdate.setTitle(successMeasure.getTitle());
  }
}
