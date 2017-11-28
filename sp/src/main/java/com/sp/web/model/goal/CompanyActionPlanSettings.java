package com.sp.web.model.goal;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the company action plan settings.
 */
public class CompanyActionPlanSettings implements Serializable {
  
  private static final long serialVersionUID = -1230108434348691980L;
  private String id;
  private String companyId;
  private String actionPlanId;
  private boolean allMembers;
  private boolean onHold;
  private List<String> memberIds;

  /**
   * Default constructor.
   */
  public CompanyActionPlanSettings() { }
  
  /**
   * Constructor from action plan.
   * @param companyId
   *          - companyId
   * @param actionPlan
   *          - action plan
   */
  public CompanyActionPlanSettings(String companyId, ActionPlan actionPlan) {
    this.companyId = companyId;
    this.actionPlanId = actionPlan.getId();
    this.allMembers = actionPlan.isAllMembers();
    this.memberIds = new ArrayList<String>();
    Assert.hasText(companyId, "Company id required.");
    Assert.hasText(actionPlanId, "Action plan id required.");
  }

  public boolean isAllMembers() {
    return allMembers;
  }
  
  public void setAllMembers(boolean allMembers) {
    this.allMembers = allMembers;
  }
  
  public List<String> getMemberIds() {
    return memberIds;
  }
  
  public void setMemberIds(List<String> memberIds) {
    this.memberIds = memberIds;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getActionPlanId() {
    return actionPlanId;
  }

  public void setActionPlanId(String actionPlanId) {
    this.actionPlanId = actionPlanId;
  }

  /**
   * Removing the given member from the member ids for the given action plan.
   * 
   * @param uid
   *          - user id
   */
  public void removeMember(String uid) {
    memberIds.remove(uid);
  }

  public boolean isOnHold() {
    return onHold;
  }

  public void setOnHold(boolean onHold) {
    this.onHold = onHold;
  }

  public void addUser(String userId) {
    memberIds.add(userId);
  }
  
}
