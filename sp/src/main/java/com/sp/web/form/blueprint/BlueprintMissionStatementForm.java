package com.sp.web.form.blueprint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintMissionStatement;

/**
 * @author Dax Abraham
 * 
 *         Form to input the mission statement for blueprint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintMissionStatementForm {
  
  private String text;
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  /**
   * Method to update the blueprint mission statement.
   * 
   * @param blueprint
   *          - blueprint to update
   */
  public void updateMissionStatement(Blueprint blueprint) {
    BlueprintMissionStatement missionStatement = blueprint.getMissionStatement();
    if (missionStatement == null) {
      missionStatement = new BlueprintMissionStatement();
      missionStatement.addUID(blueprint::getNextUID);
      blueprint.setMissionStatement(missionStatement);
    }
    missionStatement.setText(text);
  }
}
