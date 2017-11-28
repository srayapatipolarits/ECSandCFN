package com.sp.web.dto.todo;

import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionData;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO to store the data for the actions for a step of the action plan.
 */
public class ActionPlanStepActionTodoDTO  implements Serializable {
  
  private static final long serialVersionUID = 4970628181736878728L;
  private String uid;
  private String name;
  private long timeInMins;
  
  /**
   * Constructor.
   * 
   * @param dsAction
   *            - development strategy action 
   */
  public ActionPlanStepActionTodoDTO(DSAction dsAction) {
    this.uid = dsAction.getUid();
    this.name = dsAction.getTitle();
    this.timeInMins = dsAction.getTimeInMins();
  }

  /**
   * Constructor.
   * 
   * @param dsActionData
   *            - development strategy action data 
   */
  public ActionPlanStepActionTodoDTO(DSActionData dsActionData) {
    this.uid = dsActionData.getUid();
    this.name = dsActionData.getTitle();
  }

  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setTimeInMins(long timeInMins) {
    this.timeInMins = timeInMins;
  }
  
  public long getTimeInMins() {
    return timeInMins;
  }
}
