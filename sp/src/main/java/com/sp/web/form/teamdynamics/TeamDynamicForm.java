package com.sp.web.form.teamdynamics;

import org.springframework.util.Assert;

import java.util.List;

/**
 * TeamDynamicsForm contains the input request for the users for which team dynamics is to be
 * performed.
 * 
 * @author pradeepruhil
 *
 */
public class TeamDynamicForm {
  
  /**
   * list if user ids.
   */
  private List<String> userIds;
  
  /**
   * Whether team dynamics is to be performed for PA or ERT-i. Default is for Erti.
   * 
   */
  private boolean isPa;
  
  public List<String> getUserIds() {
    return userIds;
  }
  
  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }
  
  public boolean isPa() {
    return isPa;
  }
  
  public void setPa(boolean isPa) {
    this.isPa = isPa;
  }
  
  public void validate() {
    Assert.notEmpty(userIds, "No user present to perform the team dynamics.");
  }
  
}
