package com.sp.web.dto.goal;

import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.CompanyActionPlanSettings;

import java.util.HashSet;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO to send base action plan information along with user id's.
 */
public class ActionPlanUserListDTO extends BaseActionPlanDTO {
  
  private List<String> userIdList;
  private boolean editAllowed;
  private boolean readOnly;
  
  /**
   * Constructor.
   * 
   * @param ap
   *          - action plan
   * @param companyActionPlanSettings
   *          - company action plan settings
   * @param readOnlyProgramIds
   *          - read only program ids
   */
  public ActionPlanUserListDTO(ActionPlan ap, CompanyActionPlanSettings companyActionPlanSettings,
      HashSet<String> readOnlyProgramIds) {
    super(ap);
    if (companyActionPlanSettings != null) {
      userIdList = companyActionPlanSettings.getMemberIds();
    }
    // set the read only flag
    readOnly = readOnlyProgramIds.contains(ap.getId());
  }
  
  public List<String> getUserIdList() {
    return userIdList;
  }
  
  public void setUserIdList(List<String> userIdList) {
    this.userIdList = userIdList;
  }
  
  public boolean isEditAllowed() {
    return editAllowed;
  }
  
  public void setEditAllowed(boolean editAllowed) {
    this.editAllowed = editAllowed;
  }
  
  public boolean isReadOnly() {
    return readOnly;
  }
  
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  
}
