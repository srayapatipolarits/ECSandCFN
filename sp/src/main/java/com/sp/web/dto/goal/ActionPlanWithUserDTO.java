package com.sp.web.dto.goal;

import com.sp.web.model.User;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.user.UserFactory;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class to send the list of users for the given action plan.
 */
public class ActionPlanWithUserDTO extends BaseActionPlanDTO {
  
  private int actionCount;
  private List<UserActionPlanCompletionDTO> userList;
  private boolean editAllowed;
  private boolean readOnly;
  private boolean onHold;
  
  /**
   * Constructor.
   * 
   * @param ap
   *          - action plan
   * @param companyActionPlanSettings
   *          - company action plan settings
   * @param readOnlyActionPlanIds
   *          - read only action plan ids
   */
  public ActionPlanWithUserDTO(ActionPlan ap, UserFactory userFactory,
      ActionPlanFactory actionPlanFactory, CompanyActionPlanSettings companyActionPlanSettings,
      HashSet<String> readOnlyActionPlanIds) {
    super(ap);
    if (companyActionPlanSettings != null) {
      List<String> userIdList = companyActionPlanSettings.getMemberIds();
      if (!CollectionUtils.isEmpty(userIdList)) {
        final String apId = ap.getId();
        userList = userIdList
            .stream()
            .map(
                uid -> getUserActionPlanCompletion(uid, userFactory, actionPlanFactory, apId,
                    companyActionPlanSettings)).filter(uap -> uap != null)
            .collect(Collectors.toList());
      }
      onHold = companyActionPlanSettings.isOnHold();
    }
    readOnly = readOnlyActionPlanIds.contains(ap.getId());
  }
  
  /**
   * Get the user action plan completion for the given user.
   * 
   * @param uid
   *          - user id
   * @param userFactory
   *          - user factory
   * @param actionPlanFactory
   *          - action plan factory
   * @param actionPlanId
   *          - action plan id
   * @param companyActionPlanSettings
   *          - company action plan settings
   * @return the user action plan completion
   */
  private UserActionPlanCompletionDTO getUserActionPlanCompletion(String uid,
      UserFactory userFactory, ActionPlanFactory actionPlanFactory, String actionPlanId,
      CompanyActionPlanSettings companyActionPlanSettings) {
    
    User user = userFactory.getUser(uid);
    if (user == null) {
      return null;
    }
    
    return new UserActionPlanCompletionDTO(uid, actionPlanFactory.getUserActionPlan(user),
        actionPlanId);
  }
  
  public List<UserActionPlanCompletionDTO> getUserList() {
    return userList;
  }
  
  public void setUserList(List<UserActionPlanCompletionDTO> userList) {
    this.userList = userList;
  }
  
  public int getActionCount() {
    return actionCount;
  }
  
  public void setActionCount(int actionCount) {
    this.actionCount = actionCount;
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

  public boolean isOnHold() {
    return onHold;
  }

  public void setOnHold(boolean onHold) {
    this.onHold = onHold;
  }
  
}
