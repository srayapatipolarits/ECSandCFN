package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * OrgPlanGroupActivity hold the information of org action activities for a group.
 * 
 * @author pradeepruhil
 *
 */
public class OrgPlanGroupActivity implements Serializable {
  
  private String groupName;
  
  private int totalMembers;
  
  private int completdKpi;
  
  private List<String> completedGroupSteps;
  /**
   * default serial version id.
   */
  private static final long serialVersionUID = -5529045751976633701L;
  
  public String getGroupName() {
    return groupName;
  }
  
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  
  public int getTotalMembers() {
    return totalMembers;
  }
  
  public void setTotalMembers(int totalMembers) {
    this.totalMembers = totalMembers;
  }
  
  public int getCompletdKpi() {
    return completdKpi;
  }
  
  public void setCompletdKpi(int completdKpi) {
    this.completdKpi = completdKpi;
  }
  
  public void addCompletedKpi(int completedKpiCount) {
    this.completdKpi = completedKpiCount + this.completdKpi;
  }
  
  public void setCompletedGroupSteps(List<String> completedGroupSteps) {
    this.completedGroupSteps = completedGroupSteps;
  }
  
  public List<String> getCompletedGroupSteps() {
    if (completedGroupSteps == null) {
      completedGroupSteps = new ArrayList<>();
    }
    return completedGroupSteps;
  }
  
}
